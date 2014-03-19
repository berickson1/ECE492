/*
 * Solenoid.h
 *
 *  Created on: 2014-03-07
 *      Author: bitner
 */

#ifndef SOLENOID_H_
#define SOLENOID_H_

#include "includes.h"
extern "C"{
#include "altera_avalon_pio_regs.h"
}

#define UNLOCKED 1
#define LOCKED 0

class Solenoid {
public:
	static const int TIME_UNLOCKED = 3000000;//Approx. 10s
	static void unlock();
};

#endif /* SOLENOID_H_ */
