/*
 * ZFMComm.cpp
 *
 *  Created on: Jan 29, 2014
 *      Author: Brent.Erickson
 */
#include "ZFMComm.h"
ZFMComm::ZFMComm() {
	fd = -1;
	lastError = 0;
}

/**
 * Initializes the fingerprint sensor
 * @param devName Name of the device to open
 * @return true if the operation was successful
 */
bool ZFMComm::init(char* devName){
	fd = open(devName, O_RDWR);
	if (fd == -1){
		printf("Error Opening Sensor");
		return false;
	}
	return verifyPassword();
}

/**
 * Sends password to the device to unlock the device
 * @return true if the operation was successful
 */
bool ZFMComm::verifyPassword(){
	const char data[ZFM_CMD_VERIFY_PASSWORD_LEN] = {ZFM_CMD_VERIFY_PASSWORD, 0, 0, 0, 0};
	writePacket(&ZFM_PKG_CMD, data, ZFM_CMD_VERIFY_PASSWORD_LEN);
	char reply[ZFM_ACKPACKETLENGTH];
	if(readPacket(reply, ZFM_ACKPACKETLENGTH) == -1){
		return false;
	}
	return isSuccessPacket(reply);
}

/**
 *
 * @return true if there is an error
 */
bool ZFMComm::hasError(){
	return (fd == -1) || lastError != 0;
}

/**
 * Retrieves last stored error
 * @return last stored error
 */
int ZFMComm::getLastError(){
	return lastError;
}

/**
 * Clears stored error
 */
void ZFMComm::clearError(){
	lastError = 0;
}

/**
 * Scans a fingerprint and stores the image in the device image buffer
 * @return true if a fingerprint was scanned
 */
bool ZFMComm::scanFinger(){
	const char data[ZFM_CMD_STORE_TO_BUFFER_LEN] = {ZFM_CMD_CAPTURE_FINGER};
	writePacket(&ZFM_PKG_CMD, data, ZFM_CMD_CAPTURE_FINGER_LEN);
	char reply[ZFM_ACKPACKETLENGTH];
	if(readPacket(reply, ZFM_ACKPACKETLENGTH) == -1){
		return false;
	}
	return isSuccessPacket(reply);
}

/**
 * Stores image from image buffer to data buffer
 * @param buffer buffer number to store image data to
 * @return true if the operation was successful
 */
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
/**
 * Stores fingerprint (stored in buffer 1 and 2) to given fingerprint id
 * @param id id to store fingerprint to
 * @return true if the operation was successful
 */
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
	const char data[ZFM_CMD_STORE_LEN] = {ZFM_CMD_STORE, (char) 1, (char)(id >> 8), (char) id};
	writePacket(&ZFM_PKG_CMD, data, ZFM_CMD_STORE_LEN);
	char reply[ZFM_ACKPACKETLENGTH];
	if(readPacket(reply, ZFM_ACKPACKETLENGTH) == -1){
		return false;
	}
	return isSuccessPacket(reply);
}

/**
 * Scans fingerprint and checks it against given fingerprint id
 * @param id id to check scanned fingerprint against
 * @return true if the fingerprint matches
 */
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

/**
 * Load fingerprint template with given id into given buffer
 * @param id fingerprint id to find
 * @param buffer buffer to load fingerprint template into
 * @return true if the operation was successful
 */
bool ZFMComm::loadSavedFingerprint(int id, int buffer){
	if (buffer != 1 || buffer != 2){
		return false;
	}
	const char data[ZFM_CMD_LOAD_BUFFER_LEN] = {ZFM_CMD_LOAD_BUFFER, (char) buffer, (char) (id >> 8), (char) id};
	writePacket(&ZFM_PKG_CMD, data, ZFM_CMD_LOAD_BUFFER_LEN);
	char reply[ZFM_ACKPACKETLENGTH];
	if(readPacket(reply, ZFM_ACKPACKETLENGTH) == -1){
		return false;
	}
	return isSuccessPacket(reply);
}

/**
 * Removes fingerprint with given id from the fingerprint sensor
 * @param id id of fingerprint to remove
 * @return true if the operation was successful
 */
