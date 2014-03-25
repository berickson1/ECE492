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
 *
 *	RoleSchedule - id-rid
 *	UserRole - id-uid-rid
 *	UserPrints - id-uid
 *	History - id-uid
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

string Database::success(){
	Json::Value success;
	success["success"] = true;
	string result = success.toStyledString();
	return result;
}

string Database::fail(){
	Json::Value fail;
	fail["success"] = false;
	string result = fail.toStyledString();
	return result;
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
		if (file != 0){
			results.append(",");
			results.append(findEntry(path, file));
		}
	}
	results.append("]");
	return results;
}

// Creates new tables (folders)
string Database::createTable(char *tableName) {
	int ret;
	ret = makedir(&db.myFs, tableName);
	if (ret == -1) {
		printf("Table already exists\n");
		return fail();
	}
	return success();
}

// Adds a tuple to the role table
string Database::insertRole(Role value) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	if (value.id == 0){
		value.id = findNextID(ROLES);
	}
	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", ROLES, value.id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	// File already exists
	if (ret == -2) {
		printf("Role already exists\n");
		return fail();
	}

	string jsonValue = value.toJSONString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("Role could not be added\n");
		return fail();
	}
	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("Role could not be added\n");
		return fail();
	}

	return success();
}

// Adds a tuple to the user table
string Database::insertUser(User value) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	if (value.id == 0){
		value.id = findNextID(USERS);
	}

	snprintf(filename, MAXBUF_LENGTH, "%s%d.txt", USERS, value.id);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("User already exists\n");
		return fail();
	}

	string jsonValue = value.toJSONString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("User could not be added\n");
		return fail();
	}

	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("User could not be added\n");
		return fail();
	}

	return success();
}

// Adds a tuple to the role schedule table
string Database::insertRoleSched(RoleSchedule value) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	if (value.id == 0){
		value.id = findNextID(ROLE_SCHEDULE);
	}

	snprintf(filename, MAXBUF_LENGTH, "%s%d-%d.txt", ROLE_SCHEDULE, value.id, value.rid);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("Role schedule already exists\n");
		return fail();
	}

	string jsonValue = value.toJSONString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("Role schedule could not be added\n");
		return fail();
	}

	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("Role schedule could not be added\n");
		return fail();
	}

	return success();
}

// Adds a tuple to the user roles table
string Database::insertUserRole(UserRole value) {

	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	if (value.id == 0){
		value.id = findNextID(USER_ROLES);
	}

	snprintf(filename, MAXBUF_LENGTH, "%s%d-%d-%d.txt", USER_ROLES, value.id, value.uid, value.rid);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("User role already exists\n");
		return fail();
	}

	string jsonValue = value.toJSONString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("User role could not be added\n");
		return fail();
	}

	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("User role could not be added\n");
		return fail();
	}

	return success();
}

// Adds a tuple to the user prints table
string Database::insertUserPrint(UserPrint value) {
	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	if (value.id == 0){
		value.id = findNextID(USER_PRINTS);
	}

	snprintf(filename, MAXBUF_LENGTH, "%s%d-%d.txt", USER_PRINTS, value.id, value.uid);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("User prints already exists\n");
		return fail();
	}

	string jsonValue = value.toJSONString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("User print could not be added\n");
		return fail();
	}

	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("User print could not be added\n");
		return fail();
	}

	return success();
}

// Adds a tuple to the history table
string Database::insertHistory(History value) {
	File tuple;
	int ret;
	char filename[MAXBUF_LENGTH];

	if (value.id == 0){
		value.id = findNextID(HISTORY);
	}

	snprintf(filename, MAXBUF_LENGTH, "%s%d-%d.txt", HISTORY, value.id, value.uid);
	ret = file_fopen(&tuple, &db.myFs, filename, 'w');
	if (ret == -2) {
		printf("History already exists\n");
		return fail();
	}

	string jsonValue = value.toJSONString();

	ret = file_fwrite(&tuple, 0, jsonValue.length(),
			(euint8*) jsonValue.c_str());
	if (ret == 0) {
		printf("History could not be added\n");
		return fail();
	}

	ret = file_fclose(&tuple);
	if (ret != 0) {
		printf("History could not be added\n");
		return fail();
	}

	return success();
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
		return noRecord();
	}

	string attr = "";
	attr.append((char*) fileBuffer, sizeRead);
	free(fileBuffer);
	file_fclose(&tuple);

	return attr;
}

