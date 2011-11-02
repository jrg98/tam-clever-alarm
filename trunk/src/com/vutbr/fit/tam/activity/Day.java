package com.vutbr.fit.tam.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Color;


import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.calendar.Event;
import com.vutbr.fit.tam.database.EventsDatabase;
import com.vutbr.fit.tam.gui.DaySimpleAdapter;
import com.vutbr.fit.tam.gui.Days;

public class Day extends Activity implements Days {

	private enum Identifiers {
		EVENT, BEGIN, END
	};
	
	private TextView actualDay;
	
	private ListView daysEventListView;
	private ArrayList<HashMap<String, String>> eventListItems;
			
	private int day;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_day);
        
        Bundle bundle = this.getIntent().getExtras();
        this.day = bundle.getInt("day");
			        
        this.actualDay = (TextView) this.findViewById(R.id.tv_day);
        actualDay.setText(days[this.day]);
        	        
        initList();
        loadDayEvents();
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
			
	}
	    
}
