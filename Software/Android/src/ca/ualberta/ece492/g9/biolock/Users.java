package ca.ualberta.ece492.g9.biolock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

//From Manage - user wishes to manage the users
public class Users extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_users);
	}
	
	// User selected to add new user
	public void addNewUser(View v) {
		Intent newUser = new Intent(Users.this, NewUser.class);
		startActivity(newUser);
	}
}
