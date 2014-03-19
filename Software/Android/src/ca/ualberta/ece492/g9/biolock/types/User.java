//Parcelable: http://mobile.dzone.com/articles/using-android-parcel

package ca.ualberta.ece492.g9.biolock.types;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
	int id;
	String name;
	boolean enabled;
	Long startDate, endDate;
	
	public User(){
		
	}
	
	public User(JSONObject jsonObjects){
		try {
			id = jsonObjects.getInt("id");
			name = jsonObjects.getString("name");
			enabled = jsonObjects.getBoolean("enabled");
			startDate = jsonObjects.getLong("startDate");
			endDate = jsonObjects.getLong("endDate");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
    public ArrayList<User> fromJson(JSONArray userArray) {
           ArrayList<User> users = new ArrayList<User>();
           for (int i = 0; i < userArray.length(); i++) {
               try {
                  users.add(new User(userArray.getJSONObject(i)));
               } catch (JSONException e) {
                  e.printStackTrace();
               }
          }
          return users;
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
	public User(Parcel in){
		this.id = in.readInt();
		this.name = in.readString();
		// true if int == 1
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
		pc.writeLong(startDate);
		pc.writeLong(endDate);
	}
	
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel pc) {
			return new User(pc);
		}
		public User[] newArray(int size) {
			return new User[size];
		}
	};
};