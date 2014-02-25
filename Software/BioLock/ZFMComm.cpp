/*
 * ZFMComm.cpp
 *
 *  Created on: Jan 29, 2014
 *      Author: Brent.Erickson
 */
#define NOSENSOR
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
	return verifyPassword();
}

bool ZFMComm::verifyPassword(){
	const char data[ZFM_CMD_VERIFY_PASSWORD_LEN] = {ZFM_CMD_VERIFY_PASSWORD, 0, 0, 0, 0};
	writePacket(&ZFM_PKG_CMD, data, ZFM_CMD_VERIFY_PASSWORD_LEN);
	char reply[ZFM_ACKPACKETLENGTH];
	if(readPacket(reply, ZFM_ACKPACKETLENGTH) == -1){
		return false;
	}
	return isSuccessPacket(reply);
}

bool ZFMComm::hasError(){
	return (fd == -1);
}

bool ZFMComm::scanFinger(){
	const char data[ZFM_CMD_STORE_TO_BUFFER_LEN] = {ZFM_CMD_CAPTURE_FINGER};
	writePacket(&ZFM_PKG_CMD, data, ZFM_CMD_CAPTURE_FINGER_LEN);
	char reply[ZFM_ACKPACKETLENGTH];
	if(readPacket(reply, ZFM_ACKPACKETLENGTH) == -1){
		return false;
	}
	return isSuccessPacket(reply);
}

bool ZFMComm::storeImage(int buffer){
	if (buffer != 1 || buffer != 2){
		return false;
	}
	const char data[ZFM_CMD_STORE_TO_BUFFER_LEN] = {ZFM_CMD_STORE_TO_BUFFER, (char) buffer};
	writePacket(&ZFM_PKG_CMD, data, ZFM_CMD_STORE_TO_BUFFER_LEN);
	char reply[ZFM_ACKPACKETLENGTH];
	if(readPacket(reply, ZFM_ACKPACKETLENGTH) == -1){
		return false;
	}
	return isSuccessPacket(reply);
}

bool ZFMComm::storeFingerprint(int id){
	{
		const char data[ZFM_CMD_GENERATE_TEMPLATE_LEN] = {ZFM_CMD_GENERATE_TEMPLATE};
		writePacket(&ZFM_PKG_CMD, data, ZFM_CMD_GENERATE_TEMPLATE_LEN);
		char reply[ZFM_ACKPACKETLENGTH];
		if(readPacket(reply, ZFM_ACKPACKETLENGTH) == -1){
			return false;
		}
		if (!isSuccessPacket(reply)){
			return false;
		}
	}
	//Store result from buffer 1
	const char data[ZFM_CMD_STORE_LEN] = {ZFM_CMD_STORE, (char) 1, (char)(id << 8), (char) id};
	writePacket(&ZFM_PKG_CMD, data, ZFM_CMD_STORE_LEN);
	char reply[ZFM_ACKPACKETLENGTH];
	if(readPacket(reply, ZFM_ACKPACKETLENGTH) == -1){
		return false;
	}
	return isSuccessPacket(reply);
}

bool ZFMComm::checkFingerprint(int id){
	if(!scanFinger() ||
			!storeImage(1) ||
			!loadSavedFingerprint(id, 2)){
		return false;
	}
	const char data[ZFM_CMD_MATCH_BUFFERS_LEN] = {ZFM_CMD_MATCH_BUFFERS};
	writePacket(&ZFM_PKG_CMD, data, ZFM_CMD_GENERATE_TEMPLATE_LEN);
	char reply[ZFM_MATCHACKPACKETLENGTH];
	if(readPacket(reply, ZFM_MATCHACKPACKETLENGTH) == -1){
		return false;
	}
	//TODO: deal with matching score
	return isSuccessPacket(reply);
}

bool ZFMComm::loadSavedFingerprint(int id, int buffer){
	if (buffer != 1 || buffer != 2){
		return false;
	}
	const char data[ZFM_CMD_LOAD_BUFFER_LEN] = {ZFM_CMD_LOAD_BUFFER, (char) buffer, (char) (id << 8), (char) id};
	writePacket(&ZFM_PKG_CMD, data, ZFM_CMD_LOAD_BUFFER_LEN);
	char reply[ZFM_ACKPACKETLENGTH];
	if(readPacket(reply, ZFM_ACKPACKETLENGTH) == -1){
		return false;
	}
	return isSuccessPacket(reply);
}

int ZFMComm::findFingerprint(int buffer){
	//TODO: double check values here
	const char data[ZFM_CMD_SEARCH_LEN] = {ZFM_CMD_SEARCH, (char) buffer, 0, 0, 0, 0};
	writePacket(&ZFM_PKG_CMD, data, ZFM_CMD_SEARCH_LEN);
	char reply[ZFM_ACKPACKETLENGTH];
	if(readPacket(reply, ZFM_MATCHACKPACKETLENGTH) == -1){
		return -1;
	}
	if (!isSuccessPacket(reply)){
		return -1;
	}
	int retval = 0;
	//TODO: remove magic numbers
	retval += reply[10] >> 8;
	retval += reply[11];
	return retval;
}

int ZFMComm::writePacket(const char* ptype, const char* data, uint len){
	uint		pktSize = ZFMFIXEDPACKETSIZE + len;
	char	*buffer = (char*)malloc(pktSize),
			*bufferPtr = buffer;
	int retval;
	uint checksum = 0;
	//Header - 2B
	*bufferPtr++ = ZFM_HEADER[0];
	*bufferPtr++ = ZFM_HEADER[1];

	//Address - 4B
	*bufferPtr++ = ZFM_ADDRESS[0];
	*bufferPtr++ = ZFM_ADDRESS[1];
	*bufferPtr++ = ZFM_ADDRESS[2];
	*bufferPtr++ = ZFM_ADDRESS[3];

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
		*bufferPtr++ = data[i];
		checksum += data[i];
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
#ifndef NOSENSOR
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
#else
	return 1;
#endif
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

bool ZFMComm::isSuccessPacket(char * buffer){
#ifndef NOSENSOR
	if (buffer[10] != ZFM_ACK_SUCCESS){
		return false;
	}
#endif
	return true;
}

ZFMComm::~ZFMComm() {
	// TODO Auto-generated destructor stub
}

