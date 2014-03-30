/*
 * Database_Table_Types.h
 *
 *  Created on: 2014-03-03
 *      Author: be
 */

#ifndef DATABASE_TABLE_TYPES_H_
#define DATABASE_TABLE_TYPES_H_

#include "includes.h"
#include "json/json.h"
#include <sstream>
#include <list>
using namespace std;

typedef struct History {
	string toJSONString() {
		Json::Value nodeToInsert;
		nodeToInsert["id"] = id;
		nodeToInsert["uid"] = uid;
		nodeToInsert["success"] = success;
		nodeToInsert["time"] = time;

		string jsonValue = nodeToInsert.toStyledString();
		return jsonValue;
	}
	void loadFromJson(string jsonString) {
		Json::Reader reader;
		Json::Value node;
		reader.parse(jsonString, node, true);
		id = node.get("id", -1).asInt();
		uid = node.get("uid", -1).asInt();
		success = node.get("success", false).asBool();
		time = node.get("time", -1).asDouble();
	}
	int id, uid;
	bool success;
	double time;
} History;

typedef struct User {
	string toJSONString() {
		Json::Value nodeToInsert;
		nodeToInsert["id"] = id;
		nodeToInsert["name"] = name;
		nodeToInsert["enabled"] = enabled;
		ostringstream start, end;
		nodeToInsert["startDate"] = startDate;
		nodeToInsert["endDate"] = endDate;

		string jsonValue = nodeToInsert.toStyledString();
		return jsonValue;
	}
	void loadFromJson(string jsonString) {
		Json::Reader reader;
		Json::Value node;
		reader.parse(jsonString, node, true);
		id = node.get("id", -1).asInt();
		name = node.get("name", "Unknown").asString();
		enabled = node.get("enabled", false).asBool();
		startDate = node.get("startDate", -1).asDouble();
		endDate = node.get("endDate", -1).asDouble();
	}
	int id;
	string name;
	bool enabled;
	double startDate, endDate;
} User;

typedef struct UserPrint {
	string toJSONString() {
		Json::Value nodeToInsert;
		nodeToInsert["uid"] = uid;
		nodeToInsert["id"] = id;

		string jsonValue = nodeToInsert.toStyledString();
		return jsonValue;
	}
	void loadFromJson(string jsonString) {
		Json::Reader reader;
		Json::Value node;
		reader.parse(jsonString, node, true);
		uid = node.get("uid", -1).asInt();
		id = node.get("id", -1).asInt();
	}
	int id, uid;
} UserPrint;

typedef struct Role {
	string toJSONString() {
		Json::Value nodeToInsert;
		nodeToInsert["id"] = id;
		nodeToInsert["name"] = name;
		nodeToInsert["admin"] = admin;
		nodeToInsert["enabled"] = enabled;
		nodeToInsert["startDate"] = startDate;
		nodeToInsert["endDate"] = endDate;

		string jsonValue = nodeToInsert.toStyledString();
		return jsonValue;
	}
	void loadFromJson(string jsonString) {
		Json::Reader reader;
		Json::Value node;
		reader.parse(jsonString, node, true);
		id = node.get("id", -1).asInt();
		name = node.get("name", "Unknown").asString();
		admin = node.get("admin", false).asBool();
		enabled = node.get("enabled", "false").asBool();
		startDate = node.get("startDate", -1).asDouble();
		endDate = node.get("endDate", -1).asDouble();
	}
	int id;
	string name;
	bool admin, enabled;
	double startDate, endDate;
} Role;

typedef struct UserRole {
	string toJSONString() {
		Json::Value nodeToInsert;
		nodeToInsert["name"] = name;
		nodeToInsert["userName"] = userName;
		nodeToInsert["id"] = id;
		nodeToInsert["uid"] = uid;
		nodeToInsert["rid"] = rid;
		nodeToInsert["startDate"] = startDate;
		nodeToInsert["endDate"] = endDate;

		string jsonValue = nodeToInsert.toStyledString();
		return jsonValue;
	}
	void loadFromJson(string jsonString) {
		Json::Reader reader;
		Json::Value node;
		reader.parse(jsonString, node, true);
		name = node.get("name", "Unknown").asString();
		id = node.get("id", -1).asInt();
		uid = node.get("uid", -1).asInt();
		rid = node.get("rid", -1).asInt();
		startDate = node.get("startDate", -1).asDouble();
		endDate = node.get("endDate", -1).asDouble();
	}
	string name, userName;
	int id, uid, rid;
	double startDate, endDate;
} UserRole;

typedef struct UserRoles {
	string toJSONString() {
		string jsonValue = "[";
		for(list<UserRole>::iterator iter = roles.begin(); iter != roles.end(); iter++){
			jsonValue.append(iter->toJSONString());
			jsonValue.append(",");
		}
		//Replace final comma with close tag
		jsonValue.replace(jsonValue.size() - 2, 1, "]");
		return jsonValue;
	}
	void loadFromJson(string jsonString) {
		Json::Reader reader;
		Json::Value node;
		reader.parse(jsonString, node, true);
		for (int i = 0; i < node.size(); i++){
			UserRole userRole;
			userRole.loadFromJson(node[i].toStyledString());
			roles.push_front(userRole);
		}
	}
	list<UserRole> roles;
} UserRoles;

typedef struct RoleSchedule {
	string toJSONString() {
		Json::Value nodeToInsert;
		nodeToInsert["id"] = id;
		nodeToInsert["rid"] = rid;
		nodeToInsert["startTime"] = startTime;
		nodeToInsert["endTime"] = endTime;
		nodeToInsert["days"] = days;
		nodeToInsert["startDate"] = startDate;
		nodeToInsert["endDate"] = endDate;

		string jsonValue = nodeToInsert.toStyledString();
		return jsonValue;
	}
	void loadFromJson(string jsonString) {
		Json::Reader reader;
		Json::Value node;
		reader.parse(jsonString, node, true);
		id = node.get("id", -1).asInt();
		rid = node.get("rid", -1).asInt();
		startTime = node.get("startTime", -1).asInt();
		endTime = node.get("endTime", -1).asInt();
		days = node.get("days", -1).asInt();
		startDate = node.get("startDate", -1).asDouble();
		endDate = node.get("endDate", -1).asDouble();
	}
	int id, rid, startTime, endTime, days;
	double startDate, endDate;
} RoleSchedule;

typedef struct RoleSchedules {
	string toJSONString() {
		string jsonValue = "[";
		for(list<RoleSchedule>::iterator iter = schedules.begin(); iter != schedules.end(); iter++){
			jsonValue.append(iter->toJSONString());
			jsonValue.append(",");
		}
		//Replace final comma with close tag
		jsonValue.replace(jsonValue.size() - 2, 1, "]");
		return jsonValue;
	}
	void loadFromJson(string jsonString) {
		Json::Reader reader;
		Json::Value node;
		reader.parse(jsonString, node, true);
		for (int i = 0; i < node.size(); i++){
			RoleSchedule roleSchedule;
			roleSchedule.loadFromJson(node[i].toStyledString());
			schedules.push_front(roleSchedule);
		}
	}
	list<RoleSchedule> schedules;
} RoleSchedules;

#endif /* DATABASE_TABLE_TYPES_H_ */
