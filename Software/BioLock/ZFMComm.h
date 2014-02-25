/*
 * ZFMComm.h
 *
 *  Created on: Jan 29, 2014
 *      Author: Brent.Erickson
 */
#ifndef ZFMCOMM_H_
#define ZFMCOMM_H_

#define ZFMFIXEDPACKETSIZE 11
#define ZFMCHECKSUMSIZE 2

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
	bool scanFinger();
	bool storeImage(int buffer);
	bool storeFingerprint(int id);
	bool checkFingerprint(int id);
	int findFingerprint(int buffer);
	bool deleteFingerprint(int id);
	bool deleteAllFingerprints();
	int getLastError();
	bool hasError();
private:
	int fd;
	int lastError;
	void clearError();
	bool verifyPassword();
	int writePacket(const char* ptype, const char* data, uint len);
	int readPacket(char* bufferHead, int bufferSize);
	int getBytes(char* bufferHead, int bytesToRead, int bufferSize);
	bool loadSavedFingerprint(int id, int buffer);
	bool isSuccessPacket(char * buffer);

};

#endif /* ZFMCOMM_H_ */
