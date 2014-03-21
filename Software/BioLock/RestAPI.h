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
	RestAPI(int (*getFingerprintIdFunction)(bool enrollNow), OS_EVENT * databaseSem);
	virtual ~RestAPI();
	string getUsers(string URI);
	string getUser(int uid);
	string getRoles(string URI);
	string getRole(int rid);
	string getUserRoles(string URI);
	string getUserRoles(int uid);
	string getRoleUsers(int rid);
	string getRoleSchedule(string URI);
	string getRoleSchedule(int rid);
	string getHistory(string URI);
	string getPrints(string URI);
	string getPrint(int uid);
	string insertUser(string data);
	string insertRole(string data);
	string insertUserRole(string data);
	string insertRoleSchedule(string data);
	string insertPrint(string data);
	string deleteUser(int id);
	string deleteRole(int id);
	string deleteUserRole(int id);
	string deleteRoleSchedule(int id);
	string deletePrint(int id);
	string scanPrint();
	string unlockLock();
	bool checkAdminPrint();
private:
	int (*getFingerprintId)(bool enrollNow);
	OS_EVENT * m_databaseSem;
	int extractID(string URI);
	const string m_successString;
};

#endif /* RESTAPI_H_ */