string Database::findEntryByName(string name){
	File tuple;
	unsigned int sizeRead;
	int ret;
	const char *filename = name.c_str();

	ret = file_fopen(&tuple, &db.myFs, (eint8*)filename, 'r');
	if (ret == -1) {
		printf("Entry could not be found\n");
		return noRecord();
	}

	euint8 *fileBuffer = (euint8 *) malloc(tuple.FileSize * sizeof(euint8));
	sizeRead = file_read(&tuple, tuple.FileSize, fileBuffer);
	if (sizeRead == 0) {
		printf("Attributes could not read\n");
		return noRecord();
	}

	string attr = "";
	attr.append((char*) fileBuffer, sizeRead);
	free(fileBuffer);
	file_fclose(&tuple);

	return attr;
}

// Returns results if rid matches entry
string Database::findEntryByRID(char *path, int rid){
	DirList list;
	string results = "";
	int ret, file;

	// Searches through folder
	ret = ls_openDir(&list, &db.myFs, (eint8 *) path);
	if (ret == -1) {
		printf("Could not open directory\n");
		return noRecord();
	}
	results.append("[");
	if (ls_getNext(&list) == 0) {
		string filename = getFileName(path, list.currentEntry.FileName);
		if (atoi((const char*) list.currentEntry.FileName) != 0){
			file = findRID(filename);
			if (file == rid){
				results.append(findEntry(path, file));
			}
		}
	}
	while (ls_getNext(&list) == 0) {
		string filename = getFileName(path, list.currentEntry.FileName);
		if (atoi((const char*) list.currentEntry.FileName) != 0){
			file = findRID(filename);
			if (file == rid){
				results.append(",");
				results.append(findEntry(path, file));
			}
		}
	}
	if (results.find("[") == results.length() - 1){
		results.append(noRecord());
	}
	results.append("]");
	return results;
}

