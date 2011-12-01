package com.vutbr.fit.tam.widget;

import java.util.Date;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.alarm.Alarm;
import com.vutbr.fit.tam.calendar.Event;
import com.vutbr.fit.tam.database.AlarmAdapter;
import com.vutbr.fit.tam.database.EventsDatabase;
import com.vutbr.fit.tam.database.SettingsAdapter;

public class CleverAlarmWidgetProvider extends AppWidgetProvider {
		
	  private static Context context;
	  private static AppWidgetManager appWidgetManager;
	  private static int[] appWidgetIds;
	  
	  private boolean tomorrow; 
	
	  @Override
	  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		  	
		  RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget);
		  Intent intent = new Intent(context.getApplicationContext(), WidgetUpdateService.class);
		  intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

		  PendingIntent pendingIntent = PendingIntent.getService(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		  remoteViews.setOnClickPendingIntent(R.id.switchButton, pendingIntent);

		  appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
		  
		  // Static access
		  CleverAlarmWidgetProvider.context = context;
		  CleverAlarmWidgetProvider.appWidgetManager = appWidgetManager;
		  CleverAlarmWidgetProvider.appWidgetIds = appWidgetIds;
		  
		  this.update();
		  
	  }
	  
	  @Override
	  public void onReceive(Context context, Intent intent) {
		  
		  this.update();
		  super.onReceive(context, intent);
		
	  }

		
	  private void update() {
	  		  
		// Receive broadcast but widget is not show
		if (CleverAlarmWidgetProvider.context == null) {	
			return;
		}
		  		
		Event nextEvent = this.getNextEvent(CleverAlarmWidgetProvider.context);
		//Alarm nextAlarm = this.getTodayAlarm(CleverAlarmWidgetProvider.context);
		
		long alarmTime = this.getAlarmTime(CleverAlarmWidgetProvider.context);
		
		final String timeFormat = this.loadTimeFormat(context);
		
		// Actualize all showed widget
		if (appWidgetIds.length > 0) {
			for (int widgetId : appWidgetIds) {

				RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
				
				// Set info about next event
				if (nextEvent != null) {
					remoteViews.setTextViewText(R.id.tvWidgetNextEventName, nextEvent.getTitle());
					String nEventStr = nextEvent.getBeginDate().toLocaleString();
					nEventStr = nEventStr.substring(0, nEventStr.length() - 3);
					remoteViews.setTextViewText(R.id.tvWidgetNextEventDate, nEventStr);
				}
				else {
					remoteViews.setTextViewText(R.id.tvWidgetNextEventName, "-");
					remoteViews.setTextViewText(R.id.tvWidgetNextEventDate, "-");
				}
							
				// Set info about alarm
				if (alarmTime > 0) {
					remoteViews.setTextViewText(R.id.tvWidgetAlarmTime,
									DateFormat.format(timeFormat, alarmTime).toString());
					
				} else
				{
					remoteViews.setTextViewText(R.id.tvWidgetAlarmTime, "-");
				}
				
				// if show tomorrow alarm - green text color
				if (this.tomorrow) {
					remoteViews.setTextColor(R.id.tvWidgetAlarmTime, 0xff00ff00);
				}
				else {
					remoteViews.setTextColor(R.id.tvWidgetAlarmTime, 0xffffffff);	
				}
				
				

				// Set info about sleep mode
				Boolean sleepMode = isSleepMode(CleverAlarmWidgetProvider.context);
				this.sleepmMode(sleepMode, remoteViews, context);

				appWidgetManager.updateAppWidget(widgetId, remoteViews);

			}
		}
	}
	
	  /**
	   * Load time format settings from database
	   * @param context
	   * @return
	   */
	  private String loadTimeFormat(Context context) {

		  SettingsAdapter settingsAdapter = new SettingsAdapter(context);
		  settingsAdapter.open();
  	
		  String format = settingsAdapter.fetchSetting("timeformat", DateFormat.HOUR_OF_DAY + ":" + DateFormat.MINUTE + DateFormat.MINUTE);
		  settingsAdapter.close();
      
		  return format;
	  }

		private boolean isSleepMode(Context context) {
			
	    	SettingsAdapter settingsAdapter = new SettingsAdapter(context);
	    	settingsAdapter.open();
	    	
	    	final int isSleepActive = Integer.parseInt(settingsAdapter.fetchSetting("isSleepActive", "0"));
	    	settingsAdapter.close();
	    	
	    	return (isSleepActive == 1);
			
		}
	  
	
	/**
	 * Set sleepMode in GUI
	 * 
	 * @param sleepmode - true/false
	 * @param remoteViews
	 */
	private void sleepmMode(boolean sleepmode, RemoteViews remoteViews, Context context) {
		
		if (sleepmode) {
			remoteViews.setTextViewText(R.id.tvWidgetMode, context.getResources().getText(R.string.sleep));
		}
		else {
			remoteViews.setTextViewText(R.id.tvWidgetMode, "");
		}
		
		remoteViews.setImageViewBitmap(R.id.switchButton, this.getButtonImage(sleepmode, context));
		
	}
	
	// Only for try, change icon at widget
	public Bitmap getButtonImage (boolean sleepMode, Context context) {
		
		Bitmap theImage = BitmapFactory.decodeResource(context.getResources() ,
								sleepMode ? R.drawable.button_start : R.drawable.button_quit);
		
		return Bitmap.createBitmap(theImage);

	}
	
	/**
	 * Log
	 * @param context
	 * @return today alarm
	 */
	private Alarm getAlarm(Context context, int id) {
					
		AlarmAdapter adapter;

	  	Date date = new Date();
//	  	final int id = date.getDay();
	  		  		
	  	Alarm alarm = new Alarm(id, false, 0, 0, 0, false);
		
	  	try {
	  		adapter = new AlarmAdapter(context).open();
			
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
	
	/**
	 * Load first event in next 7 days
	 * @param context
	 * @return
	 */
	private Event getNextEvent(Context context) {
		
		EventsDatabase database = new EventsDatabase(context);
  			
	  	Date from = new Date();
	  	from.setTime(from.getTime());
	  	Date to = new Date();
	  	to.setTime(to.getTime() + 7 * DateUtils.DAY_IN_MILLIS);

	  	Event firstEvent = null;
	  	
	  	// Load fist event in next 7 days
		for (Event event : database.getEvents(from, to, EventsDatabase.STATUS_DONT_CARE)) {
			
			if (firstEvent == null) {
				firstEvent = event;
			}
			
			if (event.getBeginDate().compareTo(firstEvent.getBeginDate()) < 0) {
				firstEvent = event;
			}

		}
		
		return firstEvent;
		
	}
	
	private long getAlarmTime(Context context) {
		
		Date date = new Date();
		int day = date.getDay();
		
		boolean flag = false;
		
		Event event = getFirstDayEvent(context, day);
		Alarm alarm = getAlarm(context, day);
		
		long alarmTime = -1;
		
		
		if (event != null) {
			alarmTime = event.getBeginDate().getTime() - alarm.getWakeUpOffset();
		}
		else {
				alarmTime = alarm.getWakeUpTimeout();
				flag = true;
		}
		
		// If alarm was ringing today, show tomorow alarm
		long time = date.getHours() * DateUtils.HOUR_IN_MILLIS +
				    date.getMinutes() * DateUtils.MINUTE_IN_MILLIS +
				    date.getSeconds() * DateUtils.SECOND_IN_MILLIS;
		
		if (time > alarmTime) {
			
			day = day + 1 % 7;
			event = getFirstDayEvent(context, day);
			alarm = getAlarm(context, day);
			this.tomorrow = true;
			
			if (event != null) {
				alarmTime = event.getBeginDate().getTime() - alarm.getWakeUpOffset();
			}
			else {
				if (alarm.isEnabled()) {
					alarmTime = alarm.getWakeUpTimeout() - DateUtils.HOUR_IN_MILLIS;
				}
			}
			
			
			
		}
				
		if (flag) alarmTime = alarm.getWakeUpTimeout() - DateUtils.HOUR_IN_MILLIS;
		
		return alarmTime;
	}
	
    private Event getFirstDayEvent(Context context, int day) {
    	
    	EventsDatabase database = new EventsDatabase(context);
        
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
	  
	  

}