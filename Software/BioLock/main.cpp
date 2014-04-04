/*************************************************************************
 * Copyright (c) 2004 Altera Corporation, San Jose, California, USA.      *
 * All rights reserved. All use of this software and documentation is     *
 * subject to the License Agreement located at the end of this file below.*
 **************************************************************************
 * Description:                                                           *
 * The following is a simple hello world program running MicroC/OS-II.The *
 * purpose of the design is to be a very simple application that just     *
 * demonstrates MicroC/OS-II running on NIOS II.The design doesn't account*
 * for issues such as checking system call return codes. etc.             *
 *                                                                        *
 * Requirements:                                                          *
 *   -Supported Example Hardware Platforms                                *
 *     Standard                                                           *
 *     Full Featured                                                      *
 *     Low Cost                                                           *
 *   -Supported Development Boards                                        *
 *     Nios II Development Board, Stratix II Edition                      *
 *     Nios Development Board, Stratix Professional Edition               *
 *     Nios Development Board, Stratix Edition                            *
 *     Nios Development Board, Cyclone Edition                            *
 *   -System Library Settings                                             *
 *     RTOS Type - MicroC/OS-II                                           *
 *     Periodic System Timer                                              *
 *   -Know Issues                                                         *
 *     If this design is run on the ISS, terminal output will take several*
 *     minutes per iteration.                                             *
 **************************************************************************/

#include <stdio.h>
#include <fcntl.h>
#include "includes.h"
#include "ZFM-20/ZFMComm.h"
#include "RestAPI.h"
#include "Database.h"
#include "json/reader.h"
#include "Audio.h"
#include "Time.h"

extern "C" {
#include "Camera/Camera.h"
#include "WebServer/web_server.h"
#include "WebServer/http.h"
#include "Solenoid.h"
#include "LCD.h"
}


/* Definition of Task Stacks */
#define   TASK_STACKSIZE       1024
OS_STK task1_stk[TASK_STACKSIZE*4];
OS_STK task2_stk[TASK_STACKSIZE];
OS_STK task3_stk[TASK_STACKSIZE];

OS_EVENT *fingerprintMailbox;
OS_EVENT *fingerprintSem;
OS_EVENT *databaseSemaphore;

/* Definition of Task Priorities */

#define TASK1_PRIORITY      7
#define TASK2_PRIORITY      8
#define TASK3_PRIORITY		12

const char * aliveJSON = "[{\"alive\":true}]";
bool m_enrollNow;

int getCurrentFingerprintId(bool enrollNow) {
	INT8U err;
	int *fid;
	err = OSSemPost(fingerprintSem);
	m_enrollNow = enrollNow;
	fid = (int*) OSMboxPend(fingerprintMailbox, 0, &err);
	return *fid;
}
int getBufferNum(bool isFirstBuffer){
	return isFirstBuffer ? 1 : 2;
}



