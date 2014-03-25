package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;

import org.json.JSONArray;

import ca.ualberta.ece492.g9.biolock.customs.JSONCallbackFunction;
import ca.ualberta.ece492.g9.biolock.customs.JSONParser;
import ca.ualberta.ece492.g9.biolock.customs.RoleAdapter;
import ca.ualberta.ece492.g9.biolock.types.Role;

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

//From Manage - user wishes to manage the roles
public class Roles extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	private static Context mContext;
	private static Intent updateRole;
	private ProgressDialog wait;
	
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_roles);
	
		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");
	}

	public void onResume(){
		wait = ProgressDialog.show(Roles.this, "Roles", "Loading roles", true, false, null);
		
		// Obtains roles from web server & displays role names
		JSONParser parser = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null) {
					final ListView roleList = (ListView) findViewById(R.id.listRoles);
					ArrayList<Role> rolesArray = new ArrayList<Role>();
					RoleAdapter adapter = new RoleAdapter(mContext, rolesArray);
					adapter.clear();
					Role role = new Role();
					rolesArray = role.fromJson(json);
					adapter.addAll(rolesArray);
					roleList.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					wait.dismiss();
					// Role is clicked on
					roleList.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							wait = ProgressDialog.show(Roles.this,"Role Information", "Loading role information", true, false, null);
							Role selectedRole = (Role) roleList.getItemAtPosition(position);
							updateRole = new Intent(Roles.this, NewRole.class);
							updateRole.putExtra("Role", selectedRole);
							getUsers(selectedRole);
							//startActivity(updateRole);
						}
					});
				}
			}
		});
		parser.execute(ip.concat("/roles"));
		super.onResume();
	}
	
	// User selected to add new role
	public void addNewRole(View v) {
		AlertDialog requestName = new AlertDialog.Builder(mContext).create();
		requestName.setMessage("Please enter role name");
		requestName.setTitle("Role Name");
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
            		noName.setTitle("Role Name");
            		noName.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {};
            		});
            		noName.show();
            	} else {
	            	Intent newRole = new Intent(Roles.this, NewRole.class);
	            	newRole.putExtra("Name", inputName.getText().toString());
	        		startActivity(newRole);
            	}
            }
        });
		requestName.show();
	}
	
	// Gets users who have this role
	public void getUsers(final Role selectedRole){
		JSONParser parseUsers = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					updateRole.putExtra("Users", json.toString());
					getRoleSched(selectedRole);
				} else {
					AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
					noConn.setMessage("Could not get users");
					noConn.setTitle("User");
					noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                	wait.dismiss();
		                	startActivity(updateRole);
		                }
		            });
					noConn.setCancelable(false);
					noConn.setCanceledOnTouchOutside(false);
					noConn.show();
				}
			}
		});
		//parseUsers.execute(ip.concat(""))
	}
	
	// Gets role schedules 
	public void getRoleSched(Role selectedRole){
		JSONParser parseRoleSched = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					updateRole.putExtra("Role Sched", json.toString());
					wait.dismiss();
					startActivity(updateRole);
				} else {
					wait.dismiss();
					AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
					noConn.setMessage("Could not get role schedule");
					noConn.setTitle("Role Schedule");
					noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                	wait.dismiss();
		                	startActivity(updateRole);
		                }
		            });
					noConn.setCancelable(false);
					noConn.setCanceledOnTouchOutside(false);
					noConn.show();
				}
			}
		});
		parseRoleSched.execute(ip.concat("/roleSchedules").concat(String.valueOf(selectedRole.getID())));
	}
}
