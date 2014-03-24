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
#include "json/writer.h"
#include "json/reader.h"
#include "Database_CONST.h"
#include "DatabaseTableTypes.h"

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

	string findEntry(const char *path, int id);
	string findRole(int rid);
	string findUser(int uid);
	string findRoleSchedule(int id);
	string findUserRole(int id);
	string findRoleUser(int rid);
	string findUserPrint(int id);
	int findNextID(char *path);
	string findHistory(int id);

	string editRole(Role value);
	string editUser(User value);
	string editRoleSched(RoleSchedule value);
	string editUserRole(UserRole value);
	string editUserPrint(UserPrint value);

	string deleteRole(int rid);
	string deleteRoleSchedule(int id);
	string deleteUserRole(int id);
	string deleteUserPrint(int id);
	string enableUser(int uid, bool enable);

	void testPopulate();
	static string noRecord();
	static string success();
	static string fail();
	string clearAll();

private:
	OS_EVENT *m_databaseSemaphore;
	EmbeddedFileSystem db;
	string deleteEntry(char *path, int id);
	string clearTable(char *path);
};

#endif
