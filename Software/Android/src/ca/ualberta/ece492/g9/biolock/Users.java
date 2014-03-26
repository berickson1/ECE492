package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;

import org.json.JSONArray;

import ca.ualberta.ece492.g9.biolock.customs.JSONCallbackFunction;
import ca.ualberta.ece492.g9.biolock.customs.JSONParser;
import ca.ualberta.ece492.g9.biolock.customs.UserAdapter;
import ca.ualberta.ece492.g9.biolock.types.User;

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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

//From Manage - user wishes to manage the users
public class Users extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	private static Context mContext;
	private Intent updateUser;
	
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_users);

		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");
	}
	
	public void onResume(){
		final ProgressDialog loadUsersWait = ProgressDialog.show(Users.this,"Users", "Loading users", true, false, null);
		// Obtains users from web server & displays user names
		JSONParser parser = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null) {
					final ListView userList = (ListView) findViewById(R.id.listUsers);
					ArrayList<User> usersArray = new ArrayList<User>();
					UserAdapter adapter = new UserAdapter(mContext, true, usersArray);
					adapter.clear();
					User user = new User();
					usersArray = user.fromJson(json);
					adapter.addAll(usersArray);
					userList.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					loadUsersWait.dismiss();
					// User is clicked on
					userList.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							User userSelected = (User) userList.getItemAtPosition(position);
							updateUser = new Intent(Users.this, NewUser.class);
							updateUser.putExtra("User", userSelected);
							getPrints(userSelected);
						}
					});
				} else {
					loadUsersWait.dismiss();
					AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
					noConn.setMessage("Could not get retrieve users");
					noConn.setTitle("Users");
					noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {
			            	finish();
			            }
			        });
					noConn.setCancelable(false);
					noConn.setCanceledOnTouchOutside(false);
					noConn.show();
				}
			}
		});
		parser.execute(ip.concat("/users"));
		super.onResume();
	}

	// User selected to add new user
	public void addNewUser(View v) {
		AlertDialog requestName = new AlertDialog.Builder(mContext).create();
		requestName.setMessage("Please enter user's name");
		requestName.setTitle("User Name");
		final EditText inputName = new EditText(this);
		requestName.setView(inputName);
		requestName.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });
		requestName.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	// User did not set a name
            	if (inputName.getText().toString().matches("")){
            		AlertDialog noName = new AlertDialog.Builder(mContext).create();
            		noName.setMessage("No name was set");
            		noName.setTitle("User Name");
            		noName.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {};
            		});
            		noName.show();
            	} else {
	            	Intent newUser = new Intent(Users.this, NewUser.class);
	            	newUser.putExtra("Name", inputName.getText().toString());
	        		startActivity(newUser);
            	}
            }
        });
		requestName.show();
	}
	
	// Get user prints
	public void getPrints(final User selectedUser){
		final ProgressDialog loadPrintsWait = ProgressDialog.show(Users.this,"Prints", "Loading user prints", true, false, null);
		// Obtain user's enrolled fingerprints
		JSONParser parsePrints = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					updateUser.putExtra("User Prints", json.toString());
					loadPrintsWait.dismiss();
					getRoles(selectedUser);
				} else {
					loadPrintsWait.dismiss();
					connFailed();
				}
			}
		});
		parsePrints.execute(ip.concat("/prints/").concat(String.valueOf(selectedUser.getID())));
	}
	
	public void getRoles(User selectedUser){
		final ProgressDialog loadRolesWait = ProgressDialog.show(Users.this,"Roles", "Loading user roles", true, false, null);
		// Obtain user's roles
		JSONParser parseRoles = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					updateUser.putExtra("User Roles", json.toString());
					loadRolesWait.dismiss();
					startActivity(updateUser);
				} else {
					loadRolesWait.dismiss();
					connFailed();
				}
			}
		});
		parseRoles.execute(ip.concat("/userRole/").concat(String.valueOf(selectedUser.getID())));
	}
	
	public void connFailed(){
		AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
		noConn.setMessage("Could not get user information");
		noConn.setTitle("Users");
		noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	startActivity(updateUser);
            }
        });
		noConn.setCancelable(false);
		noConn.setCanceledOnTouchOutside(false);
		noConn.show();
	}	
}
