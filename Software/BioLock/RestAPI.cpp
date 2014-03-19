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

string RestAPI::insertUser(string URI, string data){
	Database db(m_databaseSem);
	User user;
	user.loadFromJson(data);
	stringstream str;
	str << db.insertUser(user);
	return str.str();
}

string RestAPI::insertRole(string URI, string data){
	Database db(m_databaseSem);
	Role role;
	role.loadFromJson(data);
	stringstream str;
	str << db.insertRole(role);
	return str.str();
}

string RestAPI::insertUserRole(string URI, string data){
	Database db(m_databaseSem);
	UserRole userRole;
	userRole.loadFromJson(data);
	stringstream str;
	str << db.insertUserRole(userRole);
	return str.str();
}

string RestAPI::insertRoleSchedule(string URI, string data){
	Database db(m_databaseSem);
	RoleSchedule roleSchedule;
	roleSchedule.loadFromJson(data);
	stringstream str;
	str << db.insertRoleSched(roleSchedule);
	return str.str();
}

string RestAPI::insertPrint(string URI, string data){
	Database db(m_databaseSem);
	UserPrint print;
	print.loadFromJson(data);
	stringstream str;
	str << db.insertUserPrint(print);
	return str.str();
}

string RestAPI::deleteUser(int id){
	Database db(m_databaseSem);
	stringstream result;
	result << db.enableUser(id, false);
	return result.str();
}

string RestAPI::deleteRole(int id){
	Database db(m_databaseSem);
	stringstream result;
	result << db.deleteRole(id);
	return result.str();
}

string RestAPI::deleteUserRole(int id){
	Database db(m_databaseSem);
	stringstream result;
	result << db.deleteUserRole(id);
	return result.str();
}

string RestAPI::deleteRoleSchedule(int id){
	Database db(m_databaseSem);
	stringstream result;
	result << db.deleteRoleSchedule(id);
	return result.str();
}

string RestAPI::deletePrint(int id){
	Database db(m_databaseSem);
	stringstream result;
	result << db.deleteUserPrint(id);
	return result.str();
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