bool ZFMComm::deleteFingerprint(int id){
	const char data[ZFM_CMD_DELETE_STORED_LEN] = {ZFM_CMD_DELETE_STORED, (char) (id >> 8), (char) id, (char) 0, (char) 1};
	writePacket(&ZFM_PKG_CMD, data, ZFM_CMD_DELETE_STORED_LEN);
	char reply[ZFM_ACKPACKETLENGTH];
	if(readPacket(reply, ZFM_ACKPACKETLENGTH) == -1){
		return false;
	}
	return isSuccessPacket(reply);
}

/**
 * Removes all fingerprints from the fingerprint sensor
 * @return true if the operation was successful
 */
bool ZFMComm::deleteAllFingerprints(){
	const char data[ZFM_CMD_DELETE_ALL_LEN] = {ZFM_CMD_DELETE_ALL};
	writePacket(&ZFM_PKG_CMD, data, ZFM_CMD_DELETE_ALL_LEN);
	char reply[ZFM_ACKPACKETLENGTH];
	if(readPacket(reply, ZFM_ACKPACKETLENGTH) == -1){
		return false;
	}
	return isSuccessPacket(reply);
}

/**
 * Search for an existing stored fingerprint from given image buffer id
 * @param buffer buffer containing fingerprint to find
 * @return fingerprint id in buffer
 */
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
/**
 * Write a packet to the serial interface
 * @param ptype packet type to be written
 * @param data additional packet data to be written
 * @param len length of packet data to be written
 * @return
 */
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
/**
 * Read packet from the serial interface
 * @param bufferHead buffer to hold incoming packet
 * @param bufferSize size of buffer
 * @return number of bytes read into the buffer
 */
int ZFMComm::readPacket(char* bufferHead, int bufferSize){
#ifndef NOSENSOR
	clearError();
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
		lastError = ZFM_ACK_ERR_DATA_RECEIVE;
		return -1;
	}
	buffer += bytesRead;
	totalBytesRead += bytesRead;
	bufferRemaining -= bytesRead;

	//Chip Address
	bytesToRead = 4;
	bytesRead = getBytes(buffer, bytesToRead, bufferRemaining);
	if(bytesRead == -1){
		lastError = ZFM_ACK_ERR_DATA_RECEIVE;
		return -1;
	}
	buffer += bytesRead;
	totalBytesRead += bytesRead;
	bufferRemaining -= bytesRead;

	//Package Identifier
	bytesToRead = 1;
	bytesRead = getBytes(buffer, bytesToRead, bufferRemaining);
	if(bytesRead == -1){
		lastError = ZFM_ACK_ERR_DATA_RECEIVE;
		return -1;
	}
	buffer += bytesRead;
	totalBytesRead += bytesRead;
	bufferRemaining -= bytesRead;

	//Package Length
	bytesToRead = 2;
	bytesRead = getBytes(buffer, bytesToRead, bufferRemaining);
	if(bytesRead == -1){
		lastError = ZFM_ACK_ERR_DATA_RECEIVE;
		return -1;
	}
	dataLen = 0;
	dataLen += (char)(*buffer << 8);
	dataLen += (char)(*buffer);
	buffer += bytesRead;
	totalBytesRead += bytesRead;
	bufferRemaining -= bytesRead;

	//Data
	bytesToRead = dataLen;
	bytesRead = getBytes(buffer, bytesToRead, bufferRemaining);
	if(bytesRead == -1){
		lastError = ZFM_ACK_ERR_DATA_RECEIVE;
		return -1;
	}
	buffer += bytesRead;
	totalBytesRead += bytesRead;
	bufferRemaining -= bytesRead;

	//Checksum
	bytesToRead = 2;
	bytesRead = getBytes(buffer, bytesToRead, bufferRemaining);
	if(bytesRead == -1){
		lastError = ZFM_ACK_ERR_DATA_RECEIVE;
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
/**
 * Reads a given number of bytes from the serial interface
 * @param bufferHead pointer to the head of the buffer
 * @param bytesToRead number of bytes to read from ther serial interface
 * @param bufferSize remaining size in the given buffer
 * @return number of bytes read from the serial buffer
 */
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
	return bytesRead;
}
/**
 * Checks if an ACK packet is a success packet
 * @param buffer pointer to the head of a packet
 * @return true if the packet is a success packet
 */
bool ZFMComm::isSuccessPacket(char * buffer){
#ifndef NOSENSOR
	if (buffer[10] != ZFM_ACK_SUCCESS){
		lastError = (int) buffer[10];
		return false;
	}
#endif
	clearError();
	return true;
}

ZFMComm::~ZFMComm() {
	close(fd);
}

