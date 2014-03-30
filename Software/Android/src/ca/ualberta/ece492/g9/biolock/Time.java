package ca.ualberta.ece492.g9.biolock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.ualberta.ece492.g9.biolock.customs.JSONCallbackFunction;
import ca.ualberta.ece492.g9.biolock.customs.JSONPost;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

// From Manage - user wishes to manage current time
public class Time extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	private static Context mContext;
	private static TextView currDate, currTime;
	private static int day, month, year;
	private static int hour, minute;
	private static Long currDateTime;

	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_time);
		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");

		// Set textview's current date
		Calendar cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);
		currDate = (TextView) findViewById(R.id.editDate);
		String currentDate = getDate(year, month, day);
		currDate.setText(getDate(year, month, day));

		// Set textview's current time
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
		currTime = (TextView) findViewById(R.id.editTime);
		String currentTime = getTime(hour, minute);
		currTime.setText(currentTime);
		
		//Gets current date time
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm");
			String dateString = currentDate + "," + currentTime;
			currDateTime = dateFormat.parse(dateString).getTime()/1000L;
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	// User wishes to change the current date
	public void changeDate(View v) {
		DatePickerDialog selectDate = new DatePickerDialog(Time.this,
				R.style.AppBaseTheme, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
						currDate.setText(getDate(selectedYear, selectedMonth, selectedDay));
					}
				}, year, month, day);
		selectDate.setTitle("Select Start Date");
		selectDate.show();
	}

	// user wishes to change the current time
	public void changeTime(View v) {
		TimePickerDialog selectTime = new TimePickerDialog(Time.this,
				new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
						currTime.setText(getTime(selectedHour, selectedMinute));
					}
				}, hour, minute, true);
		selectTime.setTitle("Select Start Time");
		selectTime.setCancelable(false);
		selectTime.show();
	}

	// Let user set the time of the system
	public void setTime(View v) {
		final ProgressDialog setTimeWait = ProgressDialog.show(Time.this, "Set Date and Time", "Setting date and time", true, false, null);
		JSONPost setOwnTime = new JSONPost(new JSONCallbackFunction(){
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							setTimeWait.dismiss();
							finish();
						} else {
							setTimeWait.dismiss();
							setFail();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
				} else {
					setTimeWait.dismiss();
					setFail();
				}
			}
		});
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm");
			String dateString = currDate.getText().toString() + "," + currTime.getText().toString();
			Long date = dateFormat.parse(dateString).getTime()/1000L;
			setOwnTime.execute(ip.concat("/time"), "update", String.valueOf(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	// Let user set the time as current unix time
	public void setCurrentDateTime(View v) {
		final ProgressDialog setTimeWait = ProgressDialog.show(Time.this, "Set Date and Time", "Setting date and time", true, false, null);
		JSONPost setCurrentTime = new JSONPost(new JSONCallbackFunction(){
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							setTimeWait.dismiss();
							finish();
						} else {
							setTimeWait.dismiss();
							setFail();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
				} else {
					setTimeWait.dismiss();
					setFail();
				}
			}
		});
		setCurrentTime.execute(ip.concat("/time"), "update", String.valueOf(currDateTime));
	}

	// Gets the date
	public String getDate(int year, int month, int day) {
		String date = year + "-";
		// Month offset
		month += 1;
		if (month < 10) {
			date += "0" + month + "-";
		} else {
			date += month + "-";
		}
		if (day < 10) {
			date += "0" + day;
		} else {
			date += day;
		}
		return date;
	}

	// Gets the time
	public String getTime(int hour, int minute) {
		String time = null;
		if (hour < 10) {
			time = "0" + hour + ":";
		} else {
			time = hour + ":";
		}
		if (minute < 10) {
			time += "0" + minute;
		} else {
			time += minute;
		}
		return time;
	}
	
	// Displays popup from failure
	public void setFail(){
		AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
		noConn.setMessage("Failed to set date and time");
		noConn.setTitle("Set Date and Time");
		noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});
		noConn.setCancelable(false);
		noConn.setCanceledOnTouchOutside(false);
		noConn.show();
	}
}
