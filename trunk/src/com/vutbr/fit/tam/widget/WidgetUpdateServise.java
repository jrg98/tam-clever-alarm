package com.vutbr.fit.tam.widget;

import java.util.Date;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.alarm.Alarm;
import com.vutbr.fit.tam.calendar.Event;
import com.vutbr.fit.tam.database.AlarmAdapter;
import com.vutbr.fit.tam.database.EventsDatabase;

public class WidgetUpdateServise extends Service {

		
	@Override
	public void onStart(Intent intent, int startId) {

				
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

		int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
		
		Event nextEvent = this.getNextEvent();
		Alarm nextAlarm = this.getNextAlarm();
		
		// Actualize all showed widget
		if (appWidgetIds.length > 0) {
			for (int widgetId : appWidgetIds) {

				RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.widget);
				
				// Set info about next event
				if (nextEvent != null) {
					remoteViews.setTextViewText(R.id.tvWidgetNextEventName, nextEvent.getTitle());
					remoteViews.setTextViewText(R.id.tvWidgetNextEventDate, nextEvent.getBeginDate().toLocaleString());
				}
				else {
					remoteViews.setTextViewText(R.id.tvWidgetNextEventName, "");
					remoteViews.setTextViewText(R.id.tvWidgetNextEventDate, "");
				}
				
				final String timeFormat = DateFormat.HOUR_OF_DAY + ":" + DateFormat.MINUTE + DateFormat.MINUTE;
				
				// Set info about alarm
				if (nextAlarm.isEnabled()) {
					remoteViews.setTextViewText(R.id.tvWidgetAlarmTime, DateFormat.format(timeFormat, nextAlarm.getWakeUpTimeout()).toString());
				} else
				{
					remoteViews.setTextViewText(R.id.tvWidgetAlarmTime, "");
				}

				// Set info about sleep mode
				Boolean sleepMode = true; // only for try
				
				if (sleepMode) {
					remoteViews.setTextViewText(R.id.tvWidgetMode, this.getResources().getText(R.string.sleep_mode));
				}
				else {
					remoteViews.setTextViewText(R.id.tvWidgetMode, "");
				}
				
				remoteViews.setImageViewBitmap(R.id.switchButton, this.getButtonImage(sleepMode));
								
				appWidgetManager.updateAppWidget(widgetId, remoteViews);

			}
			stopSelf();
		}
		super.onStart(intent, startId);
	}
	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	// Only for try, change icon at widget
	public Bitmap getButtonImage (boolean sleepMode) {
		
		Bitmap theImage = BitmapFactory.decodeResource(getResources() ,
								sleepMode ? R.drawable.button_start : R.drawable.button_quit);
		
		return Bitmap.createBitmap(theImage);

	}
	
	
	private Alarm getNextAlarm() {
					
	  		AlarmAdapter adapter;

	  		Date date = new Date();
	  		final int id = date.getDay();
	  		// id = (id + 1) % 7 ????????
	  		
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
	
	// Get next event
	private Event getNextEvent() {
		
		EventsDatabase database = new EventsDatabase(this);
    			
    	Date from = new Date();
    	from.setTime(from.getTime() - DateUtils.DAY_IN_MILLIS);
    	Date to = new Date();
    	to.setTime(to.getTime() + DateUtils.DAY_IN_MILLIS);
    	    	
		for (Event event : database.getEvents(from, to, EventsDatabase.STATUS_DONT_CARE)) {

			return event;
									
		}
		
		return null;
		
	}

}
