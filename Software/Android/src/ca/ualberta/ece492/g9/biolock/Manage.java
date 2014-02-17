package ca.ualberta.ece492.g9.biolock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

// From AdminLogin - user authenticated & can modify settings
public class Manage extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_manage);
	}

	public void displayUsers(View v) {
		Intent showUsers = new Intent(Manage.this, Users.class);
		startActivity(showUsers);
	}

	public void displayRoles(View v) {
		System.out.println("Display roles");
	}
	
	public void displayLog(View v) {
		System.out.println("Display log");
	}
	
	public void unlockDoor(View v) {
		System.out.println("Unlock door");
	}
}
