/*
 * RestAPI.cpp
 *
 *  Created on: 2014-02-27
 *      Author: be
 */

#include "RestAPI.h"
using namespace std;
RestAPI::RestAPI() {
	// TODO Auto-generated constructor stub

}

RestAPI::~RestAPI() {
	// TODO Auto-generated destructor stub
}


string RestAPI::getUsers(){
	Json::Value rootNode;
	rootNode["1"]["name"] = "Brent";
	rootNode["2"]["name"] = "Mavis";
	rootNode["3"]["name"] = "Sydney";
	return rootNode.toStyledString();
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
