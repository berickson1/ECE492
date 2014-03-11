/*
 * Audio.h
 *
 *  Created on: 2014-03-10
 *      Author: Mavis Chan
 */

#ifndef __AUDIO_H__
#define __AUDIO_H__

#include "alt_types.h"
#include "includes.h"
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <sstream>

extern "C" {
#include "altera_up_avalon_audio.h"
#include "Database/EFSL/efs.h"
#include "Database/EFSL/ls.h"
}

#define SOUNDFILE "beepbeep.wav"

using namespace std;
class Audio {
public:
	Audio(OS_EVENT * databaseSemaphore);
	void play();
	virtual ~Audio();

private:
	alt_up_audio_dev * m_audio_dev;
	OS_EVENT *m_databaseSemaphore;
	unsigned int *m_soundBuf;
};

#endif
