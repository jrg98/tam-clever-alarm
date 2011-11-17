package com.vutbr.fit.tam.sleep;

import java.util.Date;

import com.vutbr.fit.tam.alarm.Alarm;
import com.vutbr.fit.tam.alarm.AlarmLauncher;
import com.vutbr.fit.tam.database.AlarmAdapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

public class SleepLauncher extends BroadcastReceiver {

	private Date currentDate;
	
	@Override
	public void onReceive(Context c, Intent arg1) {
		// TODO launch sleep routine, if alarm is enabled
		
		
		currentDate = new Date(System.currentTimeMillis());
		
		nextSleepTime(c);

	}
	
	private void nextSleepTime(Context c) {
		AlarmAdapter aD;
		try {
			aD = new AlarmAdapter(c).open();
			Cursor cursor = aD.fetchAlarm((currentDate.getDay()+1)%7);
			
			if (cursor.moveToFirst()) {
				setSleepTime(cursor.getLong(3), c);
			}
			
		} catch (Exception ex) {
			Log.e("CalendarChecker", "AlarmAdapter error: "+ex.toString());
			Toast.makeText(c, ex.toString(), Toast.LENGTH_LONG);
			return;
		}
		
		
	}
	
	private void setSleepTime(long millis, Context c) {
		AlarmManager mgr=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
		Intent i=new Intent(c, SleepLauncher.class);
	    PendingIntent pi=PendingIntent.getBroadcast(c, 0, i, 0);
	    
	    mgr.set(AlarmManager.RTC_WAKEUP, millis, pi);
	}

}
