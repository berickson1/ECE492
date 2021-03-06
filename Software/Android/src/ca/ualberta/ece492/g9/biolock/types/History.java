package ca.ualberta.ece492.g9.biolock.types;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class History implements Parcelable{
	int id, uid;
	boolean success;
	Long time;
		
	public History(){
		
	}
	
	public History(JSONObject jsonObjects){
		try {
			id = jsonObjects.getInt("id");
			uid = jsonObjects.getInt("uid");
			success = jsonObjects.getBoolean("success");
			time = jsonObjects.getLong("time");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<History> fromJson(JSONArray historyArray) {
        ArrayList<History> history = new ArrayList<History>();
        for (int i = 0; i < historyArray.length(); i++) {
            try {
            	history.add(new History(historyArray.getJSONObject(i)));
            } catch (JSONException e) {
               e.printStackTrace();
            }
       }
       return history;
	}	
	
	public JSONObject toJson(){
    	JSONObject jsonObj = new JSONObject();
    	try {
			jsonObj.put("id", id);
			jsonObj.put("uid", uid);
			jsonObj.put("success", success);
			jsonObj.put("time", time);
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
	
	public int getUID(){
		return uid;
	}
	
	public void setUID(int uid){
		this.uid = uid;
	}
	
	public boolean getSuccess(){
		return success;
	}
	
	public void setSuccess(boolean success){
		this.success = success;
	}
	
	public Long getTime(){
		return time;
	}
	
	public void setTime(Long time){
		this.time = time;
	}
	
	public int describeContents() {
		return 0;
	}
	
	// Read in order
	public History(Parcel in){
		this.id = in.readInt();
		this.uid = in.readInt();
		// true if int == 1
		this.success = (in.readInt() == 1);
		this.time = in.readLong();
	}
		
	// Write in order
	public void writeToParcel(Parcel pc, int flags) {
		pc.writeInt(id);
		pc.writeInt(uid);
		// write 1 if true
		pc.writeInt(success ? 1:0);
		pc.writeLong(time);
	}
		
	public static final Parcelable.Creator<History> CREATOR = new Parcelable.Creator<History>() {
		public History createFromParcel(Parcel pc) {
			return new History(pc);
		}
		public History[] newArray(int size) {
			return new History[size];
		}
	};
}