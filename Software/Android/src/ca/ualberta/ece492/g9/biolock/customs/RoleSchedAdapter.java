// https://github.com/thecodepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView

package ca.ualberta.ece492.g9.biolock.customs;

import java.util.ArrayList;

import ca.ualberta.ece492.g9.biolock.R;
import ca.ualberta.ece492.g9.biolock.types.RoleSchedule;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RoleSchedAdapter extends ArrayAdapter<RoleSchedule> {
	boolean enabled;
	
	public RoleSchedAdapter(Context context, boolean enabled, ArrayList<RoleSchedule> roleScheduless) {
		super(context, R.layout.list_view_row_details, roleScheduless);
		this.enabled = enabled;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
		RoleSchedule roleSched = getItem(position);    
       
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_row_details, null);
       }
       
       // Lookup view for data population
       TextView sched = (TextView) convertView.findViewById(R.id.listDetailName);
       
       // Checks if user is disabled, set text to gray
       if (!enabled){
    	   sched.setTextColor(Color.GRAY);
    	   sched.setFocusable(true);
       }
      
       // Populate the data into the template view using the data object
       if (roleSched.getID() == -1){
    	   sched.setText("No schedules found");
       } else {
    	   sched.setText(String.valueOf(roleSched.getID()));
       }
       
       // Return the completed view to render on screen
       return convertView;
   }
}
