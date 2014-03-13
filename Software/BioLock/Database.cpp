/*
 * database.cpp
 *
 *  Created on: 2014-02-28
 *      Author: Mavis Chan
 *
 *  Notes:	REMEMBER TO UNMOUNT WHEN DONE - ELSE CHANGES WONT BE MADE TO SD CARD
 *  		All filenames saved in uppercase
 *  		Max file name is 8 characters - can be changed i think
 *  		Filenames are padded with '\0' until 8 characters long
 *  			eg: "23" becomes "23      "
 *  			Used atoi => invalid filenames become 0, therefore, start id count at 1
 *  		System has not been tested when subfolders exist
 *
 *  Issues/todos: add method to delete directories if needed
 *	
 *	EFSL Manual: https://www.ualberta.ca/~delliott/local/ece492/appnotes/2013w/SD_card_interfacing/efsl/EFSL_manual.pdf
 */

#include "Database.h"
using namespace std;

Database::Database(OS_EVENT *databaseSemaphore) :
		m_databaseSemaphore(databaseSemaphore) {
	INT8U err = OS_NO_ERR;
	int ret = 0;

	//Blocking call
	OSSemPend(m_databaseSemaphore, 0, &err);
	if (err != OS_NO_ERR) {
		printf("Database error. Check to ensure access is allowed\n");
		throw exception();
	}

	// Initialises the database on the SD card
	ret = efs_init(&db, SDCARD_NAME);
	if (ret != 0) {
		printf("Could not initialize database\n");
		throw exception();
	}
}

// JSON return value when no entry found in database
string Database::noRecord() {
	Json::Value noRecord;
	noRecord["id"] = -1;
	noRecord["msg"] = "No Records Found";
	return noRecord.toStyledString();
}

// Lists all tables (folders) or tuplets (files) in directory specified
string Database::listAll(char *path) {
	DirList list;
	int ret, file;
	string results = "[";

	ret = ls_openDir(&list, &db.myFs, path);
	if (ret == -1)
		printf("Could not open directory. Please check definition of path\n");
	if (ls_getNext(&list) == 0) {
		file = atoi((const char*) list.currentEntry.FileName);
		if (file == 0)
			return " ";
		results.append(findEntry(path, file));
	}
	while (ls_getNext(&list) == 0) {
		file = atoi((const char*) list.currentEntry.FileName);
		if (file == 0)
			break;
		results.append(",");
		results.append(findEntry(path, file));
	}
	results.append("]");
	return results;
}

// Creates new tables (folders)
int Database::createTable(char *tableName) {
	int ret;
	ret = makedir(&db.myFs, tableName);
	if (ret == -1) {
		printf("Table already exists\n");
		return -1;
	}
	return 1;
}

// Adds a tuple to the role table
int Database::insertRole(int rid, Role value) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLES, rid);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	// File already exists
	if (ret == -2) {
		printf("Role already exists\n");
		return -1;
	}

	Json::Value nodeToInsert;
	nodeToInsert["id"] = value.id;
	nodeToInsert["name"] = value.name;
	nodeToInsert["admin"] = value.admin;
	nodeToInsert["enabled"] = value.enabled;
	ostringstream start, end;
	start << static_cast<long int>(value.startDate);
	end << static_cast<long int>(value.endDate);
	nodeToInsert["startDate"] = start.str();
	nodeToInsert["endDate"] = end.str();

	string jsonValue = nodeToInsert.toStyledString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("Role could not be added\n");
		return -1;
	}
	tuple.DirEntry.Attribute = rid;
	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("Role could not be added\n");
		return -1;
	}

	return 1;
}

// Adds a tuple to the user table
int Database::insertUser(int uid, User value) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USERS, uid);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("User already exists\n");
		return -1;
	}

	Json::Value nodeToInsert;
	nodeToInsert["id"] = value.id;
	nodeToInsert["name"] = value.name;
	nodeToInsert["enabled"] = value.enabled;
	ostringstream start, end;
	start << static_cast<long int>(value.startDate);
	end << static_cast<long int>(value.endDate);
	nodeToInsert["startDate"] = start.str();
	nodeToInsert["endDate"] = end.str();

	string jsonValue = nodeToInsert.toStyledString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("User could not be added\n");
		return -1;
	}

	tuple.DirEntry.Attribute = uid;
	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("User could not be added\n");
		return -1;
	}

	return 1;
}

