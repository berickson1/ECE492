/*
 * database.cpp
 *
 *  Created on: 2014-02-28
 *      Author: Mavis Chan
 */

#include "Database.h"

Database::Database() {
}

// Initializes database for accessing
void Database::initDB() {
	// Initialises the database on the SD card
	int ret = efs_init(&db, SDCARD_NAME);
	if (ret != 0)
		printf("Could not initialize database\n");

	// Unmount the file system
	fs_umount(&db.myFs);
}

// Lists all tables (folders) or tuplets (files) in directory
void Database::listAll(char *path) {
	DirList list;
	int ret = ls_openDir(&list, &db.myFs, path);
	if (ret == -1)
		printf("Could not open directory. Please check definition of path\n");

	while (ls_getNext(&list) == 0)
		printf("%s", list.currentEntry.FileName);
}

// Creates new folders - representing tables
void Database::createTable(char *tableName) {
	int ret = makedir(&db.myFs, tableName);
	if (ret == -1)
		printf("Table already exists\n");
}

Database::~Database() {
	fs_umount(&db.myFs);
}
