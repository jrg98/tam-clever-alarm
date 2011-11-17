package com.vutbr.fit.tam.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;


import android.os.Bundle;
import android.os.Debug;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.alarm.Alarm;
import com.vutbr.fit.tam.calendar.Event;
import com.vutbr.fit.tam.database.AlarmAdapter;
import com.vutbr.fit.tam.database.EventsDatabase;
import com.vutbr.fit.tam.gui.DaySimpleAdapter;
import com.vutbr.fit.tam.gui.Days;
import com.vutbr.fit.tam.gui.ListViewUtility;

public class Day extends Activity implements Days, OnItemSelectedListener {

	private enum Identifiers {
		EVENT, BEGIN, END
	};
	
	private TextView actualDay;
	private LinearLayout dayBackground;
	private Spinner advance;
	private ListView daysEventListView;
	private ArrayList<HashMap<String, String>> eventListItems;
			
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
        
        this.alarm = this.getDayAlarm(this.day);
        	       
        initList();
        loadDayEvents();
        
        if (this.firstEvent != null) {
	        this.advance = (Spinner) this.findViewById(R.id.advance);
	        ArrayAdapter adapter = ArrayAdapter.createFromResource(
	                this, R.array.advance, android.R.layout.simple_spinner_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        this.advance.setAdapter(adapter);
	        this.advance.setSelection(this.getAdvanceId());
	        this.advance.setOnItemSelectedListener(this);
        }
        
        // Set background
        Date date = new Date();
        if (date.getDay() == this.day ) {
        	this.dayBackground = (LinearLayout) this.findViewById(R.id.tab_day_background);
        	this.dayBackground.setBackgroundResource(R.drawable.tab_today_bg);
    	}

        createList();
        
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
    	
		for (Event event : database.getEvents(from, to, EventsDatabase.STATUS_DONT_CARE)) {

			this.addListItem(event.getTitle(),
							 event.getBeginDate(),
							 event.getEndDate()
							 );
		}
		
		this.firstEvent = database.getFirstEvent(from, EventsDatabase.STATUS_DONT_CARE);
		
    }
    
    
	/**
	 * Initialize list
	 */
	private void initList () {
		this.daysEventListView = (ListView) findViewById(R.id.dayEventList);
		this.eventListItems = new ArrayList<HashMap<String, String>>();
	}
    
	
	private void addListItem(String title, Date begin, Date end) {
		
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
	private void createList () {
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
			ListViewUtility.setListViewHeightBasedOnChildren(daysEventListView);
			
	}
	
	
	/*
	 * Return index of spinner which depends on alarm time
	 */
	private int getAdvanceId() {
		
		long getWakeUpOffset = this.alarm.getWakeUpOffset();
				
		if (getWakeUpOffset == DateUtils.MINUTE_IN_MILLIS) {
			return 0;
		}
		else if (getWakeUpOffset == 5 * DateUtils.MINUTE_IN_MILLIS) {
			return 1;
		}
		else if (getWakeUpOffset == 10 * DateUtils.MINUTE_IN_MILLIS) {
			return 2;
		}
		else if (getWakeUpOffset == 15 * DateUtils.MINUTE_IN_MILLIS) {
			return 3;
		}
		else if (getWakeUpOffset == 30 * DateUtils.MINUTE_IN_MILLIS) {
			return 4;
		}
		else if (getWakeUpOffset == DateUtils.HOUR_IN_MILLIS) {
			return 5;
		}
		else if (getWakeUpOffset == 2 * DateUtils.HOUR_IN_MILLIS) {
			return 6;
		}
		
		return 7;
	}
	
	/*
	 * Reversal table to getAdvanceId
	 */
	private long getWakeUpOffsetFromAdvanceId(int index) {
		
		switch (index) {
			case 0:
				return DateUtils.MINUTE_IN_MILLIS;
			case 1:
				return 5 * DateUtils.MINUTE_IN_MILLIS;
			case 2:
				return 10 * DateUtils.MINUTE_IN_MILLIS;
			case 3:
				return 15 * DateUtils.MINUTE_IN_MILLIS;
			case 4:
				return 30 * DateUtils.MINUTE_IN_MILLIS;
			case 5:
				return DateUtils.HOUR_IN_MILLIS;
			case 6:
				return 2 * DateUtils.HOUR_IN_MILLIS;
			default:
				return 0;
		}

	}
	
	private Alarm getDayAlarm (int id) {
		
  		AlarmAdapter adapter;

  		Alarm alarm = new Alarm(id, false, 0, 0, 0);
	
  		try {
			adapter = new AlarmAdapter(this).open();
		
			Cursor cursorDAY = adapter.fetchAlarm(id);
		
			if (cursorDAY.moveToFirst()) {
			
				alarm.setEnabled(cursorDAY.getInt(0) > 0);
				alarm.setWakeUpOffset(cursorDAY.getLong(1));
				alarm.setWakeUpTimeout(cursorDAY.getLong(2));
				alarm.setSleepTime(cursorDAY.getLong(3));
								
			}
		
			adapter.close();
		
  		} catch (Exception ex) {
  			Log.e("DayAlarm", "AlarmAdapter error: "+ ex.toString());
  			return null;
  		}	  
  
  		return alarm;
  
  }
	

	@Override
	 public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		
	      Toast.makeText(parent.getContext(), "The planet is " +
	          parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
	      
	      if (this.firstEvent != null) {

	    	  if (pos < 7) {
	    		  long advance = this.getWakeUpOffsetFromAdvanceId(pos);
	    		  this.alarm.setWakeUpOffset(advance);
	    	  }
	    	  
	    	  AlarmAdapter adapter;
	    	  
	    	  try {
	  			
	  			adapter = new AlarmAdapter(this).open();
	  			
	  			if (!adapter.updateAlarm(this.alarm)) {
	  				adapter.insertAlarm(this.alarm);
	  			}
	  			
	  			adapter.close();
	  			
	  			if (this.alarm.isEnabled()) {
	  				Toast.makeText(this, this.getResources().getString(R.string.advance_set), Toast.LENGTH_SHORT).show();
	  			} else {
	  				Toast.makeText(this, this.getResources().getString(R.string.advance_not_set), Toast.LENGTH_SHORT).show();
	  			}
	  			
	  			
	  		} catch (Exception ex) {
	  			Log.e("TimeAdvance", "AlarmAdapter error: "+ ex.toString());
	  		}
	    	  
	      }
	    }
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}
