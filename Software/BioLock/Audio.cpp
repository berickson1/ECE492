/*
 * Audio.cpp
 *
 *  Created on: 2014-03-10
 *      Author: Mavis Chan
 *
 */

#include "Audio.h"
using namespace std;

Audio::Audio(OS_EVENT *databaseSemaphore) :
		m_databaseSemaphore(databaseSemaphore) {

	EmbeddedFileSystem db;
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

	m_soundBuf = (unsigned int *) malloc(
				tuple.FileSize * sizeof(euint8));

	sizeRead = file_read(&tuple, tuple.FileSize, (euint8 *) m_soundBuf);
	if (sizeRead == 0)
		printf("Sound file could not read\n");

	// Unmount the file system
	fs_umount(&db.myFs);
	OSSemPost(m_databaseSemaphore);

	//Initialize audio device
	m_audio_dev = alt_up_audio_open_dev("/dev/audio");
	if (m_audio_dev == NULL)
		printf("Error: could not open audio device \n");
	else
		printf("Opened audio device \n");
}

void Audio::play(){
		//write data to the L and R buffers; R buffer will receive a copy of L buffer data
		alt_up_audio_write_fifo(m_audio_dev, m_soundBuf, 128, ALT_UP_AUDIO_RIGHT);
		alt_up_audio_write_fifo(m_audio_dev, m_soundBuf, 128, ALT_UP_AUDIO_LEFT);
		printf("done");
}

Audio::~Audio() {
}
