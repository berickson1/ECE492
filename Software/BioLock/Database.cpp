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
	string noRecordString = noRecord.toStyledString();
	return noRecordString;
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
		// If not an empty file
		if (file != 0)
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
int Database::insertRole(Role value) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLES, value.id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	// File already exists
	if (ret == -2) {
		printf("Role already exists\n");
		return -1;
	}

	string jsonValue = value.toJSONString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("Role could not be added\n");
		return -1;
	}
	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("Role could not be added\n");
		return -1;
	}

	return 1;
}

// Adds a tuple to the user table
int Database::insertUser(User value) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USERS, value.id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("User already exists\n");
		return -1;
	}

	string jsonValue = value.toJSONString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("User could not be added\n");
		return -1;
	}

	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("User could not be added\n");
		return -1;
	}

	return 1;
}

// Adds a tuple to the role schedule table
int Database::insertRoleSched(RoleSchedule value) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLE_SCHEDULE, value.id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("Role schedule already exists\n");
		return -1;
	}

	string jsonValue = value.toJSONString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("Role schedule could not be added\n");
		return -1;
	}

	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("Role schedule could not be added\n");
		return -1;
	}

	return 1;
}

// Adds a tuple to the user roles table
int Database::insertUserRole(UserRole value) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_ROLES, value.id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("User role already exists\n");
		return -1;
	}

	string jsonValue = value.toJSONString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("User role could not be added\n");
		return -1;
	}

	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("User role could not be added\n");
		return -1;
	}

	return 1;
}

// Adds a tuple to the user prints table
int Database::insertUserPrint(UserPrint value) {
	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_PRINTS, value.id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("User prints already exists\n");
		return -1;
	}

	string jsonValue = value.toJSONString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("User print could not be added\n");
		return -1;
	}

	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("User print could not be added\n");
		return -1;
	}

	return 1;
}

// Adds a tuple to the history table
int Database::insertHistory(History value) {
	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", HISTORY, value.id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("History already exists\n");
		return -1;
	}

	string jsonValue = value.toJSONString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("History could not be added\n");
		return -1;
	}

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

	string attr = "";
	attr.append((char*) fileBuffer, sizeRead);
	free(fileBuffer);
	file_fclose(&tuple);

	return attr;
}

// Searches for a role by rid
string Database::findRole(int rid) {
	return findEntry(ROLES, rid);
}

// Searches for a user by uid
string Database::findUser(int uid) {
	return findEntry(USERS, uid);
}

// Searches for a role schedule by id
string Database::findRoleSchedule(int id) {
	return findEntry(ROLE_SCHEDULE, id);
}

// Searches for a role schedule by id
string Database::findUserRole(int id) {
	return findEntry(USER_ROLES, id);
}

// Searches for users with specific role
string Database::findRoleUser(int rid) {
	DirList list;
	int ret, file;
	string results = "";

	// Searches through user roles
	ret = ls_openDir(&list, &db.myFs, (eint8 *) USER_ROLES);
	if (ret == -1) {
		printf("Could not open directory\n");
		return noRecord();
	}
	while (ls_getNext(&list) == 0) {
		file = atoi((const char*) list.currentEntry.FileName);
		if (file == 0)
			break;
		UserRole userRole ;
		userRole.loadFromJson(findEntry(USER_ROLES, file));
		// User role with matching rid found
		if (userRole.rid == rid)
			results.append(userRole.toJSONString());
	}
	return results;
}

// Searches for a role schedule by id
string Database::findUserPrint(int id) {
	return findEntry(USER_PRINTS, id);
}

// Searches for a history by id
string Database::findHistory(int id) {
	return findEntry(HISTORY, id);
}

// Updates role by deleting entry and creating new entry
int Database::editRole(Role value) {
	deleteEntry(ROLES, value.id);
	return insertRole(value);
}

// Updates user by deleting entry and creating new entry
int Database::editUser(User value) {
	deleteEntry(USERS, value.id);
	return insertUser(value);
}

