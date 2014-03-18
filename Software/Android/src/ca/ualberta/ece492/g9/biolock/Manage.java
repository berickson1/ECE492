// TODO: Check if unlockDoor method is okay with JSONObject & handle response

package ca.ualberta.ece492.g9.biolock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

// From AdminLogin - user authenticated & can modify settings
public class Manage extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	String ip;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_manage);
		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");
	}
	
	// Jumps to Users screen
	public void displayUsers(View v) {
		Intent showUsers = new Intent(Manage.this, Users.class);
		startActivity(showUsers);
	}

	// Jumps to Roles screen
	public void displayRoles(View v) {
		Intent showRoles = new Intent(Manage.this, Roles.class);
		startActivity(showRoles);
	}
	
	// Jumps to Log screen
	public void displayLog(View v) {
		Intent showLog = new Intent(Manage.this, Log.class);
		startActivity(showLog);
	}
	
	// Unlocks door lock
	public void unlockDoor(View v) {
		// JSONObject to request unlock door
		JSONObject putRequest = new JSONObject();
		try {
			putRequest.put("request", "unlock");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Post request to unlock door
		JSONPost unlock = new JSONPost(new JSONCallbackFunction() {
			@Override
			public void execute(int response) {
				// Not a valid response
				if (response == -1){
					System.out.println("No response");
				}
			}
			public void execute(JSONArray json) {}
		});
		unlock.execute(ip.concat("/unlock"), ip.concat("/prints"), putRequest.toString());
	}
}
