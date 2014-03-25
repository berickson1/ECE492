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
import android.text.Html;
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
	private static User selectedUser;
	private static RoleAdapter roleAdapter;
	private EditText nameField;
	private CheckBox enabledStatus;
	private ListView printsList;
	private ListView rolesList;
	private TextView addPrint;
	private TextView addRole;
	private TextView update;
	private ProgressDialog wait;
	private JSONPost changeName;
	private JSONPost changeStatus;
	private ArrayList<UserRole> userRolesArray;
	private JSONArray userPrints;
	private JSONArray userRoles;
	private UserRole roleToDelete;
	private UserRole addUserRole;
	
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
		update = (TextView) findViewById(R.id.updateUser);
	}
	
	public void onResume(){
		// Retrieves information of the selected user if exists
		Intent intent = getIntent();
		selectedUser = (User) intent.getParcelableExtra("User");
		String userName = intent.getStringExtra("Name");
		if (intent.getExtras().size() > 2) {
			try {
				userPrints = new JSONArray(intent.getStringExtra("User Prints"));
				userRoles = new JSONArray(intent.getStringExtra("User Roles"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		// Displays retrieved information on screen
		if (selectedUser != null){
			nameField.setText(selectedUser.getName());
			enabledStatus.setChecked(selectedUser.getEnabled());
			if (userPrints != null){
				// Displays user prints
				ArrayList<UserPrint> printsArray = new ArrayList<UserPrint>();
				UserPrintAdapter userPrintAdapter = new UserPrintAdapter(mContext, selectedUser.getEnabled(), printsArray);
				userPrintAdapter.clear();
				UserPrint userPrint = new UserPrint();
				printsArray = userPrint.fromJson(userPrints);
				userPrintAdapter.addAll(printsArray);
				printsList.setAdapter(userPrintAdapter);
			}
			if (userRoles != null){
				// Displays user roles
				userRolesArray = new ArrayList<UserRole>();
				UserRoleAdapter userRoleAdapter = new UserRoleAdapter(mContext, selectedUser.getEnabled(), userRolesArray);
				userRoleAdapter.clear();
				UserRole userRole = new UserRole();
				userRolesArray = userRole.fromJson(userRoles);
				userRoleAdapter.addAll(userRolesArray);
				rolesList.setAdapter(userRoleAdapter);
				// User role is clicked on
				rolesList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						confirmDeleteUserRole(position);
					}
				});
			}
			// User is disabled
			if (!selectedUser.getEnabled()){
				disableScreen();
			}
		} else if (userName != null){
			nameField.setText(userName);
		} else {
			// Change update button text to add
			update.setText(Html.fromHtml("<u>Add</u>"));
		}			
		super.onResume();
	}
	
	// Jumps to AdminLogin to add new print
	public void enrollPrint(View v) {
		Intent addPrint = new Intent(NewUser.this, AdminLogin.class);
		// NewUser or UpdateUser
		if (selectedUser == null){
			addPrint.putExtra("Caller", mContext.getClass().getSimpleName());
		} else {
			addPrint.putExtra("Caller", "UpdateUser");
		}
		startActivity(addPrint);
	}
	
	// Check if user wants to delete user role
	public void confirmDeleteUserRole(final int position){
		final AlertDialog confirm  = new AlertDialog.Builder(mContext).create();
		confirm.setMessage("Do you want to remove this role");
		confirm.setTitle("Role");
		confirm.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				deleteUserRole(position);
				confirm.cancel();
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
	public void deleteUserRole(int position){
		final ProgressDialog wait = ProgressDialog.show(NewUser.this,"User Role", "Deleting user role", true, false, null);
		JSONPost deleteRole = new JSONPost(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							try {
								// Remove user role from listview
								for (int i = 0; i < userRoles.length(); i++){
									UserRole removeRole = new UserRole(userRoles.getJSONObject(i));
									if (removeRole.getName().equals(roleToDelete.getName())){
										userRoles.remove(i);
									}
								}
								// Display no roles found
								if (userRoles.length() == 0){
									UserRole noUserRole = new UserRole();
									noUserRole.setID(-1);
									userRoles.put(0, noUserRole.toJson());
								}
							} catch (JSONException e){
								e.printStackTrace();
							}
							wait.dismiss();
							// Restarts this screen
							Intent restart = getIntent();
							restart.putExtra("User Prints", userPrints.toString());
							restart.putExtra("User Roles", userRoles.toString());
							finish();
							startActivity(restart);
						} else {
							wait.dismiss();
							updateFail();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
				} else {
					wait.dismiss();
					updateFail();
				}
			}
		});
		roleToDelete = (UserRole) rolesList.getItemAtPosition(position);
		deleteRole.execute(ip.concat("/userRole"), "delete", roleToDelete.toJson().toString());
	}
	
	// Popup to select from roles
	public void addRole(View v) {
		final ProgressDialog wait = ProgressDialog.show(NewUser.this,"User Information", "Loading roles", true, false, null);
		JSONParser parseRoles = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					wait.dismiss();
					displayRoles(json);
				} else {
					wait.dismiss();
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
		roleAdapter = new RoleAdapter(mContext, rolesArray);
		roleAdapter.clear();
		Role role = new Role();
		rolesArray = role.fromJson(json);
		roleAdapter.addAll(rolesArray);
		AlertDialog.Builder roles = new AlertDialog.Builder(mContext);
		roles.setTitle("Roles");
		roles.setAdapter(roleAdapter, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	// New user
		    	if (selectedUser == null){
		    		addUserRole(which);
		    		return;
		    	}
		    	// Checks if user already has the role
		    	for (int i = 0; i < userRolesArray.size(); i++){
		    		if (userRolesArray.get(i).getRID() == roleAdapter.getItem(which).getID()){
		    			AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
						noConn.setMessage("User already has this role");
						noConn.setTitle("Roles");
						noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {}
					    });
						noConn.setCancelable(false);
						noConn.setCanceledOnTouchOutside(false);
						noConn.show();
						return;
		    		}
		    	}
		    	addUserRole(which);
		    }
		});
		roles.show();
	}
	
	// Add user role for user
	public void addUserRole(int which){
		final ProgressDialog wait = ProgressDialog.show(NewUser.this,"User Role", "Adding user role", true, false, null);
		// Add role to existing user
		if (selectedUser != null) {
    		JSONPost postRole = new JSONPost(new JSONCallbackFunction() {
				@Override
				public void execute(JSONArray json) {
					if (json != null){
						try {
							JSONObject response = (JSONObject) json.get(0);
							if (response.getString("success").equalsIgnoreCase("true")){
								// Remove 'no record' entry if exists
								UserRole noRecord = new UserRole(userRoles.getJSONObject(0));
								if (noRecord.getID() == -1){
									userRoles.remove(0);
								}
								wait.dismiss();
								// Restarts this screen
								Intent restart = getIntent();
								restart.putExtra("User Prints", userPrints.toString());
								restart.putExtra("User Roles", userRoles.toString());
								finish();
								startActivity(restart);
							} else {
								wait.dismiss();
								updateFail();
							}
						} catch (JSONException e){
							e.printStackTrace();
						}
					} else {
						wait.dismiss();
						updateFail();
					}
				}
    		});
    		// Create the user role
    		UserRole addUserRole = new UserRole();
    		Role role = roleAdapter.getItem(which);
    		addUserRole.setName(role.getName());
    		addUserRole.setID(0);
    		addUserRole.setUID(selectedUser.getID());
    		addUserRole.setRID(role.getID());
    		addUserRole.setStartDate(role.getStartDate());
    		addUserRole.setEndDate(role.getEndDate());
    		userRoles.put(addUserRole.toJson());
    		postRole.execute(ip.concat("/userRole"), "insert", addUserRole.toJson().toString());
    	// Add role to new user
		} else {
    		
    	}
	}
	
	// Displays popup when failure to add user role
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
		JSONPost addUser = new JSONPost(new JSONCallbackFunction(){
			@Override
			public void execute(JSONArray json) {
			}
		});
		// Defines user
		User newUser = new User();
		newUser.setID(0);
		newUser.setName(nameField.getText().toString());
		newUser.setEnabled(enabledStatus.isChecked());
		
		//addUser.execute(ip.concat("/users"), "insert", )
	}
	
	// Enables or disables user
	public void changeUserStatus(String status) {
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
								wait.dismiss();
								finish();
							} else {
								// Check if asynctask is done
								if(changeName.getStatus().toString().equals("FINISHED")){
									wait.dismiss();
									finish();
									return;
								}
							}
						} else {
							wait.dismiss();
							updateFail();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					wait.dismiss();
					updateFail();
				}
			}
        });
        changeStatus.execute(ip.concat("/users"), status, selectedUser.toJson().toString());
        //changeStatus.execute(ip.concat("/users"));
	}

	// Updates user name
	public void changeUserName(){
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
								wait.dismiss();
								finish();
							} else {
								// Check if asynctask is done
								if(changeStatus.getStatus().toString().equals("FINISHED")){
									wait.dismiss();
									finish();
									return;
								}
							}
						} else {
							wait.dismiss();
							updateFail();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
				} else {
					wait.dismiss();
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
	
	// Adds or updates the user
	public void updateUser(View v) {
		wait = ProgressDialog.show(NewUser.this,"Update User", "Updating user", true, false, null);
		// Update user
		if (selectedUser != null){
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
			}
		// Add user
		} else {
			if (nameField.getText().toString().matches("")){
				wait.dismiss();
				AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
				noConn.setMessage("User name cannot be empty");
				noConn.setTitle("User Name");
				noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
			    });
				noConn.setCancelable(false);
				noConn.setCanceledOnTouchOutside(false);
				noConn.show();
			} else {
				addUser();
			}
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