// Adds a tuple to the role schedule table
int Database::insertRoleSched(int id, RoleSchedule value) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLE_SCHEDULE, id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("Role schedule already exists\n");
		return -1;
	}

	Json::Value nodeToInsert;
	nodeToInsert["rid"] = value.rid;
	nodeToInsert["startTime"] = value.startTime;
	nodeToInsert["endTime"] = value.endTime;
	nodeToInsert["days"] = value.days;
	ostringstream start, end;
	start << static_cast<long int>(value.startDate);
	end << static_cast<long int>(value.endDate);
	nodeToInsert["startDate"] = start.str();
	nodeToInsert["endDate"] = end.str();

	string jsonValue = nodeToInsert.toStyledString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("Role schedule could not be added\n");
		return -1;
	}

	tuple.DirEntry.Attribute = id;
	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("Role schedule could not be added\n");
		return -1;
	}

	return 1;
}

// Adds a tuple to the user roles table
int Database::insertUserRole(int id, UserRole value) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_ROLES, id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("User role already exists\n");
		return -1;
	}

	Json::Value nodeToInsert;
	nodeToInsert["id"] = value.id;
	nodeToInsert["uid"] = value.uid;
	nodeToInsert["rid"] = value.rid;
	ostringstream start, end;
	start << static_cast<long int>(value.startDate);
	end << static_cast<long int>(value.endDate);
	nodeToInsert["startDate"] = start.str();
	nodeToInsert["endDate"] = end.str();

	string jsonValue = nodeToInsert.toStyledString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("User role could not be added\n");
		return -1;
	}

	tuple.DirEntry.Attribute = id;
	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("User role could not be added\n");
		return -1;
	}

	return 1;
}

// Adds a tuple to the user prints table
int Database::insertUserPrint(int id, UserPrint value) {
	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_PRINTS, id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("User prints already exists\n");
		return -1;
	}

	Json::Value nodeToInsert;
	nodeToInsert["id"] = value.id;
	nodeToInsert["fid"] = value.fid;
	nodeToInsert["uid"] = value.uid;

	string jsonValue = nodeToInsert.toStyledString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("User print could not be added\n");
		return -1;
	}

	tuple.DirEntry.Attribute = id;
	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("User print could not be added\n");
		return -1;
	}

	return 1;
}

// Adds a tuple to the history table
int Database::insertHistory(int id, History value) {
	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", HISTORY, id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("History already exists\n");
		return -1;
	}

	Json::Value nodeToInsert;
	nodeToInsert["id"] = value.id;
	nodeToInsert["uid"] = value.uid;
	nodeToInsert["success"] = value.success;
	ostringstream time;
	time << static_cast<long int>(value.time);
	nodeToInsert["time"] = time.str();

	string jsonValue = nodeToInsert.toStyledString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("History could not be added\n");
		return -1;
	}

	tuple.DirEntry.Attribute = id;
	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("History could not be added\n");
		return -1;
	}

	return 1;
}

// Searches for specific file at the declared folder
string Database::findEntry(const char *path, int id) {
	File tuple;
	int ret;
	unsigned int sizeRead;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", path, id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'r');
	if (ret == -1) {
		printf("Entry could not be found\n");
		return noRecord();
	}

	euint8 *fileBuffer = (euint8 *) malloc(tuple.FileSize * sizeof(euint8));

	sizeRead = file_read(&tuple, tuple.FileSize, fileBuffer);
	if (sizeRead == 0) {
		printf("Attributes could not read\n");
		return "";
	}

	string attr = string((char *) fileBuffer);
	free(fileBuffer);
	file_fclose(&tuple);

	return attr.c_str();
}

// Searches for a role by rid
string Database::findRole(int rid) {
	File tuple;
	int ret;
	unsigned int sizeRead;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLES, rid);
	ret = file_fopen(&tuple, &db.myFs, filename, 'r');
	if (ret == -1) {
		printf("Role could not be found\n");
		return noRecord();
	}

	euint8 *fileBuffer = (euint8 *) malloc(tuple.FileSize * sizeof(euint8));

	sizeRead = file_read(&tuple, tuple.FileSize, fileBuffer);
	if (sizeRead == 0) {
		printf("Role attributes could not read\n");
		return "";
	}

	string roleAttr = string((char *) fileBuffer);
	free(fileBuffer);
	file_fclose(&tuple);

	return roleAttr.c_str();
}

