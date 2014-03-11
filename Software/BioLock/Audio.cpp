/*
 * Audio.cpp
 *
 *  Created on: 2014-03-10
 *      Author: Mavis Chan
 *
 */

#include "Audio.h"
using namespace std;

Audio::Audio(OS_EVENT *databaseSemaphore, unsigned int *soundBuf) :
		m_databaseSemaphore(databaseSemaphore) {
	INT8U err = OS_NO_ERR;
	int ret = 0;
	File tuple;
	int sizeRead = 0;

	//Blocking call
	OSSemPend(m_databaseSemaphore, 0, &err);
	if (err != OS_NO_ERR) {
		printf("File system error. Try accessing the file later.\n");
		throw exception();
	}

	// Initialises the database on the SD card
	ret = efs_init(&db, SDCARD_NAME);
	if (ret != 0) {
		printf("Could not initialize file system\n");
		throw exception();
	}

	// Opens sound file & reads to buffer
	ret = file_fopen(&tuple, &db.myFs, SOUNDFILE, 'r');
	if (ret == -1)
		printf("File could not be found\n");

	soundBuf = (unsigned int *) malloc(
				tuple.FileSize * sizeof(euint8));

	sizeRead = file_read(&tuple, tuple.FileSize, (euint8 *) soundBuf);
	if (sizeRead == 0)
		printf("Sound file could not read\n");
}

// Unmount the file system
Audio::~Audio() {
	fs_umount(&db.myFs);
	OSSemPost(m_databaseSemaphore);
}
