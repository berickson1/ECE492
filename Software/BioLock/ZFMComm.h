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
#include <fcntl.h>
#include "ZFM_CONST.h"

class ZFMComm {
public:
	ZFMComm();
	virtual ~ZFMComm();

	bool init(char * devName);
	char storeFinger(int id);
private:
	int fd;
	int writePacket(const char* address, const char* ptype, const char* data, uint len);
	int readPacket(char* bufferHead, int bufferSize);
	int getBytes(char* bufferHead, int bytesToRead, int bufferSize);
	void reorderBytes(char* bufferHead, int dataSize);

};

#endif /* ZFMCOMM_H_ */
