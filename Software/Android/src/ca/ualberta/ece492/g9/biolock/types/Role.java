package ca.ualberta.ece492.g9.biolock.types;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Role{
	int id;
	public String name;
	boolean admin;
	public boolean enabled;
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
};
