// TODO: get the date and time
// https://github.com/thecodepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView

package ca.ualberta.ece492.g9.biolock.customs;

import java.util.ArrayList;

import ca.ualberta.ece492.g9.biolock.R;
import ca.ualberta.ece492.g9.biolock.types.History;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HistoryAdapter extends ArrayAdapter<History> {
	boolean enabled;
	
	public HistoryAdapter(Context context, boolean enabled, ArrayList<History> history) {
		super(context, R.layout.list_view_row_two_columns, history);
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
		History history = getItem(position);    
       
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_row_two_columns, null);
       }
       
       // Lookup view for data population
       TextView date = (TextView) convertView.findViewById(R.id.listDate);
       TextView time = (TextView) convertView.findViewById(R.id.listTime);
       
       // Populate the data into the template view using the data object
       if (history.getID() == -1){
    	   date.setText(String.valueOf(history.getTime()));
    	   time.setText(String.valueOf(history.getTime()));
       } else {
    	   date.setText("No access history found");
    	   time.setText("");
       }
       
       if (!enabled){
    	   date.setTextColor(Color.GRAY);
    	   date.setFocusable(true);
    	   time.setTextColor(Color.GRAY);
    	   time.setFocusable(true);
       }
       
       // Return the completed view to render on screen
       return convertView;
   }
}
