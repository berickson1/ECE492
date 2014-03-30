/*
 * Solenoid.h
 *
 *  Created on: 2014-03-07
 *      Author: bitner
 */

#ifndef SOLENOID_H_
#define SOLENOID_H_

#include <stdio.h>
#include "includes.h"
#include "LCD.h"

extern "C"{
#include "altera_avalon_pio_regs.h"
}


#define UNLOCKED 1
#define LOCKED 0

class Solenoid {
public:
	static void unlock(OS_EVENT * solenoidSem, OS_EVENT * solenoidMutex);
	static void timedLock(OS_EVENT * solenoidSem, OS_EVENT * solenoidMutex, int unlockedTime, LCD &lcd);
	static void lock(OS_EVENT * solenoidMutex);
};

#endif /* SOLENOID_H_ */
