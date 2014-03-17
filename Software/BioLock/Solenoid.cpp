/*
 * Solenoid.cpp
 *
 *  Created on: 2014-03-07
 *      Author: bitner
 */

#include "Solenoid.h"

void Solenoid::unlock() {
	IOWR_ALTERA_AVALON_PIO_DATA(SOLENOID_CONTROLLER_BASE, UNLOCKED);
	OSTimeDlyHMSM(0,0,10,0);
	IOWR_ALTERA_AVALON_PIO_DATA(SOLENOID_CONTROLLER_BASE, LOCKED);
}





