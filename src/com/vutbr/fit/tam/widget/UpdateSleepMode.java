package com.vutbr.fit.tam.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

import com.vutbr.fit.tam.R;

public class UpdateSleepMode extends Service {

		
	@Override
	public void onStart(Intent intent, int startId) {

				
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

		int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
		
		
		// Actualize all showed widget
		if (appWidgetIds.length > 0) {
			for (int widgetId : appWidgetIds) {

				RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.widget);
				
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
	


}
