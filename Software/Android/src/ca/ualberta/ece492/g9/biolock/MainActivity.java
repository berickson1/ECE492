// Check if device is authorized already - skip the AdminLogin screen

// http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/

package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
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

	protected void onCreate(Bundle savedInstanceState) {
		final ListView listLocks;
		final ArrayList<HashMap<String, String>> lockArray = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> lockHash = new HashMap<String, String>();
		ListAdapter adapter = new SimpleAdapter(getBaseContext(), lockArray, R.layout.list_view_row, new String[] { "Name" }, new int[] { R.id.listEntryName });
		List<LockInfo> locks;
		DatabaseHandler db = new DatabaseHandler(this);

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		// Test population of database
		// db.addLock(new LockInfo("http://berickson.ca/ECE492/", "Brent's Server"));

		listLocks = (ListView) findViewById(R.id.deviceList);
		// Display all locks
		locks = db.getAllLocks();
		for (LockInfo l : locks) {
			lockHash.put("IP", l.ip);
			lockHash.put("Name", l.name);
			lockArray.add(lockHash);
		}
		listLocks.setAdapter(adapter);

		listLocks.setOnItemClickListener(new OnItemClickListener() {
			// Lock selected, will jump to admin login screen
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String ip = lockArray.get(position).get("IP");
				Intent login = new Intent(MainActivity.this, AdminLogin.class);
				login.putExtra("IP", ip);
				startActivity(login);
			}
		});
	}

	// User selected to add new lock
	public void newLock(View v) {
		Intent newLock = new Intent(MainActivity.this, NewLock.class);
		startActivity(newLock);
	}
}
