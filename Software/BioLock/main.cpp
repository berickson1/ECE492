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
#include "includes.h"
#include "ZFMComm.h"
extern "C"{
#include "WebServer\web_server.h"
}
/* Definition of Task Stacks */
#define   TASK_STACKSIZE       2048
OS_STK task1_stk[TASK_STACKSIZE];
OS_STK task2_stk[TASK_STACKSIZE];

/* Definition of Task Priorities */

#define TASK1_PRIORITY      5
#define TASK2_PRIORITY      6

/* Prints "Hello World" and sleeps for three seconds */
void task1(void* pdata) {
	ZFMComm fingerprintSensor;
	fingerprintSensor.init(SERIAL_NAME);
	fingerprintSensor.deleteAllFingerprints();
	while (!fingerprintSensor.hasError()) {
		printf("Checking for fingerprint\n");
		while (!fingerprintSensor.scanFinger() || !fingerprintSensor.storeImage(1)){
			OSTimeDlyHMSM(0, 0, 1, 0);
		}
		OSTimeDlyHMSM(0, 0, 1, 0);
		while (!fingerprintSensor.scanFinger() || !fingerprintSensor.storeImage(2)) {
			OSTimeDlyHMSM(0, 0, 1, 0);
		}
		if(fingerprintSensor.storeFingerprint(7)){
			printf("Stored Fingerprint to 7\n");
		} else {
			printf("Failed to store fingerprint to 7\n");
		}
		OSTimeDlyHMSM(0, 0, 5, 0);
		printf("Verify");
		while(true){
			while (!fingerprintSensor.scanFinger() || !fingerprintSensor.storeImage(1)) {
				OSTimeDlyHMSM(0, 0, 1, 0);
			}
			int fid = fingerprintSensor.findFingerprint(1);
			printf("Fingerprint ID: %d\n", fid);
			OSTimeDlyHMSM(0, 0, 1, 0);
		}
	}
}
/* Prints "Hello World" and sleeps for three seconds */
void task2(void* pdata) {
	while (1) {
		printf("Hello from task2\n");
		OSTimeDlyHMSM(0, 0, 3, 0);
	}
}
/* The main function creates two task and starts multi-tasking */
int main(void) {
	startMain();
	OSTaskCreateExt(task1, NULL, &task1_stk[TASK_STACKSIZE - 1], TASK1_PRIORITY,
			TASK1_PRIORITY, task1_stk, TASK_STACKSIZE, NULL, 0);

	OSTaskCreateExt(task2, NULL, &task2_stk[TASK_STACKSIZE - 1], TASK2_PRIORITY,
			TASK2_PRIORITY, task2_stk, TASK_STACKSIZE, NULL, 0);
	OSStart();
	return 0;
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