/* Checks for fingerprint */
void task1(void* pdata) {
	INT8U err;
	bool firstBuffer = true;
	while (true) {
		//Init sensor
		ZFMComm fingerprintSensor;
		fingerprintSensor.init(SERIAL_NAME);
		//Continue while we have no error
		while (!fingerprintSensor.hasError()) {
			OSTimeDlyHMSM(0, 0, 1, 0);
			printf("Checking for fingerprint\n");
			LCD::writeToLCD("Checking for", "fingerprint");
			//Check if the web server is pending on a fingerprint
			bool sendToMailbox = OSSemAccept(fingerprintSem) > 0;
			while (!fingerprintSensor.scanFinger()
					|| !fingerprintSensor.storeImage(getBufferNum(firstBuffer))) {
				//Sleep for a second and try again
				OSTimeDlyHMSM(0, 0, 1, 0);
				if (!sendToMailbox) {
					sendToMailbox = OSSemAccept(fingerprintSem) > 0;
				}
			}
			printf("Fingerprint acquired, looking for fingerprint ID\n");
			LCD::writeToLCD("Print found", "Looking for ID");
			int fid = fingerprintSensor.findFingerprint(getBufferNum(firstBuffer));
			printf("Fingerprint id:%d\n", fid);

			stringstream sStream;
			sStream << fid;
			LCD::writeToLCD("Print ID: ", sStream.str());


			if (sendToMailbox) {
				//Swap Fingerprint Buffer used in case we enroll next
				firstBuffer = !firstBuffer;
				if(m_enrollNow){
					Database dbAccess(databaseSemaphore);
					fid = dbAccess.findNextID(USER_PRINTS);
					fingerprintSensor.storeFingerprint(fid);
					m_enrollNow = false;
				}
				//We need to wait for the mailbox to empty before we do anything
				while (true) {
					OS_MBOX_DATA mboxData;
					OSMboxQuery(fingerprintMailbox, &mboxData);
					if (mboxData.OSMsg == NULL) {
						//We can add data to the mailbox now
						break;
					}
					OSTimeDlyHMSM(0, 0, 1, 0);
				}
				err = OSMboxPost(fingerprintMailbox, &fid);
				if (err != OS_NO_ERR) {
					printf("Error sending message to fingerprint mailbox");
				}
				//Restart loop
				continue;
			}

			//Check if fingerprint is allowed access and unlock door
			//After this point, we fall through to the error if (uriString.compare(0, 7,
			{
				Database dbAccess(databaseSemaphore);
				if(dbAccess.checkAccess(fid)){
					//Success, unlock door!
					char * ledBase = (char*) GREEN_LEDS_BASE;
					for (int i = 0; i < GREEN_LEDS_DATA_WIDTH; i++){
						*ledBase = 1 << i;
						OSTimeDlyHMSM(0, 0, 0, 100);
					}
					*ledBase = 0;
					printf("Open up!!!\n\n");
					printf("Unlocking\n");
					LCD::writeToLCD("Unlocking", "");
					Solenoid::unlock();
					continue;
				}
			}

			//Fallthrough error case. Notify owner!
			printf("Failed to verify print!\n\n");
			LCD::writeToLCD("Print not", "verified");
			{
				Audio sound(databaseSemaphore);
				for (int i = 0; i < 3; i++){
					sound.play();
				}
			}
		}
	}
}
void task2(void* pdata) {
	LCD lcd = LCD();
	while (1) {
		if (*((char*) SWITCHES_BASE) & 1 << 1) {
			// Clearing database
			{
				Database db(databaseSemaphore);
				db.clearAll();
			}
			printf("Database has been cleared\n");
			LCD::writeToLCD("Database cleared", "");
			if (*((char*) SWITCHES_BASE) & 1 << 2) {
				// Populate database
				Database db(databaseSemaphore);
				db.testPopulate();
				printf("Database has been populated\n");
				LCD::writeToLCD("Database","populated");
			}
		}
		if (*((char*) SWITCHES_BASE) & 1 << 3){
			Solenoid::unlock();
			LCD::writeToLCD("Unlocking", "");
		}
		//TODO: Switches for rest api calls
		/*if (*((char*) SWITCHES_BASE) & 1 << 4){

		}*/

		OSTimeDlyHMSM(0, 0, 1, 0);
	}
}
void task3(void* pdata) {
	Camera::init();
	while (1){
		//Ensures that the lock re-locks
		Solenoid::timedLock(10 * CLOCKS_PER_SEC);
		LCD::writeToLCD("Locking", "");
	}
}

