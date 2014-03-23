package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;

import org.json.JSONArray;

import ca.ualberta.ece492.g9.biolock.customs.JSONCallbackFunction;
import ca.ualberta.ece492.g9.biolock.customs.JSONParser;
import ca.ualberta.ece492.g9.biolock.customs.RoleAdapter;
import ca.ualberta.ece492.g9.biolock.types.Role;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

//From Manage - user wishes to manage the roles
public class Roles extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	private static Context mContext;
	private static RoleAdapter adapter;
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
					adapter = new RoleAdapter(mContext, rolesArray);
					adapter.clear();
					Role role = new Role();
					rolesArray = role.fromJson(json);
					adapter.addAll(rolesArray);
					roleList.setAdapter(adapter);
					wait.dismiss();
					// Role is clicked on
					roleList.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							Role roleSelected = (Role) roleList.getItemAtPosition(position);
							Intent updateRole = new Intent(Roles.this, NewRole.class);
							updateRole.putExtra("Role", roleSelected);
							startActivity(updateRole);
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
		Intent newRole = new Intent(Roles.this, NewRole.class);
		startActivity(newRole);
	}
}
