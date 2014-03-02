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
	Database();
	virtual ~Database();
	int initDB();
	void listAll(char *path);
	int createTable(char *tableName);
	int insertRole(int rid);
	int insertUser(int uid);
	int insertRoleSched(int id);
	int insertUserRole(int id);
	int insertUserPrint(int id);
	int insertHistory(int id);
	string findRole(int rid);
	string findUser(int uid);
	string findRoleSchedule(int id);
	string findUserRole(int id);
	string findUserPrint(int id);
	string findHistory(int id);
	int editRole(int rid);
	int editUser(int uid);
	int editRoleSched(int id);
	int editUserRole(int id);
	int editUserPrint(int id);
	int deleteRole(int rid);
	int deleteUser(int uid);
	int deleteRoleSchedule(int id);
	int deleteUserRole(int id);
	int deleteUserPrint(int id);
	int deleteHistory(int id);
	void close();
private:
	EmbeddedFileSystem db;
	string noRecord();
	string tempJSON();
};

#endif
