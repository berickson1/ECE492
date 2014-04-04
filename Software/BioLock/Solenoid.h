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
	static void unlock();
	static void timedLock(int unlockedTime, LCD &lcd);
	static void lock();
	static bool init();

private:
	static OS_EVENT * solenoidSem;
	static OS_EVENT * solenoidMutex;
};

#endif /* SOLENOID_H_ */
