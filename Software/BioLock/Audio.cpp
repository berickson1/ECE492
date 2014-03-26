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

	m_soundBuf = (unsigned int *) malloc(tuple.FileSize * sizeof(euint8));
	memset(m_soundBuf, 0, tuple.FileSize * sizeof(euint8));

	m_fileSize = file_read(&tuple, tuple.FileSize, (euint8 *) m_soundBuf);
	if (m_fileSize == 0)
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

	m_audio_config = alt_up_av_config_open_dev("/dev/audio_out");
	if (m_audio_config == NULL)
		printf("Error: could not open audio config device \n");
	else
		printf("Opened audio config device \n");
    /* Configure WM8731
    alt_up_audio_reset_audio_core(m_audio_dev);
    alt_up_av_config_reset(m_audio_config);

    /* Write to configuration registers in the audio codec; see datasheet for what these values mean
    alt_up_av_config_write_audio_cfg_register(m_audio_config, 0x0, 0x17);
    alt_up_av_config_write_audio_cfg_register(m_audio_config, 0x1, 0x17);
    alt_up_av_config_write_audio_cfg_register(m_audio_config, 0x2, 0x79);
    alt_up_av_config_write_audio_cfg_register(m_audio_config, 0x3, 0x79);
    alt_up_av_config_write_audio_cfg_register(m_audio_config, 0x4, 0x15);
    alt_up_av_config_write_audio_cfg_register(m_audio_config, 0x5, 0x06);
    alt_up_av_config_write_audio_cfg_register(m_audio_config, 0x6, 0x00);*/
}

void Audio::play() {
	int curr = 0;
	while (curr < m_fileSize) {
		int writeSize = 128;
		//write data to the L and R buffers; R buffer will receive a copy of L buffer data
        int rightWrite = alt_up_audio_write_fifo(m_audio_dev, m_soundBuf+curr, writeSize, ALT_UP_AUDIO_RIGHT);
        int leftWrite = alt_up_audio_write_fifo(m_audio_dev, m_soundBuf+curr, writeSize, ALT_UP_AUDIO_LEFT);
        curr += writeSize;
	}
}

Audio::~Audio() {
	alt_up_audio_disable_write_interrupt(m_audio_dev);
	free(m_soundBuf);
}
