// https://github.com/thecodepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView

package ca.ualberta.ece492.g9.biolock.customs;

import java.util.ArrayList;

import ca.ualberta.ece492.g9.biolock.R;
import ca.ualberta.ece492.g9.biolock.types.User;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class UserAdapter extends ArrayAdapter<User> {
	boolean enabled;
	
	public UserAdapter(Context context, boolean enabled, ArrayList<User> users) {
		super(context, R.layout.list_view_row_with_checkbox, users);
		this.enabled = enabled;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       User user = getItem(position);    
       
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_row_with_checkbox, null);
       }
       
       // Lookup view for data population
       TextView name = (TextView) convertView.findViewById(R.id.listEntryName);
       CheckBox checkEnable = (CheckBox) convertView.findViewById(R.id.userEnabled);
       checkEnable.setEnabled(false);
       
       // Checks if user is disabled, set text to gray
       if (!enabled){
    	   name.setTextColor(Color.GRAY);
    	   name.setFocusable(true);
       }
       
       // Populate the data into the template view using the data object
       name.setText(user.getName());
       checkEnable.setChecked(user.getEnabled());
       
       // Return the completed view to render on screen
       return convertView;
   }
}
