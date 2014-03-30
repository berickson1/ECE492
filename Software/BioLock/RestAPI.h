/*
 * RestAPI.h
 *
 *  Created on: 2014-02-27
 *      Author: be
 */
#ifndef RESTAPI_H_
#define RESTAPI_H_

#include "json/writer.h"
#include "Database.h"
#include "Solenoid.h"
using namespace std;
class RestAPI {
public:
	RestAPI(int (*getFingerprintIdFunction)(bool enrollNow), OS_EVENT * databaseSem, OS_EVENT * solenoidSem, OS_EVENT * solenoidMutex);
	virtual ~RestAPI();
	string getUsers(string URI);
	string getUser(int uid);
	string getRoles(string URI);
	string getRole(int rid);
	string getUserRoles(string URI);
	string getUserRoles(int uid);
	string getRoleUsers(string URI);
	string getRoleUsers(int rid);
	string getRoleSchedule(string URI);
	string getRoleSchedule(int rid);
	string getHistory(string URI);
	string getPrints(string URI);
	string getPrint(int uid);
	string insertUser(string data);
	string enableUser(string data);
	string insertRole(string data);
	string insertUserRole(string data);
	string insertRoleSchedule(string data);
	string insertPrint(string data);
	string updateUser(string data);
	string updateRole(string data);
	string updateUserRole(string data);
	string updateRoleSchedule(string data);
	string updatePrint(string data);
	string deleteUser(int id);
	string deleteRole(int id);
	string deleteUserRole(int id, int uid, int rid);
	string deleteRoleSchedule(int id, int rid);
	string deletePrint(int id, int uid);
	string unlockLock();
	string enroll1();
	string enroll2();
	string setSystemTime(INT32U time);
	INT32U getSystemTime();
	bool checkAdminPrint();
private:
	int (*getFingerprintId)(bool enrollNow);
	OS_EVENT * m_databaseSem;
	OS_EVENT * m_solenoidSem;
	OS_EVENT * m_solenoidMutex;
	int extractID(string URI);
	const string m_successString;
};

#endif /* RESTAPI_H_ */
