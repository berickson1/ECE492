// https://github.com/thecodepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView

package ca.ualberta.ece492.g9.biolock.customs;

import java.util.ArrayList;

import ca.ualberta.ece492.g9.biolock.R;
import ca.ualberta.ece492.g9.biolock.types.Role;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class RoleAdapter extends ArrayAdapter<Role> {
	
	public RoleAdapter(Context context, ArrayList<Role> roles) {
		super(context, R.layout.list_view_row_with_checkbox, roles);
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       Role role = getItem(position);    
       
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_row_with_checkbox, null);
       }
       
       // Lookup view for data population
       TextView name = (TextView) convertView.findViewById(R.id.listEntryName);
       CheckBox enabled = (CheckBox) convertView.findViewById(R.id.userEnabled);
       
       // Populate the data into the template view using the data object
       name.setText(role.getName());
       enabled.setChecked(role.getEnabled());
       
       // Return the completed view to render on screen
       return convertView;
   }
}
