package com.vutbr.fit.tam.widget;

import java.util.Date;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.widget.RemoteViews;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.calendar.Event;
import com.vutbr.fit.tam.database.EventsDatabase;

public class WidgetUpdateServise extends Service {

		
	@Override
	public void onStart(Intent intent, int startId) {

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

		int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
		
		
		String nextEvent = queryNextEvent();
		
		if (appWidgetIds.length > 0) {
			for (int widgetId : appWidgetIds) {

				RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.widget);
				remoteViews.setTextViewText(R.id.tvNextEvent, nextEvent);

				changeSwitchButton(remoteViews);
				
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
	public void changeSwitchButton(RemoteViews remoteViews) {
		Bitmap theImage = BitmapFactory.decodeResource(getResources() , R.drawable.button_start);
    	theImage = Bitmap.createBitmap(theImage);
    	remoteViews.setImageViewBitmap(R.id.switchButton, theImage);
	}
	
	// Get next event
	private String queryNextEvent() {
		
		EventsDatabase database = new EventsDatabase(this);
    	
		String nextEvent = null;
		
    	Date from = new Date();
    	from.setTime(from.getTime() - DateUtils.DAY_IN_MILLIS);
    	Date to = new Date();
    	to.setTime(to.getTime() + DateUtils.DAY_IN_MILLIS);
    	
		for (Event event : database.getEvents(from, to, EventsDatabase.STATUS_AVAILABLE)) {

			String title = event.getTitle();
			Date begin = event.getBeginDate();
			Date end = event.getEndDate();

			nextEvent = "Title: " + title + " Begin: " + begin + "\nEnd: " + end + "\n";
			
			return nextEvent;
			
		}
		
		return nextEvent;
		
	}

}
