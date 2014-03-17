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
#include "ZFMComm.h"
#include "RestAPI.h"
#include "Database.h"
#include "json/reader.h"
#include "Audio.h"

extern "C" {
#include "Camera/Camera.h"
#include "WebServer/web_server.h"
#include "WebServer/http.h"
}
#define NOWEBSERVER

/* Definition of Task Stacks */
#define   TASK_STACKSIZE       2048
OS_STK task1_stk[TASK_STACKSIZE];
OS_STK task2_stk[TASK_STACKSIZE];

OS_EVENT *fingerprintMailbox;
OS_EVENT *fingerprintSem;
OS_EVENT *databaseSemaphore;

/* Definition of Task Priorities */

#define TASK1_PRIORITY      6
#define TASK2_PRIORITY      7

const char * aliveJSON = "{\"alive\":true}";

int getCurrentFingerprintId() {
	INT8U err;
	int *fid;
	err = OSSemPost(fingerprintSem);
	fid = (int*) OSMboxPend(fingerprintMailbox, 0, &err);
	return *fid;
}

/* Checks for fingerprint */
void task1(void* pdata) {
	INT8U err;
	bool enroll = false;
	while (true) {
		//Init sensor
		ZFMComm fingerprintSensor;
		fingerprintSensor.init(SERIAL_NAME);
		//Continue while we have no error
		while (!fingerprintSensor.hasError()) {
			OSTimeDlyHMSM(0, 0, 1, 0);
			printf("Checking for fingerprint\n");
			//Check if the web server is pending on a fingerprint
			bool sendToMailbox = OSSemAccept(fingerprintSem) > 0;
			while (!fingerprintSensor.scanFinger()
					|| !fingerprintSensor.storeImage(1)) {
				//Sleep for a second and try again
				OSTimeDlyHMSM(0, 0, 1, 0);
				if (!sendToMailbox) {
					sendToMailbox = OSSemAccept(fingerprintSem) > 0;
				}
			}
			//IO Demo: are we enrolling?
			char* address = (char*) SWITCHES_BASE;
			if ((*address) & 1<<0){
				int storeId = 0;
				printf("Enter ID to store print at:\n");
				scanf("%d", &storeId);
				printf("Scan finger again\n");
				while (!fingerprintSensor.scanFinger()
						|| !fingerprintSensor.storeImage(2)) {
					//Sleep for a second and try again
					OSTimeDlyHMSM(0, 0, 1, 0);
				}
				if(fingerprintSensor.storeFingerprint(storeId)){
					printf("Stored fingerprint at %d\n", storeId);
					Database dbAccess(databaseSemaphore);
					User newUser;
					newUser.enabled = true;
					newUser.id = storeId;
					newUser.name = "Mavis Chan";
					newUser.startDate = newUser.enabled = 5;
					dbAccess.insertUser(newUser);
					UserPrint userPrint;
					userPrint.fid = storeId;
					userPrint.uid = storeId;
					dbAccess.insertUserPrint(userPrint);
				} else {
					printf("Unable to store fingerprint at %d\n", storeId);
				}
				enroll = !enroll;
				continue;
			}
			printf("Fingerprint acquired, looking for fingerprint ID\n");
			int fid = fingerprintSensor.findFingerprint(1);
			printf("Fingerprint id:%d\n", fid);
			if (sendToMailbox) {
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
			if (fid >= 0) {
				Json::Value userPrintRoot;
				Json::Value userRoot;
				Json::Value roleRoot;
				Database dbAccess(databaseSemaphore);
				string userPrintJSON = dbAccess.findUserPrint(fid);
				Json::Reader jsonReader;
				int uid;
				//Todo: handle schedule lookup
				if (jsonReader.parse(userPrintJSON, userPrintRoot)){
					int uid = userPrintRoot.get("uid", -1).asInt();
					printf("User found id:%d", uid);
					string userJSON = dbAccess.findUser(uid);
					if (jsonReader.parse(userJSON, userRoot)){
						printf(" name:%s\n", userRoot.get("name", "Unknown").asCString());
						if (jsonReader.parse(dbAccess.findUserRole(uid), roleRoot)) {
						//Todo: Finish checking if user has role
						//Success, unlock door!
							char * ledBase = (char*) GREEN_LEDS_BASE;
							for (int i = 0; i < GREEN_LEDS_DATA_WIDTH; i++){
								*ledBase = 1 << i;
								OSTimeDlyHMSM(0, 0, 0, 100);
							}
							*ledBase = 0;
							printf("Open up!!!\n\n");
							continue;
						}
					}

				}
			}
			//Fallthrough error case. Notify owner!
			printf("Failed to verify print!\n\n");
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
	while (1) {
		if (*((char*) SWITCHES_BASE) & 1 << 1) {
			// Clearing database
			{
				Database db(databaseSemaphore);
				db.clearAll();
			}
			printf("Database has been cleared\n");
			if (*((char*) SWITCHES_BASE) & 1 << 2) {
				// Populate database
				Database db(databaseSemaphore);
				db.testPopulate();
				printf("Database has been populated\n");
			}
		}

		OSTimeDlyHMSM(0, 0, 1, 0);
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
string UriDecode(const string & sSrc) {
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
				*pEnd++ = (dec1 << 4) + dec2;
				pSrc += 3;
				continue;
			}
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

const char * handleHTTPPost(http_conn* conn, int *replyLen) {
	string uriString(conn->uri), retString, incomingData;
	incomingData.append(conn->rx_rd_pos, conn->content_length);
	incomingData = retString = UriDecode(incomingData);
	printf("%s", incomingData.c_str());
	RestAPI api(&getCurrentFingerprintId, databaseSemaphore);
	if (uriString.compare(0, 6, "/users") == 0) {
		//Save user
	} else if (uriString.compare(0, 6, "/roles") == 0) {
		//Save role
	} else if (uriString.compare(0, 14, "/roleSchedules") == 0) {
		//Save schedule
	} else if (uriString.compare(0, 8, "/history") == 0) {
		//Save history
	} else if (uriString.compare(0, 7, "/prints") == 0) {
		//Save Print
	} else {
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
		retString = api.getUsers();
	} else if (uriString.compare(0, 6, "/roles") == 0) {
		retString = api.getRoles();
	} else if (uriString.compare(0, 14, "/roleSchedules") == 0) {
		retString = api.getRoleSchedule();
	} else if (uriString.compare(0, 8, "/history") == 0) {
		retString = api.getHistory();
	} else if (uriString.compare(0, 7, "/prints") == 0) {
		retString = api.getPrints();
	} else if (uriString.compare(0, 4, "/pic") == 0) {
		*isImage = true;
		char * imgData = Camera::getBMP(len);
		retString.append(imgData, *len);
		free(imgData);
	} else {
		*len = 0;
		return NULL;
	}
	*len = retString.length();
	const char * retval = retString.c_str();
	return retval;
}
extern "C" {
void startTasks() {
#ifndef NOSENSOR
	OSTaskCreateExt(task1, NULL, &task1_stk[TASK_STACKSIZE - 1], TASK1_PRIORITY,
			TASK1_PRIORITY, task1_stk, TASK_STACKSIZE, NULL, 0);
#endif
	OSTaskCreateExt(task2, NULL, &task2_stk[TASK_STACKSIZE - 1], TASK2_PRIORITY,
			TASK2_PRIORITY, task2_stk, TASK_STACKSIZE, NULL, 0);
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

	Camera::enableSnapshotMode();
	Camera::mirrorHorizontal();

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
