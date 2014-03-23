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
	
	protected void onCreate(Bundle savedInstanceState) {
		JSONArray userPrints = null;
		JSONArray userRoles = null;
		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_new_user);
		
		// Get contents on screen
		nameField = (EditText) findViewById(R.id.userNameFill);
		enabledStatus = (CheckBox) findViewById(R.id.enabledUserStatusBox);
		printsList = (ListView) findViewById(R.id.userPrints);
		rolesList = (ListView) findViewById(R.id.userRoles);
		addPrint = (TextView) findViewById(R.id.enrollPrint);
		addRole = (TextView) findViewById(R.id.enrollRole);
		update = (TextView) findViewById(R.id.updateUser);
		
		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");
				
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
			UserPrint userPrint = new UserPrint();
			printsArray = userPrint.fromJson(userPrints);
			userPrintAdapter.addAll(printsArray);
			printsList.setAdapter(userPrintAdapter);
			// Displays user roles
			ArrayList<UserRole> rolesArray = new ArrayList<UserRole>();
			userRoleAdapter = new UserRoleAdapter(mContext, rolesArray);
			UserRole userRole = new UserRole();
			rolesArray = userRole.fromJson(userRoles);
			userRoleAdapter.addAll(rolesArray);
			rolesList.setAdapter(userRoleAdapter);
			// User is disabled
			if (!selectedUser.getEnabled()){
				disableScreen();
			}
		} else {
			// Change update button text to add
			update.setText(Html.fromHtml("<u>Add</u>"));
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
		// NewUser or UpdateUser
		addPrint.putExtra("Caller", mContext.getClass().getSimpleName());
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
					    	if (selectedUser != null) {
					    		// Add role to existing user
					    		JSONPost postRole = new JSONPost(new JSONCallbackFunction() {
									@Override
									public void execute(JSONArray json) {

									}
					    		});
					    		postRole.execute(ip.concat("/userRole"), "insert", selectedUser.toJson().toString());
					    	} else {
					    		// Add role to new user
					    	}
					    }
					});
					roles.show();
				}
				roleAdapter.notifyDataSetChanged();
				wait.dismiss();
			}
		});
		parseRoles.execute(ip.concat("/roles"));
	}
	
	// Enables or disables user
	public void changeUserStatus(String status) {
		final ProgressDialog wait = ProgressDialog.show(NewUser.this,"Update User Status", "Updating user status", true, false, null);
		// JSONPost to enable or disable user
        JSONPost changeStatus = new JSONPost(new JSONCallbackFunction() {
        	@Override
			public void execute(JSONArray json) {
				if (json != null) {
					try {
						if (json.toString().contains("1")){
							wait.dismiss();
							finish();
							return;
						}
						JSONObject response = (JSONObject) json.get(0);
						if (!response.getString("success").equalsIgnoreCase("false")){
							// User status changed successfully
							wait.dismiss();
							finish();
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

	// Adds or updates the user
	public void updateUser(View v) {
		// Update user
		if (selectedUser != null){
			// Check enable status
			if (enabledStatus.isChecked() != selectedUser.getEnabled()){
				if (enabledStatus.isChecked()){
					changeUserStatus("enable");
				} else {
					changeUserStatus("delete");
				}
			}
			// Check user name
		// Add user
		} else {
			
		}
		// Close this activity
		//finish();
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
