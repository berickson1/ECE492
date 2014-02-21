// Need to implement two-pane mode for large tablet size, may not be necessary
// Need to remove one hard coded value
// Check if device is authorized already - skip the AdminLogin screen

package ca.ualberta.ece492.g9.biolock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

// Appears after splash screen - displays devices currently linked to db
public class MainActivity extends FragmentActivity implements
		DeviceListFragment.Callbacks {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
	}

	@Override
	// Displays the action bar - currently not implemented
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Callback method indicating which device was selected and passes result to
	// next activity
	// TODO: Check if device  is authorized - skip AdminLogin screen
	@Override
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
