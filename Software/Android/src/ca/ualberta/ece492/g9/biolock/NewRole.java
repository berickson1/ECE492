// TODO: Add user to role

package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.ualberta.ece492.g9.biolock.customs.JSONCallbackFunction;
import ca.ualberta.ece492.g9.biolock.customs.JSONPost;
import ca.ualberta.ece492.g9.biolock.customs.RoleSchedAdapter;
import ca.ualberta.ece492.g9.biolock.customs.UserRoleAdapter;
import ca.ualberta.ece492.g9.biolock.types.Role;
import ca.ualberta.ece492.g9.biolock.types.RoleSchedule;
import ca.ualberta.ece492.g9.biolock.types.UserRole;
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
	private Role roleToDelete;
	private RoleSchedule selectedSchedule;
	private UserRole userToDelete;
	private EditText nameField;
	private CheckBox enabledStatus;
	private CheckBox enabledAdmin;
	private ListView usersList;
	private ListView schedList;
	private TextView addSched;
	private JSONArray userJSON;
	private JSONArray scheduleJSON;
	private UserRoleAdapter userAdapter;
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
		addSched = (TextView) findViewById(R.id.addSched);
	}
	
	public void onResume(){
		ArrayList<UserRole> userArray = new ArrayList<UserRole>();
		UserRole user = new UserRole();
		userJSON = new JSONArray();
		ArrayList<RoleSchedule> scheduleArray = new ArrayList<RoleSchedule>();
		RoleSchedule roleSched = new RoleSchedule();
		scheduleJSON = new JSONArray();
		
		// Retrieves information of the selected role or new role name
		Intent intent = getIntent();
		selectedRole = (Role) intent.getParcelableExtra("Role");
		String roleName = intent.getStringExtra("Name");
		if (intent.getExtras().size() > 2) {
			try {
				// Retrieves user and role schedule
				userAdapter = new UserRoleAdapter(mContext, selectedRole.getEnabled(), "userName", userArray);
				if (intent.getStringExtra("Users") != null){
					userJSON = new JSONArray(intent.getStringExtra("Users"));
				}
				roleSchedAdapter = new RoleSchedAdapter(mContext, selectedRole.getEnabled(), scheduleArray);
				if (intent.getStringExtra("Role Sched") != null){
					scheduleJSON = new JSONArray(intent.getStringExtra("Role Sched"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			userAdapter = new UserRoleAdapter(mContext, true, "userName", userArray);
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
				// Displays users
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
				// Displays role schedules
				scheduleArray = roleSched.fromJson(scheduleJSON);
				roleSchedAdapter.addAll(scheduleArray);
				schedList.setAdapter(roleSchedAdapter);
				schedList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						loadSchedule(position);
					}
				});
			}
			// Role is disabled
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
			// Set no schedule
			roleSched.setID(-1);
			roleSchedAdapter.addAll(roleSched);
			schedList.setAdapter(roleSchedAdapter);
			addRole();
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
	
	// Requests user to confirm deletion of role user
	public void confirmDeleteUser(final int position){
		final AlertDialog confirm  = new AlertDialog.Builder(mContext).create();
		confirm.setMessage("Do you want to remove this user");
		confirm.setTitle("User");
		confirm.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Check if selected row is 'No user'
				if (userAdapter.getItem(position).getID() != -1){
					deleteUser(position);
					confirm.cancel();
				}
			}
	    });
		confirm.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
	    });
		confirm.setCancelable(false);
		confirm.setCanceledOnTouchOutside(false);
		confirm.show();
	}
	
	// Deletes role user
	public void deleteUser(final int position){
		final ProgressDialog deleteUserWait = ProgressDialog.show(NewRole.this,"Role User", "Deleting role user", true, false, null);
		JSONPost deleteUser = new JSONPost(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							try {
								// Remove user from listview
								for (int i = 0; i < userJSON.length(); i++){
									UserRole removeRole = new UserRole(userJSON.getJSONObject(i));
									if (removeRole.getName().equals(userToDelete.getName())){
										userJSON.remove(i);
									}
								}
								// Display no user found
								if (userJSON.length() == 0){
									UserRole noUserRole = new UserRole();
									noUserRole.setID(-1);
									userJSON.put(0, noUserRole.toJson());
								}
							} catch (JSONException e){
								e.printStackTrace();
							}
							deleteUserWait.dismiss();
							// Restarts this screen
							Intent restart = getIntent();
							restart.putExtra("Users", userJSON.toString());
							if (userJSON.length() != 0){
								restart.putExtra("Schedule", scheduleJSON.toString());
							}
							restart.putExtra("Role", selectedRole);
							finish();
							startActivity(restart);
						} else {
							deleteUserWait.dismiss();
							updateFail();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
				} else {
					deleteUserWait.dismiss();
					updateFail();
				}
			}
		});
		userToDelete = userAdapter.getItem(position);
		deleteUser.execute(ip.concat("/userRole"), "delete", userToDelete.toJson().toString());
	}
	
	// Opens new screen to show schedule information
	public void loadSchedule(int position){
		selectedSchedule = roleSchedAdapter.getItem(position);
		Intent updateSched = new Intent(NewRole.this, NewSched.class);
		updateSched.putExtra("Role", selectedRole);
		updateSched.putExtra("Schedule", selectedSchedule);
		startActivityForResult(updateSched, 0);	
	}
	
	// Opens new blank schedule screen
	public void addSched(View v) {
		Intent addSched = new Intent(NewRole.this, NewSched.class);
		addSched.putExtra("Role", selectedRole);
		startActivityForResult(addSched, 0);
	}
	
	// Gets schedule back to add to listview
	public void onActivityResult(int requestCode, int resultCode, Intent schedule){
		// Returned from NewSched
		if (requestCode == 0){
			if (resultCode == RESULT_OK){     
				RoleSchedule newSched = schedule.getParcelableExtra("Schedule");
				// Remove 'no record' entry if exists
				RoleSchedule noRecord = new RoleSchedule(roleSchedAdapter.getItem(0).toJson());
				if ((noRecord.getID() == -1) && (scheduleJSON.length() == 1)){
					scheduleJSON.remove(0);
				}
				// Remove modified schedule
				if (newSched.getID() == selectedSchedule.getID()){
					int position = roleSchedAdapter.getPosition(selectedSchedule);
					scheduleJSON.remove(position);
				}
				scheduleJSON.put(newSched.toJson());
			} else if (resultCode == RESULT_CANCELED){
				try {
					// Remove schedule from listview
					for (int i = 0; i < scheduleJSON.length(); i++){
						RoleSchedule removeSched = new RoleSchedule(scheduleJSON.getJSONObject(i));
						if (removeSched.getID() == selectedSchedule.getID()){
							scheduleJSON.remove(i);
						}
					}
					// Display no roles found
					if (scheduleJSON.length() == 0){
						RoleSchedule noSched = new RoleSchedule();
						noSched.setID(-1);
						scheduleJSON.put(0, noSched.toJson());
					}
				} catch (JSONException e){
					e.printStackTrace();
				}
			}
			if (resultCode == 1){
				return;
			}
			// Restarts this screen
			Intent restart = getIntent();
			restart.putExtra("Role Sched", scheduleJSON.toString());
			if (userJSON.length() != 0){
				restart.putExtra("Users", userJSON.toString());
			}
			restart.putExtra("Role", selectedRole);
			finish();
			startActivity(restart);
		}
	}
	
	// Confirms deletion of role
	public void confirmDelete(View v) {
		final AlertDialog confirm  = new AlertDialog.Builder(mContext).create();
		confirm.setMessage("Do you want to remove this role");
		confirm.setTitle("Role");
		confirm.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				deleteRole();
			}
	    });
		confirm.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
	    });
		confirm.setCancelable(false);
		confirm.setCanceledOnTouchOutside(false);
		confirm.show();
	}
	
	// Deletes role
	public void deleteRole(){
		final ProgressDialog deleteRoleWait = ProgressDialog.show(NewRole.this,"Role", "Deleting role", true, false, null);
		JSONPost deleteRole = new JSONPost(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							deleteRoleWait.dismiss();
							finish();
						} else {
							deleteRoleWait.dismiss();
							updateFail();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
				} else {
					deleteRoleWait.dismiss();
					updateFail();
				}
			}
		});
		roleToDelete = selectedRole;
		deleteRole.execute(ip.concat("/roles"), "delete", roleToDelete.toJson().toString());
	}
	
	// Updates the role
	public void updateRole() {
		final ProgressDialog updateRoleWait = ProgressDialog.show(NewRole.this,"Role", "Updating role", true, false, null);
		JSONPost updateRole = new JSONPost(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							updateRoleWait.dismiss();
							finish();
						} else {
							updateRoleWait.dismiss();
							updateFail();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
				} else {
					updateRoleWait.dismiss();
					updateFail();
				}
			}
		});
		selectedRole.setName(nameField.getText().toString());
		selectedRole.setAdmin(enabledAdmin.isChecked());
		selectedRole.setEnabled(enabledStatus.isChecked());
		updateRole.execute(ip.concat("/roles"), "update", selectedRole.toJson().toString());
	}
	
	// Checks if role needs to be updated
	public void checkRole(View v) {
		// Check role name
		if (!nameField.getText().toString().equals(selectedRole.getName())){
			updateRole();
		// Check admin status
		} else if (enabledAdmin.isChecked() != selectedRole.getAdmin()) {
			updateRole();
		// Check enable status
		} else if (enabledStatus.isChecked() != selectedRole.getEnabled()) {
			updateRole();
		} else {
			finish();
		}
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
		enabledAdmin.setEnabled(false);
		usersList.setEnabled(false);
		schedList.setEnabled(false);
		addSched.setClickable(false);
		addSched.setTextColor(Color.GRAY);
	}
}
