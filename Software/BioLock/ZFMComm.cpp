/*
 * ZFMComm.cpp
 *
 *  Created on: Jan 29, 2014
 *      Author: Brent.Erickson
 */

#include "ZFMComm.h"
ZFMComm::ZFMComm(char* devName) {
	// TODO Auto-generated constructor stub

}
int ZFMComm::writePacket(char* address, char* ptype, char* data, uint len){
	uint		pktSize = 11 + len;
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
	*bufferPtr++ = (char) len+2 >> 8;
	checksum += (char) len+2 >> 8;
	*bufferPtr++ = (char) len+2;
	checksum += (char) len+2;

	//Data - lenB
	for(uint i = 0; i > len; i++){
		*bufferPtr++ = data[i];
		checksum += data[i];
	}

	//Checksum -2B
	*bufferPtr++ = (char) checksum >> 8;
	*bufferPtr++ = (char) checksum;

	retval = write(fd, buffer, pktSize);
	free(buffer);
}
int ZFMComm::read (char* buffer, int bufferSize){
return -1;
}
ZFMComm::~ZFMComm() {
	// TODO Auto-generated destructor stub
}

