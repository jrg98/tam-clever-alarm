package com.vutbr.fit.tam.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.calendar.CalendarChecker;
import com.vutbr.fit.tam.calendar.ChangeObserver;
import com.vutbr.fit.tam.calendar.Event;
import com.vutbr.fit.tam.database.AlarmAdapter;
import com.vutbr.fit.tam.database.AlarmTable;
import com.vutbr.fit.tam.database.EventsDatabase;
import com.vutbr.fit.tam.database.SettingsAdapter;
import com.vutbr.fit.tam.gui.DaySimpleAdapter;
import com.vutbr.fit.tam.gui.Days;
import com.vutbr.fit.tam.nofitication.NotificationHelper;
import com.vutbr.fit.tam.widget.WidgetRefreshService;
import com.vutbr.fit.tam.alarm.Alarm;



public class CleverAlarm extends Activity implements OnItemClickListener, Days {
	
	
	private enum Identifiers {
		DAY, ALARM, SLEEPMODE, TODAY
	};
	
	
	/**
	 * GUI elements in this activity
	 */
	/*
	TextView textView;
	Button queryButton;
	Button alarmButton;
	Button ringingButton;
		*/
	NotificationHelper notificationHelper;
	
	private ListView mainDaysListView;
	private ArrayList<HashMap<String, String>> daysListItems;
	
	
	/**
	 * Initialize activity
	 */
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        
        this.checkFirstLaunch();
        
