package com.vutbr.fit.tam.alarm;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class AlarmUpdater extends IntentService {

	public static final String LOCK_NAME_STATIC="com.vutbr.fit.tam.AlarmUpdater.Lock";
	private WakeLock lockStatic;
	
	public AlarmUpdater() {
		super("AlarmUpdater");
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		PowerManager mgr=(PowerManager)this.getSystemService(Context.POWER_SERVICE);
		lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
		lockStatic.acquire();
		
		// ziska udaje z kalendaru o najblizsom termine alarmu a nastavi podla toho alarm
		
		lockStatic.release();

	}
	
	// nastavi spustenie AlarmLauncher na hodnotu millis (ms od 1.1.1970 00:00)
	private void setAlarmTime(long millis) {
		AlarmManager mgr=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		Intent i=new Intent(this, AlarmLauncher.class);
	    PendingIntent pi=PendingIntent.getBroadcast(this, 0, i, 0);
	    
	    mgr.set(AlarmManager.RTC_WAKEUP, millis, pi);
	}

}
