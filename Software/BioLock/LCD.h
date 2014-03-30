/*
 * LCD.h
 *
 *  Created on: 2014-03-27
 *      Author: bitner
 */

#ifndef LCD_H_
#define LCD_H_

#include <stdio.h>
#include <string>
#include <stdlib.h>
#include "includes.h"

extern "C"{
#include "altera_up_avalon_character_lcd.h"
}
using namespace std;
class LCD {
public:
	LCD(OS_EVENT * lcdMutex);
	void writeToLCD(string firstLine, string secondLine);
private:
	alt_up_character_lcd_dev *char_lcd;
	OS_EVENT * m_lcdMutex;
};

#endif /* LCD_H_ */
