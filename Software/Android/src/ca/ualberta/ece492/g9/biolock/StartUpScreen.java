/* Referenced: http://www.androidhive.info/2013/07/how-to-implement-android-splash-screen-2/ */
package ca.ualberta.ece492.g9.biolock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

public class StartUpScreen extends FragmentActivity {
	
	private static final int SPLASHTIME = 2000;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.start_up_screen);  
        
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(StartUpScreen.this, ItemListActivity.class);
                startActivity(i);
 
                // close this activity
                finish();
            }
        }, SPLASHTIME);
    }
}