// Returns results if uid matches entry
string Database::findEntryByUID(char *path, int uid){
	DirList list;
	string results = "";
	int ret, file;

	// Searches through folder
	ret = ls_openDir(&list, &db.myFs, (eint8 *) path);
	if (ret == -1) {
		printf("Could not open directory\n");
		return noRecord();
	}
	results.append("[");
	if (ls_getNext(&list) == 0) {
		string name = getFileName(path, list.currentEntry.FileName);
		if (atoi((const char*) list.currentEntry.FileName) != 0){
			file = findUID(name);
			if (file == uid){
				results.append(findEntryByName(name));
			}
		}
	}
	while (ls_getNext(&list) == 0) {
		string name = getFileName(path, list.currentEntry.FileName);
		if (atoi((const char*) list.currentEntry.FileName) != 0){
			file = findUID(name);
			if (file == uid){
				results.append(",");
				results.append(findEntryByName(name));
			}
		}
	}
	if (results.find("[") == results.length() - 1){
		results.append(noRecord());
	}
	results.append("]");
	return results;
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
string Database::findRoleSchedule(int rid) {
	return findEntryByRID(ROLE_SCHEDULE, rid);
}

// Searches for a role schedule by id
string Database::findUserRole(int uid) {
	return findEntryByUID(USER_ROLES, uid);
}

// Searches for users with specific role
string Database::findRoleUser(int rid) {
	return findEntryByRID(USER_ROLES, rid);
}

// Searches for a role schedule by id
string Database::findUserPrint(int uid) {
	return findEntryByUID(USER_PRINTS, uid);
}

// Finds next lowest id number for filename
int Database::findNextID(char *path) {
	DirList list;
	int ret, file;
	int id = 1;

	ret = ls_openDir(&list, &db.myFs, path);
	if (ret == -1)
		printf("Could not open directory. Please check definition of path\n");
	if (ls_getNext(&list) == 0) {
		string name = getFileName(path, list.currentEntry.FileName);
		file = findID(name);
		if(file >= id){
			id = file + 1;
		}
	}
	while (ls_getNext(&list) == 0) {
		string name = getFileName(path, list.currentEntry.FileName);
		file = findID(name);
		if(file >= id){
			id = file + 1;
		}
	}
	return id;
}

// Searches for a history by id
string Database::findHistory(int uid) {
	return findEntryByUID(HISTORY, uid);
}

// Updates role by deleting entry and creating new entry
string Database::editRole(Role value) {
	stringstream idStream;
	idStream << ROLES << value.id;
	string name = idStream.str();
	deleteEntry(name);
	return insertRole(value);
}

// Updates user by deleting entry and creating new entry
string Database::editUser(User value) {
	stringstream idStream;
	idStream << USERS << value.id;
	string name = idStream.str();
	deleteEntry(name);
	return insertUser(value);
}

// Updates role sched by deleting entry and creating new entry
string Database::editRoleSched(RoleSchedule value) {
	stringstream idStream;
	idStream << ROLE_SCHEDULE << value.id << "-" << value.rid;
	string name = idStream.str();
	deleteEntry(name);
	return insertRoleSched(value);
}

// Updates user role by deleting entry and creating new entry
string Database::editUserRole(UserRole value) {
	stringstream idStream;
	idStream << USER_ROLES << value.id << "-" << value.uid << "-" << value.rid;
	string name = idStream.str();
	deleteEntry(name);
	return insertUserRole(value);
}

// Updates user print by deleting entry and creating new entry
string Database::editUserPrint(UserPrint value) {
	stringstream idStream;
	idStream << USER_PRINTS << value.id << "-" << value.uid;
	string name = idStream.str();
	deleteEntry(name);
	return insertUserPrint(value);
}

// Deletes role entry with corresponding rid
string Database::deleteRole(int rid) {
	DirList list;
	int ret, file;
	string result;

	// Delete role
	stringstream idStream;
	idStream << ROLES << rid;
	string name = idStream.str();
	result = deleteEntry(name);
	if (result.find("false") != string::npos){
		return result;
	}

	// Delete role schedule with same rid
	ret = ls_openDir(&list, &db.myFs, (eint8 *) ROLE_SCHEDULE);
	if (ret == -1) {
		printf("Could not open directory\n");
		return fail();
	}
	while (ls_getNext(&list) == 0) {
		file = atoi((const char*) list.currentEntry.FileName);
		if (file != 0){
			RoleSchedule roleSchedule;
			roleSchedule.loadFromJson(findEntry(ROLE_SCHEDULE, file));
			if (roleSchedule.rid == rid) {
				string name = getFileName(ROLE_SCHEDULE, list.currentEntry.FileName);
				result = deleteEntry(name);
				if (result.find("false") != string::npos){
					return result;
				}
			}
		}
	}

	// Delete user roles with same rid
	ret = ls_openDir(&list, &db.myFs, (eint8 *) USER_ROLES);
	if (ret == -1){
		printf("Could not open directory\n");
		return fail();
	}
	while (ls_getNext(&list) == 0) {
		file = atoi((const char*) list.currentEntry.FileName);
		if (file != 0){
			UserRole userRole;
			userRole.loadFromJson(findEntry(USER_ROLES, file));
			if (userRole.rid == rid) {
				string name = getFileName(USER_ROLES, list.currentEntry.FileName);
				result = deleteEntry(name);
				if (result.find("false") != string::npos){
					return result;
				}
			}
		}
	}
	return success();
}

// Enables/disables user entry with corresponding uid
string Database::enableUser(int uid, bool enable) {
	User updatedUser;
	string result;
	stringstream idStream;

	User user;
	user.loadFromJson(findUser(uid));
	idStream << USERS << user.id;
	string name = idStream.str();
	result = deleteEntry(name);
	if (result.find("false") != string::npos){
		printf("User enable status could not be changed, please try again later\n");
		return result;
	}
	user.enabled = enable;
	result = insertUser(user);

	return result;
}

// Deletes role schedule entry with corresponding id
string Database::deleteRoleSchedule(int id, int rid) {
	stringstream idStream;
	idStream << ROLE_SCHEDULE << id << "-" << rid;
	string name = idStream.str();

	return deleteEntry(name);
}

// Deletes user role entry with corresponding id
string Database::deleteUserRole(int id, int uid, int rid) {
	stringstream idStream;
	idStream << USER_ROLES << id << "-" << uid << "-" << rid;
	string name = idStream.str();

	return deleteEntry(name);
}

// Deletes user print entry with corresponding id
string Database::deleteUserPrint(int id, int uid) {
	stringstream idStream;
	idStream << USER_PRINTS << id << "-" << uid;
	string name = idStream.str();

	return deleteEntry(name);
}

//Clears the database
string Database::clearAll() {
	string ret;

	ret = clearTable(ROLES);
	if (ret.find("false") != string::npos){
		return ret;
	}
	ret = clearTable(USERS);
	if (ret.find("false") != string::npos){
		return ret;
	}
	ret = clearTable(ROLE_SCHEDULE);
	if (ret.find("false") != string::npos){
		return ret;
	}
	ret = clearTable(USER_ROLES);
	if (ret.find("false") != string::npos){
		return ret;
	}
	ret = clearTable(USER_PRINTS);
	if (ret.find("false") != string::npos){
		return ret;
	}
	ret = clearTable(HISTORY);
	if (ret.find("false") != string::npos){
		return ret;
	}
	return success();
}

// Clears the table
string Database::clearTable(char *path) {
	string result;
	int ret = -1, file;
	DirList list;

	ret = ls_openDir(&list, &db.myFs, (eint8 *) path);
	if (ret == -1){
		printf("Could not open directory\n");
		return fail();
	}
	while (ls_getNext(&list) == 0) {
		file = atoi((const char*) list.currentEntry.FileName);
		if (file != 0){
			string name = getFileName(path, list.currentEntry.FileName);
			result = deleteEntry(name);
			if (result.find("false") != string::npos){
				return result;
			}
		}
	}
	return success();
}

// Deletes the specified file
string Database::deleteEntry(string file) {
	int ret;
	file.append(".txt");
	const char *filename = file.c_str();

	ret = rmfile(&db.myFs, (euint8*) filename);
	if (ret != 0) {
		printf("Entry could not be deleted, please try again later\n");
		return fail();
	}
	return success();
}

int Database::findID(string filename){
	int pos = filename.find_first_of('-');
	string idString;
	idString = filename.substr(0, pos - 1);
	const char * idToConvert = idString.c_str();
	int id = strtol(idToConvert, NULL, 10);
	return id;
}

int Database::findRID(string filename){
	int pos = filename.find_last_of('-');
	string ridString;
	ridString = filename.substr(pos + 1, -1);
	const char * ridToConvert = ridString.c_str();
	int rid = strtol(ridToConvert, NULL, 10);
	return rid;
}

int Database::findUID(string filename){
	string uidString;
	int pos = filename.find_last_of('-');
	int pos2 = filename.find_first_of('-');
	if (pos == pos2){
		//name only contains uid
		return findRID(filename);
	} else {
		uidString = filename.substr(pos2 + 1, pos - pos2 - 1);
	}
	const char * uidToConvert = uidString.c_str();
	int uid = strtol(uidToConvert, NULL, 10);
	return uid;
}

string Database::getFileName(char *path, euint8 *filename){
	stringstream idStream;
	idStream << path << filename;
	string name = idStream.str();
	int pos = name.find_first_of(" ");
	return name.substr(0, pos);
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
	ur1.name = "Owner";
	ur1.id = 1;
	ur1.uid = 1;
	ur1.rid = 1;
	ur1.startDate = time(0);
	ur1.endDate = time(0);
	insertUserRole(ur1);

	UserRole ur2;
	ur2.name = "Owner";
	ur2.id = 2;
	ur2.uid = 2;
	ur2.rid = 1;
	ur2.startDate = time(0);
	ur2.endDate = time(0);
	insertUserRole(ur2);

	UserRole ur3;
	ur3.name = "Guest";
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

bool Database::checkAccess(int fid){
	UserPrint userPrint;
	User user;
	UserRole userRole;
	RoleSchedule roleSchedule;

	time_t rawtime;
	struct tm * timeInfo;

	time(&rawtime);
	timeInfo = localtime(&rawtime);

	string userPrintJSON = findUserPrint(fid);
	userPrint.loadFromJson(userPrintJSON);
	History h;
	h.id = fid;
	h.uid = -1;
	h.success = false;
	h.time = mktime(timeInfo);

	return true;
	if (userPrint.uid != -1){
		int uid = userPrint.uid;
		printf("User found. ID:%d", uid);
		h.uid = uid;
		string userJSON = findUser(uid);
		user.loadFromJson(userJSON);
		if (!user.name.empty()){
			printf(" Name:%s\n", user.name);
			//Check if user is enabled
			if(user.enabled){
				//Check if current date falls within allowed dates
				double currentDate = mktime(timeInfo);
				double startDate = user.startDate;
				double endDate = user.endDate;
				if ((currentDate > startDate) && (currentDate < endDate)){
					//Check if role
					string userRoleJSON = findUserRole(uid);
					userRole.loadFromJson(userRoleJSON);
					if (userRole.rid != -1) {
						int rid = userRole.rid;
						printf("Role found. ID:%d\n", rid);
						string roleScheduleJSON = findRoleSchedule(rid);
						roleSchedule.loadFromJson(roleScheduleJSON);
						int days = roleSchedule.days;
						if (days != -1){
							//Check days
							int currentDay = timeInfo->tm_wday;
							//TODO: check current day with allowed days
							if (currentDay < days){
								//Check if current time falls within allowed times
								int currentTime = timeInfo->tm_hour;
								int startTime = roleSchedule.startTime;
								int endTime = roleSchedule.endTime;
								if ((currentTime >= startTime) && (currentTime < endTime)){
									h.success = true;
									insertHistory(h);
									return true;
								}
							}
						}
					}
				}
			}
		}
	}

	insertHistory(h);
	return false;
}
// Unmount the file system
Database::~Database() {
	fs_umount(&db.myFs);
	OSSemPost(m_databaseSemaphore);
}
