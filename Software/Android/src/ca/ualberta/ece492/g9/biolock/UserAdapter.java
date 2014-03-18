// https://github.com/thecodepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView

package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;

import ca.ualberta.ece492.g9.biolock.types.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UserAdapter extends ArrayAdapter<User> {
	
	public UserAdapter(Context context, ArrayList<User> users) {
		super(context, R.layout.list_view_row, users);
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       User user = getItem(position);    
       
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_row, null);
       }
       
       // Lookup view for data population
       TextView name = (TextView) convertView.findViewById(R.id.listEntryName);
       
       // Populate the data into the template view using the data object
       name.setText(user.name);
       
       // Return the completed view to render on screen
       return convertView;
   }
}
