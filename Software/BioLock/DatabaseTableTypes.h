/*
 * Database_Table_Types.h
 *
 *  Created on: 2014-03-03
 *      Author: be
 */

#ifndef DATABASE_TABLE_TYPES_H_
#define DATABASE_TABLE_TYPES_H_

#include "json/json.h"
#include <sstream>
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
		reader.parse(jsonString, node, false);
		id = node["id"].asInt();
		uid = node["uid"].asInt();
		success = node["success"].asBool();
		time = node["time"].asString();
	}
	int id, uid;
	bool success;
	string time;
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
	void loadFromJson(string jsonString1) {
		{
			Json::Reader reader = Json::Reader();
			Json::Value node;
			reader.parse(jsonString1, node, false);
			id = node["id"].asInt();
			name = node["name"].asString();
			enabled = node["enabled"].asBool();
			startDate = node["startDate"].asString();
			endDate = node["endDate"].asString();
		}
	}
	int id;
	string name, startDate, endDate;
	bool enabled;
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
		{
			Json::Reader reader2 = Json::Reader();
			Json::Value node2;
			reader2.parse(jsonString, node2, false);
			uid = node2["uid"].asInt();
			id = node2["id"].asInt();
		}
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
		reader.parse(jsonString, node, false);
		id = node["id"].asInt();
		name = node["name"].asString();
		admin = node["admin"].asBool();
		enabled = node["enabled"].asBool();
		startDate = node["startDate"].asString();
		endDate = node["endDate"].asString();
	}
	int id;
	string name, startDate, endDate;
	bool admin, enabled;
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
		reader.parse(jsonString, node, false);
		name = node["name"].asString();
		id = node["id"].asInt();
		uid = node["uid"].asInt();
		rid = node["rid"].asInt();
		startDate = node["startDate"].asString();
		endDate = node["endDate"].asString();
	}
	string name, userName, startDate, endDate;
	int id, uid, rid;
} UserRole;

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
		reader.parse(jsonString, node, false);
		id = node["id"].asInt();
		rid = node["rid"].asInt();
		startTime = node["startTime"].asString();
		endTime = node["endTime"].asString();
		days = node["days"].asInt();
		startDate = node["startDate"].asString();
		endDate = node["endDate"].asString();
	}
	int id, rid, days;
	string startDate, endDate, startTime, endTime;
} RoleSchedule;

#endif /* DATABASE_TABLE_TYPES_H_ */
