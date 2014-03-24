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
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

//From Manage - user wishes to manage the users
public class Users extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	private static Context mContext;
	private Intent updateUser;
	private ProgressDialog wait;
	
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
		wait = ProgressDialog.show(Users.this,"Users", "Loading users", true, false, null);
		
		// Obtains users from web server & displays user names
		JSONParser parser = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null) {
					final ListView userList = (ListView) findViewById(R.id.listUsers);
					ArrayList<User> usersArray = new ArrayList<User>();
					UserAdapter adapter = new UserAdapter(mContext, usersArray);
					adapter.clear();
					User user = new User();
					usersArray = user.fromJson(json);
					adapter.addAll(usersArray);
					userList.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					wait.dismiss();
					// User is clicked on
					userList.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							wait = ProgressDialog.show(Users.this,"User Information", "Loading user information", true, false, null);
							User userSelected = (User) userList.getItemAtPosition(position);
							updateUser = new Intent(Users.this, NewUser.class);
							updateUser.putExtra("User", userSelected);
							getPrints(userSelected);
						}
					});
				}
			}
		});
		parser.execute(ip.concat("/users"));
		super.onResume();
	}

	// User selected to add new user
	public void addNewUser(View v) {
		Intent newUser = new Intent(Users.this, NewUser.class);
		startActivity(newUser);
	}
	
	// Get user prints
	public void getPrints(final User selectedUser){
		// Obtain user's enrolled fingerprints
		JSONParser parsePrints = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					updateUser.putExtra("User Prints", json.toString());
					getRoles(selectedUser);
				} else {
					AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
					noConn.setMessage("Could not get user prints");
					noConn.setTitle("User Prints");
					noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                	wait.dismiss();
		                	startActivity(updateUser);
		                }
		            });
					noConn.setCancelable(false);
					noConn.setCanceledOnTouchOutside(false);
					noConn.show();
				}
			}
		});
		parsePrints.execute(ip.concat("/prints/").concat(String.valueOf(selectedUser.getID())));
		//parsePrints.execute(ip.concat("/prints"));
	}
	
	public void getRoles(User selectedUser){
		// Obtain user's roles
		JSONParser parseRoles = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					updateUser.putExtra("User Roles", json.toString());
					wait.dismiss();
					startActivity(updateUser);
				} else {
					wait.dismiss();
					AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
					noConn.setMessage("Could not get user roles");
					noConn.setTitle("User Roles");
					noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                	wait.dismiss();
		                	startActivity(updateUser);
		                }
		            });
					noConn.setCancelable(false);
					noConn.setCanceledOnTouchOutside(false);
					noConn.show();
				}
			}
		});
		parseRoles.execute(ip.concat("/userRole/").concat(String.valueOf(selectedUser.getID())));
		//parseRoles.execute(ip.concat("/roles"));
	}
	
}
