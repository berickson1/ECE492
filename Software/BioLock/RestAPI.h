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
using namespace std;
class RestAPI {
public:
	RestAPI(int (*getFingerprintIdFunction)(), OS_EVENT * databaseSem);
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
	string scanPrint();
private:
	int (*getFingerprintId)();
	OS_EVENT * m_databaseSem;
	int extractID(string URI);
};

#endif /* RESTAPI_H_ */
