package ca.ualberta.ece492.g9.biolock;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import ca.ualberta.ece492.g9.biolock.types.History;
import ca.ualberta.ece492.g9.biolock.types.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

//From Manage - user wishes to view the access log
public class DetailHistory extends Activity {
	private History selectedHistory;
	private User selectedUser;
	private TextView user;
	private TextView accessResult;
	private TextView dateResult;
	private TextView timeResult;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_log_detail);
		
		// Get contents on screen
		user = (TextView) findViewById(R.id.user);
		accessResult = (TextView) findViewById(R.id.accessResult);
		dateResult = (TextView) findViewById(R.id.dateResult);
		timeResult = (TextView) findViewById(R.id.timeResult);
		
		// Retrieves information of the selected history
		Intent intent = getIntent();
		selectedHistory = (History) intent.getParcelableExtra("History");
		selectedUser = (User) intent.getParcelableExtra("User");
		
		// User was recognized
		if (selectedHistory.getUID() != -1) {
			user.setText(selectedUser.getName());
		} else {
			user.setText("Guest was unknown");
		}
		// Access result
		if (selectedHistory.getSuccess()){
			accessResult.setText("Access granted");
		} else {
			accessResult.setText("Access not granted");
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm");
 	   	dateFormat.setTimeZone(TimeZone.getDefault());
 	   	String dateString = dateFormat.format(selectedHistory.getTime()*1000L).toString();
 	   	int split = dateString.indexOf(",");
 	   	dateResult.setText(String.valueOf(dateString.substring(split + 1)));
 	   	timeResult.setText(dateString.subSequence(0,  split));
	}
}
