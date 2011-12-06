package com.vutbr.fit.tam.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.alarm.Alarm;
import com.vutbr.fit.tam.calendar.Event;
import com.vutbr.fit.tam.database.AlarmAdapter;
import com.vutbr.fit.tam.database.EventsDatabase;
import com.vutbr.fit.tam.database.SettingsAdapter;
import com.vutbr.fit.tam.gui.Days;
import com.vutbr.fit.tam.widget.CleverAlarmWidgetProvider;


public class Day extends Activity implements Days, OnItemSelectedListener {

	private enum Identifiers {
		EVENT, BEGIN, END
	};
	
	final private int timeMinutesAdvance[] = {0, 1, 5, 10, 15, 30, 45};
	final private int timeHoursAdvance[] = {1, 2, 3, 4, 5, 6, 8, 10, 12, 20};
	
	private TextView actualDay;
	private TextView dayAlarmAdvanceTime;
	private LinearLayout dayBackground;
	private Spinner advance;
	private ListView daysEventListView;
	private ArrayList<HashMap<String, String>> eventListItems;
	
	ScrollView scrollView;
	
	private boolean flag;
	
	private int day;
	private Alarm alarm;
	private Event firstEvent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_day);
        
        Bundle bundle = this.getIntent().getExtras();
        this.day = bundle.getInt("day");
			        
        this.actualDay = (TextView) this.findViewById(R.id.tv_day);
        actualDay.setText(days[this.day]);
                
        this.dayAlarmAdvanceTime = (TextView) this.findViewById(R.id.dayAlarmAdvance);
        this.advance = (Spinner) this.findViewById(R.id.advance);
        
        this.alarm = this.getDayAlarm(this.day);
        
        String[] advanceArray = getTimeAdapterArray();
	    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, advanceArray);
	    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
	    this.advance.setAdapter(spinnerArrayAdapter);
	    this.advance.setOnItemSelectedListener(this);
  	  
        // Set background
        Date date = new Date();
        if (date.getDay() == this.day ) {
        	this.dayBackground = (LinearLayout) this.findViewById(R.id.tab_day_background);
        	this.dayBackground.setBackgroundResource(R.drawable.tab_today_bg);
    	}
        
        scrollView = (ScrollView) findViewById(R.id.scrollView1);
        //scrollView.fullScroll(ScrollView.FOCUS_UP);
    }
    
    @Override
	protected void onResume() {
		
		super.onResume();
		
		this.alarm = this.getDayAlarm(this.day);
		
    	final String timeFormat = this.loadTimeFormat();
  	    
    	  this.initEventList();
    	    this.loadDayEvents();
    	    this.createEventList();
          
    	//  if (this.firstEvent != null) {
  	        
  	        
  	        
  	        this.advance.setSelection(this.getAdvanceId());
  	        
    //    }

    	
    	
	    if (this.firstEvent != null) {
	    	final long advance = alarm.getWakeUpOffset();
	    	this.setDayAlarmAdvanceTime(advance);
	    }
	    else {
	    	final long alarmWithAdvance = alarm.getWakeUpTimeout() - DateUtils.HOUR_IN_MILLIS;
	    	if (alarm.isEnabled()) {
	    		dayAlarmAdvanceTime.setText(DateFormat.format(timeFormat, alarmWithAdvance).toString());
	    	}
	    	else {
	    		dayAlarmAdvanceTime.setText(this.getResources().getString(R.string.not_set));
	    	}
	    }
	    
  	  //ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView1);
  	 
  	    if (flag) {
  	    
  	    	scrollView.post(new Runnable() {
  	    		public void run() {
  	    			scrollView.fullScroll(ScrollView.FOCUS_UP);
  	    		}
  	    	});
        
  	    }
  		flag = true;
    }
    
    /**
     * Load events from db
     * events starting at choosed day
     */
    public void loadDayEvents() {
    	
    	EventsDatabase database = new EventsDatabase(this);
    
    	Date date = new Date();
        int today = date.getDay();
        
        // How many days to choosen day
        today = this.day - today;
        if (today < 0) today += 7; 
  
    	Date from = new Date();
    	final long startDay = from.getTime() - from.getHours() * DateUtils.HOUR_IN_MILLIS
    			 							 - from.getMinutes() * DateUtils.MINUTE_IN_MILLIS
    			 							 - from.getSeconds() * DateUtils.SECOND_IN_MILLIS;
    				    	
    	from.setTime(startDay + DateUtils.DAY_IN_MILLIS * (today));
    	
    	Date to = new Date();
    	to.setTime(startDay + DateUtils.DAY_IN_MILLIS * (today + 1));
    	
    	Map<Long, Event> map = new TreeMap<Long, Event>();
    	Random rnd = new Random();
    	
    	
		for (Event event : database.getEvents(from, to, EventsDatabase.STATUS_DONT_CARE)) {

			map.put(event.getBeginDate().getTime() * 123 + rnd.nextInt(122), event);
		}

		 Set s=map.entrySet();
		 Iterator iterator = s.iterator();
		 
		 while(iterator.hasNext()) {
			 
			 Map.Entry m =(Map.Entry) iterator.next();
			 
			 Event event = (Event) m.getValue();
			 
				this.addListEventItem(event.getTitle(),
					 	  event.getBeginDate(),
					      event.getEndDate()
					    );
		 }
		
		
		this.firstEvent = database.getFirstEvent(from, EventsDatabase.STATUS_DONT_CARE);
		
    }
    
    
	/**
	 * Initialize list
	 */
	private void initEventList () {
		this.daysEventListView = (ListView) findViewById(R.id.dayEventList);
		this.eventListItems = new ArrayList<HashMap<String, String>>();
	}
    
	
	private void addListEventItem(String title, Date begin, Date end) {
		
    	 HashMap<String, String> map = new HashMap<String, String>();
    	 
    	 map.put(Identifiers.EVENT.toString(), title);
    	 map.put(Identifiers.BEGIN.toString(), begin.toString()) ;
    	 map.put(Identifiers.END.toString(), end.toString()) ;	    	 
    	 this.eventListItems.add(map);
    	 
	}
	
    /**
	 * Create list with items
	 * Must call initList at first
	 */
	private void createEventList () {
				
		SimpleAdapter adapter = new SimpleAdapter(
				this.getBaseContext(),
				this.eventListItems, 
				R.layout.day_event_row, 
				new String[] {
					Identifiers.EVENT.toString(),
					Identifiers.BEGIN.toString(),
					Identifiers.END.toString(),
				}, 
				new int[] {
					R.id.dayEventName,
					R.id.dayEventBeginDate,
					R.id.dayEventEndDate,
				}
			);
		
			this.daysEventListView.setAdapter(adapter);
			
			final int height = 90;
			
			ViewGroup.LayoutParams params = daysEventListView.getLayoutParams();
			
	        params.height = this.eventListItems.size() * height;
	        daysEventListView.setLayoutParams(params);
	        daysEventListView.requestLayout();
			
	}
	

	private String[] getTimeAdapterArray() {
		
		String[] timeArray = new String[this.timeMinutesAdvance.length + this.timeHoursAdvance.length];
		
		// If plural nouns - 's' on the end of words
		int start = 0;
		
		if (this.timeMinutesAdvance[start] == 1) {
			timeArray[start] = String.valueOf(this.timeMinutesAdvance[start]) + " " + getResources().getText(R.string.minute);
			start++;
		}
		
		for (int i = start; i < this.timeMinutesAdvance.length; i++) {
			timeArray[i] = String.valueOf(this.timeMinutesAdvance[i]) + " " + getResources().getText(R.string.minutes);
		}
		
		start = 0;
		if (this.timeHoursAdvance[start] == 1) {
			timeArray[start + this.timeMinutesAdvance.length] = String.valueOf(this.timeHoursAdvance[start]) + " " + getResources().getText(R.string.hour);
			start++;
		}
		
		for (int i = start; i <this.timeHoursAdvance.length; i++) {
			timeArray[i + this.timeMinutesAdvance.length] = String.valueOf(this.timeHoursAdvance[i]) + " " + getResources().getText(R.string.hours);
		}
		
		
		return timeArray;
	}
	
	/*
	 * Return index of spinner which depends on offsettime
	 */
	private int getAdvanceId() {
		
		long wakeUpOffset = this.alarm.getWakeUpOffset();
				
		for (int i = 0; i < this.timeMinutesAdvance.length; i++) {
			if (wakeUpOffset == timeMinutesAdvance[i] * DateUtils.MINUTE_IN_MILLIS) {
				return i;
			}
		}
		
		for (int i = 0; i < this.timeHoursAdvance.length; i++) {
			if (wakeUpOffset == timeHoursAdvance[i] * DateUtils.HOUR_IN_MILLIS) {
				return i + timeMinutesAdvance.length;
			}
		}
		
		return 0;
	
	}
	
	/*
	 * Reversal table to getAdvanceId
	 */
	private long getWakeUpOffsetFromAdvanceId(int index) {
		
		 int[] advanceArray = this.timeMinutesAdvance;
		 long millis = DateUtils.MINUTE_IN_MILLIS;
		 
		 if (index >= this.timeMinutesAdvance.length) {
    		 index = index - this.timeMinutesAdvance.length;
    		 advanceArray = this.timeHoursAdvance;
    		 millis = DateUtils.HOUR_IN_MILLIS;
    	  } 
		
		 
		return advanceArray[index] * millis; 

	}
	
	private Alarm getDayAlarm (int id) {
		
  		AlarmAdapter adapter;

  		Alarm alarm = new Alarm(id, false, 0, 0, 0, false);
	
  		try {
			adapter = new AlarmAdapter(this).open();
		
			Cursor cursorDAY = adapter.fetchAlarm(id);
		
			if (cursorDAY.moveToFirst()) {
			
				alarm.setEnabled(cursorDAY.getInt(0) > 0);
				alarm.setWakeUpOffset(cursorDAY.getLong(1));
				alarm.setWakeUpTimeout(cursorDAY.getLong(2));
				alarm.setSleepTime(cursorDAY.getLong(3));
				alarm.setSleepEnabled(cursorDAY.getInt(4) > 0);
								
			}
		
			adapter.close();
		
  		} catch (Exception ex) {
  			Log.e("DayAlarm", "AlarmAdapter error: "+ ex.toString());
  			return null;
  		}	  
  
  		return alarm;
  
  }
	

	private String loadTimeFormat() {
    	
    	SettingsAdapter settingsAdapter = new SettingsAdapter(this);
    	settingsAdapter.open();
    	
    	String format = settingsAdapter.fetchSetting("timeformat",
    						DateFormat.HOUR_OF_DAY + ":" + DateFormat.MINUTE + DateFormat.MINUTE);
    	    	
        settingsAdapter.close();
        
        return format;
    }
	
	//@Override
	 public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		
		 long advance = this.getWakeUpOffsetFromAdvanceId(pos);
	     this.alarm.setWakeUpOffset(advance);
	    	
	     this.setDayAlarmAdvanceTime(advance);
	    	 	    	  
	      		// Save
	     AlarmAdapter adapter;
	    	  
	     try {
	  			
	    	 adapter = new AlarmAdapter(this).open();
	  			
	    	 if (!adapter.updateAlarm(this.alarm)) {
	  				adapter.insertAlarm(this.alarm);
	    	 }
	  			
	  		 adapter.close();
	  			
	     } catch (Exception ex) {
	    	 Log.e("TimeAdvance", "AlarmAdapter error: "+ ex.toString());
	     }
	    	  
	    
	    // Send broadcast update to widget
	  	Intent intent=new Intent(getApplicationContext(),CleverAlarmWidgetProvider.class);
		sendBroadcast(intent);
	    	  
	    }
	 
	public void setDayAlarmAdvanceTime(long advance) {
		
		final String timeFormat = this.loadTimeFormat();
		String alarmStr = this.getResources().getString(R.string.not_set);
		
	    long eventAlarm = Long.MAX_VALUE;
		
		if (this.firstEvent != null) {
			alarmStr = DateFormat.format(timeFormat, firstEvent.getBeginDate().getTime() -
													 advance).toString();
			eventAlarm = firstEvent.getBeginDate().getHours() * DateUtils.HOUR_IN_MILLIS +
						 firstEvent.getBeginDate().getMinutes() * DateUtils.MINUTE_IN_MILLIS + 
						 firstEvent.getBeginDate().getSeconds() * DateUtils.SECOND_IN_MILLIS -					
						 advance;
		}
		
		if (alarm.isEnabled()) {
			
			if (alarm.getWakeUpTimeout() < eventAlarm) {
				alarmStr = DateFormat.format(timeFormat, alarm.getWakeUpTimeout() - DateUtils.HOUR_IN_MILLIS).toString();
			}
				
		}
		else {
		   
		   alarmStr = this.getResources().getString(R.string.not_set);
		   
		}
		  
	  
	  dayAlarmAdvanceTime.setText(alarmStr);
		
	}
	 
//	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}