// Searches for a user by uid
string Database::findUser(int uid) {
	File tuple;
	int ret;
	unsigned int sizeRead;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USERS, uid);
	ret = file_fopen(&tuple, &db.myFs, filename, 'r');
	if (ret == -1) {
		printf("User could not be found\n");
		return noRecord();
	}

	euint8 *fileBuffer = (euint8 *) malloc(tuple.FileSize * sizeof(euint8));

	sizeRead = file_read(&tuple, tuple.FileSize, fileBuffer);
	if (sizeRead == 0) {
		printf("User attributes could not read\n");
		return "";
	}

	string userAttr = string((char *) fileBuffer);
	free(fileBuffer);
	file_fclose(&tuple);

	return userAttr.c_str();
}

// Searches for a role schedule by id
string Database::findRoleSchedule(int id) {
	File tuple;
	int ret;
	unsigned int sizeRead;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLE_SCHEDULE, id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'r');
	if (ret == -1) {
		printf("Role schedule could not be found\n");
		return noRecord();
	}

	euint8 *fileBuffer = (euint8 *) malloc(tuple.FileSize * sizeof(euint8));

	sizeRead = file_read(&tuple, tuple.FileSize, fileBuffer);
	if (sizeRead == 0) {
		printf("Role schedule attributes could not read\n");
		return "";
	}

	string roleSched = string((char *) fileBuffer);
	free(fileBuffer);
	file_fclose(&tuple);

	return roleSched.c_str();
}

// Searches for a role schedule by id
string Database::findUserRole(int id) {
	File tuple;
	int ret;
	unsigned int sizeRead;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_ROLES, id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'r');
	if (ret == -1) {
		printf("User role could not be found\n");
		return noRecord();
	}

	euint8 *fileBuffer = (euint8 *) malloc(tuple.FileSize * sizeof(euint8));

	sizeRead = file_read(&tuple, tuple.FileSize, fileBuffer);
	if (sizeRead == 0) {
		printf("User role attributes could not read\n");
		return "";
	}

	string userRole = string((char *) fileBuffer);
	free(fileBuffer);
	file_fclose(&tuple);

	return userRole.c_str();
}

// Searches for users with specific role
string Database::findRoleUser(int rid) {
	Json::Reader reader;
	Json::Value attr;
	DirList list;
	int ret, file;
	string results = "";

	// Searches through user roles
	ret = ls_openDir(&list, &db.myFs, (eint8 *) USER_ROLES);
	if (ret == -1){
		printf("Could not open directory\n");
		return noRecord();
	}
	while (ls_getNext(&list) == 0) {
		file = atoi((const char*) list.currentEntry.FileName);
		if (file == 0)
			break;
		string userRole = findEntry(USER_ROLES, file);
		reader.parse(userRole, attr, true);
		// User role with matching rid found
		if (attr["rid"].asInt() == rid)
			results.append(userRole);
	}
	return results;
}

// Searches for a role schedule by id
string Database::findUserPrint(int id) {
	File tuple;
	int ret;
	unsigned int sizeRead;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_PRINTS, id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'r');
	if (ret == -1) {
		printf("User prints could not be found\n");
		return noRecord();
	}

	euint8 *fileBuffer = (euint8 *) malloc(tuple.FileSize * sizeof(euint8));

	sizeRead = file_read(&tuple, tuple.FileSize, fileBuffer);
	if (sizeRead == 0) {
		printf("User prints attributes could not read\n");
		return "";
	}

	string userPrint = string((char *) fileBuffer);
	free(fileBuffer);
	file_fclose(&tuple);

	return userPrint.c_str();
}

// Searches for a history by id
string Database::findHistory(int id) {
	File tuple;
	int ret;
	unsigned int sizeRead;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", HISTORY, id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'r');
	if (ret == -1) {
		printf("History could not be found\n");
		return noRecord();
	}

	euint8 *fileBuffer = (euint8 *) malloc(tuple.FileSize * sizeof(euint8));

	sizeRead = file_read(&tuple, tuple.FileSize, fileBuffer);
	if (sizeRead == 0) {
		printf("History attributes could not read\n");
		return "";
	}

	string history = string((char *) fileBuffer);
	free(fileBuffer);
	file_fclose(&tuple);

	return history.c_str();
}

