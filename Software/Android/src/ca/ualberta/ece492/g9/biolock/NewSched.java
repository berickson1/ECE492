
package ca.ualberta.ece492.g9.biolock;

import ca.ualberta.ece492.g9.biolock.types.Role;
import ca.ualberta.ece492.g9.biolock.types.RoleSchedule;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;



//From NewRole - adding of new sched or updating a sched
public class NewSched extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	private static Context mContext;
	private Role selectedRole;
	private RoleSchedule selectedSchedule;
	
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_new_schedule);
		
		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");
	}
	
	public void onResume(){
		// Retrieves information of the selected user or new user name
		Intent intent = getIntent();
		if (intent.getParcelableExtra("Role") != null) {
			selectedRole = intent.getParcelableExtra("Role");
		}
		if (intent.getParcelableExtra("Schedule") != null) {
			selectedSchedule = intent.getParcelableExtra("Schedule");
		}
		
		super.onResume();
	}

	
}
