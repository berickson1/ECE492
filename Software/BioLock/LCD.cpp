/*
 * LCD.cpp
 *
 *  Created on: 2014-03-27
 *      Author: bitner
 */

#include "LCD.h"
using namespace std;

OS_EVENT * LCD::m_lcdMutex;
alt_up_character_lcd_dev *LCD::char_lcd = NULL;

LCD::LCD(){

}

bool LCD::init(){
	INT8U err = OS_NO_ERR;
	m_lcdMutex = OSMutexCreate(1, &err); //TODO: Double check if prio is correct
	if(err != OS_NO_ERR){
		printf("Error initializing LCD mutex\n");
		return false;
	}
	OSMutexPend(m_lcdMutex, 0, &err);
	if(err != OS_NO_ERR){
		printf("Error pending on LCD mutex during LCD init\n");
		return false;
	}
	char_lcd = alt_up_character_lcd_open_dev(CHARACTER_LCD_0_NAME);
	if(char_lcd == NULL){
		printf("Could not open LCD device\n");
		return false;
	}
	alt_up_character_lcd_init(char_lcd);
	err = OSMutexPost(m_lcdMutex);
	if(err != OS_NO_ERR){
		printf("Error posting to LCD mutex during LCD init\n");
		return false;
	}

	return true;
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
