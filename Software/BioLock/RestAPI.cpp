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


string RestAPI::getUsers(){
	Database db(m_databaseSem);
	return db.listAll(USERS);
}
string RestAPI::getUser(int uid){
	Database db(m_databaseSem);
	return db.findUser(uid);
}
string RestAPI::getRoles(){
	Database db(m_databaseSem);
	return db.listAll(ROLES);

}
string RestAPI::getRole(int rid){
	Database db(m_databaseSem);
	return db.findRole(rid);

}
string RestAPI::getUserRoles(){
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
string RestAPI::getRoleSchedule(){
	Database db(m_databaseSem);
	return db.listAll(ROLE_SCHEDULE);

}
string RestAPI::getRoleSchedule(int rid){
	Database db(m_databaseSem);
	return db.findRoleSchedule(rid);
}
string RestAPI::getHistory(){
	Database db(m_databaseSem);
	return db.listAll(HISTORY);

}
string RestAPI::getPrints(){
	Database db(m_databaseSem);
	return db.listAll(USER_PRINTS);

}
string RestAPI::getPrint(int uid){
	Database db(m_databaseSem);
	return db.findUserPrint(uid);

}
string RestAPI::scanPrint(){

}
