package ca.ualberta.ece492.g9.biolock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.ualberta.ece492.g9.biolock.customs.DatabaseHandler;
import ca.ualberta.ece492.g9.biolock.customs.JSONCallbackFunction;
import ca.ualberta.ece492.g9.biolock.customs.JSONParser;
import ca.ualberta.ece492.g9.biolock.types.LockInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
	private static Context mContext;
	private static TextView searchButton;
	private TextView lockStatus;
	
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_new_lock);
	}

	// User attempted connection
	public void searchLock(View v) {
		lockStatus = (TextView) findViewById(R.id.lock_detected);
		// Disable repeated button click
		searchButton = (TextView) findViewById(R.id.detect_lock);
		searchButton.setEnabled(false);
		// Check if ip address is already in database
		EditText ipName = (EditText) findViewById(R.id.ipName);
		EditText ipInput = (EditText) findViewById(R.id.ip_address);
		final String name = ipName.getText().toString();
		final String ip = "http://".concat(ipInput.getText().toString());
		DatabaseHandler db = new DatabaseHandler(mContext);
		if (db.getLock(ip) != null){
			// Lock already in device
			lockStatus.setVisibility(View.INVISIBLE);
			AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
			noConn.setMessage("Lock already exists");
			noConn.setTitle("New Lock");
			noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });
			noConn.setCancelable(false);
			noConn.setCanceledOnTouchOutside(false);
			noConn.show();
			searchButton.setEnabled(true);
			return;
		}
		// Store ip in preferences for access in other intents
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
	    editor.putString("ipAddress", ip);
	    editor.commit();
	    
	    // Attempt communicate with declared ip address 
		JSONParser parser = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("alive").equalsIgnoreCase("true")){
							DatabaseHandler db = new DatabaseHandler(mContext);
							if (db.addLock(new LockInfo(ip, name)) != -1){
								// Displays text stating lock is found
								lockStatus.setText("Lock detected");
								lockStatus.setVisibility(View.VISIBLE);
								// Jumps to AdminLogin screen after 1 second delay
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										// Runs AdminLogin
										Intent login = new Intent(NewLock.this, AdminLogin.class);
										login.putExtra("Caller", mContext.getClass().getSimpleName());
										searchButton.setEnabled(true);
										startActivity(login);
										// Close this activity
										finish();
									}
								}, DELAY);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					// Displays text stating lock not found
					lockStatus.setText("Lock not detected");
					lockStatus.setVisibility(View.VISIBLE);
					searchButton.setEnabled(true);
				}
			}
		});
		parser.execute(ip.concat("/alive"));
	}
}
