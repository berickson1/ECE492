package ca.ualberta.ece492.g9.biolock.types;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class UserRole{
	int id, uid, rid;
	Long startDate, endDate;
	
	public UserRole(){
		
	}
	
	public UserRole(JSONObject jsonObjects){
		try {
			id = jsonObjects.getInt("id");
			uid = jsonObjects.getInt("uid");
			rid = jsonObjects.getInt("rid");
			startDate = jsonObjects.getLong("startDate");
			endDate = jsonObjects.getLong("endDate");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<UserRole> fromJson(JSONArray rolesArray) {
        ArrayList<UserRole> userRoles = new ArrayList<UserRole>();
        for (int i = 0; i < rolesArray.length(); i++) {
            try {
            	userRoles.add(new UserRole(rolesArray.getJSONObject(i)));
            } catch (JSONException e) {
               e.printStackTrace();
            }
       }
       return userRoles;
	}
	
	public JSONObject toJson(){
    	JSONObject jsonObj = new JSONObject();
    	try {
			jsonObj.put("id", id);
			jsonObj.put("uid", uid);
			jsonObj.put("rid", rid);
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
	
	public int getUID(){
		return uid;
	}
	
	public int getRID(){
		return rid;
	}
	
	public long getStartDate(){
		return startDate;
	}
	
	public long getEndDate(){
		return endDate;
	}
	
	public int describeContents() {
		return 0;
	}
	
	// Read in order
	public UserRole(Parcel in){
		this.id = in.readInt();
		this.uid = in.readInt();
		this.rid = in.readInt();
		this.startDate = in.readLong();
		this.endDate = in.readLong();
	}
		
	// Write in order
	public void writeToParcel(Parcel pc, int flags) {
		pc.writeInt(id);
		pc.writeInt(uid);
		pc.writeInt(rid);
		pc.writeLong(startDate);
		pc.writeLong(endDate);
	}
		
	public static final Parcelable.Creator<UserRole> CREATOR = new Parcelable.Creator<UserRole>() {
		public UserRole createFromParcel(Parcel pc) {
			return new UserRole(pc);
		}
		public UserRole[] newArray(int size) {
			return new UserRole[size];
		}
	};
};
