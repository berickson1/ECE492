package ca.ualberta.ece492.g9.biolock.types;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class UserPrint implements Parcelable{
	int uid, id;
	
	public UserPrint(){
		
	}
	
	public UserPrint(JSONObject jsonObjects){
		try {
			id = jsonObjects.getInt("id");
			uid = jsonObjects.getInt("uid");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
    public ArrayList<UserPrint> fromJson(JSONArray printArray) {
           ArrayList<UserPrint> prints = new ArrayList<UserPrint>();
           for (int i = 0; i < printArray.length(); i++) {
               try {
            	   prints.add(new UserPrint(printArray.getJSONObject(i)));
               } catch (JSONException e) {
                  e.printStackTrace();
               }
          }
          return prints;
    }
    
    public JSONObject toJson(){
    	JSONObject jsonObj = new JSONObject();
    	try {
			jsonObj.put("id", id);
			jsonObj.put("uid", uid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return jsonObj;
    }
    
    public int getUID(){
		return uid;
	}
    
    public void setUID(int uid){
    	this.uid = uid;
    }
	
	public int getID(){
		return id;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public int describeContents() {
		return 0;
	}
	
	// Read in order
	public UserPrint(Parcel in){
		this.id = in.readInt();
		this.uid = in.readInt();
	}
		
	// Write in order
	public void writeToParcel(Parcel pc, int flags) {
		pc.writeInt(id);
		pc.writeInt(uid);
	}
		
	public static final Parcelable.Creator<UserPrint> CREATOR = new Parcelable.Creator<UserPrint>() {
		public UserPrint createFromParcel(Parcel pc) {
			return new UserPrint(pc);
		}
		public UserPrint[] newArray(int size) {
			return new UserPrint[size];
		}
	};
};
