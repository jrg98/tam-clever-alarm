package com.vutbr.fit.tam.database;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.vutbr.fit.tam.calendar.Calendar;
import com.vutbr.fit.tam.calendar.Event;

/**
 * Events database 
 * 
 * @author Zsolt Horváth
 *
 */
public class EventsDatabase {

	public static final int STATUS_AVAILABLE = 0;
	public static final int STATUS_BUSY = 1;
	public static final int STATUS_DONT_CARE = 2;
	
	private final String EVENTS_CONTENT_URI_ECLAIR = "content://calendar/instances/when"; 
	private final String EVENTS_CONTENT_URI_FROYO = "content://com.android.calendar/instances/when"; 
	
	private Context context;
	
	public EventsDatabase (Context context) {
		this.context = context;
	}
	
	public String getCalendarURI() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
			return this.EVENTS_CONTENT_URI_FROYO;
		} else{
			return this.EVENTS_CONTENT_URI_ECLAIR;
		}
	}
	
	/**
	 * Finds the first event from date "from" in 24 hours interval
	 * @param from
	 * @param status database status
	 * @return first event, or null if no events are scheduled
	 */
	public Event getFirstEvent(Date from, int status) {
		Event e = new Event();
		// treshhold
		//Date to = new Date(from.getTime()+24*60*60*1000);
		Date to = new Date(from.getTime()+86400000);
		
		e.setBeginDate(to);

		for (Event event : getEvents(from, to, status)) {
			
			Log.v("LOOG", event.getTitle());
			
			if (event.getBeginDate().compareTo(e.getBeginDate()) <= 0) {
				
				Log.v("LOOG", event.getTitle());
				
				e.setAllDayEvent(event.isAllDayEvent());
				e.setBusy(event.isBusy());
				e.setBeginDate(event.getBeginDate());
				e.setColor(event.getColor());
				e.setEndDate(event.getEndDate());
				e.setTitle(event.getTitle());
			}
		}
		
		if (e.getBeginDate().compareTo(to) == 0) return null;
		
		return e;
	}
	
	public Set<Event> getEvents(Date from, Date to, int status) {
  		Set<Calendar> calendars = new CalendarDatabase(this.context).loadCalendars();
  		Set<Event> events = new HashSet<Event>();
  		
		// For each calendar, display all the events from the previous day to the end of next day.		
		for (Calendar calendar : calendars) {
			
			if (!calendar.isEnabled()) {
				continue;
			}
			
					
			Uri.Builder builder = Uri.parse(this.getCalendarURI()).buildUpon();
			ContentUris.appendId(builder, from.getTime());
			ContentUris.appendId(builder, to.getTime());

			Cursor eventCursor = this.context.getContentResolver().query(builder.build(),
					new String[] { "title", "begin", "end", "allDay", "eventStatus", "color"}, "Calendars._id=" + calendar.getId(),
					null, "startDay ASC, startMinute ASC"); 
			
			// For a full list of available columns see this URL:
			// http://www.google.com/codesearch/p?hl=en&sa=N&cd=3&ct=rc#uX1GffpyOZk/core/java/android/provider/Calendar.java

			while (eventCursor.moveToNext()) {
				
				final String title = eventCursor.getString(0);
				final Date begin = new Date(eventCursor.getLong(1));
				final Date end = new Date(eventCursor.getLong(2));
				final Boolean allDay = !eventCursor.getString(3).equals("0");
				
				//final boolean busy = eventCursor.getString(4).equals("1");
				
				boolean busy = false;
				
				final int color = Integer.parseInt(eventCursor.getString(5));
				
				// EDIT Android 2.1
				if (eventCursor.getString(4) != null) {
					busy = eventCursor.getString(4).equals("1");
				}
				
				if ((status == EventsDatabase.STATUS_AVAILABLE && !busy) ||
					(status == EventsDatabase.STATUS_BUSY && busy) ||
					((status == EventsDatabase.STATUS_DONT_CARE))) {
				
					Event event = new Event();
					event.setTitle(title);
					event.setBeginDate(begin);
					event.setEndDate(end);
					event.setAllDayEvent(allDay);
					event.setBusy(busy);
					event.setColor(color);
					
					events.add(event);
				}
				
			}
		}
		
		return events;	
	}
	
}
