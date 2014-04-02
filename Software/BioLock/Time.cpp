/*
 * Time.cpp
 *
 *  Created on: 2014-04-02
 *      Author: be
 */

#include "Time.h"
#include <stdio.h>

INT32U Time::unixStartTime = 0;
INT32U Time::sysBaseTime = 0;
OS_EVENT *Time::timeMutex;

Time::Time() {
	// TODO Auto-generated constructor stub

}

Time::~Time() {
	// TODO Auto-generated destructor stub
}
bool Time::init(){
	INT8U err = OS_NO_ERR;
	timeMutex = OSMutexCreate(4, &err);
	if (err != OS_NO_ERR){
		printf("Error initializing Time Mutex\n");
		return false;
	}
	return true;
}
void Time::setTime(INT32U time){
	INT8U err = OS_NO_ERR;
	OSMutexPend(timeMutex, 0, &err);
	if (err != OS_NO_ERR){
		printf("Error pending on Time Mutex\n");
		return;
	}
	unixStartTime = time;
	sysBaseTime = OSTimeGet();
	if (OSMutexPost(timeMutex) != OS_NO_ERR){
		printf("Error posting to Time Mutex\n");
	}
}
INT32U Time::getTime(){
	INT8U err = OS_NO_ERR;
	OSMutexPend(timeMutex, 0, &err);
	if (err != OS_NO_ERR){
		printf("Error pending on Time Mutex\n");
		return 0;
	}
	INT32U retTime = unixStartTime + ((OSTimeGet() - sysBaseTime)/1000);
	if (OSMutexPost(timeMutex) != OS_NO_ERR){
		printf("Error posting to Time Mutex\n");
	}
	return retTime;
}
