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
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/* From MainActivity or NewLock or NewUser
 * User have selected their device or registering new user
 */
public class AdminLogin extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static Context mContext;
	private static String ip;
	private TextView printStatus;
	
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_admin_login);
		printStatus = (TextView) findViewById(R.id.print_detected);
		
		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");
	}
	
	// Requests for print & waits for response from web server
	public void onResume() {
		Intent caller = getIntent();
		String className = caller.getStringExtra("Caller");
		
		if (className.equals("MainActivity")){
			checkPrint();
		} else if (className.equals("NewLock")) {
			checkPrint();
		} else if (className.equals("UpdateUser")){
			addPrint();
		}
		super.onStart();
	}
	
	public void checkPrint(){
		printStatus.setText("Checking fingerprint");
		printStatus.setVisibility(View.VISIBLE);
		JSONParser checkUser = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					try{
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							AlertDialog auth  = new AlertDialog.Builder(mContext).create();
							auth.setMessage("Fingerprint Authorized");
							auth.setTitle("Fingerprint");
							auth.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									// Go to manage
									Intent authorized = new Intent(AdminLogin.this, Manage.class);
									startActivity(authorized);
									// Close this activity
									finish();
								}
						    });
							auth.setCancelable(false);
							auth.setCanceledOnTouchOutside(false);
							auth.show();
						} else {
							displayFail();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					displayFail();
				}
			}
		});
		//checkUser.execute(ip.concat("/checkAdmin"));
	}
	
	// Scan fingerprint for the first time
	public void addPrint(){
		printStatus.setText("Scan fingerprint");
		printStatus.setVisibility(View.VISIBLE);
		JSONParser enrollUser1 = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					try{
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							requestPrintAgain();
						} else {
							displayFail();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else{
					displayFail();
				}
			}
		});
		//enrollUser1.execute(ip.concat("/enroll1"));
	}
	
	// Requests user to scan print again for adding
	public void requestPrintAgain(){
		AlertDialog scanAgain  = new AlertDialog.Builder(mContext).create();
		scanAgain.setMessage("Scan fingerprint again");
		scanAgain.setTitle("Fingerprint");
		scanAgain.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				addPrintAgain();
			}
	    });
		scanAgain.setCancelable(false);
		scanAgain.setCanceledOnTouchOutside(false);
		scanAgain.show();
	}
	
	// Scan fingerprint for the second time
	public void addPrintAgain(){
		JSONParser enrollUser2 = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null) {
					try{
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							final JSONObject id = (JSONObject) json.get(1);
							AlertDialog added  = new AlertDialog.Builder(mContext).create();
							added.setMessage("Fingerprint Added");
							added.setTitle("Fingerprint");
							added.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									try {
										Intent addedPrint = new Intent();
										addedPrint.putExtra("id", id.getInt("id"));
									} catch (JSONException e) {
										e.printStackTrace();
									}
									finish();
								}
						    });
							added.setCancelable(false);
							added.setCanceledOnTouchOutside(false);
							added.show();
						} else {
							displayFail();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
				} else {
					displayFail();
				}
			}
		});
		enrollUser2.execute(ip.concat("/enroll2"));	
	}
	
	// Displays message for failure
	public void displayFail(){
		AlertDialog fail  = new AlertDialog.Builder(mContext).create();
		fail.setMessage("Fingerprint sensor could not detect print");
		fail.setTitle("Fingerprint");
		fail.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Go back to caller screen
				finish();
			}
	    });
		fail.setCancelable(false);
		fail.setCanceledOnTouchOutside(false);
		fail.show();
	}
	
	// Temp button to simulate fingerprint detected & accepted
	public void tempPrintDetected(View v) {

		int DELAY = 1000;

		// Displays text stating print is found (or not found)
		printStatus = (TextView) findViewById(R.id.print_detected);
		printStatus.setVisibility(View.VISIBLE);

		// Jumps to Manage screen after 1 second delay
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// Runs Manage
				Intent i = new Intent(AdminLogin.this, Manage.class);
				startActivity(i);

				// Close this activity
				finish();
			}
		}, DELAY);
	}

	// Temp button to simulate fingerprint detected & accepted
	public void tempNewUser(View v) {

		Intent i = new Intent(AdminLogin.this, NewUser.class);
		startActivity(i);
		
		// Close this activity
		finish();

	}
}
