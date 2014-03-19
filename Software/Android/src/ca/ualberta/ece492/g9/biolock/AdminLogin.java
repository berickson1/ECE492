// Set up communication with board - fingerprint communication

package ca.ualberta.ece492.g9.biolock;

import org.json.JSONArray;

import ca.ualberta.ece492.g9.biolock.customs.JSONCallbackFunction;
import ca.ualberta.ece492.g9.biolock.customs.JSONPost;

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
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_admin_login);
		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");
	}
	
	// Requests for print & waits for response from web server
	public void onStart(Bundle savedInstanceState) {
		JSONPost requestPrint = new JSONPost(new JSONCallbackFunction() {
			@Override
			public void execute(Integer response) {
				System.out.println(response);
			}
			public void execute(JSONArray json) {}
		});
		requestPrint.execute(ip.concat("/print"), ip.concat("/print"), "fingerprint");
	}
	
	// TODO: Display result of fingerprint detection once board replies with
	// result
	// TODO: Jumps to manage or newUser screen depending on status
	// TODO: Enable editText if new print

	// Temp button to simulate fingerprint detected & accepted
	public void tempPrintDetected(View v) {

		int DELAY = 1000;

		// Displays text stating print is found (or not found)
		TextView printStatus = (TextView) findViewById(R.id.print_detected);
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
