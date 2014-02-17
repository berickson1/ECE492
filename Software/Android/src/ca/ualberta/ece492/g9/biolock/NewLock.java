// Set up communication with board - check if valid ip

package ca.ualberta.ece492.g9.biolock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

// From MainActivity - user requests to add new lock
public class NewLock extends Activity {
	private static final int DELAY = 1000;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_new_lock);
	}

	// User has entered ip address for lock - will request for communication
	// with declared ip address
	public void searchLock(View v) {
		// TODO: Check if ip address is valid before displaying result -
		// detected or not detected

		// Displays text stating lock is found (or not found)
		TextView lockStatus = (TextView) findViewById(R.id.lock_detected);
		lockStatus.setVisibility(View.VISIBLE);

		// Jumps to AdminLogin screen after 1 second delay
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// Runs AdminLogin
				Intent i = new Intent(NewLock.this, AdminLogin.class);
				startActivity(i);

				// Close this activity
				finish();
			}
		}, DELAY);
	}
}
