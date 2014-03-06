/*
 * Database_CONST.h
 *
 *		Created on: 2014-03-02
 *      Author: Mavis Chan
 */

#ifndef __Database_CONST_H_
#define __Database_CONST_H_

static char* ROLES = "/ROLES/";
static char* USERS = "/USERS/";
static char* ROLE_SCHEDULE = "/ROLESCHD/";
static char* USER_ROLES = "/USRROLES/";
static char* USER_PRINTS = "/USRPRNTS/";
static char* HISTORY = "/HISTORY/";

static const char BMPHEADER1[] = {0x42, 0x4d};
static const int BMPHEADER1LEN = 2;
static const char BMPHEADER2[] = {0x00, 0x00, 0x00, 0x00, 0x36, 0x00, 0x00, 0x00, 0x28, 0x00, 0x00, 0x00};
static const int BMPHEADER2LEN = 12;
static const char BMPHEADER3[] = {0x01, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
								 0xc4, 0x0e, 0x00, 0x00, 0xc4, 0x0e, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
static const int BMPHEADER3LEN = 28;

#endif /* __Database_CONST_H_ */
