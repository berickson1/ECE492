// https://github.com/thecodepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView

package ca.ualberta.ece492.g9.biolock.customs;

import java.util.ArrayList;

import ca.ualberta.ece492.g9.biolock.R;
import ca.ualberta.ece492.g9.biolock.types.UserPrint;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UserPrintAdapter extends ArrayAdapter<UserPrint> {
	boolean enabled;
	
	public UserPrintAdapter(Context context, boolean enabled, ArrayList<UserPrint> userPrints) {
		super(context, R.layout.list_view_row_details, userPrints);
		this.enabled = enabled;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
		UserPrint userPrint = getItem(position);    
       
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_row_details, null);
       }
       
       // Lookup view for data population
       TextView print = (TextView) convertView.findViewById(R.id.listDetailName);
       
       // Checks if user is disabled, set text to gray
       if (!enabled){
    	   print.setTextColor(Color.GRAY);
       }
       print.setFocusable(true);
       // Populate the data into the template view using the data object
       if (userPrint.getID() == -1){
    	   print.setText("No fingerprint found");
       } else {
    	   print.setText(String.valueOf(userPrint.getID()));
       }
       
       // Return the completed view to render on screen
       return convertView;
   }
}
