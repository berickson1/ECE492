/*
 * Database_Table_Types.h
 *
 *  Created on: 2014-03-03
 *      Author: be
 */

#ifndef DATABASE_TABLE_TYPES_H_
#define DATABASE_TABLE_TYPES_H_

#include "includes.h"
#include "json/writer.h"
#include "json/reader.h"
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
		reader.parse(jsonString, node, true);
		id = node["id"].asInt();
		uid = node["uid"].asInt();
		success = node["success"].asBool();
		time = node["time"].asDouble();
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
		id = node["id"].asInt();
		name = node["name"].asString();
		enabled = node["enabled"].asBool();
		startDate = node["startDate"].asDouble();
		endDate = node["endDate"].asDouble();
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
		uid = node["uid"].asInt();
		id = node["id"].asInt();
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
		id = node["id"].asInt();
		name = node["name"].asString();
		admin = node["admin"].asBool();
		enabled = node["enabled"].asBool();
		startDate = node["startDate"].asDouble();
		endDate = node["endDate"].asDouble();
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
		name = node["name"].asString();
		id = node["id"].asInt();
		uid = node["uid"].asInt();
		rid = node["rid"].asInt();
		startDate = node["startDate"].asDouble();
		endDate = node["endDate"].asDouble();
	}
	string name;
	int id, uid, rid;
	double startDate, endDate;
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
		reader.parse(jsonString, node, true);
		id = node["id"].asInt();
		rid = node["rid"].asInt();
		startTime = node["startTime"].asInt();
		endTime = node["endTime"].asInt();
		days = node["days"].asInt();
		startDate = node["startDate"].asDouble();
		endDate = node["endDate"].asDouble();
	}
	int id, rid, startTime, endTime, days;
	double startDate, endDate;
} RoleSchedule;

#endif /* DATABASE_TABLE_TYPES_H_ */
