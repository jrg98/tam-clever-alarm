package com.vutbr.fit.tam.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.database.SettingsAdapter;

public class WidgetUpdateService extends Service {

	@Override
	public void onStart(Intent intent, int startId) {

		Log.v("LOOG", "klik");
		
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

		int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
				
		//load info about sleep mod form db
		Boolean sleepMode = isSleepMode(); 
				
		// Actualize all showed widget
		if (appWidgetIds.length > 0) {
			for (int widgetId : appWidgetIds) {

				RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.widget);
				
				// Set info about sleep mode - reverse state
				this.showSleepMode(!sleepMode, remoteViews);
				this.setSleepMode(!sleepMode);
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
    	
    	final int isSleepActive = Integer.parseInt(settingsAdapter.fetchSetting("isSleepActive", "0"));
    	settingsAdapter.close();
    	
    	return (isSleepActive == 1);
		
	}
	

	/**
	 * Set sleepMode GUI
	 * 
	 * @param sleepmode - true/false
	 * @param remoteViews
	 */
	private void showSleepMode(boolean sleepmode, RemoteViews remoteViews) {
		
		if (sleepmode) {
			remoteViews.setTextViewText(R.id.tvWidgetMode, this.getResources().getText(R.string.sleep));
		}
		else {
			remoteViews.setTextViewText(R.id.tvWidgetMode, "");
		}
		
		remoteViews.setImageViewBitmap(R.id.switchButton, this.getButtonImage(sleepmode));
			
	}
	
	/**
	 * Save info about sleepmode to db
	 * Set audio profile
	 */
	private void setSleepMode(boolean sleepmode) {
		
		SettingsAdapter settingsAdapter = new SettingsAdapter(getApplicationContext()); 
		AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		
		settingsAdapter.open();
			    		
		final String sleep = sleepmode ? "1" : "0";
		
		if (!settingsAdapter.updateSetting("isSleepActive", sleep))
		   	settingsAdapter.insertSetting("isSleepActive", sleep);
		
		if (sleepmode == true) {
			final int sleepRingMode = Integer.parseInt(settingsAdapter.fetchSetting("sleepRingMode", "0"));
			
			if (!settingsAdapter.updateSetting("sleepRingModeB", Integer.toString(audioManager.getRingerMode())))
			 	settingsAdapter.insertSetting("sleepRingModeB", Integer.toString(audioManager.getRingerMode()));
				audioManager.setRingerMode(sleepRingMode);
				
		} else {
			final int backupRingMode = Integer.parseInt(settingsAdapter.fetchSetting("sleepRingModeB", "2"));
			audioManager.setRingerMode(backupRingMode);
		}
		
		
		
				
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
