package com.vutbr.fit.tam.calendar;

import java.util.Date;
import java.util.Set;

import com.vutbr.fit.tam.alarm.Alarm;
import com.vutbr.fit.tam.alarm.AlarmLauncher;
import com.vutbr.fit.tam.database.AlarmAdapter;
import com.vutbr.fit.tam.database.EventsDatabase;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class CalendarChecker extends BroadcastReceiver {

	private static Date toDay;
	private static long cTime;
	private static Date cDate;
	
	private int AlarmColumnIndex = 3;

	
	@Override
	public void onReceive(Context c, Intent arg1) {
		// TODO vybrat udaje z kalendara a upravit podla nich alarm
		
		cTime = System.currentTimeMillis();
		cDate = new Date(cTime);
		
		EventsDatabase eD = new EventsDatabase(c);
		this.getToday();
		Event e = eD.getFirstEvent(toDay, EventsDatabase.STATUS_AVAILABLE);
		
		// today as day of week 0(Sun)-6(Sat)
		//int dayID = (6+cDate.getDay())%7;
		int dayID = cDate.getDay();
		
		long actAlarm;
		AlarmAdapter aD;
		Alarm alarm;
		
		try {
			aD = new AlarmAdapter(c).open();
			Cursor cursorACT = aD.fetchAlarm(Alarm.ACTUAL_ALARM_ID);
			Cursor cursorDAY = aD.fetchAlarm(dayID);
			
			if (cursorDAY != null) {
				alarm = new Alarm(dayID, cursorDAY.getInt(0)>0, cursorDAY.getInt(1), cursorDAY.getInt(2), cursorDAY.getInt(3));
			} else {
				// ak nie je ziadny obsah v tabulke pre dany den, nic sa nedeje
				Log.e("CalendarChecker", "Empty DB.");
				aD.close();
				return;
			}
			 
			if (cursorACT != null) {
				// ak mame nejaky zaznam alarmu, tak zistime ci je potrebne alarm updatovat
				actAlarm = cursorACT.getLong(AlarmColumnIndex);
				if (updateNecessary(actAlarm, e, alarm) && alarm.isEnabled()) updateExistingAlarm(aD, alarm, e, c);
			} else if (alarm.isEnabled()){
				// ak ziaden alarm ulozeny nemame, a ma sa spustit, tak ho pridame
				addNewAlarm(aD, alarm, e);
			} 
			// !!!!!!!!!! bacha pri boote treba vzdy nastavit alarm !!!!!!!!!!!!!!!!!!!!!!
			
			aD.close();
			
		} catch (SQLException ex) {
			// mozna hlaska o zlyhani nacitania DB
			Log.e("CalendarChecker", "AlarmAdapter error: "+ex.toString());
			return;
		}
		
		

	}
	// Sets variable toDay to beggining of current day
	private void getToday() {
		toDay = new Date(cTime-cDate.getHours()*(60*60*1000)-cDate.getMinutes()*(60*1000));
	}
	
	public void updateExistingAlarm(AlarmAdapter aD, Alarm alarm, Event e, Context c) {
		Alarm a = new Alarm(Alarm.ACTUAL_ALARM_ID, true, 0, 0, (int)(e.getBeginDate().getTime()-alarm.getWakeUpOffset()*60*1000));
		aD.updateAlarm(a);
		setAlarmTime(a.getSleepTime(), c);
	}
	
	public void addNewAlarm(AlarmAdapter aD, Alarm alarm, Event e) {
		Alarm a = new Alarm(Alarm.ACTUAL_ALARM_ID, true, 0, 0, (int)(e.getBeginDate().getTime()-alarm.getWakeUpOffset()*60*1000));
		aD.insertAlarm(a);
	}
	
	public boolean updateNecessary(long act, Event e, Alarm alarm) {
		if (act != e.getBeginDate().getTime()-alarm.getWakeUpOffset()*60*1000) return true;
		else return false;
	}
	
	private void setAlarmTime(long millis, Context c) {
		AlarmManager mgr=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
		Intent i=new Intent(c, AlarmLauncher.class);
	    PendingIntent pi=PendingIntent.getBroadcast(c, 0, i, 0);
	    
	    mgr.set(AlarmManager.RTC_WAKEUP, millis, pi);
	}
	
	// Sets variable lastDay to Sunday of the current week, at 23:59:XX
	//private void getLastDayOfWeek() {
	//	lastDay = new Date(cTime+(7-(6+cDate.getDay())%7+1)*(24*60*60*1000)+(23-cDate.getHours())*(60*60*1000)+(59-cDate.getMinutes())*(60*1000));
	//}

}
