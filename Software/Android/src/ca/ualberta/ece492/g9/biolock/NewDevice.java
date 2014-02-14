package ca.ualberta.ece492.g9.biolock;

import android.app.Activity;
import android.os.Bundle;

// From MainActivity - user requests to add new device
public class NewDevice extends Activity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_device);
	}
}
