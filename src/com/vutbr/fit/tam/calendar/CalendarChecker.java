package com.vutbr.fit.tam.calendar;

import java.util.Date;
import java.util.Set;

import com.vutbr.fit.tam.alarm.Alarm;
import com.vutbr.fit.tam.alarm.AlarmLauncher;
import com.vutbr.fit.tam.database.AlarmAdapter;
import com.vutbr.fit.tam.database.EventsDatabase;
import com.vutbr.fit.tam.sleep.SleepLauncher;
import com.vutbr.fit.tam.widget.CleverAlarmWidgetProvider;
import com.vutbr.fit.tam.widget.WidgetRefreshService;

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
	private int SleepColumnIndex = 2;

	
	@Override
	public void onReceive(Context c, Intent arg1) {
		
		currentTime = System.currentTimeMillis();
		currentDate = new Date(currentTime);
		
		EventsDatabase eD = new EventsDatabase(c);
		this.getToday();
		Event e;
		
		int dayID = currentDate.getDay();
		
		Log.i("Calendar checker", "Checking calendar....");
		
		c.startService(new Intent(c, WidgetRefreshService.class));
		
		AlarmAdapter aD;
		Alarm dbAlarm;
		
		long actAlarm;
		long dayAlarm;
		
		long actSleep;
		long daySleep;
		
		try {
			aD = new AlarmAdapter(c).open();
			Cursor cursorACT = aD.fetchAlarm(Alarm.ACTUAL_ALARM_ID);
			Cursor cursorDAY = aD.fetchAlarm(dayID);
			
			e = eD.getFirstEvent(toDay, EventsDatabase.STATUS_AVAILABLE);
			
			if (cursorDAY.moveToFirst()) {
				Log.i("Calendar Checker", "Nacital sa den z databaze "+cursorDAY.getLong(2));
				dbAlarm = new Alarm(dayID, cursorDAY.getInt(0)>0, cursorDAY.getLong(1), cursorDAY.getLong(2), cursorDAY.getLong(3));
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
				Log.i("Calendar Checker", "Na dnes neni alarm aktivovany");
				cancelAlarm(c);
				aD.close();
				return;
			}
			
			daySleep = dbAlarm.getSleepTime();
			
			// nastavenie hodnoty dayAlarm
			if (e != null) {
				Log.i("Calendar Checker", "Mame event");
				if (e.getBeginDate().getTime() - dbAlarm.getWakeUpOffset() < dbAlarm.getWakeUpTimeout()) dayAlarm = e.getBeginDate().getTime() - dbAlarm.getWakeUpOffset();
				else dayAlarm = dbAlarm.getWakeUpTimeout();
			} else {dayAlarm = dbAlarm.getWakeUpTimeout();Log.i("Calendar Checker", "Nemame event");}
			
			// nastavenie actAlarm, ak neexistuje zaznam v tabulke, nastavi sa na dayAlarm a vlozi do db, a nastavi sa nan alarm
			if (cursorACT.moveToFirst()) {
				Log.i("Calendar Checker", "Mame zaznam v tabulke o dnesku");
				actAlarm = cursorACT.getLong(AlarmColumnIndex);
				actSleep = cursorACT.getLong(SleepColumnIndex);
				alarmActive = cursorACT.getInt(0)>0;
			} else {
				Log.i("Calendar Checker", "Nemame zaznam v tabulke o dnesku");
				actAlarm = dayAlarm;
				actSleep = daySleep;
				addNewAlarm(aD, actAlarm, actSleep, c);
				setAlarmTime(actAlarm, c);
				alarmActive = true;
			}
			
			// ak je alarm spravne na dnesok a nema byt spusteny, tak rusim vsetky alarmy - nastane ak sa zrusi na widgete
			if (alarmIsToday(actAlarm) && !alarmActive) {
				Log.i("Calendar Checker", "nikdy nenastane");
				cancelAlarm(c);
				cancelSleep(c);
				aD.close();
				return;
			}
			
			Date dll = new Date(dayAlarm);
			Log.i("Calendar Checker", "Stav alarmov aktualny " + actAlarm + " v DB " + dll.toString() + " momentalny cas " + currentDate.toString());
			
			// moyna moye robit problemy, ale skorej nie
			if (actSleep != daySleep) {
				if (daySleep > currentTime) {
					updateExistingSleep(aD, dayAlarm, daySleep, c);
				}
			}
			
			if (actAlarm != dayAlarm) {
				Log.i("Calendar Checker", "Je treba prestavit alarm");
				// nastavujeme alarm iba ak este nenastal
				if (dayAlarm > currentTime) {
					Log.i("Calendar Checker", "Updatuje sa alarm");
					updateExistingAlarm(aD, dayAlarm, daySleep, c);
				}
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
	public void updateExistingAlarm(AlarmAdapter aD, long atime,long stime, Context c) {
		Alarm a = new Alarm(Alarm.ACTUAL_ALARM_ID, true, 0, stime, atime);
		aD.updateAlarm(a);
		if (atime > currentTime) setAlarmTime(atime, c);
	}
	
	public void updateExistingSleep(AlarmAdapter aD, long atime, long stime, Context c) {
		Alarm a = new Alarm(Alarm.ACTUAL_ALARM_ID, true, 0, stime, atime);
		aD.updateAlarm(a);
		if (stime > currentTime) setSleepTime(stime, c);
	}
	
	public void addNewAlarm(AlarmAdapter aD, long atime, long stime, Context c) {
		Alarm a = new Alarm(Alarm.ACTUAL_ALARM_ID, true, 0, stime, atime);
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
	
	private void cancelSleep(Context c) {
		AlarmManager mgr=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
		Intent i=new Intent(c, SleepLauncher.class);
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
	
	private void setSleepTime(long millis, Context c) {
		AlarmManager mgr=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
		Intent i=new Intent(c, SleepLauncher.class);
	    PendingIntent pi=PendingIntent.getBroadcast(c, 0, i, 0);
	    
	    mgr.set(AlarmManager.RTC_WAKEUP, millis, pi);
	}
	
	// Sets variable lastDay to Sunday of the current week, at 23:59:XX
	//private void getLastDayOfWeek() {
	//	lastDay = new Date(cTime+(7-(6+cDate.getDay())%7+1)*(24*60*60*1000)+(23-cDate.getHours())*(60*60*1000)+(59-cDate.getMinutes())*(60*1000));
	//}

}
