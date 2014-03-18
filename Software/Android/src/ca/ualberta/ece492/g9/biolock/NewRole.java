package ca.ualberta.ece492.g9.biolock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

//From AdminLogin - addition of new user
public class NewRole extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_new_role);
	}

	// Jumps to AdminLogin to add new print
	public void enrollPrint(View v) {
		Intent addPrint = new Intent(NewRole.this, AdminLogin.class);
		startActivity(addPrint);
	}

	// Confirms deletion of user
	public void confirmDelete(View v) {
		// CONFIRM DELETE!
		System.out.println("Delete role");
	}

	// TODO: Check updated values before updating users
	// Jumps to Users
	public void updateRole(View v) {
		// Runs Users
		Intent i = new Intent(NewRole.this, Roles.class);
		startActivity(i);

		// Close this activity
		finish();
	}
}
