package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

//From Manage - user wishes to manage the roles
public class Roles extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_roles);

		JSONParser parser = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null) {
					// String[] userID = new String[json.length()];
					String[] name = new String[json.length()];
					// String[] enabled = new String[json.length()];
					// String[] startDate = new String[json.length()];
					// String[] endDate = new String[json.length()];
					ListView roleList;
					ListAdapter displayRole;

					roleList = (ListView) findViewById(R.id.listRoles);
					ArrayList<HashMap<String, String>> roles = new ArrayList<HashMap<String, String>>();
					for (int i = 0; i < json.length(); i++) {
						try {
							JSONObject role = (JSONObject) json.get(i);
							// userID[i] = user.getString("id");
							HashMap<String, String> rolesHash = new HashMap<String, String>();
							rolesHash.put("Name", role.getString("name"));
							roles.add(rolesHash);
							/*
							 * enabled[i] = user.getString("enabled");
							 * startDate[i] = user.getString("startDate");
							 * endDate[i] = user.getString("endDate");
							 */
							displayRole = new SimpleAdapter(getBaseContext(),
									roles, R.layout.list_view_row,
									new String[] { "Name" },
									new int[] { R.id.listEntryName });
							roleList.setAdapter(displayRole);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
			public void execute(Integer response) {}
		});
		//parser.execute("http://berickson.ca/ECE492/roles");
		parser.execute("http://192.168.1.120/roles");
	}

	// User selected to add new role
	public void addNewRole(View v) {
		Intent newRole = new Intent(Roles.this, NewRole.class);
		startActivity(newRole);
	}
}
