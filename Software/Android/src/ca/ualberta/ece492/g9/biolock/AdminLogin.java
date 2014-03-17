// Set up communication with board - fingerprint communication

package ca.ualberta.ece492.g9.biolock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

//From MainActivity - user has selected their device
public class AdminLogin extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		Bundle mainActivity = getIntent().getExtras();
		String ip = mainActivity.getString("IP");
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_admin_login);
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

	}
}
