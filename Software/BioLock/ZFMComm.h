/*
 * ZFMComm.h
 *
 *  Created on: Jan 29, 2014
 *      Author: Brent.Erickson
 */
#ifndef ZFMCOMM_H_
#define ZFMCOMM_H_

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include "ZFM_CONST.h"

class ZFMComm {
public:
	ZFMComm(char* devName);
	virtual ~ZFMComm();
private:
	int fd;
	int writePacket(char* address, char* ptype, char* data, uint datalen);
	int readPacket(char* bufferHead, int bufferSize);
	int getBytes(char* bufferHead, int bytesToRead, int bufferSize);
	void reorderBytes(char* bufferHead, int dataSize);

};

#endif /* ZFMCOMM_H_ */
