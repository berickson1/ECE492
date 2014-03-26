// https://github.com/thecodepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView

package ca.ualberta.ece492.g9.biolock.customs;

import java.util.ArrayList;

import ca.ualberta.ece492.g9.biolock.R;
import ca.ualberta.ece492.g9.biolock.types.Role;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class RoleAdapter extends ArrayAdapter<Role> {
	String layoutView;
	boolean enabled;
	
	public RoleAdapter(Context context, String layoutView, boolean enabled, ArrayList<Role> roles) {
		super(context, R.layout.list_view_row_with_checkbox, roles);
		this.layoutView = layoutView;
		this.enabled = enabled;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
	   TextView name;
		
       // Get the data item for this position
       Role role = getItem(position);    
       
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
    	   if (layoutView.equals("check")){
    		   convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_row_with_checkbox, null);
    	   } else {
    		   convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_row_details, null);
    	   }
       }
       
       // Layout with checkbox
       if (layoutView.equals("check")){
    	   name = (TextView) convertView.findViewById(R.id.listEntryName);
    	   CheckBox valEnabled = (CheckBox) convertView.findViewById(R.id.userEnabled);
    	   valEnabled.setChecked(role.getEnabled());
    	   valEnabled.setEnabled(false);
       // Layout without checkbox
       } else {
    	   name = (TextView) convertView.findViewById(R.id.listDetailName);
           
       }
       if (role.getID() != -1 ){
    	   name.setText(role.getName());
       } else {
    	   name.setText("No roles found");
       }
       // Gray out textview
       if (!enabled){
		   name.setTextColor(Color.GRAY);
		   name.setFocusable(true);
       }
       
       // Return the completed view to render on screen
       return convertView;
   }
}
