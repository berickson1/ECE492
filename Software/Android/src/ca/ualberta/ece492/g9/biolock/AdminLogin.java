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
import android.widget.TextView;

/* From MainActivity or NewLock or NewUser
 * User have selected their device or registering new user
 */
public class AdminLogin extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	private static String className;
	private TextView printStatus;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_admin_login);
		printStatus = (TextView) findViewById(R.id.print_detected);
		Intent caller = getIntent();
		className = caller.getStringExtra("Caller");
		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");
	}
	
	// Requests for print & waits for response from web server
	public void onResume() {
		if (className.equals("MainActivity")){
			checkPrint();
		} else if (className.equals("NewLock")) {
			checkPrint();
		} else if (className.equals("NewUser")){
			addPrint();
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
							printStatus.setText("Fingerprint authorized");
							goNext();
						} else {
							printStatus.setText("Fingerprint unauthorized");
							goBack();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
		//checkUser.execute(ip.concat("/checkAdmin"));
	}
	
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
							printStatus.setText("Scan fingerprint again");
							JSONParser enrollUser2 = new JSONParser(new JSONCallbackFunction() {
								@Override
								public void execute(JSONArray json) {
									if (json != null) {
										try{
											JSONObject response = (JSONObject) json.get(0);
											if (response.getString("success").equalsIgnoreCase("true")){
												printStatus.setText("Fingerprint enrolled");
											} else {
												printStatus.setText("Enrollment failed");
											}
											goBack();
										} catch (JSONException e){
											e.printStackTrace();
										}
									}
								}
							});
							enrollUser2.execute(ip.concat("/enroll2"));
						} else {
							printStatus.setText("Enrollment failed");
							goBack();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
		//enrollUser1.execute(ip.concat("/enroll1"));
	}
	
	public void goNext(){
		int DELAY = 500;
		
		// Jumps to Manage screen after 1 second delay
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// Runs Manage
				Intent authorized = new Intent(AdminLogin.this, Manage.class);
				startActivity(authorized);

				// Close this activity
				finish();
			}
		}, DELAY);
	}
	
	public void goBack(){
		int DELAY = 500;
		
		// Jumps to Manage screen after 1 second delay
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// Goes back to caller screen
				finish();
			}
		}, DELAY);
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
