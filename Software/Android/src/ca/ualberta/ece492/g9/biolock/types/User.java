package ca.ualberta.ece492.g9.biolock.types;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User{
	int id;
	public String name;
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
};