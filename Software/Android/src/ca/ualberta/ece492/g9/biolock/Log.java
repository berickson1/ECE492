package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;

import org.json.JSONArray;

import ca.ualberta.ece492.g9.biolock.customs.HistoryAdapter;
import ca.ualberta.ece492.g9.biolock.customs.JSONCallbackFunction;
import ca.ualberta.ece492.g9.biolock.customs.JSONParser;
import ca.ualberta.ece492.g9.biolock.types.History;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

//From Manage - user wishes to view the access log
public class Log extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	private static Context mContext;
	private static HistoryAdapter adapter;
	
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_log);
		final ProgressDialog wait = ProgressDialog.show(Log.this,"Log", "Loading access history log", true, false, null);
		
		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");

		// Obtains log from web server & displays log information
		JSONParser parser = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null) {
					final ListView historyList = (ListView) findViewById(R.id.listLog);
					ArrayList<History> historyArray = new ArrayList<History>();
					adapter = new HistoryAdapter(mContext, historyArray);
					History history = new History();
					historyArray = history.fromJson(json);
					adapter.addAll(historyArray);
					historyList.setAdapter(adapter);
					wait.dismiss();
					// User is clicked on
					/*historyList.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							User userSelected = (User) userList.getItemAtPosition(position);
							Intent updateUser = new Intent(Users.this, NewUser.class);
							updateUser.putExtra("User", userSelected);
							startActivity(updateUser);
						}
					});*/
				}
			}
			public void execute(Integer response) {}
		});
		parser.execute(ip.concat("/history"));
	}
	
	public void onResume(){
		if (adapter != null){
			// Updates listview 
			adapter.notifyDataSetChanged();
		}
		super.onResume();
	}
}
