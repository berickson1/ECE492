/*
 * ZFM_CONST.h
 *
 *  Created on: Jan 29, 2014
 *      Author: Brent.Erickson
 */

#include <stdint.h>;
#include <ctype.h>;
#include <cfinter.h>;

#ifndef ZFM_CONST_H_
#define ZFM_CONST_H_

#define ZFM_HEADER 0xEF01

enum ZFM_PKG{
	CMD = 1,
	DATA,
	ACK = 7,
	END
};

enum ZFM_ACK{
	SUCCESS = 0,
	ERR_D_PKG,
	ERR_NO_FINGER,
	ERR_ENROLL,
	ERR_CHAR_DISORDERLY = 6,
	ERR_CHAR_UNIQUENESS,
	ERR_MATCH_FAIL,
	ERR_MATCH_NOT_FOUND,
	ERR_CHAR_COMBINE_FAIL,
	ERR_ADDRESS_OUT_OF_RANGE,
	ERR_TEMPLATE_INVALID,
	ERR_TEMPLATE_UPLOAD,
	ERR_DATA_RECEIVE,
	ERR_UPLOAD_IMAGE,
	ERR_DEELTE_TEMPLATE,
	ERR_CLEAR_LIBRARY,
	ERR_GENERATE_IMAGE = 0x15,
	ERR_WRITE_FLASH = 0x18,
	ERR_NO_DEFINITION,
	ERR_INVALID_REGISTER,
	ERR_REGISTER_CONFIGURATION,
	ERR_NOTEPAD_PAGE_NUMBER,
	ERR_COMM_PORT
};

enum ZFM_CMD{
	CAPTURE_FINGER = 0x01,		//GenImage
	STORE_TO_BUFFER = 0x02,		//Img2Tz
	MATCH_BUFFERS = 0x03,		//Match
	SEARCH = 0x04,				//Search
	GENERATE_TEMPLATE = 0x05,	//RegModel
	STORE = 0x06,				//Store
	LOAD_BUFFER = 0x07,			//LoadChar
	UPLOAD_BUFFER = 0x08,		//UpChar
	DOWNLOAD_BUFFER = 0x09,		//DownChar
	UPLOAD_IMAGE = 0x0A,		//UpImage
	DOWNLOAD_IMAGE = 0x0B,		//DownImage
	DELETE_STORED = 0x0C,		//DeleteChar
	DELETE_ALL = 0x0D,			//Empty
	SET_SYS_PARAMETER = 0x0E,	//SetSysPara
	READ_SYS_PARAMETER = 0x0F,	//ReadSysPara
	GET_RAND = 0x14,			//GetRandomCode
	SET_ADDRESS = 0x15,			//SetAddr
	HANDSHAKE = 0x17,			//Handshake
	WRITE_DATA = 0x18,			//WriteNotepad
	READ_DATA = 0x19,			//ReadNotepad
	READ_TEMPLATE = 0x1D,		//TemplateNum
};





#endif /* ZFM_CONST_H_ */
