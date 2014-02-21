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
	
	// Jumps to Log screen
	public void displayLog(View v) {
		Intent showLog = new Intent(Manage.this, Log.class);
		startActivity(showLog);
	}
	
	// Unlocks door lock
	// TODO: Implement unlocking
	public void unlockDoor(View v) {
		System.out.println("Unlock door");
	}
}
