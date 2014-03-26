// https://github.com/thecodepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView

package ca.ualberta.ece492.g9.biolock.customs;

import java.util.ArrayList;

import ca.ualberta.ece492.g9.biolock.R;
import ca.ualberta.ece492.g9.biolock.types.UserRole;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UserRoleAdapter extends ArrayAdapter<UserRole> {
	String display;
	boolean enabled;
	
	public UserRoleAdapter(Context context, boolean enabled, String display, ArrayList<UserRole> userRoles) {
		super(context, R.layout.list_view_row_details, userRoles);
		this.display = display;
		this.enabled = enabled;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
		UserRole userRole = getItem(position);    
       
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_row_details, null);
       }
      
       // Lookup view for data population
       TextView role = (TextView) convertView.findViewById(R.id.listDetailName);
       
       // Checks if user is disabled, set text to gray
       if (!enabled){
    	   role.setTextColor(Color.GRAY);
    	   role.setFocusable(true);
       }
       
       // Populate the data into the template view using the data object
       if (userRole.getID() == -1){
    	   role.setText("No user roles found");
       } else if (display.equals("userName")){
    	   role.setText(String.valueOf(userRole.getUserName()));
       } else if (display.equals("roleName")){
    	   role.setText(String.valueOf(userRole.getName()));
       }
       
       // Return the completed view to render on screen
       return convertView;
   }
	
	public void remove(UserRole userRole){
		super.remove(userRole);
	}
}
