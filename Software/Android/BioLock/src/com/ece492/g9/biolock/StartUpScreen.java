package com.ece492.g9.biolock;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;

public class StartUpScreen extends Activity {

	private static final int SPLASHTIME = 2500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set as full screen with no title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_start_up_screen);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// Splash screen starts
				Intent i = new Intent(StartUpScreen.this, DeviceList.class);
				startActivity(i);

				// Closes splash screen
				finish();
			}
		}, SPLASHTIME);
	}

}