const char HEX2DEC[256] = {
		/*       0  1  2  3   4  5  6  7   8  9  A  B   C  D  E  F */
		/* 0 */-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		/* 1 */-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		/* 2 */-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		/* 3 */0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1,

		/* 4 */-1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		/* 5 */-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		/* 6 */-1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		/* 7 */-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,

		/* 8 */-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		/* 9 */-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		/* A */-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		/* B */-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,

		/* C */-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		/* D */-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		/* E */-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		/* F */-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

//http://www.codeguru.com/cpp/cpp/algorithms/strings/article.php/c12759/URI-Encoding-and-Decoding.htm
string UriDecode(const string & sSrc, bool plusAsSpace = false) {
	// Note from RFC1630:  "Sequences which start with a percent sign
	// but are not followed by two hexadecimal characters (0-9, A-F) are reserved
	// for future extension"

	const unsigned char * pSrc = (const unsigned char *) sSrc.c_str();
	const int SRC_LEN = sSrc.length();
	const unsigned char * const SRC_END = pSrc + SRC_LEN;
	const unsigned char * const SRC_LAST_DEC = SRC_END - 2; // last decodable '%'

	char * const pStart = new char[SRC_LEN];
	char * pEnd = pStart;

	while (pSrc < SRC_LAST_DEC) {
		if (*pSrc == '%') {
			char dec1, dec2;
			if (-1 != (dec1 = HEX2DEC[*(pSrc + 1)])
					&& -1 != (dec2 = HEX2DEC[*(pSrc + 2)])) {
				*pEnd = (dec1 << 4) + dec2;
				pSrc += 3;
				pEnd++;
				continue;
			}
		} else if(plusAsSpace && *pSrc == '+'){
			//If we want '+' encoded as ' ', do the replacement
			*pEnd = ' ';
			pSrc++;
			pEnd++;
			continue;
		}

		*pEnd++ = *pSrc++;
	}

	// the last 2- chars
	while (pSrc < SRC_END)
		*pEnd++ = *pSrc++;

	string sResult(pStart, pEnd);
	delete[] pStart;
	return sResult;
}

string getPOSTPayload(string data, string tag){
	int tagStart = 0,
			tagEnd = -1,
			dataEnd = -1;;
	//Iterate across data looking for tags
	//Format {tag}={data}&{tag2}={data2}...
	string currentTag;
	while(currentTag != tag){
		tagStart = dataEnd + 1;
		tagEnd = data.find("=", tagStart);
		if(tagEnd == string::npos){
			return "";
		}
		currentTag = data.substr(tagStart, tagEnd - tagStart);
		dataEnd = data.find("&", tagEnd);
		if(dataEnd == string::npos){
			dataEnd = data.size();
		}
	}
	//Found Tag
	return UriDecode(data.substr(tagEnd + 1, dataEnd - tagEnd - 1), true);
}

const char * handleHTTPPost(http_conn* conn, int *replyLen) {
	string uriString(conn->uri), retString, incomingData, jsonData, postType;
	incomingData.append(conn->rx_rd_pos, conn->content_length);
	retString = "{\"success\":false}";
	jsonData = getPOSTPayload(incomingData, "json");
	postType = getPOSTPayload(incomingData, "type");
	RestAPI api(&getCurrentFingerprintId, databaseSemaphore);
	if (uriString.compare(0, 6, "/users") == 0) {
		if (postType == "delete"){
			User user;
			user.loadFromJson(jsonData);
			retString = api.deleteUser(user.id);
		} else if (postType == "insert"){
			retString = api.insertUser(jsonData);
		} else if (postType == "enable"){
			retString = api.enableUser(jsonData);
		} else if (postType == "update"){
			retString = api.updateUser(jsonData);
		}
	} else if (uriString.compare(0, 6, "/roles") == 0) {
		if (postType == "delete"){
			Role role;
			role.loadFromJson(jsonData);
			retString = api.deleteRole(role.id);
		} else if (postType == "insert"){
			retString = api.insertRole(jsonData);
		} else if (postType == "update"){
			retString = api.updateRole(jsonData);
		}
	} else if (uriString.compare(0, 14, "/roleSchedules") == 0) {
		if (postType == "delete"){
			RoleSchedule schedule;
			schedule.loadFromJson(jsonData);
			retString = api.deleteRoleSchedule(schedule.id, schedule.rid);
		} else if (postType == "insert"){
			retString = api.insertRoleSchedule(jsonData);
		} else if (postType == "update"){
			retString = api.updateRoleSchedule(jsonData);
		}
	} else if (uriString.compare(0, 9, "/userRole") == 0) {
		if (postType == "delete"){
			UserRole userrole;
			userrole.loadFromJson(jsonData);
			retString = api.deleteUserRole(userrole.id, userrole.uid, userrole.rid);
		} else if (postType == "insert"){
			retString = api.insertUserRole(jsonData);
		} else if (postType == "update"){
			retString = api.updateUserRole(jsonData);
		}
	}else if (uriString.compare(0, 8, "/history") == 0) {
		//retString = api.insertHistory(jsonData);
	} else if (uriString.compare(0, 7, "/prints") == 0) {
		if (postType == "delete"){
			UserPrint print;
			print.loadFromJson(jsonData);
			retString = api.deletePrint(print.id, print.uid);
		} else if (postType == "insert"){
			retString = api.insertPrint(jsonData);
		} else if (postType == "update"){
			retString = api.updatePrint(jsonData);
		}
	} else if (uriString.compare(0, 5, "/time") == 0) {
		if (postType == "update"){
			istringstream reader(jsonData);
			INT32U time;
			reader >> time;
			retString = api.setSystemTime(time);
		}
	}else {
		*replyLen = 0;
		return NULL;
	}

	*replyLen = retString.length();
	const char * retval = retString.c_str();
	return retval;
}

const char * createHttpResponse(const char * URI, int *len, bool *isImage) {

	*isImage = false;
	string uriString(URI), retString;
	RestAPI api(&getCurrentFingerprintId, databaseSemaphore);
	if (uriString.compare(0, 6, "/alive") == 0) {
		retString = aliveJSON;
	} else if (uriString.compare(0, 6, "/users") == 0) {
		retString = api.getUsers(uriString);
	} else if (uriString.compare(0, 6, "/roles") == 0) {
		retString = api.getRoles(uriString);
	} else if (uriString.compare(0, 14, "/roleSchedules") == 0) {
		retString = api.getRoleSchedule(uriString);
	} else if (uriString.compare(0, 9, "/userRole") == 0) {
		retString = api.getUserRoles(uriString);
	} else if (uriString.compare(0, 9, "/roleUser") == 0){
		retString = api.getRoleUsers(uriString);
	} else if (uriString.compare(0, 8, "/history") == 0) {
		retString = api.getHistory(uriString);
	} else if (uriString.compare(0, 7, "/prints") == 0) {
		retString = api.getPrints(uriString);
	} else if (uriString.compare(0, 11, "/checkAdmin") == 0) {
		if (uriString.length() == 11){
			retString = api.checkAdminPrint();
		} else {
			retString = api.checkAdminPrint(atoi(uriString.substr(12).c_str()));
		}
	}  else if (uriString.compare(0, 8, "/enroll1") == 0) {
		retString = api.enroll1();
	} else if (uriString.compare(0, 8, "/enroll2") == 0) {
		retString = api.enroll2();
	} else if (uriString.compare(0, 7, "/unlock") == 0) {
		retString = api.unlockLock();
	} else if (uriString.compare(0, 4, "/pic") == 0) {
		*isImage = true;
		char * imgData = Camera::getBMP(len);
		retString.append(imgData, *len);
		free(imgData);
	} else if (uriString.compare(0, 5, "/time") == 0) {
		stringstream writer;
		writer << api.getSystemTime();
		retString = writer.str();
	}else {
		*len = 0;
		return NULL;
	}
	*len = retString.length();
	const char * retval = retString.c_str();
	return retval;
}

extern "C" {
void startTasks() {
	OSTaskCreateExt(task1, NULL, &task1_stk[(TASK_STACKSIZE * 4) - 1], TASK1_PRIORITY,
			TASK1_PRIORITY, task1_stk, TASK_STACKSIZE * 4, NULL, 0 );
	OSTaskCreateExt(task2, NULL, &task2_stk[TASK_STACKSIZE - 1], TASK2_PRIORITY,
			TASK2_PRIORITY, task2_stk, TASK_STACKSIZE, NULL, 0);

	OSTaskCreateExt(task3, NULL, &task3_stk[TASK_STACKSIZE - 1], TASK3_PRIORITY,
			TASK3_PRIORITY, task3_stk, TASK_STACKSIZE, NULL, 0);
}
/* The main function creates two task and starts multi-tasking */
int main(void) {

	INT8U err;
	fingerprintSem = OSSemCreate(0);
	if (fingerprintSem == NULL) {
		printf("Error initializing sensor semaphore");
		return -1;
	}
	fingerprintMailbox = OSMboxCreate(NULL);
	if (fingerprintMailbox == NULL) {
		printf("Error fingerprint mailbox");
		return -1;
	}

	databaseSemaphore = OSSemCreate(1);
	if (databaseSemaphore == NULL) {
		printf("Error initializing database semaphore");
		return -1;
	}

	if(!Solenoid::init()){
		printf("Error initializing solenoid\n");
		return -1;
	}


	if(!LCD::init()){
		printf("Error initializing LCD\n");
		return -1;
	}

	if (!Time::init()){
		printf("Failed to initialize time object\n");
		return -1;
	}

#ifndef NOWEBSERVER
	startWebServer(&startTasks, &createHttpResponse, &handleHTTPPost);
#else
	startTasks();
#endif

	OSStart();
	return 0;
}
}

/******************************************************************************
 *                                                                             *
 * License Agreement                                                           *
 *                                                                             *
 * Copyright (c) 2004 Altera Corporation, San Jose, California, USA.           *
 * All rights reserved.                                                        *
 *                                                                             *
 * Permission is hereby granted, free of charge, to any person obtaining a     *
 * copy of this software and associated documentation files (the "Software"),  *
 * to deal in the Software without restriction, including without limitation   *
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,    *
 * and/or sell copies of the Software, and to permit persons to whom the       *
 * Software is furnished to do so, subject to the following conditions:        *
 *                                                                             *
 * The above copyright notice and this permission notice shall be included in  *
 * all copies or substantial portions of the Software.                         *
 *                                                                             *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,    *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER      *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING     *
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER         *
 * DEALINGS IN THE SOFTWARE.                                                   *
 *                                                                             *
 * This agreement shall be governed in all respects by the laws of the State   *
 * of California and by the laws of the United States of America.              *
 * Altera does not recommend, suggest or require that this reference design    *
 * file be used in conjunction or combination with any other product.          *
 ******************************************************************************/
