package com.vutbr.fit.tam.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.vutbr.fit.tam.calendar.Calendar;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

/**
 * Database class for handling calendars
 * 
 * @author Zsolt Horváth
 *
 */
public class CalendarDatabase {

	/***
	 * URI of calendar data
	 */
	private static final String CALENDAR_CONTENT_URI_ECLAIR = "content://calendar/calendars";
	private static final String CALENDAR_CONTENT_URI_FROYO = "content://com.android.calendar/calendars";
	
	/**
	 * Application context
	 */
	private Context context;
	
	/**
	 * Database adapter for calendars
	 */
	private CalendarAdapter calendarAdapter;
	
	/**
	 * Constructor
	 * @param context
	 */
	public CalendarDatabase (Context context) {
		this.context = context;
		
        this.calendarAdapter = new CalendarAdapter(this.context);
        this.calendarAdapter.open();
	}
	
	private String getCalendarURI() {
		
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
			return CalendarDatabase.CALENDAR_CONTENT_URI_ECLAIR;
		} else{
			return CalendarDatabase.CALENDAR_CONTENT_URI_FROYO;
		}
	}
	
	/**
	 * Get available calendars and with user set options
	 * @return Set of calendars
	 */
	public Set<Calendar> loadCalendars () {
		
		HashMap<String, Calendar> calendarsMap = new HashMap<String, Calendar>();
		Set<Calendar> calendarsSet = new HashSet<Calendar>();
		
    	try {
    		
    		ContentResolver contentResolver = this.context.getContentResolver();

    		// Fetch a list of all calendars synchronized with the device, their display names and whether the
    		// user has them selected for display.
    		
    		Uri cal = Uri.parse(this.getCalendarURI());;
    		//Toast.makeText(this, cal.toString(), Toast.LENGTH_LONG).show();
    		
    		final Cursor cursor = contentResolver.query(cal,
    				(new String[] { "_id", "displayName", "selected", "color" }), null, null, null);
    		// For a full list of available columns see http://tinyurl.com/yfbg76w

    		while (cursor.moveToNext()) {

    			final String id = cursor.getString(0);
    			final String title = cursor.getString(1);
    			final int color = Integer.parseInt(cursor.getString(3));
    			//final Boolean selected = !cursor.getString(2).equals("0");

    			Calendar calendar = new Calendar(id, title);
    			calendar.setColor(color);
    			calendarsMap.put(id, calendar);
    		}
    		
		} catch (Exception e) {
			// catch error
		}
    	
		Cursor calendarsCursor = this.calendarAdapter.fetchAllCalendars();

		while (calendarsCursor.moveToNext()) {
    		String id = calendarsCursor.getString(calendarsCursor.getColumnIndex(CalendarHelper.KEY_CALENDAR_ID));
    		//String title = calendarsCursor.getString(calendarsCursor.getColumnIndex(CalendarHelper.KEY_TITLE));
    		boolean enabled = !calendarsCursor.getString(calendarsCursor.getColumnIndex(CalendarHelper.KEY_ENABLED)).equals("0");

    		Calendar calendar = calendarsMap.get(id);
    		
    		if (calendar != null) {
    			calendar.setEnabled(enabled);
    			calendarsMap.put(id, calendar);
    		}
		}
		  
		for (String key : calendarsMap.keySet()) {
			calendarsSet.add(calendarsMap.get(key));
		}
		
		return calendarsSet;
	}
	
	/**
	 * Save calendars with their options
	 * @param calendars
	 */
	public void saveCalendars(Set<Calendar> calendars) {
		this.calendarAdapter.deleteCalendars();
    	
    	for (Calendar calendar : calendars) {
    		this.calendarAdapter.insertCalendar(calendar);
    	}
	}
	
	/**
	 * Close connection with database
	 */
	protected void finalize () {
		if (this.calendarAdapter != null) {
			this.calendarAdapter.close();
		}
	}
	
	
	
}
