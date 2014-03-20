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
	
	protected void onCreate(Bundle savedInstanceState) {
		final ListView listLocks;
		final ArrayList<HashMap<String, String>> lockArray = new ArrayList<HashMap<String, String>>();
		ListAdapter adapter = new SimpleAdapter(getBaseContext(), lockArray, R.layout.list_view_row, new String[] { "Name" }, new int[] { R.id.listDeviceName });
		List<LockInfo> locks;
		DatabaseHandler db = new DatabaseHandler(this);

		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

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
				String ip = lockArray.get(position).get("IP");
			    editor.putString("ipAddress", ip);
			    editor.commit();

				// Check connection to that lock
				JSONParser parser = new JSONParser(new JSONCallbackFunction() {
					@Override
					public void execute(JSONArray json) {
						if (json != null){
							try {
								JSONObject response = (JSONObject) json.get(0);
								if (response.getString("alive").equalsIgnoreCase("true")){
									// Lock is valid, will jump to admin login screen
									Intent login = new Intent(MainActivity.this, AdminLogin.class);
									wait.dismiss();
									listLocks.setEnabled(true);
									startActivity(login);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							// Cannot connect to lock
							wait.dismiss();
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
					public void execute(Integer response) {}
				});
				parser.execute(ip.concat("/alive"));
			}
		});
	}

	// User selected to add new lock
	public void newLock(View v) {
		Intent newLock = new Intent(MainActivity.this, NewLock.class);
		startActivity(newLock);
	}
}
