/* Referenced: http://www.androidhive.info/2013/07/how-to-implement-android-splash-screen-2/ */
package ca.ualberta.ece492.g9.biolock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

// Splash screen displaying BitLock logo for 2 seconds
public class StartUpScreen extends FragmentActivity {
	
	private static final int SPLASHTIME = 2000;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.start_up_screen);  
        
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
            	// Runs MainActivity
                Intent i = new Intent(StartUpScreen.this, MainActivity.class);
                startActivity(i);
 
                // Close this activity
                finish();
            }
        }, SPLASHTIME);
    }
}