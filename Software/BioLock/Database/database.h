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

extern "C" {
#include "EFSL/efs.h"
#include "EFSL/ls.h"
}

class Database {
public:
	Database();
	virtual ~Database();
	void initDB();
	void listAll(char *path);
	void createTable(char *tableName);
private:
	EmbeddedFileSystem db;
};

#endif
