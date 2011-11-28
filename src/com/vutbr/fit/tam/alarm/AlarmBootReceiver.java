package com.vutbr.fit.tam.alarm;

import java.util.Date;

import com.vutbr.fit.tam.calendar.CalendarChecker;
import com.vutbr.fit.tam.database.AlarmAdapter;
import com.vutbr.fit.tam.sleep.SleepLauncher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.SystemClock;
import android.util.Log;

public class AlarmBootReceiver extends BroadcastReceiver {
	
	private static final int PERIOD=60000;
	private static final int DEFAULT_PERIOD=-1;
	

	@Override
	public void onReceive(Context context, Intent intent) {
		// pri nabootovani nastavi na poslednu ulozenu hodnotu
		
		
		// nastavi alarmManager pre kontrolovanie zmien v kalendari
		this.setCalendarCheckerTime(context, DEFAULT_PERIOD);
		this.setAlarmTime(context);
		this.setSleepTime(context);

	}
	
	private void setCalendarCheckerTime(Context c, int p) {
		
		int pFinal;
		if (p<=0) pFinal = PERIOD;
		else pFinal = p;
		AlarmManager mgr=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
		Intent i=new Intent(c, CalendarChecker.class);
	    PendingIntent pi=PendingIntent.getBroadcast(c, 0, i, 0);
	    
	    mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime()+60000,
                pFinal,
                pi);
	}
	
	private void setAlarmTime(Context c) {
		AlarmAdapter aD;
		long actAlarm;
		try {
			aD = new AlarmAdapter(c).open();
			Cursor cursorACT = aD.fetchAlarm(Alarm.ACTUAL_ALARM_ID);
			 
				if (cursorACT.moveToFirst()) {
					// ak mame nejaky zaznam alarmu, tak zistime ci je potrebne alarm updatovat
					actAlarm = cursorACT.getLong(3);
				
					// nastavi alarm iba ak este nenastal ked bol device vypnuty, inak by ho odpalilo hned
					if (actAlarm > System.currentTimeMillis()) {
						AlarmManager mgr=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
						Intent i=new Intent(c, AlarmLauncher.class);
						PendingIntent pi=PendingIntent.getBroadcast(c, 0, i, 0);
			    
						mgr.set(AlarmManager.RTC_WAKEUP,
								actAlarm,
								pi);
					}
				}
			
			aD.close();
			
		} catch (SQLException ex) {
			// mozna hlaska o zlyhani nacitania DB
			Log.e("AlarmBootReciever", "AlarmAdapter error: "+ex.toString());
			return;
		}
	}
	
	private void setSleepTime(Context c) {
		AlarmAdapter aD;
		long actSleep;
		try {
			aD = new AlarmAdapter(c).open();
			Cursor cursorACT = aD.fetchAlarm(new Date(System.currentTimeMillis()).getDay());
			 
				if (cursorACT.moveToFirst()) {
					// ak mame nejaky zaznam alarmu, tak zistime ci je potrebne alarm updatovat
					actSleep = cursorACT.getLong(2);
				
					// nastavi alarm iba ak este nenastal ked bol device vypnuty, inak by ho odpalilo hned
					AlarmManager mgr=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
					Intent i=new Intent(c, SleepLauncher.class);
					PendingIntent pi=PendingIntent.getBroadcast(c, 0, i, 0);
			    
					mgr.set(AlarmManager.RTC_WAKEUP, actSleep, pi);
				}
			
			aD.close();
			
		} catch (SQLException ex) {
			// mozna hlaska o zlyhani nacitania DB
			Log.e("AlarmBootReciever", "AlarmAdapter error: "+ex.toString());
			return;
		}
	}

}
