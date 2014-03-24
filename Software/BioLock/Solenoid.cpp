/*
 * Solenoid.cpp
 *
 *  Created on: 2014-03-07
 *      Author: bitner
 */

#include "Solenoid.h"

void Solenoid::unlock(OS_EVENT * solenoidSem) {
	IOWR_ALTERA_AVALON_PIO_DATA(SOLENOID_CONTROLLER_BASE, UNLOCKED);
	INT8U err = OSSemPost(solenoidSem);
	if(err != OS_NO_ERR){
		printf("Error posting to solenoid semaphore\n");
	}
}

void Solenoid::lock() {
	IOWR_ALTERA_AVALON_PIO_DATA(SOLENOID_CONTROLLER_BASE, LOCKED);
}



