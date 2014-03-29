/*
 * LCD.h
 *
 *  Created on: 2014-03-27
 *      Author: bitner
 */

#ifndef LCD_H_
#define LCD_H_

#include <stdio.h>
#include "includes.h"

extern "C"{
#include "altera_up_avalon_character_lcd.h"
}

class LCD {
public:
	LCD(OS_EVENT * lcdMutex);
	void writeToLCD(OS_EVENT * lcdMutex, char * firstLine, char * secondLine);
private:
	alt_up_character_lcd_dev *char_lcd;
};

#endif /* LCD_H_ */
