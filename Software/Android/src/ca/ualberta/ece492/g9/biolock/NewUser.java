package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.ualberta.ece492.g9.biolock.customs.JSONCallbackFunction;
import ca.ualberta.ece492.g9.biolock.customs.JSONParser;
import ca.ualberta.ece492.g9.biolock.customs.JSONPost;
import ca.ualberta.ece492.g9.biolock.customs.RoleAdapter;
import ca.ualberta.ece492.g9.biolock.customs.UserPrintAdapter;
import ca.ualberta.ece492.g9.biolock.customs.UserRoleAdapter;
import ca.ualberta.ece492.g9.biolock.types.Role;
import ca.ualberta.ece492.g9.biolock.types.User;
import ca.ualberta.ece492.g9.biolock.types.UserPrint;
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

//From AdminLogin - addition of new user
//From Users - displaying of user info
public class NewUser extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	private static Context mContext;
	private static User selectedUser = null;
	private User newUser;
	private UserRole roleToDelete;
	private UserRole addUserRole;
	private UserPrint newPrint;
	private EditText nameField;
	private CheckBox enabledStatus;
	private ListView printsList;
	private ListView rolesList;
	private TextView addPrint;
	private TextView addRole;
	private JSONPost changeName;
	private JSONPost changeStatus;
	private JSONArray userPrintsJSON;
	private JSONArray userRolesJSON;
	private RoleAdapter roleAdapter;
	private UserPrintAdapter userPrintAdapter;
	private UserRoleAdapter userRoleAdapter;
	
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_new_user);
		
		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");
		// Get contents on screen
		nameField = (EditText) findViewById(R.id.userNameFill);
		enabledStatus = (CheckBox) findViewById(R.id.enabledUserStatusBox);
		printsList = (ListView) findViewById(R.id.userPrints);
		rolesList = (ListView) findViewById(R.id.userRoles);
		addPrint = (TextView) findViewById(R.id.enrollPrint);
		addRole = (TextView) findViewById(R.id.enrollRole);
	}
	
	public void onResume(){
		String userName = null;
		ArrayList<UserPrint> printsArray = new ArrayList<UserPrint>();
		UserPrint userPrint = new UserPrint();
		userPrintsJSON = new JSONArray();
		ArrayList<UserRole> userRolesArray = new ArrayList<UserRole>();
		UserRole userRole = new UserRole();
		userRolesJSON = new JSONArray();
		
		// Retrieves information of the selected user or new user name
		Intent intent = getIntent();
		if (selectedUser == null) {
			selectedUser = (User) intent.getParcelableExtra("User");
			userName = intent.getStringExtra("Name");
		}
		if (intent.getExtras().size() > 2) {
			try {
				// Retrieves user prints and user roles
				userPrintAdapter = new UserPrintAdapter(mContext, selectedUser.getEnabled(), printsArray);
				if (intent.getStringExtra("User Prints") != null){
					userPrintsJSON = new JSONArray(intent.getStringExtra("User Prints"));
				}
				userRoleAdapter = new UserRoleAdapter(mContext, selectedUser.getEnabled(), "roleName", userRolesArray);
				if (intent.getStringExtra("User Roles") != null){
					userRolesJSON = new JSONArray(intent.getStringExtra("User Roles"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			userPrintAdapter = new UserPrintAdapter(mContext, true, printsArray);
			userRoleAdapter = new UserRoleAdapter(mContext, true, "roleName", userRolesArray);
		}
		userPrintAdapter.clear();
		userRoleAdapter.clear();
		
		// Update User - Displays retrieved user information on screen
		if (selectedUser != null){
			nameField.setText(selectedUser.getName());
			enabledStatus.setChecked(selectedUser.getEnabled());
			if (userPrintsJSON.length() != 0){
				// Displays user prints
				printsArray = userPrint.fromJson(userPrintsJSON);
				userPrintAdapter.addAll(printsArray);
				printsList.setAdapter(userPrintAdapter);
			} else {
				// Set no fingerprint
				userPrint.setID(-1);
				userPrintAdapter.add(userPrint);
				printsList.setAdapter(userPrintAdapter);
			}
			if (userRolesJSON.length() != 0){
				// Displays user roles
				userRolesArray = userRole.fromJson(userRolesJSON);
				userRoleAdapter.addAll(userRolesArray);
				rolesList.setAdapter(userRoleAdapter);
				// User role is clicked on
				rolesList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						confirmDeleteUserRole(position);
					}
				});
			} else {
				// Set no user role
				userRole.setID(-1);
				userRoleAdapter.add(userRole);
				rolesList.setAdapter(userRoleAdapter);
			}
			// User is disabled
			if (!selectedUser.getEnabled()){
				disableScreen();
			}
		// New User - Adds new user immediately after user inputs name in pop up screen
		} else if (userName != null){
			nameField.setText(userName);
			enabledStatus.setChecked(true);
			// Set no fingerprint
			userPrint.setID(-1);
			userPrintAdapter.add(userPrint);
			printsList.setAdapter(userPrintAdapter);
			// Set no user role
			userRole.setID(-1);
			userRoleAdapter.add(userRole);
			rolesList.setAdapter(userRoleAdapter);
			addUser();
		}	
		super.onResume();
	}
	
	// Jumps to AdminLogin to add new print
	public void enrollPrint(View v) {
		Intent addPrint = new Intent(NewUser.this, AdminLogin.class);
		addPrint.putExtra("Caller", "UpdateUser");
		startActivityForResult(addPrint, 0);
	}
	
	// Gets fingerprint id & adds to db
	public void onActivityResult(int requestCode, int resultCode, Intent fingerprint){
		// Returned from AdminLogin
		if (requestCode == 0){
			if (resultCode == RESULT_OK) {
				int id = fingerprint.getIntExtra("id", -1);
				if (id != -1){
					addUserPrint(id);
				}
			}
		}
	}
	
	// Adds user print to database
	public void addUserPrint(int id){
		final ProgressDialog addPrintWait = ProgressDialog.show(NewUser.this,"User Print", "Adding user print", true, false, null);
		//Add userprint to db
		JSONPost addUserPrint = new JSONPost (new JSONCallbackFunction(){
			@Override
			public void execute(JSONArray json) {
				if (json!= null) {
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							if (userPrintAdapter.getCount() != 0){
								// Remove 'no record' entry if exists
								UserPrint noRecord = new UserPrint(userPrintAdapter.getItem(0).toJson());
								if ((noRecord.getID() == -1) && (userPrintsJSON.length() == 1)){
									userPrintsJSON.remove(0);
								}
							}
							userPrintsJSON.put(newPrint.toJson());
							addPrintWait.dismiss();
							// Restarts this screen
							Intent restart = getIntent();
							restart.putExtra("User Prints", userPrintsJSON.toString());
							if (userRolesJSON.length() != 0){
								restart.putExtra("User Roles", userRolesJSON.toString());
							}
							restart.putExtra("User", selectedUser);
							finish();
							startActivity(restart);
						} else {
							addPrintWait.dismiss();
							updateFail();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
				} else {
					addPrintWait.dismiss();
					updateFail();
				}
			}
		});
		newPrint =  new UserPrint();
		newPrint.setID(id);
		newPrint.setUID(selectedUser.getID());
		addUserPrint.execute(ip.concat("/prints"), "insert", newPrint.toJson().toString());	
	}
	
	// Check if user wants to delete user role
	public void confirmDeleteUserRole(final int position){
		final AlertDialog confirm  = new AlertDialog.Builder(mContext).create();
		confirm.setMessage("Do you want to remove this role");
		confirm.setTitle("Role");
		confirm.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Check if selected row is 'No role'
				if (userRoleAdapter.getItem(position).getID() != -1){
					deleteUserRole(position);
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
	
	// Deletes user role
	public void deleteUserRole(final int position){
		final ProgressDialog deleteRoleWait = ProgressDialog.show(NewUser.this,"User Role", "Deleting user role", true, false, null);
		JSONPost deleteRole = new JSONPost(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							try {
								// Remove user role from listview
								for (int i = 0; i < userRolesJSON.length(); i++){
									UserRole removeRole = new UserRole(userRolesJSON.getJSONObject(i));
									if (removeRole.getID() == roleToDelete.getID()){
										userRolesJSON.remove(i);
									}
								}
								// Display no roles found
								if (userRolesJSON.length() == 0){
									UserRole noUserRole = new UserRole();
									noUserRole.setID(-1);
									userRolesJSON.put(0, noUserRole.toJson());
								}
							} catch (JSONException e){
								e.printStackTrace();
							}
							deleteRoleWait.dismiss();
							// Restarts this screen
							Intent restart = getIntent();
							restart.putExtra("User Roles", userRolesJSON.toString());
							if (userPrintsJSON.length() != 0){
								restart.putExtra("User Prints", userPrintsJSON.toString());
							}
							restart.putExtra("User", selectedUser);
							finish();
							startActivity(restart);
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
		roleToDelete = userRoleAdapter.getItem(position);
		deleteRole.execute(ip.concat("/userRole"), "delete", roleToDelete.toJson().toString());
	}
	
	// Popup to select from roles
	public void addRole(View v) {
		final ProgressDialog displayRolesWait = ProgressDialog.show(NewUser.this,"Roles", "Loading roles", true, false, null);
		JSONParser parseRoles = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					displayRolesWait.dismiss();
					displayRoles(json);
				} else {
					displayRolesWait.dismiss();
					AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
					noConn.setMessage("Could not get roles");
					noConn.setTitle("Roles");
					noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
				    });
					noConn.setCancelable(false);
					noConn.setCanceledOnTouchOutside(false);
					noConn.show();
				}
			}
		});
		parseRoles.execute(ip.concat("/roles"));
	}
	
	//Displays role in popup
	public void displayRoles(JSONArray json){
		ArrayList<Role> rolesArray = new ArrayList<Role>();
		roleAdapter = new RoleAdapter(mContext, "noCheck", true, rolesArray);
		roleAdapter.clear();
		Role role = new Role();
		rolesArray = role.fromJson(json);
		roleAdapter.addAll(rolesArray);
		AlertDialog.Builder roles = new AlertDialog.Builder(mContext);
		roles.setTitle("Roles");
		roles.setAdapter(roleAdapter, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	// Checks if user already has the role
		    	for (int i = 0; i < userRolesJSON.length(); i++){
		    		try {
						if (userRolesJSON.getJSONObject(i).getInt("id") == roleAdapter.getItem(which).getID()){
							AlertDialog hasRole  = new AlertDialog.Builder(mContext).create();
							hasRole.setMessage("User already has this role");
							hasRole.setTitle("Roles");
							hasRole.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {}
						    });
							hasRole.setCancelable(false);
							hasRole.setCanceledOnTouchOutside(false);
							hasRole.show();
							return;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
		    	}
		    	addUserRole(which);
		    }
		});
		roles.show();
	}
	
	// Add user role for user
	public void addUserRole(int which){
		final ProgressDialog addRoleWait = ProgressDialog.show(NewUser.this,"User Role", "Adding user role", true, false, null);
    	JSONPost postRole = new JSONPost(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							// Remove 'no record' entry if exists
							UserRole noRecord = new UserRole(userRoleAdapter.getItem(0).toJson());
							if ((noRecord.getID() == -1) && (userRolesJSON.length() == 1)){
								userRolesJSON.remove(0);
							}
							JSONObject idVal = (JSONObject) json.get(1);
							addUserRole.setID(idVal.getInt("id"));
							userRolesJSON.put(addUserRole.toJson());
							addRoleWait.dismiss();
							// Restarts this screen
							Intent restart = getIntent();
							restart.putExtra("User Roles", userRolesJSON.toString());
							if (userPrintsJSON.length() != 0){
								restart.putExtra("User Prints", userPrintsJSON.toString());
							}
							restart.putExtra("User", selectedUser);
							finish();
							startActivity(restart);
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
    	// Create the user role
    	addUserRole = new UserRole();
    	Role role = roleAdapter.getItem(which);
    	addUserRole.setName(role.getName());
    	addUserRole.setUserName(selectedUser.getName());
    	addUserRole.setID(0);
    	addUserRole.setUID(selectedUser.getID());
    	addUserRole.setRID(role.getID());
    	addUserRole.setStartDate(role.getStartDate());
    	addUserRole.setEndDate(role.getEndDate());
    	postRole.execute(ip.concat("/userRole"), "insert", addUserRole.toJson().toString());
	}
	
	// Displays popup from failure
	public void updateFail(){
		AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
		noConn.setMessage("Failed to update user");
		noConn.setTitle("Update");
		noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
	    });
		noConn.setCancelable(false);
		noConn.setCanceledOnTouchOutside(false);
		noConn.show();
	}
	
	// Add user 
	public void addUser(){
		final ProgressDialog addUserWait = ProgressDialog.show(NewUser.this,"User", "Adding user", true, false, null);
		JSONPost addUser = new JSONPost(new JSONCallbackFunction(){
			@Override
			public void execute(JSONArray json) {
				if (json != null) {
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							JSONObject idVal = (JSONObject) json.get(1);
							newUser.setID(idVal.getInt("id"));
							selectedUser = newUser;
							addUserWait.dismiss();
						} else {
							addUserWait.dismiss();
							updateFail();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
				} else {
					addUserWait.dismiss();
					updateFail();
				}
			}
		});
		// Defines user
		newUser = new User();
		newUser.setID(0);
		newUser.setName(nameField.getText().toString());
		newUser.setEnabled(enabledStatus.isChecked());
		newUser.setStartDate(0L);
		newUser.setEndDate(99L);
		addUser.execute(ip.concat("/users"), "insert", newUser.toJson().toString());
	}
	
	// Enables or disables user
	public void changeUserStatus(String status) {
		final ProgressDialog changeStatusWait = ProgressDialog.show(NewUser.this,"Update User", "Updating user status", true, false, null);
		// JSONPost to enable or disable user
        changeStatus = new JSONPost(new JSONCallbackFunction() {
        	@Override
			public void execute(JSONArray json) {
				if (json != null) {
					try {
						JSONObject response = (JSONObject) json.get(0);
						// User status changed successfully
						if (response.getString("success").equalsIgnoreCase("true")){
							// Don't need to wait for other asynctask
							if (nameField.getText().toString().equals(selectedUser.getName())){
								changeStatusWait.dismiss();
								finish();
							} else {
								changeStatusWait.dismiss();
								// Check if asynctask is done
								if(changeName.getStatus().toString().equals("FINISHED")){
									finish();
									return;
								}
							}
						} else {
							changeStatusWait.dismiss();
							updateFail();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					changeStatusWait.dismiss();
					updateFail();
				}
			}
        });
        changeStatus.execute(ip.concat("/users"), status, selectedUser.toJson().toString());
	}

	// Updates user name
	public void changeUserName(){
		final ProgressDialog changeNameWait = ProgressDialog.show(NewUser.this,"Update User", "Updating user name", true, false, null);
		// JSONPost to change user name
        changeName = new JSONPost(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {		
				if (json != null){
					try {
						JSONObject response = (JSONObject) json.get(0);
						// User name changed successfully
						if (response.getString("success").equalsIgnoreCase("true")){
							// Don't need to wait for other asynctask
							if (enabledStatus.isChecked() == selectedUser.getEnabled()){
								changeNameWait.dismiss();
								finish();
							} else {
								changeNameWait.dismiss();
								// Check if asynctask is done
								if(changeStatus.getStatus().toString().equals("FINISHED")){
									finish();
									return;
								}
							}
						} else {
							changeNameWait.dismiss();
							updateFail();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
				} else {
					changeNameWait.dismiss();
					updateFail();
				}
			}      	
        });
        // Change user info
        User updateUser = new User();
        updateUser.setID(selectedUser.getID());
        updateUser.setName(nameField.getText().toString());
        updateUser.setEnabled(selectedUser.getEnabled());
        updateUser.setStartDate(selectedUser.getStartDate());
        updateUser.setEndDate(selectedUser.getEndDate());
        changeName.execute(ip.concat("/users"), "update", updateUser.toJson().toString());
	}
	
	// Updates the user
	public void updateUser(View v) {
		// Check user name
		if (!nameField.getText().toString().equals(selectedUser.getName())){
			changeUserName();
		}
		// Check enable status
		if (enabledStatus.isChecked() != selectedUser.getEnabled()){
			if (enabledStatus.isChecked()){
				changeUserStatus("enable");
			} else {
				changeUserStatus("delete");
			}
		} else {
			finish();
		}
	}
	
	public void disableScreen(){
		nameField.setEnabled(false);
		printsList.setEnabled(false);
		rolesList.setEnabled(false);
		addPrint.setClickable(false);
		addPrint.setTextColor(Color.GRAY);
		addRole.setClickable(false);
		addRole.setTextColor(Color.GRAY);
	}
}
