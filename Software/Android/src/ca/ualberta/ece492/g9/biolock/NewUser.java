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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

//From AdminLogin - addition of new user
//From Users - displaying of user info
public class NewUser extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	private static Context mContext;
	private static User selectedUser;
	private static UserPrintAdapter userPrintAdapter;
	private static UserRoleAdapter userRoleAdapter;
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
	}
	
	public void onResume(){
		// Get contents on screen
		nameField = (EditText) findViewById(R.id.userNameFill);
		enabledStatus = (CheckBox) findViewById(R.id.enabledUserStatusBox);
		printsList = (ListView) findViewById(R.id.userPrints);
		rolesList = (ListView) findViewById(R.id.userRoles);
		addPrint = (TextView) findViewById(R.id.enrollPrint);
		addRole = (TextView) findViewById(R.id.enrollRole);
		update = (TextView) findViewById(R.id.updateUser);
		
		// Retrieves information of the selected user if exists
		Intent intent = getIntent();
		selectedUser = (User) intent.getParcelableExtra("User");
		if (intent.getExtras() != null) {
			try {
				userPrints = new JSONArray(intent.getStringExtra("User Prints"));
				userRoles = new JSONArray(intent.getStringExtra("User Roles"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (selectedUser != null){
			// Displays retrieved information on screen
			nameField.setText(selectedUser.getName());
			enabledStatus.setChecked(selectedUser.getEnabled());
			// Displays user prints
			ArrayList<UserPrint> printsArray = new ArrayList<UserPrint>();
			userPrintAdapter = new UserPrintAdapter(mContext, printsArray);
			userPrintAdapter.clear();
			UserPrint userPrint = new UserPrint();
			printsArray = userPrint.fromJson(userPrints);
			userPrintAdapter.addAll(printsArray);
			printsList.setAdapter(userPrintAdapter);
			// Displays user roles
			userRolesArray = new ArrayList<UserRole>();
			userRoleAdapter = new UserRoleAdapter(mContext, userRolesArray);
			userRoleAdapter.clear();
			UserRole userRole = new UserRole();
			userRolesArray = userRole.fromJson(userRoles);
			userRoleAdapter.addAll(userRolesArray);
			rolesList.setAdapter(userRoleAdapter);
			// User is disabled
			if (!selectedUser.getEnabled()){
				disableScreen();
			}
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
		    	for (int i = 0; i < userRolesArray.size(); i++){
		    		// Checks if user already has the role
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
							if (response.getString("success").equalsIgnoreCase("false")){
								wait.dismiss();
								AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
								noConn.setMessage("Could not add user role");
								noConn.setTitle("Add User Role");
								noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {}
							    });
								noConn.setCancelable(false);
								noConn.setCanceledOnTouchOutside(false);
								noConn.show();
							} else {
								// Remove 'no record' entry if exists
								UserRole noRecord = new UserRole(userRoles.getJSONObject(0));
								if (noRecord.getID() == -1){
									userRoles.remove(0);
								}
								wait.dismiss();
								finish();
								Intent restart = getIntent();
								restart.putExtra("User Prints", userPrints.toString());
								restart.putExtra("User Roles", userRoles.toString());
								startActivity(getIntent());
							}
						} catch (JSONException e){
							e.printStackTrace();
						}
					} else {
						wait.dismiss();
						AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
						noConn.setMessage("Could not add user role");
						noConn.setTitle("Add User Role");
						noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {}
					    });
						noConn.setCancelable(false);
						noConn.setCanceledOnTouchOutside(false);
						noConn.show();
					}
				}
    		});
    		UserRole addUserRole = new UserRole();
    		Role role = roleAdapter.getItem(which);
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
	
	// Add user 
	public void addUser(){
		JSONPost addUser = new JSONPost(new JSONCallbackFunction(){
			@Override
			public void execute(JSONArray json) {
			}
		});
		User newUser = new User();
		
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
						if (response.getString("success").equalsIgnoreCase("true")){
							// User status changed successfully
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
							AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
							noConn.setMessage("Could not change user status");
							noConn.setTitle("Enable/Disable User");
							noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {}
						    });
							noConn.setCancelable(false);
							noConn.setCanceledOnTouchOutside(false);
							noConn.show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					wait.dismiss();
					AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
					noConn.setMessage("Could not change user status");
					noConn.setTitle("Enable/Disable User");
					noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
				    });
					noConn.setCancelable(false);
					noConn.setCanceledOnTouchOutside(false);
					noConn.show();
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
						if (response.getString("success").equalsIgnoreCase("true")){
							// User name changed successfully
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
							AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
							noConn.setMessage("Could not change user name");
							noConn.setTitle("User Name");
							noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {}
						    });
							noConn.setCancelable(false);
							noConn.setCanceledOnTouchOutside(false);
							noConn.show();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
				} else {
					wait.dismiss();
					AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
					noConn.setMessage("Could not change user name");
					noConn.setTitle("User Name");
					noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
				    });
					noConn.setCancelable(false);
					noConn.setCanceledOnTouchOutside(false);
					noConn.show();
				}
			}      	
        });
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
	
// TODO: gray out listview
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
