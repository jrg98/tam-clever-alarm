package com.vutbr.fit.tam.widget;

import java.util.Date;

import android.R.bool;
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
import com.vutbr.fit.tam.database.SettingsAdapter;

public class WidgetUpdateService extends Service {

	@Override
	public void onStart(Intent intent, int startId) {

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

		int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
		
		//load info about sleep mod form db
		Boolean sleepMode = isSleepMode(); 
				
		// Actualize all showed widget
		if (appWidgetIds.length > 0) {
			for (int widgetId : appWidgetIds) {

				RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.widget);
				
				// Set info about sleep mode - reverse state
				this.sleepMode(!sleepMode, remoteViews);
				appWidgetManager.updateAppWidget(widgetId, remoteViews);

			}
			
		}
		
		stopSelf();
		super.onStart(intent, startId);
	}
	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Load info about sleepmode from db
	 * @return
	 */
	private boolean isSleepMode() {
		
    	SettingsAdapter settingsAdapter = new SettingsAdapter(this);
    	settingsAdapter.open();
    	
    	final String sleepmode = settingsAdapter.fetchSetting("sleepmode", "false");
    	settingsAdapter.close();
    	
    	return sleepmode.startsWith("true");
		
	}
	

	/**
	 * Set sleepMode GUI
	 * 
	 * @param sleepmode - true/false
	 * @param remoteViews
	 */
	private void sleepMode(boolean sleepmode, RemoteViews remoteViews) {
		
		if (sleepmode) {
			remoteViews.setTextViewText(R.id.tvWidgetMode, this.getResources().getText(R.string.sleep));
		}
		else {
			remoteViews.setTextViewText(R.id.tvWidgetMode, "");
		}
		
		remoteViews.setImageViewBitmap(R.id.switchButton, this.getButtonImage(sleepmode));
			
		// Save info about sleepmode to db
		SettingsAdapter settingsAdapter = new SettingsAdapter(getApplicationContext()); 
		
		settingsAdapter.open();
	    
		if (!settingsAdapter.updateSetting("sleepmode", String.valueOf(sleepmode)))
	       	settingsAdapter.insertSetting("sleepmode", String.valueOf(sleepmode));
		
	    settingsAdapter.close();
		
		
	}
	
	/**
	 * 	Load image for widget button
	 * @param sleepMode
	 * @return Bitmap image loaded from resource
	 */
	public Bitmap getButtonImage (boolean sleepMode) {
		
		Bitmap theImage = BitmapFactory.decodeResource(getResources() ,
								sleepMode ? R.drawable.button_start : R.drawable.button_quit);
		
		return Bitmap.createBitmap(theImage);

	}
	

}