// Updates role by deleting entry and creating new entry
int Database::editRole(int rid, Role value) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLES, rid);
	rmfile(&db.myFs, (euint8*) filename);
	return insertRole(rid, value);
}

// Updates user by deleting entry and creating new entry
int Database::editUser(int uid, User value) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USERS, uid);
	rmfile(&db.myFs, (euint8*) filename);
	return insertUser(uid, value);
}

// Updates role sched by deleting entry and creating new entry
int Database::editRoleSched(int id, RoleSchedule value) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLE_SCHEDULE, id);
	rmfile(&db.myFs, (euint8*) filename);
	return insertRoleSched(id, value);
}

// Updates user role by deleting entry and creating new entry
int Database::editUserRole(int id, UserRole value) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_ROLES, id);
	rmfile(&db.myFs, (euint8*) filename);
	return insertUserRole(id, value);
}

// Updates user print by deleting entry and creating new entry
int Database::editUserPrint(int id, UserPrint value) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_PRINTS, id);
	rmfile(&db.myFs, (euint8*) filename);
	return insertUserPrint(id, value);
}

// Deletes role entry with corresponding rid
int Database::deleteRole(int rid) {
	Json::Reader reader;
	Json::Value attr;
	DirList list;
	int ret, file;
	char filename[MAXBUF_LENGTH];

	// Delete role
	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLES, rid);
	ret = rmfile(&db.myFs, (euint8*) filename);
	if (ret != 0) {
		printf("Role could not be deleted, please try again later\n");
		return -1;
	}

	// Delete role schedule with same rid
	ret = ls_openDir(&list, &db.myFs, (eint8 *) ROLE_SCHEDULE);
	if (ret == -1)
		printf("Could not open directory\n");
	while (ls_getNext(&list) == 0) {
		file = atoi((const char*) list.currentEntry.FileName);
		if (file == 0)
			break;
		string roleSched = findEntry(ROLE_SCHEDULE, file);
		reader.parse(roleSched, attr, true);
		if (attr["rid"] == rid) {
			ret = deleteRoleSchedule(file);
			if (ret == -1) {
				printf("Role schedule: %d could not be deleted\n", file);
				return -1;
			}
		}
	}

	// Delete user roles with same rid
	ret = ls_openDir(&list, &db.myFs, (eint8 *) USER_ROLES);
	if (ret == -1)
		printf("Could not open directory\n");
	while (ls_getNext(&list) == 0) {
		file = atoi((const char*) list.currentEntry.FileName);
		if (file == 0)
			break;
		string userRole = findEntry(USER_ROLES, file);
		reader.parse(userRole, attr, true);
		if (attr["rid"] == rid) {
			deleteUserRole(file);
			if (ret == -1) {
				printf("User role: %d could not be deleted\n", file);
				return -1;
			}
		}
	}

	return 1;
}

// Enables/disables user entry with corresponding uid
int Database::enableUser(int uid, bool enable) {
	Json::Reader reader;
	Json::Value userAttr;
	User updatedUser;
	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	string user = findUser(uid);
	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USERS, uid);
	ret = rmfile(&db.myFs, (euint8*) filename);
	if (ret != 0) {
		printf(
				"User enable status could not be changed, please try again later\n");
		return -1;
	}
	reader.parse(user, userAttr, true);
	userAttr["enabled"] = enable;
	string jsonValue = userAttr.toStyledString();
	file_fopen(&tuple, &db.myFs, filename, 'w');
	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf(
				"User enable status could not be changed, please try again later\n");
		return -1;
	}
	tuple.DirEntry.Attribute = uid;
	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("User enable status could not be changed\n");
		return -1;
	}

	return 1;
}

// Deletes role schedule entry with corresponding id
int Database::deleteRoleSchedule(int id) {
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLE_SCHEDULE, id);
	ret = rmfile(&db.myFs, (euint8*) filename);
	if (ret != 0) {
		printf("Role schedule could not be deleted, please try again later\n");
		return -1;
	}
	return 1;
}

// Deletes user role entry with corresponding id
int Database::deleteUserRole(int id) {
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_ROLES, id);
	ret = rmfile(&db.myFs, (euint8*) filename);
	if (ret != 0) {
		printf("User role could not be deleted, please try again later\n");
		return -1;
	}
	return 1;
}

