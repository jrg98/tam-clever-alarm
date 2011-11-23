package com.vutbr.fit.tam.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.vutbr.fit.tam.R;

public class CleverAlarmWidgetProvider extends AppWidgetProvider {
		
	  @Override
	  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		  	
		  RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget);
		  Intent intent = new Intent(context.getApplicationContext(), WidgetUpdateServise.class);
		  intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

		  PendingIntent pendingIntent = PendingIntent.getService(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		  remoteViews.setOnClickPendingIntent(R.id.switchButton, pendingIntent);

		  appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

		  // Update the widgets via the service
		  context.startService(intent);
		  		  
	  }
	  
	  @Override
	  public void onReceive(Context context, Intent intent) {
		  super.onReceive(context, intent);
		  Log.i("CAWidgetProvider", "Prisla sprava.");
	  }

}