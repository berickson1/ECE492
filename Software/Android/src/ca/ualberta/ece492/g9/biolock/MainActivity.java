// TODO: deal with unconnectable devices

package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.ualberta.ece492.g9.biolock.customs.DatabaseHandler;
import ca.ualberta.ece492.g9.biolock.customs.JSONCallbackFunction;
import ca.ualberta.ece492.g9.biolock.customs.JSONParser;
import ca.ualberta.ece492.g9.biolock.types.LockInfo;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

// Appears after splash screen - displays devices app already knows
public class MainActivity extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static Context mContext;
	private static DatabaseHandler db;
	private static String ip;
	private static ListView listLocks;
	
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
	}

	public void onResume() {
		final ArrayList<HashMap<String, String>> lockArray = new ArrayList<HashMap<String, String>>();
		ListAdapter adapter = new SimpleAdapter(getBaseContext(), lockArray, R.layout.list_view_row, new String[] { "Name" }, new int[] { R.id.listDeviceName });
		List<LockInfo> locks;
		db = new DatabaseHandler(this);

		// Test population of database
		//db.addLock(new LockInfo("http://192.168.1.120", "Group 9 Project"));
		
		listLocks = (ListView) findViewById(R.id.deviceList);
		// Display all locks
		locks = db.getAllLocks();
		for (LockInfo l : locks) {
			HashMap<String, String> lockHash = new HashMap<String, String>();
			lockHash.put("IP", l.getIP());
			lockHash.put("Name", l.getName());
			lockArray.add(lockHash);
		}
		listLocks.setAdapter(adapter);
		
		// User clicked on a lock
		listLocks.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Forces user to wait
				final ProgressDialog wait = ProgressDialog.show(MainActivity.this,"Device Connection", "Please wait for connection", true, false, null);
				listLocks.setEnabled(false);
				// Store ip in preferences for access in other intents
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				ip = lockArray.get(position).get("IP");
			    editor.putString("ipAddress", ip);
			    editor.commit();

				// Check connection to that lock
				JSONParser parser = new JSONParser(new JSONCallbackFunction() {
					@Override
					public void execute(JSONArray json) {
						if (json != null){
							try {
								JSONObject response = (JSONObject) json.get(0);
								// Connection with lock can be established
								if (response.getString("alive").equalsIgnoreCase("true")){
									DatabaseHandler db = new DatabaseHandler(mContext);
									LockInfo lock = db.getLock(ip);
									// Owner of this device is not admin of the lock
									if (lock.getAdmin() == 0){
										wait.dismiss();
										login();
									// Owner if this device is admin - skip login screen
									} else if (lock.getAdmin() == 1) {
										wait.dismiss();
										checkUser(lock);
									}
								} else {
									wait.dismiss();
									noConnection();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						// Cannot connect to lock
						} else {
							wait.dismiss();
							noConnection();
						}
					}
				});
				parser.execute(ip.concat("/alive"));
			}
		});	
		super.onResume();
	}
	
	// User selected to add new lock
	public void newLock(View v) {
		Intent newLock = new Intent(MainActivity.this, NewLock.class);
		startActivity(newLock);
	}
	
	// Check if admin status has be revoked or user has been disabled
	public void checkUser(final LockInfo lock){
		final ProgressDialog checkUserWait = ProgressDialog.show(MainActivity.this,"Permissions", "Checking permissions", true, false, null);
		JSONParser checkUserStatus = new JSONParser(new JSONCallbackFunction() {		
			@Override
			public void execute(JSONArray json) {
				if (json != null) {
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.isNull("success")){
							// Owner of this device is an admin, can skip the login screen
							if (response.getString("admin").equalsIgnoreCase("true")) {
								checkUserWait.dismiss();
								listLocks.setEnabled(true);
								Intent manage = new Intent(MainActivity.this, Manage.class);
								startActivity(manage);
							// User is no longer admin
							} else {
								lock.setAdmin(0);
								lock.setUserPrint(-1);
								db.updateLock(lock);
								checkUserWait.dismiss();
								login();
							}
						// Fingerprint no longer authorized
						} else {
							lock.setAdmin(0);
							lock.setUserPrint(-1);
							db.updateLock(lock);
							checkUserWait.dismiss();
							login();
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					checkUserWait.dismiss();
					noConnection();
				}
			}
		});
		checkUserStatus.execute(ip.concat("/checkAdmin/").concat(String.valueOf(lock.getUserPrint())));
	}
	
	// User needs to login
	public void login(){
		Intent login = new Intent(MainActivity.this, AdminLogin.class);
		login.putExtra("Caller", mContext.getClass().getSimpleName());
		listLocks.setEnabled(true);
		startActivity(login);
	}
	
	// Pop up message stating connection was not established
	public void noConnection(){
		AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
		noConn.setMessage("Could not connection to device");
		noConn.setTitle("Device Connection");
		noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });
		noConn.setCancelable(false);
		noConn.setCanceledOnTouchOutside(false);
		noConn.show();
		listLocks.setEnabled(true);
	}
}
