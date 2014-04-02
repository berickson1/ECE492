/*
 * Time.h
 *
 *  Created on: 2014-04-02
 *      Author: be
 */

#ifndef TIME_H_
#define TIME_H_
#include "includes.h"

class Time {
public:
	Time();
	virtual ~Time();
	static void setTime(INT32U time);
	static bool init();
	static INT32U getTime();
private:
	static INT32U unixStartTime;
	static INT32U sysBaseTime;
	static OS_EVENT *timeMutex;
};
#endif /* TIME_H_ */
