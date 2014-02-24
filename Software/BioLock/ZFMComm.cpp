/*
 * ZFMComm.cpp
 *
 *  Created on: Jan 29, 2014
 *      Author: Brent.Erickson
 */

#include "ZFMComm.h"
ZFMComm::ZFMComm() {
	fd = -1;
}

bool ZFMComm::init(char* devName){
	fd = open(devName, O_RDWR);
	if (fd == -1){
		printf("Error Opening Sensor");
		return false;
	}
	return true;
}

char ZFMComm::storeFinger(int id){
	char address[4] = {0xFF, 0xFF, 0xFF, 0xFF};
	writePacket(address, &ZFM_PKG_CMD, &ZFM_CMD_CAPTURE_FINGER, 1);
	char reply[12];
	//if(readPacket(reply, 12) == -1){
	//	return -1;
	//}
	return 1;//reply[10];
}

int ZFMComm::writePacket(const char* address, const char* ptype, const char* data, uint len){
	uint		pktSize = ZFMFIXEDPACKETSIZE + len;
	char	*buffer = (char*)malloc(pktSize),
			*bufferPtr = buffer;
	int retval;
	uint checksum = 0;
	//Header - 2B
	*bufferPtr++ = ZFM_HEADER[1];
	*bufferPtr++ = ZFM_HEADER[0];

	//Address - 4B
	*bufferPtr++ = address[3];
	*bufferPtr++ = address[2];
	*bufferPtr++ = address[1];
	*bufferPtr++ = address[0];

	//Packet Type - 1B
	*bufferPtr++ = *ptype;
	checksum += *ptype;

	//Data Length - 2B
	//Add 2 because len does not include checksum
	*bufferPtr++ = (char) ((len + ZFMCHECKSUMSIZE) >> 8);
	checksum += (char) ((len + ZFMCHECKSUMSIZE) >> 8);
	*bufferPtr++ = (char) (len + ZFMCHECKSUMSIZE);
	checksum += (char) (len + ZFMCHECKSUMSIZE);

	//Data - lenB
	for(uint i = 0; i < len; i++){
		*bufferPtr++ = data[len - 1 - i];
		checksum += data[len - 1 - i];
	}

	//Checksum -2B
	*bufferPtr++ = (char) checksum >> 8;
	*bufferPtr++ = (char) checksum;

	if (fd == -1){
		printf("Invalid Sensor FD");
		//Prevent leak
		free(buffer);
		return -1;
	}
	retval = write(fd, buffer, pktSize);
	free(buffer);
	return retval;
}
int ZFMComm::readPacket(char* bufferHead, int bufferSize){
	char* buffer = bufferHead;
	int bufferRemaining = bufferSize,
			totalBytesRead = 0,
			bytesRead,
			bytesToRead = 0,
			dataLen = 0;
	//Header
	bytesToRead = 2;
	bytesRead = getBytes(buffer, bytesToRead, bufferRemaining);
	if(bytesRead == -1){
		return -1;
	}
	buffer += bytesRead;
	totalBytesRead += bytesRead;
	bufferRemaining -= bytesRead;

	//Chip Address
	bytesToRead = 4;
	bytesRead = getBytes(buffer, bytesToRead, bufferRemaining);
	if(bytesRead == -1){
		return -1;
	}
	buffer += bytesRead;
	totalBytesRead += bytesRead;
	bufferRemaining -= bytesRead;

	//Package Identifier
	bytesToRead = 1;
	bytesRead = getBytes(buffer, bytesToRead, bufferRemaining);
	if(bytesRead == -1){
		return -1;
	}
	buffer += bytesRead;
	totalBytesRead += bytesRead;
	bufferRemaining -= bytesRead;

	//Package Length
	bytesToRead = 2;
	bytesRead = getBytes(buffer, bytesToRead, bufferRemaining);
	if(bytesRead == -1){
		return -1;
	}
	dataLen = *((unsigned short *) buffer);
	buffer += bytesRead;
	totalBytesRead += bytesRead;
	bufferRemaining -= bytesRead;

	//Data
	bytesToRead = dataLen;
	bytesRead = getBytes(buffer, bytesToRead, bufferRemaining);
	if(bytesRead == -1){
		return -1;
	}
	buffer += bytesRead;
	totalBytesRead += bytesRead;
	bufferRemaining -= bytesRead;

	//Checksum
	bytesToRead = 2;
	bytesRead = getBytes(buffer, bytesToRead, bufferRemaining);
	if(bytesRead == -1){
		return -1;
	}
	buffer += bytesRead;
	totalBytesRead += bytesRead;
	bufferRemaining -= bytesRead;

	return totalBytesRead;
}

int ZFMComm::getBytes(char* bufferHead, int bytesToRead, int bufferSize){
	int bytesRead;
	if(bytesToRead > bufferSize){
		return -1;
	}
	if (fd == -1){
		printf("Invalid Sensor FD");
		return -1;
	}
	bytesRead = read(fd, bufferHead, bytesToRead);
	if (bytesRead != bytesToRead){
		return -1;
	}
	reorderBytes(bufferHead, bytesRead);
	return bytesRead;
}
void ZFMComm::reorderBytes(char* bufferHead, int dataSize){
	char swapTemp;
	for(int i = 0; i < dataSize / 2; i++){
		swapTemp = *(bufferHead + i);
		*(bufferHead + i) = *(bufferHead + dataSize - 1);
		*(bufferHead + dataSize - 1) = swapTemp;
	}
}
ZFMComm::~ZFMComm() {
	// TODO Auto-generated destructor stub
}

