package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.ualberta.ece492.g9.biolock.customs.JSONCallbackFunction;
import ca.ualberta.ece492.g9.biolock.customs.JSONPost;
import ca.ualberta.ece492.g9.biolock.customs.RoleSchedAdapter;
import ca.ualberta.ece492.g9.biolock.customs.UserAdapter;
import ca.ualberta.ece492.g9.biolock.types.Role;
import ca.ualberta.ece492.g9.biolock.types.RoleSchedule;
import ca.ualberta.ece492.g9.biolock.types.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

//From Roles - adding of new role or updating a role
public class NewRole extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	private static Context mContext;
	private Role selectedRole;
	private Role newRole;
	private EditText nameField;
	private CheckBox enabledStatus;
	private CheckBox enabledAdmin;
	private ListView usersList;
	private ListView schedList;
	private TextView updateRole;
	private TextView deleteRole;
	private TextView addSched;
	private JSONArray userJSON;
	private JSONArray scheduleJSON;
	private UserAdapter userAdapter;
	private RoleSchedAdapter roleSchedAdapter;
	
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_new_role);
		
		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");
		// Get contents on screen
		nameField = (EditText) findViewById(R.id.roleNameFill);
		enabledAdmin = (CheckBox) findViewById(R.id.enabledAdminStatusBox);
		enabledStatus = (CheckBox) findViewById(R.id.enabledRoleStatusBox);
		usersList = (ListView) findViewById(R.id.usersList);
		schedList = (ListView) findViewById(R.id.accessList);
		updateRole = (TextView) findViewById(R.id.updateRole);
		deleteRole = (TextView) findViewById(R.id.deleteRole);	
		addSched = (TextView) findViewById(R.id.addSched);
	}
	
	public void onResume(){
		ArrayList<User> userArray = new ArrayList<User>();
		User user = new User();
		userJSON = new JSONArray();
		ArrayList<RoleSchedule> scheduleArray = new ArrayList<RoleSchedule>();
		RoleSchedule roleSched = new RoleSchedule();
		scheduleJSON = new JSONArray();
		
		// Retrieves information of the selected user or new user name
		Intent intent = getIntent();
		selectedRole = (Role) intent.getParcelableExtra("Role");
		String roleName = intent.getStringExtra("Name");
		if (intent.getExtras().size() > 2) {
			try {
				// Retrieves user and role schedule
				userAdapter = new UserAdapter(mContext, "noCheck", selectedRole.getEnabled(), userArray);
				if (intent.getStringExtra("User") != null){
					userJSON = new JSONArray(intent.getStringExtra("Users"));
				}
				roleSchedAdapter = new RoleSchedAdapter(mContext, selectedRole.getEnabled(), scheduleArray);
				if (intent.getStringExtra("User Roles") != null){
					scheduleJSON = new JSONArray(intent.getStringExtra("Role Sched"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			userAdapter = new UserAdapter(mContext, "noCheck", true, userArray);
			roleSchedAdapter = new RoleSchedAdapter(mContext, true, scheduleArray);
		}
		userAdapter.clear();
		roleSchedAdapter.clear();
		
		// Update Role - Displays retrieved role information on screen
		if (selectedRole != null){
			nameField.setText(selectedRole.getName());
			enabledAdmin.setChecked(selectedRole.getAdmin());
			enabledStatus.setChecked(selectedRole.getEnabled());
			if (userJSON != null){
				// Displays user prints
				userArray = user.fromJson(userJSON);
				userAdapter.addAll(userArray);
				usersList.setAdapter(userAdapter);
				usersList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						confirmDeleteUser(position);
					}
				});
			}
			if (scheduleJSON != null){
				// Displays user roles
				scheduleArray = roleSched.fromJson(scheduleJSON);
				roleSchedAdapter.addAll(scheduleArray);
				schedList.setAdapter(roleSchedAdapter);
				schedList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						confirmDeleteSchedule(position);
					}
				});
			}
			// User is disabled
			if (!selectedRole.getEnabled()){
				disableScreen();
			}
		// New Role - Adds new role immediately after user inputs name in pop up screen
		} else if (roleName != null){
			nameField.setText(roleName);
			enabledAdmin.setChecked(true);
			enabledStatus.setChecked(true);
			// Set no users
			user.setID(-1);
			userAdapter.add(user);
			usersList.setAdapter(userAdapter);
			addRole();
			// Set no schedule
			roleSched.setID(-1);
			roleSchedAdapter.addAll(roleSched);
			schedList.setAdapter(roleSchedAdapter);
		}		
		super.onRestart();
	}

	// Add role
	public void addRole(){
		final ProgressDialog addRoleWait = ProgressDialog.show(NewRole.this,"Role", "Adding role", true, false, null);
		JSONPost addRole = new JSONPost(new JSONCallbackFunction(){
			@Override
			public void execute(JSONArray json) {
				if (json != null) {
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							JSONObject idVal = (JSONObject) json.get(1);
							newRole.setID(idVal.getInt("id"));
							selectedRole = newRole;
							addRoleWait.dismiss();
						} else {
							addRoleWait.dismiss();
							updateFail();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
				} else {
					addRoleWait.dismiss();
					updateFail();
				}
			}
		});
		// Defines role
		newRole = new Role();
		newRole.setID(0);
		newRole.setName(nameField.getText().toString());
		newRole.setAdmin(enabledAdmin.isChecked());
		newRole.setEnabled(enabledStatus.isChecked());
		newRole.setStartDate(0L);
		newRole.setEndDate(99L);
		addRole.execute(ip.concat("/roles"), "insert", newRole.toJson().toString());
	}
	
	// Jumps to AdminLogin to add new print
	public void enrollPrint(View v) {
		Intent addPrint = new Intent(NewRole.this, AdminLogin.class);
		startActivity(addPrint);
	}

	// Add schedule to role
	public void addSched(View v) {
			
	}
	
	// Requests user to confirm deletion of role user
	public void confirmDeleteUser(int position){
		
	}
	
	// Requests user to confirm deletion of schedule
	public void confirmDeleteSchedule(int position){
		
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
	
	// Displays popup from failure
	public void updateFail(){
		AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
		noConn.setMessage("Failed to update role");
		noConn.setTitle("Update");
		noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});
		noConn.setCancelable(false);
		noConn.setCanceledOnTouchOutside(false);
		noConn.show();
	}
		
	// Grays out views on screen
	public void disableScreen(){
		nameField.setEnabled(false);
		usersList.setEnabled(false);
		schedList.setEnabled(false);
		addSched.setClickable(false);
		addSched.setTextColor(Color.GRAY);
		updateRole.setClickable(false);
		updateRole.setTextColor(Color.GRAY);
		deleteRole.setClickable(false);
		deleteRole.setTextColor(Color.GRAY);
	}
}
