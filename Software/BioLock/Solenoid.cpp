/*
 * Solenoid.cpp
 *
 *  Created on: 2014-03-07
 *      Author: bitner
 */

#include "Solenoid.h"
OS_EVENT * Solenoid::solenoidSem;
OS_EVENT * Solenoid::solenoidMutex;

bool Solenoid::init(){
	INT8U err = OS_NO_ERR;
	solenoidSem = OSSemCreate(0);
	if (solenoidSem == NULL){
		printf("Error initializing solenoid semaphore");
		return false;
	}

	solenoidMutex = OSMutexCreate(0, &err);
	if(err != OS_NO_ERR){
		printf("Error initializing solenoid mutex\n");
		return false;
	}
	return true;
}

void Solenoid::unlock() {
	INT8U err = OS_NO_ERR;
	OSMutexPend(solenoidMutex, 0, &err);
	if(err != OS_NO_ERR){
		printf("Error pending on solenoid mutex in unlock\n");
		return;
	}
	IOWR_ALTERA_AVALON_PIO_DATA(SOLENOID_CONTROLLER_BASE, UNLOCKED);
	err = OSMutexPost(solenoidMutex);
	if(err != OS_NO_ERR){
		printf("Error posting to solenoid mutex in unlock\n");
	}
	err = OSSemPost(solenoidSem);
	if(err != OS_NO_ERR){
		printf("Error posting to solenoid semaphore\n");
	}
}

void Solenoid::timedLock(int unlockedTime, LCD &lcd){
	INT8U err = OS_NO_ERR;
	OSSemPend(solenoidSem, 0, &err);
	if(err != OS_NO_ERR){
		printf("Error pending on solenoid semaphore\n");
	}
	do{
		//If the Semaphore is posted to again, sleep for 10 seconds
		OSSemPend(solenoidSem, unlockedTime, &err);
		if(err == OS_NO_ERR){
			printf("Door unlocked again, sleep some more\n");
			lcd.writeToLCD("Door unlocked", "again");
		}
	} while (err == OS_NO_ERR);
	printf("Locking\n");
	lock();
}

void Solenoid::lock() {
	INT8U err = OS_NO_ERR;
	OSMutexPend(solenoidMutex, 0, &err);
	if(err != OS_NO_ERR){
		printf("Error pending on solenoid mutex\n");
		return;
	}
	IOWR_ALTERA_AVALON_PIO_DATA(SOLENOID_CONTROLLER_BASE, LOCKED);
	err = OSMutexPost(solenoidMutex);
	if(err != OS_NO_ERR){
		printf("Error posting to solenoid mutex\n");
	}
}

