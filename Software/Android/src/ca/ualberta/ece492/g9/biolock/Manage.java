package ca.ualberta.ece492.g9.biolock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.ualberta.ece492.g9.biolock.customs.JSONCallbackFunction;
import ca.ualberta.ece492.g9.biolock.customs.JSONParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

// From AdminLogin - user authenticated & can modify settings
public class Manage extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	private static Context mContext;
	
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
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
	
	// Load and displays picture
	public void displayPicture(View v) {
		Intent showPicture = new Intent(Manage.this, Picture.class);
		startActivity(showPicture);
	}
	
	// Jumps to Log screen
	public void displayLog(View v) {
		Intent showLog = new Intent(Manage.this, Log.class);
		startActivity(showLog);
	}
	
	// Unlocks door lock
	public void unlockDoor(View v) {
		// Disable repeated button click
		final TextView unlockButton = (TextView) findViewById(R.id.unlock);
		unlockButton.setEnabled(false);
		
		// Post request to unlock door
		JSONParser unlock = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				// Popup displaying if lock was unlocked or not
				AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
				noConn.setTitle("Unlock");
				noConn.setCancelable(false);
				noConn.setCanceledOnTouchOutside(false);
				noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {}
	            });
				if (json != null) {
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							// Request succeeded
							noConn.setMessage("Lock was unlocked");
						} else {
							noConn.setMessage("Lock could not be unlocked");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				} else {
					// Request failed
					noConn.setMessage("Lock could not be unlocked");
				}
				noConn.show();
				unlockButton.setEnabled(true);
			}
		});
		unlock.execute(ip.concat("/unlock"));
	}
}
