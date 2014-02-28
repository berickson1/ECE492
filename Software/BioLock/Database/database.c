/*************************************************************************
 * Copyright (c) 2004 Altera Corporation, San Jose, California, USA.      *
 * All rights reserved. All use of this software and documentation is     *
 * subject to the License Agreement located at the end of this file below.*
 **************************************************************************
 * Description:                                                           *
 * The following is a simple hello world program running MicroC/OS-II.The *
 * purpose of the design is to be a very simple application that just     *
 * demonstrates MicroC/OS-II running on NIOS II.The design doesn't account*
 * for issues such as checking system call return codes. etc.             *
 *                                                                        *
 * Requirements:                                                          *
 *   -Supported Example Hardware Platforms                                *
 *     Standard                                                           *
 *     Full Featured                                                      *
 *     Low Cost                                                           *
 *   -Supported Development Boards                                        *
 *     Nios II Development Board, Stratix II Edition                      *
 *     Nios Development Board, Stratix Professional Edition               *
 *     Nios Development Board, Stratix Edition                            *
 *     Nios Development Board, Cyclone Edition                            *
 *   -System Library Settings                                             *
 *     RTOS Type - MicroC/OS-II                                           *
 *     Periodic System Timer                                              *
 *   -Know Issues                                                         *
 *     If this design is run on the ISS, terminal output will take several*
 *     minutes per iteration.                                             *
 **************************************************************************/

#include <stdio.h>
#include "includes.h"
#include "database.h"
#include "EFSL/efs.h"
#include "EFSL/ls.h"

// Create EFSL containers
EmbeddedFileSystem db;

// Initializes database for accessing
void initDB() {
	// Initialises the database on the SD card
	int ret = efs_init(&db, SDCARD_NAME);
	if (ret != 0)
		printf("Could not initialize database\n");

	// Unmount the file system
	fs_umount(&db.myFs);
}

// Lists all tables (folders) or tuplets (files) in directory
void listAll(char *path){
	DirList list;

	int ret = ls_openDir(&list, &db.myFs, path);
	if (ret == -1)
		printf("Could not open directory. Please check definition of path\n");

	while(ls_getNext(&list) == 0)
		printf("%s", list.currentEntry.FileName);


}

// Creates new folders - representing tables
void createTable(char *tableName) {
	int ret = makedir(&db.myFs, tableName);
	if (ret == -1)
		printf("Table already exists\n");
}