// Deletes user print entry with corresponding id
int Database::deleteUserPrint(int id) {
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_PRINTS, id);
	ret = rmfile(&db.myFs, (euint8*) filename);
	if (ret != 0) {
		printf("User print could not be deleted, please try again later\n");
		return -1;
	}
	return 1;
}

void Database::testPopulate() {
	User u1;
	u1.id = 1;
	u1.name = "Mavis Chan";
	u1.enabled = true;
	u1.startDate = time(0);
	u1.endDate = time(0);
	insertUser(u1.id, u1);

	User u2;
	u2.id = 2;
	u2.name = "Brent Erickson";
	u2.enabled = true;
	u2.startDate = time(0);
	u2.endDate = time(0);
	insertUser(u2.id, u2);

	User u3;
	u3.id = 3;
	u3.name = "Sydney Bitner";
	u3.enabled = true;
	u3.startDate = time(0);
	u3.endDate = time(0);
	insertUser(u3.id, u3);

	Role r1;
	r1.id = 1;
	r1.name = "Owner";
	r1.admin = true;
	r1.enabled = true;
	r1.startDate = time(0);
	r1.endDate = time(0);
	insertRole(r1.id, r1);

	Role r2;
	r2.id = 2;
	r2.name = "Guest";
	r2.admin = false;
	r2.enabled = true;
	r2.startDate = time(0);
	r2.endDate = time(0);
	insertRole(r2.id, r2);

	RoleSchedule rs1;
	rs1.id = 1;
	rs1.rid = 1;
	rs1.startDate = time(0);
	rs1.endDate = time(0);
	rs1.days = 50;
	rs1.startTime = 9;
	rs1.endTime = 23;
	insertRoleSched(rs1.id, rs1);

	RoleSchedule rs2;
	rs2.id = 2;
	rs2.rid = 2;
	rs2.startDate = time(0);
	rs2.endDate = time(0);
	rs2.days = 5;
	rs2.startTime = 9;
	rs2.endTime = 11;
	insertRoleSched(rs2.id, rs2);

	RoleSchedule rs3;
	rs3.id = 3;
	rs3.rid = 2;
	rs3.startDate = time(0);
	rs3.endDate = time(0);
	rs3.days = 1;
	rs3.startTime = 13;
	rs3.endTime = 14;
	insertRoleSched(rs3.id, rs3);

	UserRole ur1;
	ur1.id = 1;
	ur1.uid = 1;
	ur1.rid = 1;
	ur1.startDate = time(0);
	ur1.endDate = time(0);
	insertUserRole(ur1.id, ur1);

	UserRole ur2;
	ur2.id = 2;
	ur2.uid = 2;
	ur2.rid = 1;
	ur2.startDate = time(0);
	ur2.endDate = time(0);
	insertUserRole(ur2.id, ur2);

	UserRole ur3;
	ur3.id = 3;
	ur3.uid = 3;
	ur3.rid = 2;
	ur3.startDate = time(0);
	ur3.endDate = time(0);
	insertUserRole(ur3.id, ur3);

	UserPrint up1;
	up1.id = 1;
	up1.uid = 1;
	up1.fid = 1;
	insertUserPrint(up1.id, up1);

	UserPrint up2;
	up2.id = 2;
	up2.uid = 2;
	up2.fid = 1;
	insertUserPrint(up2.id, up2);

	UserPrint up3;
	up3.id = 3;
	up3.uid = 2;
	up3.fid = 1;
	insertUserPrint(up3.id, up3);

	UserPrint up4;
	up4.id = 4;
	up4.uid = 3;
	up4.fid = 1;
	insertUserPrint(up4.id, up4);

	History h1;
	h1.id = 1;
	h1.uid = 1;
	h1.success = true;
	h1.time = time(0);
	insertHistory(h1.id, h1);

	History h2;
	h2.id = 2;
	h2.uid = 1;
	h2.success = false;
	h2.time = time(0);
	insertHistory(h2.id, h2);

	History h3;
	h3.id = 3;
	h3.uid = 2;
	h3.success = false;
	h3.time = time(0);
	insertHistory(h3.id, h3);

	History h4;
	h4.id = 4;
	h4.uid = 3;
	h4.success = false;
	h4.time = time(0);
	insertHistory(h4.id, h4);
}

// Unmount the file system
Database::~Database() {
	fs_umount(&db.myFs);
	OSSemPost(m_databaseSemaphore);
}
