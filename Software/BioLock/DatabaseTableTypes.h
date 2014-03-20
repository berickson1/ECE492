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
		ostringstream timeStr;
		timeStr << static_cast<long int>(time);
		nodeToInsert["time"] = timeStr.str();

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
		time = strtol(node["time"].asString().c_str(), NULL, 10);
	}
	int id, uid;
	bool success;
	time_t time;
} History;

typedef struct User {
	string toJSONString() {
		Json::Value nodeToInsert;
		nodeToInsert["id"] = id;
		nodeToInsert["name"] = name;
		nodeToInsert["enabled"] = enabled;
		ostringstream start, end;
		start << static_cast<long int>(startDate);
		end << static_cast<long int>(endDate);
		nodeToInsert["startDate"] = start.str();
		nodeToInsert["endDate"] = end.str();

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
		startDate = strtol(node["startDate"].asString().c_str(), NULL, 10);
		endDate = strtol(node["endDate"].asString().c_str(), NULL, 10);
	}
	int id;
	string name;
	bool enabled;
	time_t startDate, endDate;
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
		ostringstream start, end;
		start << static_cast<long int>(startDate);
		end << static_cast<long int>(endDate);
		nodeToInsert["startDate"] = start.str();
		nodeToInsert["endDate"] = end.str();

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
		startDate = strtol(node["startDate"].asString().c_str(), NULL, 10);
		endDate = strtol(node["endDate"].asString().c_str(), NULL, 10);
	}
	int id;
	string name;
	bool admin, enabled;
	time_t startDate, endDate;
} Role;

typedef struct UserRole {
	string toJSONString() {
		Json::Value nodeToInsert;
		nodeToInsert["id"] = id;
		nodeToInsert["uid"] = uid;
		nodeToInsert["rid"] = rid;
		ostringstream start, end;
		start << static_cast<long int>(startDate);
		end << static_cast<long int>(endDate);
		nodeToInsert["startDate"] = start.str();
		nodeToInsert["endDate"] = end.str();

		string jsonValue = nodeToInsert.toStyledString();
		return jsonValue;
	}
	void loadFromJson(string jsonString) {
		Json::Reader reader;
		Json::Value node;
		reader.parse(jsonString, node, true);
		id = node["id"].asInt();
		uid = node["uid"].asInt();
		rid = node["rid"].asInt();
		startDate = strtol(node["startDate"].asString().c_str(), NULL, 10);
		endDate = strtol(node["endDate"].asString().c_str(), NULL, 10);
	}
	int id, uid, rid;
	time_t startDate, endDate;
} UserRole;

typedef struct RoleSchedule {
	string toJSONString() {
		Json::Value nodeToInsert;
		nodeToInsert["id"] = id;
		nodeToInsert["rid"] = rid;
		nodeToInsert["startTime"] = startTime;
		nodeToInsert["endTime"] = endTime;
		nodeToInsert["days"] = days;
		ostringstream start, end;
		start << static_cast<long int>(startDate);
		end << static_cast<long int>(endDate);
		nodeToInsert["startDate"] = start.str();
		nodeToInsert["endDate"] = end.str();

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
		startDate = strtol(node["startDate"].asString().c_str(), NULL, 10);
		endDate = strtol(node["endDate"].asString().c_str(), NULL, 10);
	}
	int id, rid, startTime, endTime, days;
	time_t startDate, endDate;
} RoleSchedule;

#endif /* DATABASE_TABLE_TYPES_H_ */