        /*
        // TODO not sure this is working always as requested, eg. app is in background
        // maybe a service will be better
        this.registerCalendarChangeObserver();

        // NOTIFICATION EXAMPLE
        this.notificationHelper = new NotificationHelper(this);
        this.notificationHelper.setFlags(Notification.FLAG_NO_CLEAR);
        this.notificationHelper.show(
        		R.string.app_name, 
        		R.string.notification_example, 
        		NotificationHelper.TYPE_APPLICATION);
		*/
    }
    
    /**
     * Create menu
     * OnResume because update after setting alarm
     */
	@Override
	protected void onResume() {
		
		super.onResume();
        // initialize GUI elements
		this.createtWeekMenu();
	}

	/**
	 * Create menu for 7 days
	 */
	private void createtWeekMenu() {
		
        this.initList();
        
		Date date = new Date();
        int day = date.getDay();
                
        for (int i = 0; i < 7; i++) {

    		this.addListItem(i, shortDays[i], i == day);
    		
        }
 
		this.createList();
	}
	
	/**
	 * Initialize list
	 */
	private void initList () {
		this.mainDaysListView = (ListView) findViewById(R.id.mainDaysListView);
		this.daysListItems = new ArrayList<HashMap<String, String>>();
	}
		
	/**
	 * Add new item to list
	 * Must call initList at first
	 * Must call before createList 
	 * 
	 * @param title string resource ID
	 * @param alarm string actual day set alarm 
	 * @param sleepmode string actual day
	 */
    private void addListItem (int index, int day, boolean today) {
    	
    	 AlarmAdapter aD;
    	 String alarm = null;
    	 String sleepmode = null;
    	 
    	 final String timeFormat = this.loadTimeFormat();
    			     	 
    	 HashMap<String, String> map = new HashMap<String, String>();
    	 map.put(Identifiers.DAY.toString(), this.getString(day));
    	 map.put(Identifiers.TODAY.toString(), String.valueOf(today));

    	 
    	try {
			aD = new AlarmAdapter(this).open();
			Cursor cursorDAY = aD.fetchAlarm(index);
						
			if (cursorDAY.moveToFirst()) {
				
				Event event = this.getFirstDayEvent(index);
				
				
				if (event != null) {
						alarm = DateFormat.format(timeFormat, event.getBeginDate().getTime() -
															  cursorDAY.getLong(1)).toString();
				}
				else {
					if (cursorDAY.getInt(0) > 0) {
						alarm = DateFormat.format(timeFormat, cursorDAY.getLong(2) - DateUtils.HOUR_IN_MILLIS).toString();
					}
				}
				
				if (cursorDAY.getInt(4) > 0) {
					sleepmode = DateFormat.format(timeFormat, cursorDAY.getLong(3) - DateUtils.HOUR_IN_MILLIS).toString();
				}

			}
			
			if (alarm == null) {
				alarm = this.getResources().getString(R.string.not_set) ;				
			}
			
			if (sleepmode == null) {
				sleepmode = this.getResources().getString(R.string.not_set);
			}
			

			map.put(Identifiers.ALARM.toString(), alarm);
			map.put(Identifiers.SLEEPMODE.toString(), sleepmode);
			

			aD.close();
		
		} catch (Exception ex) {
			// mozna hlaska o zlyhani nacitania DB
			Log.e("CalendarChecker", "AlarmAdapter error: "+ ex.toString());
			//Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
			return;
		} finally {
			this.daysListItems.add(map);
		}

	}
	
    private Event getFirstDayEvent(int day) {
    	
    	EventsDatabase database = new EventsDatabase(this);
        
    	Date date = new Date();
        int today = date.getDay();
        
        // How many days to choosen day
        today = day - today;
        if (today < 0) today += 7; 
  
    	Date from = new Date();
    	final long startDay = from.getTime() - from.getHours() * DateUtils.HOUR_IN_MILLIS
    			 							 - from.getMinutes() * DateUtils.MINUTE_IN_MILLIS
    			 							 - from.getSeconds() * DateUtils.SECOND_IN_MILLIS;
    				        	
    	from.setTime(startDay + DateUtils.DAY_IN_MILLIS * (today));
    	
    	return database.getFirstEvent(from, EventsDatabase.STATUS_DONT_CARE);
    	
    }
    
    private String loadTimeFormat() {
    	
    	SettingsAdapter settingsAdapter = new SettingsAdapter(this);
    	settingsAdapter.open();
    	
    	String format = settingsAdapter.fetchSetting("timeformat", "0");
    	    	
    	// Defaultni format
    	if (format == "0") {
    		
    		format = DateFormat.HOUR_OF_DAY + ":" + DateFormat.MINUTE + DateFormat.MINUTE;
    		
    		if (!settingsAdapter.updateSetting("timeformat", format))
    	        	settingsAdapter.insertSetting("timeformat",  format);
    
    	}
        settingsAdapter.close();
        
        return format;
    }
	
	/**
	 * Create list with items
	 * Must call initList at first
	 */
	private void createList () {
		DaySimpleAdapter adapter = new DaySimpleAdapter(
				this.getBaseContext(),
				this.daysListItems, 
				R.layout.mains_row, 
				new String[] {
					Identifiers.DAY.toString(),
					Identifiers.ALARM.toString(),
					Identifiers.SLEEPMODE.toString(),
					Identifiers.TODAY.toString(),
				}, 
				new int[] {
					R.id.dayRowTitle,
					R.id.dayRowAlarm,
					R.id.dayRowSleepmode,
					R.id.dayRowToday
				}
			);

		
			this.mainDaysListView.setAdapter(adapter);			
			this.mainDaysListView.setOnItemClickListener(this);
			
			
	}
	 	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = this.getMenuInflater();
    	inflater.inflate(R.menu.main_menu, menu);
    	return true;
    }
	
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case R.id.button_main_menu_settings:
    			this.showSettings();
    			break;
    		case R.id.button_main_menu_quit:
    			this.quit();
    			break;
    		default:	
    			return super.onOptionsItemSelected(item);
    	}
    	
    	return true;
    }
    
    private void registerCalendarChangeObserver () {
        ChangeObserver observer = new ChangeObserver(new Handler(), this);
        ContentResolver cr = this.getApplicationContext().getContentResolver();
        cr.registerContentObserver(Uri.parse(ChangeObserver.getCalendarURI()), true, observer);      
    }
    
    private void showSettings () {
    	Intent intent = new Intent(this, Settings.class);
    	this.startActivityForResult(intent, 0);
    }
    
    private void quit() {
    	this.notificationHelper.cancel(NotificationHelper.TYPE_APPLICATION);
    	this.finish();
    }

	//@Override
	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
		
		Intent intent = new Intent(this, DayInfoTab.class);
		intent.putExtra("day", position);
		this.startActivityForResult(intent, 0);
					
	}
	
	private void checkFirstLaunch() {
		SettingsAdapter ad = new SettingsAdapter(this);
		String result;
		
		try {
			ad.open();
			result = ad.fetchSetting("FIRST_LAUNCH", "true");
			
			if (result.equals("true")) {
				ad.insertSetting("FIRST_LAUNCH", "false");
				Log.i("FirstLaunch", "nastal");
				AlarmManager mgr=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
				Intent i=new Intent(this, CalendarChecker.class);
			    PendingIntent pi=PendingIntent.getBroadcast(this, 0, i, 0);
			    
			    mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		                SystemClock.elapsedRealtime()+60000,
		                30000,
		                pi);
			}
			
			ad.close();
		} catch (Exception e) {
			Log.e("SettingsAdapter", "Failed to load data from DB.");
		} 
	}
	    
}