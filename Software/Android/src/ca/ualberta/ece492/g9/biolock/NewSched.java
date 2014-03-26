
package ca.ualberta.ece492.g9.biolock;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.ualberta.ece492.g9.biolock.customs.JSONCallbackFunction;
import ca.ualberta.ece492.g9.biolock.customs.JSONPost;
import ca.ualberta.ece492.g9.biolock.types.Role;
import ca.ualberta.ece492.g9.biolock.types.RoleSchedule;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

//From NewRole - adding of new sched or updating a sched
public class NewSched extends Activity {
	public static final String PREFS_NAME = "CONNECTION";
	private static String ip;
	private static Context mContext;
	private RoleSchedule selectedSchedule;
	private RoleSchedule changeSchedule;
	private Role selectedRole;
	private CheckBox sundayBox;
	private CheckBox mondayBox;
	private CheckBox tuesdayBox;
	private CheckBox wednesdayBox;
	private CheckBox thursdayBox;
	private CheckBox fridayBox;
	private CheckBox saturdayBox;
	private TextView startHour;
	private TextView endHour;
	private TextView startDate;
	private TextView endDate;
	private TextView deleteSched;
	
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_new_schedule);
		
		// Gets the ip address
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ip = settings.getString("ipAddress", "noConn");
		// Get contents on screen
		sundayBox = (CheckBox) findViewById(R.id.sundayBox);
		mondayBox = (CheckBox) findViewById(R.id.mondayBox);
		tuesdayBox = (CheckBox) findViewById(R.id.tuesdayBox);
		wednesdayBox = (CheckBox) findViewById(R.id.wednesdayBox);
		thursdayBox = (CheckBox) findViewById(R.id.thursdayBox);
		fridayBox = (CheckBox) findViewById(R.id.fridayBox);
		saturdayBox = (CheckBox) findViewById(R.id.saturdayBox);
		startHour = (TextView) findViewById(R.id.startHourValue);
		endHour = (TextView) findViewById(R.id.endHourValue);
		startDate = (TextView) findViewById(R.id.startDateValue);
		endDate = (TextView) findViewById(R.id.endDateValue);
		deleteSched = (TextView) findViewById(R.id.deleteSched);
	}
	
	public void onResume(){
		// Retrieves information of the selected user or new user name
		Intent intent = getIntent();
		if (intent.getParcelableExtra("Role") != null) {
			selectedRole = intent.getParcelableExtra("Role");
		}
		if (intent.getParcelableExtra("Schedule") != null) {
			selectedSchedule = intent.getParcelableExtra("Schedule");
		}
		// Updating schedule
		if (selectedSchedule != null) {
			displaySchedule();
		} else {
			// Cannot click delete
			deleteSched.setEnabled(false);
		}
		super.onResume();
	}
	
	// Shows the schedule selected
	public void displaySchedule(){
		checkDays(selectedSchedule.getDays());
		startHour.setText(String.valueOf(selectedSchedule.getStartTime()));
		endHour.setText(String.valueOf(selectedSchedule.getEndTime()));
		startDate.setText(String.valueOf(selectedSchedule.getStartDate()));
		endDate.setText(String.valueOf(selectedSchedule.getEndDate()));
	}
	
	// Checks the day authorized
	public void checkDays(int days) {
		// Sunday
		if (days >= 1000000){
			sundayBox.setChecked(true);
		}
		// Monday
		if (days >= 100000){
			mondayBox.setChecked(true);
		}
		// Tuesday
		if (days >= 10000){
			tuesdayBox.setChecked(true);	
		}
		// Wednesday
		if (days >= 1000){
			wednesdayBox.setChecked(true);
		}
		// Thursday
		if (days >= 100){
			thursdayBox.setChecked(true);
		}
		// Friday
		if (days >= 10){
			fridayBox.setChecked(true);
		}
		// Saturday
		if (days >= 1){
			saturdayBox.setChecked(true);
		}
	}
	
	// Confirms deletion of schedule
	public void confirmDelete(View v) {
		final AlertDialog confirm  = new AlertDialog.Builder(mContext).create();
		confirm.setMessage("Do you want to remove this schedule");
		confirm.setTitle("Schedule");
		confirm.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				deleteSchedule();
			}
	    });
		confirm.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
	    });
		confirm.setCancelable(false);
		confirm.setCanceledOnTouchOutside(false);
		confirm.show();
	}
	
	// Delete this schedule
	public void deleteSchedule() {
		final ProgressDialog deleteSchedWait = ProgressDialog.show(NewSched.this, "Schedule", "Deleting role schedule", true, false, null);
		JSONPost deleteSchedule = new JSONPost(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							Intent deletedSched = getIntent();
							deleteSchedWait.dismiss();
							setResult(RESULT_CANCELED, deletedSched); 
							finish();
						} else {
							deleteSchedWait.dismiss();
							updateFail();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
				} else {
					deleteSchedWait.dismiss();
					updateFail();
				}
			}
		});
		deleteSchedule.execute(ip.concat("/roleSchedules"), "delete", selectedSchedule.toJson().toString());
	}
	
	// Updates the schedule
	public void updateSchedule() {
		final ProgressDialog updateSchedWait = ProgressDialog.show(NewSched.this, "Schedule", "Updating role schedule", true, false, null);
		JSONPost updateSched = new JSONPost(new JSONCallbackFunction() {
			@Override
			public void execute(JSONArray json) {
				if (json != null){
					try {
						JSONObject response = (JSONObject) json.get(0);
						if (response.getString("success").equalsIgnoreCase("true")){
							JSONObject idVal = (JSONObject) json.get(1);
							changeSchedule.setID(idVal.getInt("id"));
							Intent updatedSched = getIntent();
							updatedSched.putExtra("Schedule", changeSchedule);
							updateSchedWait.dismiss();
							setResult(RESULT_OK, updatedSched); 
							finish();
						} else {
							updateSchedWait.dismiss();
							updateFail();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
				} else {
					updateSchedWait.dismiss();
					updateFail();
				}
			}
		});
		int days = getDays();
		changeSchedule.setDays(days);
		changeSchedule.setStartTime(Integer.valueOf(startHour.getText().toString()));
		changeSchedule.setEndTime(Integer.valueOf(endHour.getText().toString()));
		changeSchedule.setStartDate(Long.valueOf(startDate.getText().toString()));
		changeSchedule.setEndDate(Long.valueOf(endDate.getText().toString()));
		// Update schedule
		if (selectedSchedule != null) {
			changeSchedule.setID(selectedSchedule.getID());
			changeSchedule.setRID(selectedSchedule.getRID());
			// Add schedule
			updateSched.execute(ip.concat("/roleSchedules"), "update", changeSchedule.toJson().toString());
		// Add schedule
		} else {
			changeSchedule.setID(0);
			changeSchedule.setRID(selectedRole.getID());
			// Add schedule
			updateSched.execute(ip.concat("/roleSchedules"), "insert", changeSchedule.toJson().toString());
		}
	}
	
	// Check if schedule has changed
	public void checkSched(View v){
		// Check for changes
		if (selectedSchedule != null) {
			// Check days
			int days = getDays();
			if (days != selectedSchedule.getDays()){
				updateSchedule();
			// Check start time
			} else if (Integer.valueOf(startHour.getText().toString()) != selectedSchedule.getStartTime()) {
				updateSchedule();
			// Check end time
			} else if (Integer.valueOf(endHour.getText().toString()) != selectedSchedule.getEndTime()) {
				updateSchedule();
			// Check start date
			} else if (Long.valueOf(startDate.getText().toString()) != selectedSchedule.getStartDate()) {
				updateSchedule();
			// Check end date
			} else if (Long.valueOf(endDate.getText().toString()) != selectedSchedule.getEndDate()) {
				updateSchedule();
			} else {
				Intent noChange = getIntent();
				setResult(RESULT_OK, noChange); 
				finish();
			}
		// New schedule, add to db
		} else {
			updateSchedule();
		}
	}
	
	// Get the days from the checkboxes
	public int getDays(){
		int sunday = sundayBox.isChecked() ? 1000000:0;
		int monday = mondayBox.isChecked() ? 100000:0;
		int tuesday = tuesdayBox.isChecked() ? 10000:0;
		int wednesday = wednesdayBox.isChecked() ? 1000:0;
		int thursday = thursdayBox.isChecked() ? 100:0;
		int friday = fridayBox.isChecked() ? 10:0;
		int saturday = saturdayBox.isChecked() ? 1:0;
		return sunday + monday + tuesday + wednesday + thursday + friday + saturday;
	}
	
	// Set start time of role
	public void setStartHour(View v){
		 Calendar calendar = Calendar.getInstance();
         int hour = calendar.get(Calendar.HOUR_OF_DAY);
         int minute = calendar.get(Calendar.MINUTE);
         TimePickerDialog selectStartHour = new TimePickerDialog(NewSched.this, new TimePickerDialog.OnTimeSetListener() {
             @Override
             public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            	 String startTime = null; 
            	 if (selectedHour < 10){
            		 startTime = "0" + selectedHour + ":";
            	 } else {
            		 startTime = selectedHour + ":";
            	 }
            	 if (selectedMinute < 10){
            		 startTime += "0" + selectedMinute;
            	 } else {
            		 startTime += selectedMinute;
            	 }
                 startHour.setText(startTime);
             }
         }, hour, minute, true);
         selectStartHour.setTitle("Select Start Time");
         selectStartHour.setCancelable(false);
         selectStartHour.show();
	}
	
	// Set end time of role
	public void setEndHour(View v){
		Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog selectEndHour = new TimePickerDialog(NewSched.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            	String endTime = null; 
	           	if (selectedHour < 10){
	           		endTime = "0" + selectedHour + ":";
	           	} else {
	           		endTime = selectedHour + ":";
	           	}
	           	if (selectedMinute < 10) {
	           		endTime += "0" + selectedMinute;
	           	} else {
	           		endTime += selectedMinute;
	           	}
	           	endHour.setText(endTime);
            }
        }, hour, minute, true);
        selectEndHour.setTitle("Select End Time");
        selectEndHour.setCancelable(false);
        selectEndHour.show();
	}
	
	// Set start date of role
	public void setStartDate(View v){
		Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog selectStartDate = new DatePickerDialog(NewSched.this, R.style.AppBaseTheme, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int selectedYear, int selectedMonth,
					int selectedDay) {
				String startDay = selectedYear + "-";
				if (selectedMonth < 10) {
					startDay += "0" + selectedMonth + "-";
				} else {
					startDay += selectedMonth + "-";
				}
				if (selectedDay < 10){
					startDay += "0" + selectedDay;
				} else {
					startDay += selectedDay;
				}
				startDate.setText(startDay);
			}
        }, year, month, day);
        selectStartDate.setTitle("Select Start Date");
        selectStartDate.show();
	}
	
	// Set end date of role
	public void setEndDate(View v){
		Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog selectEndDate = new DatePickerDialog(NewSched.this, R.style.AppBaseTheme, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int selectedYear, int selectedMonth,
					int selectedDay) {
				String endDay = selectedYear + "-";
				if (selectedMonth < 10) {
					endDay += "0" + selectedMonth + "-";
				} else {
					endDay += selectedMonth + "-";
				}
				if (selectedDay < 10){
					endDay += "0" + selectedDay;
				} else {
					endDay += selectedDay;
				}
				endDate.setText(endDay);
			}
        }, year, month, day);
        selectEndDate.setTitle("Select End Date");
        selectEndDate.show();
	}
	
	// Displays popup from failure
	public void updateFail(){
		AlertDialog noConn  = new AlertDialog.Builder(mContext).create();
		noConn.setMessage("Failed to update role");
		noConn.setTitle("Update");
		noConn.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});
		noConn.setCancelable(false);
		noConn.setCanceledOnTouchOutside(false);
		noConn.show();
	}
}
