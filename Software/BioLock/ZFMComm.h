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
	int read (char* buffer, int bufferSize);

};

#endif /* ZFMCOMM_H_ */