// Updates role sched by deleting entry and creating new entry
int Database::editRoleSched(RoleSchedule value) {
	deleteEntry(ROLE_SCHEDULE, value.id);
	return insertRoleSched(value);
}

// Updates user role by deleting entry and creating new entry
int Database::editUserRole(UserRole value) {
	deleteEntry(USER_ROLES, value.id);
	return insertUserRole(value);
}

// Updates user print by deleting entry and creating new entry
int Database::editUserPrint(UserPrint value) {
	deleteEntry(USER_PRINTS, value.id);
	return insertUserPrint(value);
}

// Deletes role entry with corresponding rid
int Database::deleteRole(int rid) {
	DirList list;
	int ret, file;

	// Delete role
	deleteEntry(ROLES, rid);

	// Delete role schedule with same rid
	ret = ls_openDir(&list, &db.myFs, (eint8 *) ROLE_SCHEDULE);
	if (ret == -1)
		printf("Could not open directory\n");
	while (ls_getNext(&list) == 0) {
		file = atoi((const char*) list.currentEntry.FileName);
		if (file == 0)
			break;
		RoleSchedule roleSchedule;
		roleSchedule.loadFromJson(findEntry(ROLE_SCHEDULE, file));
		if (roleSchedule.rid == rid) {
			ret = deleteEntry(ROLE_SCHEDULE, rid);
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
		UserRole userRole;
		userRole.loadFromJson(findEntry(USER_ROLES, file));
		if (userRole.rid == rid) {
			ret = deleteEntry(USER_ROLES, rid);
		}
	}
	return 1;
}

// Enables/disables user entry with corresponding uid
int Database::enableUser(int uid, bool enable) {
	User updatedUser;
	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	User user;
	user.loadFromJson(findUser(uid));
	ret = deleteEntry(USERS, uid);
	if (ret != 0) {
		printf(
				"User enable status could not be changed, please try again later\n");
		return -1;
	}
	user.enabled = enable;
	string jsonValue = user.toJSONString();
	file_fopen(&tuple, &db.myFs, filename, 'w');
	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf(
				"User enable status could not be changed, please try again later\n");
		return -1;
	}
	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("User enable status could not be changed\n");
		return -1;
	}

	return 1;
}

// Deletes role schedule entry with corresponding id
int Database::deleteRoleSchedule(int id) {
	return deleteEntry(ROLE_SCHEDULE, id);
}

// Deletes user role entry with corresponding id
int Database::deleteUserRole(int id) {
	return deleteEntry(USER_ROLES, id);
}

// Deletes user print entry with corresponding id
int Database::deleteUserPrint(int id) {
	return deleteEntry(USER_PRINTS, id);
}

//Clears the database
int Database::clearAll() {
	int ret;

	ret = clearTable(ROLES);
	if (ret != 1) {
		return ret;
	}
	ret = clearTable(USERS);
	if (ret != 1) {
		return ret;
	}
	ret = clearTable(ROLE_SCHEDULE);
	if (ret != 1) {
		return ret;
	}
	ret = clearTable(USER_ROLES);
	if (ret != 1) {
		return ret;
	}
	ret = clearTable(USER_PRINTS);
	if (ret != 1) {
		return ret;
	}
	ret = clearTable(HISTORY);
	if (ret != 1) {
		return ret;
	}
	return ret;
}

// Clears the table
int Database::clearTable(char *path) {
	int ret, file;
	DirList list;

	ret = ls_openDir(&list, &db.myFs, (eint8 *) path);
	if (ret == -1)
		printf("Could not open directory\n");
	while (ls_getNext(&list) == 0) {
		file = atoi((const char*) list.currentEntry.FileName);
		if (file == 0)
			break;
		ret = deleteEntry(path, file);
	}
	return ret;
}

