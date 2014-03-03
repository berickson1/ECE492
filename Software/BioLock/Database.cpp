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
 *  		System has not been tested when subfolders exist
 *
 *  Issues/todos: add method to delete directories if needed
 *	
 *	EFSL Manual: https://www.ualberta.ca/~delliott/local/ece492/appnotes/2013w/SD_card_interfacing/efsl/EFSL_manual.pdf
 */

#include "Database.h"
using namespace std;

Database::Database(OS_EVENT *databaseSemaphore) : m_databaseSemaphore(databaseSemaphore) {
	INT8U err = OS_NO_ERR;
	int ret = 0;

	//Blocking call
	OSMutexPend(m_databaseSemaphore, 0, &err);
	if (err != OS_NO_ERR) {
		printf("Database error. Check to ensure access is allowed.\n");
		throw exception();
	}

	// Initialises the database on the SD card
	ret = efs_init(&db, SDCARD_NAME);
	if (ret != 0) {
		printf("Could not initialize database\n");
		throw exception();
	}
}

// temp json value
string Database::tempJSON() {
	Json::Value rootNode;
	rootNode["id"] = "id";
	rootNode["name"] = "test name";
	rootNode["admin"] = "y";
	rootNode["enabled"] = "y";
	return rootNode.toStyledString();
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
	char results[MAXBUF_LENGTH];

	ret = ls_openDir(&list, &db.myFs, path);
	if (ret == -1)
		printf("Could not open directory. Please check definition of path\n");
	if (ls_getNext(&list) == 0) {
		file = atoi((const char*) list.currentEntry.FileName);
		string attr = findEntry(path, file);
		strcpy(results, attr.c_str());
	}
	while (ls_getNext(&list) == 0) {
		file = atoi((const char*) list.currentEntry.FileName);
		string attr = findEntry(path, file);
		strcat(results, attr.c_str());
	}
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

// TODO: check to make sure role doesn't have the same name as existing entry
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

	ret = file_fwrite(&tuple, 0, jsonValue.length(), (euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("Role could not be added");
		return -1;
	}
	tuple.DirEntry.Attribute = rid;
	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("Role could not be added");
		return -1;
	}

	return 1;
}

// TODO: check to make sure user doesn't have the same name as existing entry
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

	ret = file_fwrite(&tuple, 0, jsonValue.length(), (euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("User could not be added");
		return -1;
	}

	tuple.DirEntry.Attribute = uid;
	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("User could not be added");
		return -1;
	}

	return 1;
}

// TODO: check to make sure rid exists in roles
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
	nodeToInsert["rid"] = value.r_id;
	nodeToInsert["startTime"] = value.startTime;
	nodeToInsert["endTime"] = value.endTime;
	nodeToInsert["days"] = value.days;
	ostringstream start, end;
	start << static_cast<long int>(value.startDate);
	end << static_cast<long int>(value.endDate);
	nodeToInsert["startDate"] = start.str();
	nodeToInsert["endDate"] = end.str();

	string jsonValue = nodeToInsert.toStyledString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(), (euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("Role schedule could not be added");
		return -1;
	}

	tuple.DirEntry.Attribute = id;
	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("Role schedule could not be added");
		return -1;
	}

	return 1;
}

// TODO: check to make sure rid exists in roles and uid exists in users
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
	nodeToInsert["uid"] = value.u_id;
	nodeToInsert["rid"] = value.r_id;
	ostringstream start, end;
	start << static_cast<long int>(value.startDate);
	end << static_cast<long int>(value.endDate);
	nodeToInsert["startDate"] = start.str();
	nodeToInsert["endDate"] = end.str();

	string jsonValue = nodeToInsert.toStyledString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(), (euint8*) jsonValue.c_str());
	if (ret != 0) {
		printf("User role could not be added");
		return -1;
	}

	tuple.DirEntry.Attribute = id;
	file_fclose(&tuple);
	if (ret != 0) {
		printf("User role could not be added");
		return -1;
	}

	return 1;
}

