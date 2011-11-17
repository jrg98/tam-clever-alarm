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
import android.widget.Toast;

public class CalendarChecker extends BroadcastReceiver {

	private Date toDay;
	private long currentTime;
	private Date currentDate;
	private boolean alarmActive;
	
	private int AlarmColumnIndex = 3;

	
	@Override
	public void onReceive(Context c, Intent arg1) {
		
		currentTime = System.currentTimeMillis();
		currentDate = new Date(currentTime);
		
		EventsDatabase eD = new EventsDatabase(c);
		this.getToday();
		Event e;
		
		int dayID = currentDate.getDay();
		
		
		
		long actAlarm;
		AlarmAdapter aD;
		Alarm dbAlarm;
		long dayAlarm;
		
		try {
			aD = new AlarmAdapter(c).open();
			Cursor cursorACT = aD.fetchAlarm(Alarm.ACTUAL_ALARM_ID);
			Cursor cursorDAY = aD.fetchAlarm(dayID);
			
			e = eD.getFirstEvent(toDay, EventsDatabase.STATUS_AVAILABLE);
			
			if (cursorDAY.moveToFirst()) {
				dbAlarm = new Alarm(dayID, cursorDAY.getInt(0)>0, cursorDAY.getInt(1), cursorDAY.getInt(2), cursorDAY.getInt(3));
				// uprava hodnoty aby zodpovedala pouzitiu
				dbAlarm.setWakeUpTimeout(alarmRestruct(dbAlarm.getWakeUpTimeout()));
				
			} else {
				// ak nie je ziadny obsah v tabulke pre dany den, nic sa nedeje
				Log.e("CalendarChecker", "Empty DB.");
				aD.close();
				return;
			}
			
			// ak neni na dnes alarm, zrusime vsetky a koncime
			if (!dbAlarm.isEnabled()) {
				cancelAlarm(c);
				aD.close();
				return;
			}
			
			// nastavenie hodnoty dayAlarm
			if (e.getBeginDate().getTime() - dbAlarm.getWakeUpOffset() < dbAlarm.getWakeUpTimeout()) dayAlarm = e.getBeginDate().getTime() - dbAlarm.getWakeUpOffset();
			else dayAlarm = dbAlarm.getWakeUpTimeout();	
			
			// nastavenie actAlarm, ak neexistuje zaznam v tabulke, nastavi sa na dayAlarm a vlozi do db, a nastavi sa nan alarm
			if (cursorACT.moveToFirst()) {
				actAlarm = cursorACT.getInt(AlarmColumnIndex);
				alarmActive = cursorACT.getInt(0)>0;
			} else { 
				actAlarm = dayAlarm;
				addNewAlarm(aD, actAlarm, c);
				setAlarmTime(actAlarm, c);
				alarmActive = true;
			}
			
			// ak je alarm spravne na dnesok a nema byt spusteny, tak rusim vsetky alarmy - nastane ak sa zrusi na widgete
			if (alarmIsToday(actAlarm) && !alarmActive) {
				cancelAlarm(c);
				aD.close();
				return;
			}
			
			if (actAlarm != dayAlarm) {
				// nastavujeme alarm iba ak este nenastal
				if (dayAlarm < currentTime) updateExistingAlarm(aD, dayAlarm, c);
			}
			
			aD.close();
			
		} catch (Exception ex) {
			// mozna hlaska o zlyhani nacitania DB
			Log.e("CalendarChecker", "AlarmAdapter error: "+ex.toString());
			Toast.makeText(c, ex.toString(), Toast.LENGTH_LONG);
			return;
		}
		
		

	}
	// Sets variable toDay to beggining of current day
	private void getToday() {
		toDay = new Date(currentTime-currentDate.getHours()*(60*60*1000)-currentDate.getMinutes()*(60*1000));
	}
	
	// updatene alarm v databazi a zaroven nastavi dany alarm na spustenie
	public void updateExistingAlarm(AlarmAdapter aD, long atime, Context c) {
		Alarm a = new Alarm(Alarm.ACTUAL_ALARM_ID, true, 0, 0, atime);
		aD.updateAlarm(a);
		if (a.getSleepTime() > currentTime) setAlarmTime(a.getSleepTime(), c);
	}
	
	public void addNewAlarm(AlarmAdapter aD, long atime, Context c) {
		Alarm a = new Alarm(Alarm.ACTUAL_ALARM_ID, true, 0, 0, atime);
		aD.insertAlarm(a);
		if (a.getSleepTime() > currentTime) setAlarmTime(a.getSleepTime(), c);

	}
	
	
	private void setAlarmTime(long millis, Context c) {
		AlarmManager mgr=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
		Intent i=new Intent(c, AlarmLauncher.class);
	    PendingIntent pi=PendingIntent.getBroadcast(c, 0, i, 0);
	    
	    mgr.set(AlarmManager.RTC_WAKEUP, millis, pi);
	}
	
	private void cancelAlarm(Context c) {
		AlarmManager mgr=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
		Intent i=new Intent(c, AlarmLauncher.class);
	    PendingIntent pi=PendingIntent.getBroadcast(c, 0, i, 0);
	    mgr.cancel(pi);
	}
	
	private long alarmRestruct(long offset) {
		Date a = new Date(currentDate.getYear(), currentDate.getMonth(), currentDate.getDate());
		return a.getTime()+offset;
	}
	
	private boolean alarmIsToday(long a) {
		Date d = new Date(a);
		if (d.getDay() == currentDate.getDay()) return true;
		else return false;
	}
	
	// Sets variable lastDay to Sunday of the current week, at 23:59:XX
	//private void getLastDayOfWeek() {
	//	lastDay = new Date(cTime+(7-(6+cDate.getDay())%7+1)*(24*60*60*1000)+(23-cDate.getHours())*(60*60*1000)+(59-cDate.getMinutes())*(60*1000));
	//}

}