// Deletes the specified file
int Database::deleteEntry(char *path, int id) {
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", path, id);
	ret = rmfile(&db.myFs, (euint8*) filename);
	if (ret != 0) {
		printf("Entry could not be deleted, please try again later\n");
		return -1;
	}
	return id;
}

void Database::testPopulate() {
	User u1;
	u1.id = 1;
	u1.name = "Mavis Chan";
	u1.enabled = true;
	u1.startDate = time(0);
	u1.endDate = time(0);
	insertUser(u1);

	User u2;
	u2.id = 2;
	u2.name = "Brent Erickson";
	u2.enabled = true;
	u2.startDate = time(0);
	u2.endDate = time(0);
	insertUser(u2);

	User u3;
	u3.id = 3;
	u3.name = "Sydney Bitner";
	u3.enabled = true;
	u3.startDate = time(0);
	u3.endDate = time(0);
	insertUser(u3);

	Role r1;
	r1.id = 1;
	r1.name = "Owner";
	r1.admin = true;
	r1.enabled = true;
	r1.startDate = time(0);
	r1.endDate = time(0);
	insertRole(r1);

	Role r2;
	r2.id = 2;
	r2.name = "Guest";
	r2.admin = false;
	r2.enabled = true;
	r2.startDate = time(0);
	r2.endDate = time(0);
	insertRole(r2);

	RoleSchedule rs1;
	rs1.id = 1;
	rs1.rid = 1;
	rs1.startDate = time(0);
	rs1.endDate = time(0);
	rs1.days = 50;
	rs1.startTime = 9;
	rs1.endTime = 23;
	insertRoleSched(rs1);

	RoleSchedule rs2;
	rs2.id = 2;
	rs2.rid = 2;
	rs2.startDate = time(0);
	rs2.endDate = time(0);
	rs2.days = 5;
	rs2.startTime = 9;
	rs2.endTime = 11;
	insertRoleSched(rs2);

	RoleSchedule rs3;
	rs3.id = 3;
	rs3.rid = 2;
	rs3.startDate = time(0);
	rs3.endDate = time(0);
	rs3.days = 1;
	rs3.startTime = 13;
	rs3.endTime = 14;
	insertRoleSched(rs3);

	UserRole ur1;
	ur1.id = 1;
	ur1.uid = 1;
	ur1.rid = 1;
	ur1.startDate = time(0);
	ur1.endDate = time(0);
	insertUserRole(ur1);

	UserRole ur2;
	ur2.id = 2;
	ur2.uid = 2;
	ur2.rid = 1;
	ur2.startDate = time(0);
	ur2.endDate = time(0);
	insertUserRole(ur2);

	UserRole ur3;
	ur3.id = 3;
	ur3.uid = 3;
	ur3.rid = 2;
	ur3.startDate = time(0);
	ur3.endDate = time(0);
	insertUserRole(ur3);

	UserPrint up1;
	up1.uid = 1;
	up1.id = 1;
	insertUserPrint(up1);

	UserPrint up2;
	up2.uid = 2;
	up2.id = 2;
	insertUserPrint(up2);

	UserPrint up3;
	up3.uid = 3;
	up3.id = 3;
	insertUserPrint(up3);

	UserPrint up4;
	up4.uid = 2;
	up4.id = 7;
	insertUserPrint(up4);

	History h1;
	h1.id = 1;
	h1.uid = 1;
	h1.success = true;
	h1.time = time(0);
	insertHistory(h1);

	History h2;
	h2.id = 2;
	h2.uid = 1;
	h2.success = false;
	h2.time = time(0);
	insertHistory(h2);

	History h3;
	h3.id = 3;
	h3.uid = 2;
	h3.success = false;
	h3.time = time(0);
	insertHistory(h3);

	History h4;
	h4.id = 4;
	h4.uid = 3;
	h4.success = false;
	h4.time = time(0);
	insertHistory(h4);
}

// Unmount the file system
Database::~Database() {
	fs_umount(&db.myFs);
	OSSemPost(m_databaseSemaphore);
}
