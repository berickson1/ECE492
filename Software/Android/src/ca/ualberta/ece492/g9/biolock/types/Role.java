package ca.ualberta.ece492.g9.biolock.types;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Role implements Parcelable{
	int id;
	String name;
	boolean admin;
	boolean enabled;
	Long startDate, endDate;
	
	public Role(){
		
	}
	
	public Role(JSONObject jsonObjects){
		try {
			id = jsonObjects.getInt("id");
			name = jsonObjects.getString("name");
			admin = jsonObjects.getBoolean("admin");
			enabled = jsonObjects.getBoolean("enabled");
			startDate = jsonObjects.getLong("startDate");
			endDate = jsonObjects.getLong("endDate");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
    public ArrayList<Role> fromJson(JSONArray roleArray) {
           ArrayList<Role> roles = new ArrayList<Role>();
           for (int i = 0; i < roleArray.length(); i++) {
               try {
            	   roles.add(new Role(roleArray.getJSONObject(i)));
               } catch (JSONException e) {
                  e.printStackTrace();
               }
          }
          return roles;
    }
    
    public JSONObject toJson(){
    	JSONObject jsonObj = new JSONObject();
    	try {
			jsonObj.put("id", id);
			jsonObj.put("name", name);
			jsonObj.put("admin", admin);
			jsonObj.put("enabled", enabled);
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
	
	public String getName(){
		return name;
	}
	
	public boolean getEnabled(){
		return enabled;
	}
	
	public boolean getAdmin(){
		return admin;
	}
	
	public Long getStartDate(){
		return startDate;
	}
	
	public Long getEndDate(){
		return endDate;
	}
	
	public int describeContents() {
		return 0;
	}

	// Read in order
	public Role(Parcel in){
		this.id = in.readInt();
		this.name = in.readString();
		// true if int == 1
		this.admin = (in.readInt() == 1);
		this.enabled = (in.readInt() == 1);
		this.startDate = in.readLong();
		this.endDate = in.readLong();
    }
	
	// Write in order
	public void writeToParcel(Parcel pc, int flags) {
		pc.writeInt(id);
		pc.writeString(name);
		// write 1 if true
		pc.writeInt(enabled ? 1:0);
		pc.writeInt(enabled ? 1:0);
		pc.writeLong(startDate);
		pc.writeLong(endDate);
	}
	
	public static final Parcelable.Creator<Role> CREATOR = new Parcelable.Creator<Role>() {
		public Role createFromParcel(Parcel pc) {
			return new Role(pc);
		}
		public Role[] newArray(int size) {
			return new Role[size];
		}
	};
};
