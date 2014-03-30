/*
 * LCD.cpp
 *
 *  Created on: 2014-03-27
 *      Author: bitner
 */

#include "LCD.h"
using namespace std;
LCD::LCD(OS_EVENT * lcdMutex):m_lcdMutex(lcdMutex){
	INT8U err = OS_NO_ERR;
	OSMutexPend(m_lcdMutex, 0, &err);
	if(err != OS_NO_ERR){
		printf("Error pending on LCD mutex during LCD init\n");
		return;
	}
	char_lcd = alt_up_character_lcd_open_dev(CHARACTER_LCD_0_NAME);
	if(char_lcd == NULL){
		printf("Could not open LCD device\n");
	}
	alt_up_character_lcd_init(char_lcd);
	err = OSMutexPost(m_lcdMutex);
	if(err != OS_NO_ERR){
		printf("Error posting to LCD mutex during LCD init\n");
	}

}

void LCD::writeToLCD(string firstLine, string secondLine){
	INT8U err = OS_NO_ERR;
	OSMutexPend(m_lcdMutex, 0, &err);
	if(err != OS_NO_ERR){
		printf("Error pending on LCD mutex\n");
		return;
	}
		//First, clear LCD
	for (int j = 0; j < 2; j++){
		for(int i = 0; i < 16; i++){
			alt_up_character_lcd_erase_pos(char_lcd, i, j);
		}
	}
	//Write status to screen

	alt_up_character_lcd_set_cursor_pos(char_lcd, 0, 0);
	alt_up_character_lcd_string(char_lcd, firstLine.c_str());
	alt_up_character_lcd_set_cursor_pos(char_lcd, 0, 1);
	alt_up_character_lcd_string(char_lcd, secondLine.c_str());
	err = OSMutexPost(m_lcdMutex);
	if(err != OS_NO_ERR){
		printf("Error posting to LCD mutex\n");
		return;
	}

}
