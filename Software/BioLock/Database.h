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
	int createTable(char *tableName);

	int insertRole(Role value);
	int insertUser(User value);
	int insertRoleSched(RoleSchedule value);
	int insertUserRole(UserRole value);
	int insertUserPrint(UserPrint value);
	int insertHistory(History value);

	string findEntry(const char *path, int id);
	string findRole(int rid);
	string findUser(int uid);
	string findRoleSchedule(int id);
	string findUserRole(int id);
	string findRoleUser(int rid);
	string findUserPrint(int id);
	string findHistory(int id);

	int editRole(Role value);
	int editUser(User value);
	int editRoleSched(RoleSchedule value);
	int editUserRole(UserRole value);
	int editUserPrint(UserPrint value);

	int deleteRole(int rid);
	int deleteRoleSchedule(int id);
	int deleteUserRole(int id);
	int deleteUserPrint(int id);

	int enableUser(int uid, bool enable);

	void testPopulate();
	static string noRecord();

private:
	OS_EVENT *m_databaseSemaphore;
	EmbeddedFileSystem db;
};

#endif
