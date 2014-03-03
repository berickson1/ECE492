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
 *
 *  Issues/todos: add method to delete directories if needed
 */

#include "Database.h"

Database::Database(OS_EVENT *databaseMutex) : m_databaseMutex(databaseMutex) {
	INT8U err = OS_NO_ERR;
	int ret = 0;

	//Blocking call
	OSMutexPend(m_databaseMutex, 0, &err);
	if (err != OS_NO_ERR) {
		printf("Database error. Check to ensure access is allowed.");
	}

	// Initialises the database on the SD card
	ret = efs_init(&db, SDCARD_NAME);
	if (ret != 0) {
		printf("Could not initialize database\n");
		//We should handle an error here
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
void Database::listAll(char *path) {
	DirList list;
	int ret;
	ret = ls_openDir(&list, &db.myFs, path);
	if (ret == -1)
		printf("Could not open directory. Please check definition of path\n");
	while (ls_getNext(&list) == 0)
		printf("%s\n", list.currentEntry.FileName);
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
int Database::insertRole(int rid) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLES, rid);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("Role already exists\n");
		return -1;
	}

	//Temp reference, change to JSON library reference
	string newRole = tempJSON();
	file_fwrite(&tuple, 0, newRole.length(), (euint8*) newRole.c_str());

	tuple.DirEntry.Attribute = rid;
	file_fclose(&tuple);

	return 1;
}

// TODO: check to make sure user doesn't have the same name as existing entry
// Adds a tuple to the user table
int Database::insertUser(int uid) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USERS, uid);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("User already exists\n");
		return -1;
	}

	//Temp reference, change to JSON library reference
	string newRole = tempJSON();
	file_fwrite(&tuple, 0, newRole.length(), (euint8*) newRole.c_str());

	tuple.DirEntry.Attribute = uid;
	file_fclose(&tuple);

	return 1;
}

// TODO: check to make sure rid exists in roles
// Adds a tuple to the role schedule table
int Database::insertRoleSched(int id) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLE_SCHEDULE, id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("Role schedule already exists\n");
		return -1;
	}

	//Temp reference, change to JSON library reference
	string newRole = tempJSON();
	file_fwrite(&tuple, 0, newRole.length(), (euint8*) newRole.c_str());

	tuple.DirEntry.Attribute = id;
	file_fclose(&tuple);

	return 1;
}

// TODO: check to make sure rid exists in roles and uid exists in users
// Adds a tuple to the user roles table
int Database::insertUserRole(int id) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_ROLES, id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("User role already exists\n");
		return -1;
	}

	//Temp reference, change to JSON library reference
	string newRole = tempJSON();
	file_fwrite(&tuple, 0, newRole.length(), (euint8*) newRole.c_str());

	tuple.DirEntry.Attribute = id;
	file_fclose(&tuple);

	return 1;
}

// TODO: check to make sure uid exists in users
// Adds a tuple to the user prints table
int Database::insertUserPrint(int id) {
	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_PRINTS, id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("User prints already exists\n");
		return -1;
	}

	//Temp reference, change to JSON library reference
	string newRole = tempJSON();
	file_fwrite(&tuple, 0, newRole.length(), (euint8*) newRole.c_str());

	tuple.DirEntry.Attribute = id;
	file_fclose(&tuple);

	return 1;
}

// TODO: check to make sure uid exists in users
// Adds a tuple to the history table
int Database::insertHistory(int id) {
	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", HISTORY, id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("History already exists\n");
		return -1;
	}

	//Temp reference, change to JSON library reference
	string newRole = tempJSON();
	file_fwrite(&tuple, 0, newRole.length(), (euint8*) newRole.c_str());
	tuple.DirEntry.Attribute = id;
	file_fclose(&tuple);

	return 1;
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

	std::string roleAttr = std::string((char *) fileBuffer);
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

	std::string userAttr = std::string((char *) fileBuffer);
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

	std::string roleSched = std::string((char *) fileBuffer);
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

	std::string userRole = std::string((char *) fileBuffer);
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

	std::string userPrint = std::string((char *) fileBuffer);
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

	std::string history = std::string((char *) fileBuffer);
	free(fileBuffer);
	file_fclose(&tuple);

	return history.c_str();
}

// TODO: update role schedule, and user roles to new rid
// Updates role by deleting entry and creating new entry
int Database::editRole(int rid) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLES, rid);
	rmfile(&db.myFs, (euint8*) filename);
	return insertRole(rid);
}

// TODO: update user roles, user prints, and history to new uid
// Updates user by deleting entry and creating new entry
int Database::editUser(int uid) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USERS, uid);
	rmfile(&db.myFs, (euint8*) filename);
	return insertUser(uid);
}

// TODO: make sure rid exists
// Updates role sched by deleting entry and creating new entry
int Database::editRoleSched(int id) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLE_SCHEDULE, id);
	rmfile(&db.myFs, (euint8*) filename);
	return insertRoleSched(id);
}

// TODO: make sure rid and uid exists
// Updates user role by deleting entry and creating new entry
int Database::editUserRole(int id) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_ROLES, id);
	rmfile(&db.myFs, (euint8*) filename);
	return insertUserRole(id);
}

// TODO: make sure uid exists
// Updates user print by deleting entry and creating new entry
int Database::editUserPrint(int id) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_PRINTS, id);
	rmfile(&db.myFs, (euint8*) filename);
	return insertUserPrint(id);
}

// The following functions delete the entries in accordance to each entry id
int Database::deleteRole(int rid) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLES, rid);
	rmfile(&db.myFs, (euint8*) filename);
}

int Database::deleteUser(int uid) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USERS, uid);
	rmfile(&db.myFs, (euint8*) filename);
}

int Database::deleteRoleSchedule(int id) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLE_SCHEDULE, id);
	return rmfile(&db.myFs, (euint8*) filename);
}

int Database::deleteUserRole(int id) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_ROLES, id);
	return rmfile(&db.myFs, (euint8*) filename);
}

int Database::deleteUserPrint(int id) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USER_PRINTS, id);
	return rmfile(&db.myFs, (euint8*) filename);
}

int Database::deleteHistory(int id) {
	char filename[MAXBUF_LENGTH];

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", HISTORY, id);
	return rmfile(&db.myFs, (euint8*) filename);
}

// Unmount the file system
Database::~Database() {
	fs_umount(&db.myFs);
	OSSemPost(m_databaseMutex);
}