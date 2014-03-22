package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;

import org.json.JSONArray;

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
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

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
	
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_new_user);
		final ProgressDialog wait = ProgressDialog.show(NewUser.this,"User Information", "Loading user information", true, false, null);

		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");
				
		// Retrieves information of the selected user if exists
		Intent intent = getIntent();
		selectedUser = (User) intent.getParcelableExtra("User");
		if (selectedUser != null){
			// Displays retrieved information on screen
			EditText nameField = (EditText) findViewById(R.id.userNameFill);
			CheckBox enabledStatus = (CheckBox) findViewById(R.id.enabledUserStatusBox);
			nameField.setText(selectedUser.getName());
			enabledStatus.setChecked(selectedUser.getEnabled());
			
			// Obtain user's enrolled fingerprints
			JSONParser parsePrints = new JSONParser(new JSONCallbackFunction() {
				@Override
				public void execute(JSONArray json) {
					if (json != null){
						final ListView printsList = (ListView) findViewById(R.id.userPrints);
						ArrayList<UserPrint> printsArray = new ArrayList<UserPrint>();
						userPrintAdapter = new UserPrintAdapter(mContext, printsArray);
						UserPrint userPrint = new UserPrint();
						printsArray = userPrint.fromJson(json);
						userPrintAdapter.addAll(printsArray);
						printsList.setAdapter(userPrintAdapter);
					}
				}
				public void execute(Integer response) {}
			});
			//parsePrints.execute(ip.concat("/users/").concat(String.valueOf(selectedUser.getID())));
			parsePrints.execute(ip.concat("/prints"));
			
			// Obtain user's roles
			JSONParser parseRoles = new JSONParser(new JSONCallbackFunction() {
				@Override
				public void execute(JSONArray json) {
					if (json != null){
						final ListView rolesList = (ListView) findViewById(R.id.userRoles);
						ArrayList<UserRole> rolesArray = new ArrayList<UserRole>();
						userRoleAdapter = new UserRoleAdapter(mContext, rolesArray);
						UserRole userRole = new UserRole();
						rolesArray = userRole.fromJson(json);
						userRoleAdapter.addAll(rolesArray);
						rolesList.setAdapter(userRoleAdapter);
						wait.dismiss();
					}
				}
				public void execute(Integer response) {}
			});
			parseRoles.execute(ip.concat("/userRole/").concat(String.valueOf(selectedUser.getID())));
			//parseRoles.execute(ip.concat("/roles"));
		}
	}
	
	public void onResume(){
		// Updates listview 
		if (userPrintAdapter != null){
			userPrintAdapter.notifyDataSetChanged();
		}
		if (userRoleAdapter != null) {
			userRoleAdapter.notifyDataSetChanged();
		}
		if (roleAdapter != null){
			roleAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}
	
	// Jumps to AdminLogin to add new print
	public void enrollPrint(View v) {
		Intent addPrint = new Intent(NewUser.this, AdminLogin.class);
		startActivity(addPrint);
	}
	
	// Popup to select from roles
	public void addRole(View v) {
		final ProgressDialog wait = ProgressDialog.show(NewUser.this,"User Information", "Loading roles", true, false, null);

		JSONParser parseRoles = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					ArrayList<Role> rolesArray = new ArrayList<Role>();
					roleAdapter = new RoleAdapter(mContext, rolesArray);
					Role role = new Role();
					rolesArray = role.fromJson(json);
					roleAdapter.addAll(rolesArray);
					AlertDialog.Builder roles = new AlertDialog.Builder(mContext);
					roles.setTitle("Roles");
					roles.setAdapter(roleAdapter, new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					    }
					});
					roles.show();
				}
				roleAdapter.notifyDataSetChanged();
				wait.dismiss();
			}
			public void execute(Integer response) {}
		});
		parseRoles.execute(ip.concat("/roles"));
	}

	// Confirms deletion of user before deletion
	public void confirmDelete(View v) {
		AlertDialog deleteConfirm  = new AlertDialog.Builder(mContext).create();
		deleteConfirm.setTitle("Delete user");
		deleteConfirm.setMessage("Are you sure you want to delete this user?");
		deleteConfirm.setCancelable(false);
		deleteConfirm.setCanceledOnTouchOutside(false);
		deleteConfirm.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	final ProgressDialog wait = ProgressDialog.show(NewUser.this,"Delete User", "Deleting user", true, false, null);
            	// JSONPost to delete user
            	JSONPost deleteUser = new JSONPost(new JSONCallbackFunction() {
            		@Override
					public void execute(Integer response) {
						if (response == 200){
							// User deleted
							wait.dismiss();
							finish();
						} else {
							// User could not be deleted
							AlertDialog result  = new AlertDialog.Builder(mContext).create();
							result.setTitle("Delete");
							result.setMessage("User could not be deleted");
							result.setCancelable(false);
							result.setCanceledOnTouchOutside(false);
							result.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
				                public void onClick(DialogInterface dialog, int which) {}
				            });
							wait.dismiss();
							result.show();
						}
						
					}
					public void execute(JSONArray json) {}
            	});
            	//deleteUser.execute(ip.concat("/users/".concat(String.valueOf(selectedUser.getID()))), "delete", selectedUser.toString());
            	deleteUser.execute(ip.concat("/users"));
            }
        });
		deleteConfirm.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });
		deleteConfirm.show();
	}

	// TODO: Check updated values before updating users
	// Jumps to Users
	public void updateUser(View v) {
		// Runs Users
		//Intent i = new Intent(NewUser.this, Users.class);
		//startActivity(i);

		// Close this activity
		finish();
	}
}
