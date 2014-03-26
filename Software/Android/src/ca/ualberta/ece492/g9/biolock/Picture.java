package ca.ualberta.ece492.g9.biolock;

import ca.ualberta.ece492.g9.biolock.customs.GetPicture;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/* From Manage to display image
 */
public class Picture extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_picture);
		
		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");
		
		ImageView image = (ImageView) findViewById(R.id.pictureView);
		GetPicture loadPicture = new GetPicture(image, this);
		loadPicture.execute(ip.concat("/pic"));
	}
	
	// Disable back button press
	@Override
	public void onBackPressed() {}
}
