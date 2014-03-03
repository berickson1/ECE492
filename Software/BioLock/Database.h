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
#include <string.h>
#include "json/writer.h"
#include "Database_CONST.h"

extern "C" {
#include "Database/EFSL/efs.h"
#include "Database/EFSL/ls.h"
}

#define MAXBUF_LENGTH 256

using namespace std;
class Database {
public:
	Database(OS_EVENT * databaseMutex);
	virtual ~Database();
	string listAll(char *path);
	int createTable(char *tableName);
	int insertRole(int rid, string value);
	int insertUser(int uid, string value);
	int insertRoleSched(int id, string value);
	int insertUserRole(int id, string value);
	int insertUserPrint(int id, string value);
	int insertHistory(int id, string value);
	string findRole(int rid);
	string findUser(int uid);
	string findRoleSchedule(int id);
	string findUserRole(int id);
	string findUserPrint(int id);
	string findHistory(int id);
	int editRole(int rid, string value);
	int editUser(int uid, string value);
	int editRoleSched(int id, string value);
	int editUserRole(int id, string value);
	int editUserPrint(int id, string value);
	int deleteRole(int rid);
	int deleteUser(int uid);
	int deleteRoleSchedule(int id);
	int deleteUserRole(int id);
	int deleteUserPrint(int id);
	int deleteHistory(int id);
private:
	OS_EVENT *m_databaseMutex;
	EmbeddedFileSystem db;
	string noRecord();
	string tempJSON();
};

#endif
