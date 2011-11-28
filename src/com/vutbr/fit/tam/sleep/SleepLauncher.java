package com.vutbr.fit.tam.sleep;

import java.util.Date;

import com.vutbr.fit.tam.alarm.Alarm;
import com.vutbr.fit.tam.alarm.AlarmLauncher;
import com.vutbr.fit.tam.database.AlarmAdapter;
import com.vutbr.fit.tam.database.SettingsAdapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class SleepLauncher extends BroadcastReceiver {

	private SettingsAdapter settingsAdapter;
	private AudioManager audioManager;
	private int isSleepActive;
	private int sleepRingMode;
	
	@Override
	public void onReceive(Context c, Intent arg1) {
	
		settingsAdapter = new SettingsAdapter(c);
		audioManager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
		
		settingsAdapter.open();
		isSleepActive = Integer.parseInt(settingsAdapter.fetchSetting("isSleepActive", "0"));
		sleepRingMode = Integer.parseInt(settingsAdapter.fetchSetting("sleepRingMode", "0"));
	
		if (isSleepActive == 0) {
			if (!settingsAdapter.updateSetting("sleepRingModeB", Integer.toString(audioManager.getRingerMode())))
	        	settingsAdapter.insertSetting("sleepRingModeB", Integer.toString(audioManager.getRingerMode()));
			audioManager.setRingerMode(sleepRingMode);
			if (!settingsAdapter.updateSetting("isSleepActive", Integer.toString(1)))
	        	settingsAdapter.insertSetting("isSleepActive", Integer.toString(1));
		}
		
		this.settingsAdapter.close();
	}
}