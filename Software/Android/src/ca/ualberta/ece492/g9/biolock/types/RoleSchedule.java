package ca.ualberta.ece492.g9.biolock.types;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class RoleSchedule implements Parcelable{
	int id, rid, startTime, endTime, days;
	Long startDate, endDate;
	
	public RoleSchedule(){
		
	}
	
	public RoleSchedule(JSONObject jsonObjects){
		try {
			id = jsonObjects.getInt("id");
			rid = jsonObjects.getInt("rid");
			startTime = jsonObjects.getInt("startTime");
			endTime = jsonObjects.getInt("endTime");
			days = jsonObjects.getInt("days");
			startDate = jsonObjects.getLong("startDate");
			endDate = jsonObjects.getLong("endDate");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<RoleSchedule> fromJson(JSONArray schedArray) {
        ArrayList<RoleSchedule> rolesSched = new ArrayList<RoleSchedule>();
        for (int i = 0; i < schedArray.length(); i++) {
            try {
            	rolesSched.add(new RoleSchedule(schedArray.getJSONObject(i)));
            } catch (JSONException e) {
               e.printStackTrace();
            }
       }
       return rolesSched;
	}
	
	public JSONObject toJson(){
    	JSONObject jsonObj = new JSONObject();
    	try {
			jsonObj.put("id", id);
			jsonObj.put("rid", rid);
			jsonObj.put("startTime", startTime);
			jsonObj.put("endTime", endTime);
			jsonObj.put("days", days);
			jsonObj.put("startDate", startDate);
			jsonObj.put("endDate", endDate);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return jsonObj;
    }
	
	public int getID(){
		return id;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public int getRID(){
		return rid;
	}
	
	public void setRID(int rid){
		this.rid = rid;
	}
	
	public int getStartTime(){
		return startTime;
	}
	
	public void setStartTime(int startTime){
		this.startTime = startTime;
	}
	
	public int getEndTime(){
		return endTime;
	}
	
	public void setEndTime(int endTime){
		this.endTime = endTime;
	}
	
	public int getDays(){
		return days;
	}
	
	public void setDays(int days){
		this.days = days;
	}
	
	public Long getStartDate(){
		return startDate;
	}
	
	public void setStartDate(Long startDate){
		this.startDate = startDate;
	}
	
	public Long getEndDate(){
		return endDate;
	}
	
	public void setEndDate(Long endDate){
		this.endDate = endDate;
	}
	
	public int describeContents() {
		return 0;
	}
	
	// Read in order
	public RoleSchedule(Parcel in){
		this.id = in.readInt();
		this.rid = in.readInt();
		this.startTime = in.readInt();
		this.endTime = in.readInt();
		this.days = in.readInt();
		this.startDate = in.readLong();
		this.endDate = in.readLong();
	}
		
	// Write in order
	public void writeToParcel(Parcel pc, int flags) {
		pc.writeInt(id);
		pc.writeInt(rid);
		pc.writeInt(startTime);
		pc.writeInt(endTime);
		pc.writeInt(days);
		pc.writeLong(startDate);
		pc.writeLong(endDate);
	}
		
	public static final Parcelable.Creator<RoleSchedule> CREATOR = new Parcelable.Creator<RoleSchedule>() {
		public RoleSchedule createFromParcel(Parcel pc) {
			return new RoleSchedule(pc);
		}
		public RoleSchedule[] newArray(int size) {
			return new RoleSchedule[size];
		}
	};
};
