/*
 * Database_Table_Types.h
 *
 *  Created on: 2014-03-03
 *      Author: be
 */

#ifndef DATABASE_TABLE_TYPES_H_
#define DATABASE_TABLE_TYPES_H_
using namespace std;

typedef struct History{
	int id, u_id;
	bool success;
	time_t time;
} History;

typedef struct User{
	int id;
	string name;
	bool enabled;
	time_t startDate, endDate;
} User;

typedef struct UserPrint{
	int id, u_id, f_id;
} UserPrint;

typedef struct Role{
	int id;
	string name;
	bool admin, enabled;
	time_t startDate, endDate;
} Role;

typedef struct UserRole{
	int id, u_id, r_id;
	time_t startDate, endDate;
} UserRole;

typedef struct RoleSchedule{
	int id, r_id, startTime, endTime, days;
	time_t startDate, endDate;
} RoleSchedule;


#endif /* DATABASE_TABLE_TYPES_H_ */
