package ca.ualberta.ece492.g9.biolock;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import ca.ualberta.ece492.g9.biolock.customs.HistoryAdapter;
import ca.ualberta.ece492.g9.biolock.customs.JSONCallbackFunction;
import ca.ualberta.ece492.g9.biolock.customs.JSONParser;
import ca.ualberta.ece492.g9.biolock.types.History;
import ca.ualberta.ece492.g9.biolock.types.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

//From Manage - user wishes to view the access log
public class Log extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	private static Context mContext;
	private static HistoryAdapter adapter;
	private Intent showHistory;
	
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
					adapter = new HistoryAdapter(mContext, true, historyArray);
					History history = new History();
					historyArray = history.fromJson(json);
					adapter.addAll(historyArray);
					historyList.setAdapter(adapter);
					wait.dismiss();
					// User is clicked on
					historyList.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							History historySelected = (History) historyList.getItemAtPosition(position);
							showHistory = new Intent(Log.this, DetailHistory.class);
							showHistory.putExtra("History", historySelected);
							getUser(historySelected);
						}
					});
				}
			}
		});
		parser.execute(ip.concat("/history"));
	}
	
	public void getUser(History historySelected) {
		final ProgressDialog loadHistoryWait = ProgressDialog.show(Log.this, "History", "Loading log history details", true, false, null);
		JSONParser parseUser = new JSONParser(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					try {
						showHistory.putExtra("User", new User(json.getJSONObject(0)));
						loadHistoryWait.dismiss();
						startActivity(showHistory);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					loadHistoryWait.dismiss();
					AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
					noConn.setMessage("Could not get log history information");
					noConn.setTitle("Log History");
					noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {}
			        });
					noConn.setCancelable(false);
					noConn.setCanceledOnTouchOutside(false);
					noConn.show();
				}
			}
		});
		parseUser.execute(ip.concat("/users/").concat(String.valueOf(historySelected.getUID())));
	}
}
