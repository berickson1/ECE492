package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

//From Manage - user wishes to manage the users
public class Users extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_users);

		JSONParser parser = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null) {
					// String[] userID = new String[json.length()];
					String[] name = new String[json.length()];
					// String[] enabled = new String[json.length()];
					// String[] startDate = new String[json.length()];
					// String[] endDate = new String[json.length()];
					ListView userList;
					ListAdapter displayUser;

					userList = (ListView) findViewById(R.id.listUsers);
					ArrayList<HashMap<String, String>> users = new ArrayList<HashMap<String, String>>();
					for (int i = 0; i < json.length(); i++) {
						try {
							JSONObject user = (JSONObject) json.get(i);
							// userID[i] = user.getString("id");
							HashMap<String, String> usersHash = new HashMap<String, String>();
							usersHash.put("Name", user.getString("name"));
							users.add(usersHash);
							/*
							 * enabled[i] = user.getString("enabled");
							 * startDate[i] = user.getString("startDate");
							 * endDate[i] = user.getString("endDate");
							 */
							displayUser = new SimpleAdapter(getBaseContext(),
									users, R.layout.list_view_row,
									new String[] { "Name" },
									new int[] { R.id.listEntryName });
							userList.setAdapter(displayUser);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		// parser.execute("http://berickson.ca/ECE492/users");
		parser.execute("http://192.168.1.120/users");
	}

	// User selected to add new user
	public void addNewUser(View v) {
		Intent newUser = new Intent(Users.this, NewUser.class);
		startActivity(newUser);
	}
}
