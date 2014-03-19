package ca.ualberta.ece492.g9.biolock;

import ca.ualberta.ece492.g9.biolock.types.Role;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

//From AdminLogin - addition of new user
public class NewRole extends Activity {
	Role selectedRole;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_new_role);
		// Retrieves information of the selected user
		Intent intent = getIntent();
		selectedRole = (Role) intent.getParcelableExtra("Role");
		if (selectedRole != null){
			// Displays retrieved information on screen
			EditText nameField = (EditText) findViewById(R.id.roleNameFill);
			CheckBox enabledStatus = (CheckBox) findViewById(R.id.enabledRoleStatusBox);
			nameField.setText(selectedRole.getName());
			enabledStatus.setChecked(selectedRole.getEnabled());
		}
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
