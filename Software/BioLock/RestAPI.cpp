/*
 * RestAPI.cpp
 *
 *  Created on: 2014-02-27
 *      Author: be
 */

#include "RestAPI.h"
using namespace std;
RestAPI::RestAPI(int (*getFingerprintIdFunction)(), OS_EVENT * databaseSem):
	getFingerprintId(getFingerprintIdFunction),
	m_databaseSem(databaseSem)
{

}

RestAPI::~RestAPI() {
}


string RestAPI::getUsers(string URI){
	int id = extractID(URI);
	if (id != -1){
		return getUser(id);
	}
	Database db(m_databaseSem);
	return db.listAll(USERS);
}
string RestAPI::getUser(int uid){
	Database db(m_databaseSem);
	return db.findUser(uid);
}
string RestAPI::getRoles(string URI){
	int id = extractID(URI);
	if (id != -1){
		return getRole(id);
	}
	Database db(m_databaseSem);
	return db.listAll(ROLES);

}
string RestAPI::getRole(int rid){
	Database db(m_databaseSem);
	return db.findRole(rid);

}
string RestAPI::getUserRoles(string URI){
	int id = extractID(URI);
	if (id != -1){
		return getUserRoles(id);
	}
	Database db(m_databaseSem);
	return db.listAll(USER_ROLES);

}
string RestAPI::getUserRoles(int uid){
	Database db(m_databaseSem);
	return db.findUserRole(uid);

}
string RestAPI::getRoleUsers(int rid){
//TODO: Is there a database call for this?
}
string RestAPI::getRoleSchedule(string URI){
	int id = extractID(URI);
	if (id != -1){
		return getRoleSchedule(id);
	}
	Database db(m_databaseSem);
	return db.listAll(ROLE_SCHEDULE);

}
string RestAPI::getRoleSchedule(int rid){
	Database db(m_databaseSem);
	return db.findRoleSchedule(rid);
}
string RestAPI::getHistory(string URI){
	Database db(m_databaseSem);
	return db.listAll(HISTORY);

}
string RestAPI::getPrints(string URI){
	int id = extractID(URI);
	if (id != -1){
		return getPrint(id);
	}
	Database db(m_databaseSem);
	return db.listAll(USER_PRINTS);

}
string RestAPI::getPrint(int uid){
	Database db(m_databaseSem);
	return db.findUserPrint(uid);

}
string RestAPI::scanPrint(){

}

int RestAPI::extractID(string URI){
	//At this point we have {ip-addr}/{TYPE}
	//or {ip-addr}/{TYPE}/
	//or {ip-addr}/{TYPE}/{NUM}
	//or {ip-addr}/{TYPE}/{NUM}/
	int pos = URI.find_last_of("/");
	string idString;
	if (pos == URI.size() - 1){
		//Request is closed with '/'
		int pos2 = URI.find_last_not_of("/", pos - 1);
		if (pos2 == -1){
			//If there is no 2nd '/', no id
			return -1;
		}
		idString = URI.substr(pos2 + 1, pos2 - pos - 1);
	} else {
		idString = URI.substr(pos + 1, URI.size() - 1 - pos - 1);
	}
	const char * idToConvert = idString.c_str();
	int id = strtol(idToConvert, NULL, 10);
	if (id == 0){
		return -1;
	}
	return id;
}
