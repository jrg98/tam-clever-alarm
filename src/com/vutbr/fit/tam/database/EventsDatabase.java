package com.vutbr.fit.tam.database;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;

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
	public static final int STATUS_BUSY = 0;
	public static final int STATUS_DONT_CARE = 0;
	
	
	private final String EVENTS_CONTENT_URI = "content://com.android.calendar/instances/when";
	
	private Context context;
	
	public EventsDatabase (Context context) {
		this.context = context;
	}
	
	public Set<Event> getEvents(Date from, Date to, int status) {
  		Set<Calendar> calendars = new CalendarDatabase(this.context).loadCalendars();
  		Set<Event> events = new HashSet<Event>();
		
		// For each calendar, display all the events from the previous day to the end of next day.		
		for (Calendar calendar : calendars) {
			
			if (!calendar.isEnabled()) {
				continue;
			}
			
			Uri.Builder builder = Uri.parse(this.EVENTS_CONTENT_URI).buildUpon();
			long now = new Date().getTime();
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
				final boolean busy = eventCursor.getString(4).equals("1");
				final int color = Integer.parseInt(eventCursor.getString(5));
				
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
