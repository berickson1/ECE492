// Need to implement two-pane mode for large tablet size, may not be necessary
// Need to remove one hard coded value
// Check if device is authorized already - skip the AdminLogin screen

// http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/

package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

// Appears after splash screen - displays devices currently linked to db
public class MainActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		ListView listLocks;
		ArrayList<String> lockArray = new ArrayList<String>();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lockArray);
		DatabaseHandler db = new DatabaseHandler(this);
		List<LockInfo> locks;
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		// Test population of database
		//db.addLock(new LockInfo("http://berickson.ca/ECE492/", "Brent's Server"));
		
		listLocks = (ListView) findViewById(R.id.deviceList);
		locks = db.getAllLocks();
		for (LockInfo l : locks) {
			lockArray.add(l.name);
		}
		listLocks.setAdapter(adapter);
	}
	
	
	// Callback method indicating which device was selected and passes result to
	// next activity
	// TODO: Check if device  is authorized - skip AdminLogin screen
	public void onDeviceSelected(String id) {
		Intent deviceSelected = new Intent(MainActivity.this, AdminLogin.class);
		// TODO: item_id should be referenced in AdminLogin fragment
		deviceSelected.putExtra("item_id", id);
		startActivity(deviceSelected);
	}

	// User selected to add new lock
	public void newLock(View v) {
		Intent newLock = new Intent(MainActivity.this, NewLock.class);
		startActivity(newLock);
	}
}
