/*
 * database.h
 *
 *  Created on: 2014-02-28
 *      Author: Mavis Chan
 */

#ifndef __DATABASE_H__
#define __DATABASE_H__

#include "alt_types.h"
#include "includes.h"
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <sstream>
#include <list>
#include "json/writer.h"
#include "json/reader.h"
#include "Database_CONST.h"
#include "DatabaseTableTypes.h"
#include "LCD.h"

extern "C" {
#include "Database/EFSL/efs.h"
#include "Database/EFSL/ls.h"
}

#define MAXBUF_LENGTH 256

using namespace std;
class Database {
public:
	Database(OS_EVENT * databaseSemaphore);
	virtual ~Database();
	string listAll(char *path);
	string createTable(char *tableName);

	string insertRole(Role value);
	string insertUser(User value);
	string insertRoleSched(RoleSchedule value);
	string insertUserRole(UserRole value);
	string insertUserPrint(UserPrint value);
	string insertHistory(History value);

	string findEntryByName(string name);
	string findRole(int rid);
	string findUser(int uid);
	string findRoleSchedule(int id);
	string findUserRole(int id);
	string findRoleUser(int rid);
	string findUserPrint(int id);
	string findUserPrintUID(int uid);
	int findNextID(char *path);
	string findHistory(int id);

	string editRole(Role value);
	string editUser(User value);
	string editRoleSched(RoleSchedule value);
	string editUserRole(UserRole value);
	string editUserPrint(UserPrint value);

	string deleteRole(int rid);
	string deleteRoleSchedule(int id, int rid);
	string deleteUserRole(int id, int uid, int rid);
	string deleteUserPrint(int id, int uid);
	string enableUser(int uid, bool enable);

	void testPopulate();
	bool checkAccess(int fid, LCD &lcd);
	static string noRecord();
	static string success();
	static string fail();
	string clearAll();

private:
	OS_EVENT *m_databaseSemaphore;
	EmbeddedFileSystem db;
	string findEntry(const char *path, int id);
	string deleteEntry(string file);
	string clearTable(char *path);
	string addID(int id);
	int findID(string filename);
	int findRID(string filename);
	int findUID(string filename);
	string getFileName(char *path, euint8 *filename);
	string findEntryByRID(char *path, int rid);
	string findEntryByUID(char *path, int uid);
};

#endif
