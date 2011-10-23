package com.vutbr.fit.tam.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.calendar.ChangeObserver;
import com.vutbr.fit.tam.calendar.Event;
import com.vutbr.fit.tam.database.EventsDatabase;
import com.vutbr.fit.tam.gui.DaySimpleAdapter;
import com.vutbr.fit.tam.gui.Days;
import com.vutbr.fit.tam.nofitication.NotificationHelper;



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
	
	/*
	private final int[] days = {R.string.shortday_MO, R.string.shortday_TU, R.string.shortday_WE,
						  R.string.shortday_TH, R.string.shortday_FR, R.string.shortday_SA,
						  R.string.shortday_SU };
	*/
	
	
	/**
	 * Initialize activity
	 */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);


        // initialize GUI elements
        this.initList();
        
        Date date = new Date();
        int day = date.getDay();
        
        
        for (int i=0; i < 7; i++) {
        	
        	
        	
        	// load alarm and sleep time from db
   
    		this.addListItem(
    				shortDays[i], 
    				"07:55",
    				"00:00",
    				i == day
    			);
    		
        }
        
  

      this.createList();
        
        
 
       
        /*
        this.textView = (TextView) this.findViewById(R.id.textView);
        
        if (this.textView == null)
        	Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();
        
        this.queryButton = (Button) this.findViewById(R.id.queryButton);
        this.alarmButton = (Button) this.findViewById(R.id.alarmButton);
        this.ringingButton = (Button) this.findViewById(R.id.ringingButton);

        
        // 	append onClickListener, which is this class
        this.queryButton.setOnClickListener(this);
        this.alarmButton.setOnClickListener(this);
        this.ringingButton.setOnClickListener(this);
         */
        
        // TODO not sure this is working always as requested, eg. app is in background
        // maybe a service will be better
       // this.registerCalendarChangeObserver();

        // NOTIFICATION EXAMPLE
        this.notificationHelper = new NotificationHelper(this);
        this.notificationHelper.setFlags(Notification.FLAG_NO_CLEAR);
        this.notificationHelper.show(
        		R.string.app_name, 
        		R.string.notification_example, 
        		NotificationHelper.TYPE_APPLICATION);

    }

    
    /**
     * Query calendars for events
     */
    private void query () {

 
    	EventsDatabase database = new EventsDatabase(this);
    	
    	Date from = new Date();
    	from.setTime(from.getTime() - DateUtils.DAY_IN_MILLIS);
    	Date to = new Date();
    	to.setTime(to.getTime() + DateUtils.DAY_IN_MILLIS);
    	
		for (Event event : database.getEvents(from, to, EventsDatabase.STATUS_AVAILABLE)) {

			int color = event.getColor();
			String title = event.getTitle();
			Date begin = event.getBeginDate();
			Date end = event.getEndDate();
			String allDay = event.isAllDayEvent() ? "Yes" : "No";
			String busy = event.isBusy() ? "Yes" : "No";
			/*
			String colorString = Color.red(color) + ", " + Color.green(color) + "," + Color.blue(color);
			String text = (String) textView.getText();
			text += "Title: " + title + "\nBegin: " + begin + "\nEnd: " + end +
					"\nAll Day: " + allDay + "\nBusy: " + busy + "\nColor: " + colorString + "\n\n";
			textView.setText(text);
		*/
		}
		
    	
    }
    

    
   
	public void onClick(View view) {
		/*
		switch (view.getId()) {
			case R.id.queryButton:
				this.query();
				break;
			case R.id.alarmButton:
				this.showAlarm();
				break;
			case R.id.ringingButton:
				this.showRinging();
				break;
		
		}
		*/
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
    private void addListItem (int day, String alarm, String sleepmode, boolean today) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Identifiers.DAY.toString(), this.getString(day));
		map.put(Identifiers.ALARM.toString(), alarm);
		map.put(Identifiers.SLEEPMODE.toString(), sleepmode);
		map.put(Identifiers.TODAY.toString(), String.valueOf(today));
		
		this.daysListItems.add(map);
	}
	
	
	/**
	 * Create list with items
	 * Must call initList at first
	 */
	private void createList () {
		DaySimpleAdapter adapter = new DaySimpleAdapter(
				this.getBaseContext(),
				this.daysListItems, 
				R.layout.mains_rows, 
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

	@Override
	public void onItemClick(AdapterView<?> a, View v, int position, long id) {

		Intent intent = new Intent(this, DayInfoTab.class);
		intent.putExtra("day", position);
		this.startActivityForResult(intent, 0);
					
	}
    
}