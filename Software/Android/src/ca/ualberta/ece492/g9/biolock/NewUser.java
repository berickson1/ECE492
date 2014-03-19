package ca.ualberta.ece492.g9.biolock;

import ca.ualberta.ece492.g9.biolock.types.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

//From AdminLogin - addition of new user
public class NewUser extends Activity {
	User selectedUser;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_new_user);
		// Retrieves information of the selected user
		Intent intent = getIntent();
		selectedUser = (User) intent.getParcelableExtra("User");
		if (selectedUser != null){
			// Displays retrieved information on screen
			EditText nameField = (EditText) findViewById(R.id.userNameFill);
			CheckBox enabledStatus = (CheckBox) findViewById(R.id.enabledUserStatusBox);
			nameField.setText(selectedUser.getName());
			enabledStatus.setChecked(selectedUser.getEnabled());
		}
	}

	// Jumps to AdminLogin to add new print
	public void enrollPrint(View v) {
		Intent addPrint = new Intent(NewUser.this, AdminLogin.class);
		startActivity(addPrint);
	}

	// Confirms deletion of user
	public void confirmDelete(View v) {
		// CONFIRM DELETE!
		System.out.println("Delete user");
	}

	// TODO: Check updated values before updating users
	// Jumps to Users
	public void updateUser(View v) {
		// Runs Users
		Intent i = new Intent(NewUser.this, Users.class);
		startActivity(i);

		// Close this activity
		finish();
	}
}
