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
	// TODO Auto-generated constructor stub

}

RestAPI::~RestAPI() {
	// TODO Auto-generated destructor stub
}


string RestAPI::getUsers(){
	Database db(m_databaseSem);
	db.listAll(USERS);
	return db.listAll(USERS);
}
string RestAPI::getUser(int uid){

}
string RestAPI::getRoles(){

}
string RestAPI::getRole(int rid){

}
string RestAPI::getUserRoles(){

}
string RestAPI::getUserRoles(int uid){

}
string RestAPI::getRoleUsers(int rid){

}
string RestAPI::getRoleSchedule(){

}
string RestAPI::getRoleSchedule(int rid){

}
string RestAPI::getHistory(){

}
string RestAPI::getPrints(){

}
string RestAPI::getPrint(int uid){

}
string RestAPI::scanPrint(){

}
