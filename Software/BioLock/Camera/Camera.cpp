/*
 * Camera.cpp
 *
 *  Created on: Mar 7, 2014
 *      Author: Brent.Erickson
 */

#include "Camera.h"
const char Camera::BMPHEADER1[] = {0x42, 0x4d};
const char Camera::BMPHEADER2[] = {0x00, 0x00, 0x00, 0x00, 0x36, 0x00, 0x00, 0x00, 0x28, 0x00, 0x00, 0x00};
const char Camera::BMPHEADER3[] = {0x01, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
									 0xc4, 0x0e, 0x00, 0x00, 0xc4, 0x0e, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

char* Camera::getBMP(int * memsize){
	*memsize = CAM_WIDTH*CAM_HEIGHT*4 + BMPHEADERLEN;
	OSTimeDlyHMSM(0, 0, 1, 0);
	char * dataBuff = (char*)malloc((*memsize));
	//TODO: Check return value
	memset(dataBuff, (*memsize), 0);
	int offset = 0;
	int headerData = *memsize;

	memcpy((void*)(dataBuff + offset), (void*)&BMPHEADER1, BMPHEADER1LEN);
	offset += BMPHEADER1LEN;
	memcpy((void*)(dataBuff + offset), (void*)&headerData, sizeof(int));
	offset += sizeof(int);
	memcpy((void*)(dataBuff + offset), (void*)&BMPHEADER2, BMPHEADER2LEN);
	offset += BMPHEADER2LEN;
	headerData = CAM_WIDTH;
	memcpy((void*)(dataBuff + offset), (void*)&headerData, sizeof(int));
	offset += sizeof(int);
	headerData = CAM_HEIGHT;
	memcpy((void*)(dataBuff + offset), (void*)&headerData, sizeof(int));
	offset += sizeof(int);
	memcpy((void*)(dataBuff + offset), (void*)&BMPHEADER3, BMPHEADER3LEN);
	offset += BMPHEADER3LEN;
	imageToBuffer(dataBuff + offset);
	return dataBuff;
}

char* Camera::getRawImage(){
	int memsize = CAM_WIDTH*CAM_HEIGHT*4;
	char * dataBuff = (char*)malloc(memsize);
	imageToBuffer(dataBuff);
	return dataBuff;
}

void Camera::imageToBuffer(char* buffer){
	int imagesize = CAM_WIDTH*CAM_HEIGHT*4;
	memcpy((void*)(buffer), (void*)SRAM_0_BASE,  imagesize);
}
void Camera::init(){
	alt_up_av_config_dev* camConfig = alt_up_av_config_open_dev(CAMERA_NAME);
	//Override config type
	camConfig -> type = TRDB_D5M_CONFIG;
	OSTimeDlyHMSM(0, 0, 1, 0);
	//Mirror Rows and Columns
	alt_up_av_config_write_D5M_cfg_register(camConfig, 0x20, 0xC000);
	OSTimeDlyHMSM(0, 0, 1, 0);
	//PLL On
	alt_up_av_config_write_D5M_cfg_register(camConfig, 0x0D, 0x51);
	OSTimeDlyHMSM(0, 0, 1, 0);
	//Set PLL Factor m=24 n=4
	alt_up_av_config_write_D5M_cfg_register(camConfig, 0x11, 0x1804);
	OSTimeDlyHMSM(0, 0, 1, 0);
	//Set PLL Dividor to 1
	alt_up_av_config_write_D5M_cfg_register(camConfig, 0x12, 0x01);
	OSTimeDlyHMSM(0, 0, 1, 0);
	//Use PLL
	alt_up_av_config_write_D5M_cfg_register(camConfig, 0x0D, 0x53);
}


