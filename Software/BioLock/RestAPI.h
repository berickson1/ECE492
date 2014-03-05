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
	string getUsers();
	string getUser(int uid);
	string getRoles();
	string getRole(int rid);
	string getUserRoles();
	string getUserRoles(int uid);
	string getRoleUsers(int rid);
	string getRoleSchedule();
	string getRoleSchedule(int rid);
	string getHistory();
	string getPrints();
	string getPrint(int uid);
	string scanPrint();
private:
	int (*getFingerprintId)();
	OS_EVENT * m_databaseSem;
};

#endif /* RESTAPI_H_ */
