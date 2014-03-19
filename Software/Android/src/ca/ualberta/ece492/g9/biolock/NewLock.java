package ca.ualberta.ece492.g9.biolock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.ualberta.ece492.g9.biolock.customs.JSONCallbackFunction;
import ca.ualberta.ece492.g9.biolock.customs.JSONParser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

// From MainActivity - user requests to add new lock
public class NewLock extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static final int DELAY = 1000;
	TextView searchButton;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_new_lock);
	}

	// User attempted connection
	public void searchLock(View v) {
		// Disable repeated button click
		searchButton = (TextView) findViewById(R.id.detect_lock);
		searchButton.setEnabled(false);
		// Store ip in preferences for access in other intents
		EditText ipInput = (EditText) findViewById(R.id.ip_address);
		String ip = ipInput.getText().toString();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
	    editor.putString("ipAddress", ip);
	    editor.commit();
	    
	    // Attempt communicate with declared ip address 
		JSONParser parser = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				TextView lockStatus = (TextView) findViewById(R.id.lock_detected);
				if (json != null){
					try {
						JSONObject response = (JSONObject) json.get(0);
// TODO: Need to change this to alive
						if (response.getString("enabled").equalsIgnoreCase("true")){
							// Displays text stating lock is found
							lockStatus.setText("Lock detected");
							lockStatus.setVisibility(View.VISIBLE);
							// Jumps to AdminLogin screen after 1 second delay
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									// Runs AdminLogin
									Intent login = new Intent(NewLock.this, AdminLogin.class);
									searchButton.setEnabled(true);
									startActivity(login);
									// Close this activity
									finish();
								}
							}, DELAY);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					lockStatus.setText("Lock not detected");
					lockStatus.setVisibility(View.VISIBLE);
					searchButton.setEnabled(true);
				}
			}
			public void execute(Integer response) {}
		});
// TODO: Change to alive
		parser.execute(ip.concat("/users"));
	}
}
