/*
 * RestAPI.cpp
 *
 *  Created on: 2014-02-27
 *      Author: be
 */

#include "RestAPI.h"
using namespace std;
RestAPI::RestAPI(int (*getFingerprintIdFunction)(bool enrollNow), OS_EVENT * databaseSem, OS_EVENT * solenoidSem, OS_EVENT * solenoidMutex):
	getFingerprintId(getFingerprintIdFunction),
	m_databaseSem(databaseSem),
	m_solenoidSem(solenoidSem),
	m_solenoidMutex(solenoidMutex),
	m_successString("{\"success\":true}")
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
string RestAPI::getRoleUsers(string URI){
	int id = extractID(URI);
	if (id != -1){
		return getRoleUsers(id);
	}
	Database db(m_databaseSem);
	return db.listAll(USER_ROLES);
}
string RestAPI::getRoleUsers(int rid){
	Database db(m_databaseSem);
	return db.findRoleUser(rid);
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
	return db.findUserPrintUID(uid);

}

string RestAPI::insertUser(string data){
	Database db(m_databaseSem);
	User user;
	user.loadFromJson(data);
	stringstream str;
	str << db.insertUser(user);
	return str.str();
}

string RestAPI::enableUser(string data){
	Database db(m_databaseSem);
	User user;
	user.loadFromJson(data);
	stringstream str;
	str << db.enableUser(user.id, true);
	return str.str();
}

string RestAPI::insertRole(string data){
	Database db(m_databaseSem);
	Role role;
	role.loadFromJson(data);
	stringstream str;
	str << db.insertRole(role);
	return str.str();
}

string RestAPI::insertUserRole(string data){
	Database db(m_databaseSem);
	UserRole userRole;
	userRole.loadFromJson(data);
	stringstream str;
	str << db.insertUserRole(userRole);
	return str.str();
}

string RestAPI::insertRoleSchedule(string data){
	Database db(m_databaseSem);
	RoleSchedule roleSchedule;
	roleSchedule.loadFromJson(data);
	stringstream str;
	str << db.insertRoleSched(roleSchedule);
	return str.str();
}

string RestAPI::insertPrint(string data){
	Database db(m_databaseSem);
	UserPrint print;
	print.loadFromJson(data);
	stringstream str;
	str << db.insertUserPrint(print);
	return str.str();
}

string RestAPI::updateUser(string data){
	Database db(m_databaseSem);
	User user;
	user.loadFromJson(data);
	stringstream str;
	str << db.editUser(user);
	return str.str();
}

string RestAPI::updateRole(string data){
	Database db(m_databaseSem);
	Role role;
	role.loadFromJson(data);
	stringstream str;
	str << db.editRole(role);
	return str.str();
}

string RestAPI::updateUserRole(string data){
	Database db(m_databaseSem);
	UserRole userRole;
	userRole.loadFromJson(data);
	stringstream str;
	str << db.editUserRole(userRole);
	return str.str();
}

string RestAPI::updateRoleSchedule(string data){
	Database db(m_databaseSem);
	RoleSchedule roleSchedule;
	roleSchedule.loadFromJson(data);
	stringstream str;
	str << db.editRoleSched(roleSchedule);
	return str.str();
}

string RestAPI::updatePrint(string data){
	Database db(m_databaseSem);
	UserPrint print;
	print.loadFromJson(data);
	stringstream str;
	str << db.editUserPrint(print);
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

string RestAPI::deleteUserRole(int id, int uid, int rid){
	Database db(m_databaseSem);
	stringstream result;
	result << db.deleteUserRole(id, uid, rid);
	return result.str();
}

string RestAPI::deleteRoleSchedule(int id, int rid){
	Database db(m_databaseSem);
	stringstream result;
	result << db.deleteRoleSchedule(id, rid);
	return result.str();
}

string RestAPI::deletePrint(int id, int uid){
	Database db(m_databaseSem);
	stringstream result;
	result << db.deleteUserPrint(id, uid);
	return result.str();
}

string RestAPI::unlockLock(){
	Solenoid::unlock(m_solenoidSem, m_solenoidMutex);
	return m_successString;
}

bool RestAPI::checkAdminPrint(){
	int fid = getFingerprintId(false);
	if (fid == -1){
		return fid;
	}
	Database db(m_databaseSem);
	UserPrint print;
	print.loadFromJson(db.findUserPrint(fid));
	if(print.id == -1){
		return print.id;
	}
	User user;
	user.loadFromJson(db.findUser(print.uid));
	//TODO: check start and end date
	if(user.id == -1 || !user.enabled){
		return user.id;
	}
	UserRole userRole;
	string uRole = db.findUserRole(user.id);
	if (uRole.find("[") == 0){
		userRole.loadFromJson(uRole.substr(1, uRole.size() - 1));
	} else {
		userRole.loadFromJson(uRole);
	}
	if(userRole.id == -1){
		return userRole.id;
	}
	Role role;
	role.loadFromJson(db.findRole(userRole.rid));
	if(role.id == -1 || !role.enabled){
		return role.id;
	}
	// Is admin
	if (role.admin == true){
		return 1;
	}
	// Not admin
	return 0;
}


string RestAPI::enroll1(){
	getFingerprintId(false);
	return m_successString;
}
string RestAPI::enroll2(){
	int fid = getFingerprintId(true);
	stringstream retString;
	retString << m_successString << ",{\"fid\":\"" << fid << "\"}";
	return retString.str();
}

int RestAPI::extractID(string URI){
	//At this point we have {ip-addr}/{TYPE}
	//or {ip-addr}/{TYPE}/
	//or {ip-addr}/{TYPE}/{NUM}
	//or {ip-addr}/{TYPE}/{NUM}/
	int pos = URI.find_last_of('/');
	string idString;
	if (pos == URI.size() - 1){
		//Request is closed with '/'
		int pos2 = URI.find_last_of('/', pos - 1);
		if (pos2 == -1){
			//If there is no 2nd '/', no id
			return -1;
		}
		idString = URI.substr(pos2 + 1, pos2 - pos - 1);
	} else {
		idString = URI.substr(pos + 1, URI.size() - 1 - pos);
	}
	const char * idToConvert = idString.c_str();
	int id = strtol(idToConvert, NULL, 10);
	if (id == 0){
		return -1;
	}
	return id;
}

bool RestAPI::setSystemTime(INT32U time){
	OSTimeSet(time * CLOCKS_PER_SEC);
	return true;
}

INT32U RestAPI::getSystemTime(){
	INT32U time = OSTimeGet();
	return time / CLOCKS_PER_SEC;
}