// TODO: check to make sure uid exists in users
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
	nodeToInsert["fid"] = value.f_id;
	nodeToInsert["uid"] = value.u_id;

	string jsonValue = nodeToInsert.toStyledString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(), (euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("User print could not be added");
		return -1;
	}

	tuple.DirEntry.Attribute = id;
	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("User print could not be added");
		return -1;
	}

	return 1;
}

// TODO: check to make sure uid exists in users
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
	nodeToInsert["uid"] = value.u_id;
	nodeToInsert["success"] = value.success;
	ostringstream time;
	time << static_cast<long int>(value.time);
	nodeToInsert["time"] = time.str();

	string jsonValue = nodeToInsert.toStyledString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(), (euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("History could not be added");
		return -1;
	}

	tuple.DirEntry.Attribute = id;
	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("History could not be added");
		return -1;
	}

	return 1;
}

string Database::findEntry(char *path, int id) {
	File tuple;
	int ret;
	unsigned int sizeRead;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", path, id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'r');
	if (ret == -1) {
		printf("Entry could not be found\n");
		return "";
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

// TODO: search by name
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
		return "";
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

// TODO: search by name
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
		return "";
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

// TODO: search by rid
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
		return "";
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

// TODO: search by uid and rid
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
		return "";
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

// TODO: search by uid and fid
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
		return "";
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

// TODO: search by uid
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
		return "";
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

// TODO: update role schedule, and user roles to new rid
// Updates role by deleting entry and creating new entry
int Database::editRole(int rid, Role value) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLES, rid);
	rmfile(&db.myFs, (euint8*) filename);
	return insertRole(rid, value);
}

// TODO: update user roles, user prints, and history to new uid
// Updates user by deleting entry and creating new entry
int Database::editUser(int uid, User value) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USERS, uid);
	rmfile(&db.myFs, (euint8*) filename);
	return insertUser(uid, value);
}

// TODO: make sure rid exists
// Updates role sched by deleting entry and creating new entry
int Database::editRoleSched(int id, RoleSchedule value) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLE_SCHEDULE, id);
	rmfile(&db.myFs, (euint8*) filename);
	return insertRoleSched(id, value);
}

// TODO: make sure rid and uid exists
// Updates user role by deleting entry and creating new entry
int Database::editUserRole(int id, UserRole value) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_ROLES, id);
	rmfile(&db.myFs, (euint8*) filename);
	return insertUserRole(id, value);
}

// TODO: make sure uid exists
// Updates user print by deleting entry and creating new entry
int Database::editUserPrint(int id, UserPrint value) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_PRINTS, id);
	rmfile(&db.myFs, (euint8*) filename);
	return insertUserPrint(id, value);
}

// The following functions delete the entries in accordance to each entry id
int Database::deleteRole(int rid) {
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLES, rid);
	rmfile(&db.myFs, (euint8*) filename);
	if (ret != 0) {
		printf("Entry could not be deleted, please try again later");
		return -1;
	}
	return 1;
}

int Database::deleteUser(int uid) {
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USERS, uid);
	rmfile(&db.myFs, (euint8*) filename);
	if (ret != 0) {
		printf("Entry could not be deleted, please try again later");
		return -1;
	}
	return 1;
}

int Database::deleteRoleSchedule(int id) {
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLE_SCHEDULE, id);
	rmfile(&db.myFs, (euint8*) filename);
	if (ret != 0) {
		printf("Entry could not be deleted, please try again later");
		return -1;
	}
	return 1;
}

int Database::deleteUserRole(int id) {
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_ROLES, id);
	rmfile(&db.myFs, (euint8*) filename);
	if (ret != 0) {
		printf("Entry could not be deleted, please try again later");
		return -1;
	}
	return 1;
}

int Database::deleteUserPrint(int id) {
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_PRINTS, id);
	rmfile(&db.myFs, (euint8*) filename);
	if (ret != 0) {
		printf("Entry could not be deleted, please try again later");
		return -1;
	}
	return 1;
}

int Database::deleteHistory(int id) {
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", HISTORY, id);
	rmfile(&db.myFs, (euint8*) filename);
	if (ret != 0) {
		printf("Entry could not be deleted, please try again later");
		return -1;
	}
	return 1;
}

// Unmount the file system
Database::~Database() {
	fs_umount(&db.myFs);
	OSSemPost(m_databaseSemaphore);
}
