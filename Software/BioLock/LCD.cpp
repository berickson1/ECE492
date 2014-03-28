/*
 * LCD.cpp
 *
 *  Created on: 2014-03-27
 *      Author: bitner
 */

#include "LCD.h"

LCD::LCD(){
	char_lcd = alt_up_character_lcd_open_dev(CHARACTER_LCD_0_NAME);
	if(char_lcd == NULL){
		printf("Could not open LCD device\n");
	}
	alt_up_character_lcd_init(char_lcd);
}

void LCD::writeStatus(){
	//First, clear LCD
	for (int j = 0; j < 2; j++){
		for(int i = 0; i < 16; i++){
			alt_up_character_lcd_erase_pos(char_lcd, i, j);
		}
	}

	//Get Status
	char *lock;
	char *database = "-";

	if(*(char *)SWITCHES_BASE & 1 << 1){
		database = "clr";
	}
	if(*(char *)SWITCHES_BASE & 1 << 2){
		database = "pop";
	}
	if(*(char *)SWITCHES_BASE & 1 << 3){
		lock = "unlck";
	}
	else lock = "lck";

	//Write status to screen
	char statusFirstLine[] = "DB: ";
	char statusSecondLine[] = "Lock: ";

	alt_up_character_lcd_set_cursor_pos(char_lcd, 0, 0);
	alt_up_character_lcd_string(char_lcd, statusFirstLine);
	alt_up_character_lcd_string(char_lcd, database);
	alt_up_character_lcd_set_cursor_pos(char_lcd, 0, 1);
	alt_up_character_lcd_string(char_lcd, statusSecondLine);
	alt_up_character_lcd_string(char_lcd, lock);

}
