/*
 * Camera.h
 *
 *  Created on: Mar 7, 2014
 *      Author: Brent.Erickson
 */

#ifndef CAMERA_H_
#define CAMERA_H_

#include "includes.h"
#include <cstdlib>
#include <cstdio>
#include <string.h>
extern "C"{
#include "altera_avalon_pio_regs.h"
#include "altera_up_avalon_audio_and_video_config.h"
}
class Camera {
public:

	static const int CAM_WIDTH = 320;
	static const int CAM_HEIGHT = 240;
	static char* getBMP(int * memsize);
	static char* getRawImage();
	static void init();

private:
	static const int SNAPSHOT_MODE_REG = 0x1E;
	static const int  SNAPSHOT_MODE_VAL = 0x4006 | (1<<8);
	static const int HORIZONTAL_REG = 0x20;
	static const int  HORIZONTAL_VAL = 0x0040 | (1<<14);
	static const char BMPHEADER1[];
	static const int BMPHEADER1LEN = 2;
	static const char BMPHEADER2[];
	static const int BMPHEADER2LEN = 12;
	static const char BMPHEADER3[];
	static const int BMPHEADER3LEN = 84;
	static const int BMPHEADERLEN = BMPHEADER1LEN + BMPHEADER2LEN + BMPHEADER3LEN + 12;

	static void imageToBuffer(char* buffer);
};


#endif /* CAMERA_H_ */
