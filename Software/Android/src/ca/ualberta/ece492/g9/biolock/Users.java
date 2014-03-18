package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;

import org.json.JSONArray;

import ca.ualberta.ece492.g9.biolock.types.User;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

//From Manage - user wishes to manage the users
public class Users extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	private static Context mContext;
	
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_users);
		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");

		JSONParser parser = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null) {
					ListView userList = (ListView) findViewById(R.id.listUsers);
					ArrayList<User> usersArray = new ArrayList<User>();
					UserAdapter adapter = new UserAdapter(mContext, usersArray);
					User user = new User();
					usersArray = user.fromJson(json);
					adapter.addAll(usersArray);
					userList.setAdapter(adapter);
				}
			}
			public void execute(Integer response) {}
		});
		parser.execute(ip.concat("/users"));
	}

	// User selected to add new user
	public void addNewUser(View v) {
		Intent newUser = new Intent(Users.this, NewUser.class);
		startActivity(newUser);
		finish();
	}
}
