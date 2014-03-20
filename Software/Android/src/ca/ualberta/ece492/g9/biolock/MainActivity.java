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
import android.widget.Toast;

// Appears after splash screen - displays devices app already knows
public class MainActivity extends Activity {
	public static final String PREFS_NAME = "CONNECTION";

	protected void onCreate(Bundle savedInstanceState) {
		final ListView listLocks;
		final ArrayList<HashMap<String, String>> lockArray = new ArrayList<HashMap<String, String>>();
		ListAdapter adapter = new SimpleAdapter(getBaseContext(), lockArray, R.layout.list_view_row, new String[] { "Name" }, new int[] { R.id.listDeviceName });
		List<LockInfo> locks;
		DatabaseHandler db = new DatabaseHandler(this);

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
// TODO: Need to change this to alive
								if (response.getString("enabled").equalsIgnoreCase("true")){
									// Lock is valid, will jump to admin login screen
									Intent login = new Intent(MainActivity.this, AdminLogin.class);
									startActivity(login);
									listLocks.setEnabled(true);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							// Cannot connect to lock
							Toast.makeText(getApplicationContext(), "Cannot connect currently", Toast.LENGTH_SHORT).show();
							listLocks.setEnabled(true);
						}
					}
					public void execute(Integer response) {}
				});
// TODO: Need to change this to alive
				parser.execute(ip.concat("/users"));
			}
		});
	}

	// User selected to add new lock
	public void newLock(View v) {
		Intent newLock = new Intent(MainActivity.this, NewLock.class);
		startActivity(newLock);
	}
}
