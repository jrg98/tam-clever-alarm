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
import android.widget.Toast;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.alarm.Alarm;
import com.vutbr.fit.tam.calendar.Event;
import com.vutbr.fit.tam.database.AlarmAdapter;
import com.vutbr.fit.tam.database.EventsDatabase;
import com.vutbr.fit.tam.database.SettingsAdapter;

public class CleverAlarmWidgetProvider extends AppWidgetProvider {
		
	  private Context context;
	  private AppWidgetManager appWidgetManager;
	  private int[] appWidgetIds; 
	
	  @Override
	  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		  	
		  RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget);
		  Intent intent = new Intent(context.getApplicationContext(), WidgetUpdateService.class);
		  intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

		  PendingIntent pendingIntent = PendingIntent.getService(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		  remoteViews.setOnClickPendingIntent(R.id.switchButton, pendingIntent);

		  appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

		  this.update();
		  
		  this.context = context;
		  this.appWidgetManager = appWidgetManager;
		  this.appWidgetIds = appWidgetIds;
		  
		  Log.v("LOOG", "Widget on update");
		  
		  // Update the widgets via the service
		  //context.startService(intent);
		  		  
	  }
	  
	  @Override
	  public void onReceive(Context context, Intent intent) {
		  
		  Log.i("CAWidgetProvider", "Prisla sprava.");
		  Toast.makeText(context, "onReceiver()", Toast.LENGTH_LONG).show();
		  this.update();
		  super.onReceive(context, intent);
		
	  }


		//int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
		
	  private void update() {
	  
		if (this.context == null) {
			Log.v("LOOG", "WIdget neni context");
			return;
		}
		  
		Event nextEvent = this.getNextEvent(this.context);
		Alarm nextAlarm = this.getNextAlarm(this.context);
		
		final String timeFormat = this.loadTimeFormat(context);
		
		// Actualize all showed widget
		if (appWidgetIds.length > 0) {
			for (int widgetId : appWidgetIds) {

				RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
				
				// Set info about next event
				if (nextEvent != null) {
					remoteViews.setTextViewText(R.id.tvWidgetNextEventName, nextEvent.getTitle());
					remoteViews.setTextViewText(R.id.tvWidgetNextEventDate, nextEvent.getBeginDate().toLocaleString());
				}
				else {
					remoteViews.setTextViewText(R.id.tvWidgetNextEventName, "-");
					remoteViews.setTextViewText(R.id.tvWidgetNextEventDate, "-");
				}
							
				// Set info about alarm
				if (nextAlarm.isEnabled()) {
					remoteViews.setTextViewText(R.id.tvWidgetAlarmTime, DateFormat.format(timeFormat, nextAlarm.getWakeUpTimeout()).toString());
				} else
				{
					remoteViews.setTextViewText(R.id.tvWidgetAlarmTime, "-");
				}

				// Set info about sleep mode
				Boolean sleepMode = true; // TODO: load status from db
				this.sleepmMode(sleepMode, remoteViews, context);

				appWidgetManager.updateAppWidget(widgetId, remoteViews);

			}
		//	stopSelf();
		}
		//super.onStart(intent, startId);
	}
	
  private String loadTimeFormat(Context context) {

  	SettingsAdapter settingsAdapter = new SettingsAdapter(context);
  	settingsAdapter.open();
  	
  	String format = settingsAdapter.fetchSetting("timeformat", DateFormat.HOUR_OF_DAY + ":" + DateFormat.MINUTE + DateFormat.MINUTE);
    settingsAdapter.close();
      
    return format;
  }

	
	/**
	 * Set sleepMode GUI
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
	
	
	private Alarm getNextAlarm(Context context) {
					
	  		AlarmAdapter adapter;

	  		Date date = new Date();
	  		final int id = date.getDay();
	  		// id = (id + 1) % 7 ????????
	  		
	  		Alarm alarm = new Alarm(id, false, 0, 0, 0);
		
	  		try {
				adapter = new AlarmAdapter(context).open();
			
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
	private Event getNextEvent(Context context) {
		
		EventsDatabase database = new EventsDatabase(context);
  			
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