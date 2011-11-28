package com.vutbr.fit.tam.alarm;

import com.vutbr.fit.tam.activity.Ringing;
import com.vutbr.fit.tam.database.SettingsAdapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class AlarmLauncher extends BroadcastReceiver {

	private SettingsAdapter settingsAdapter;
	private AudioManager audioManager;
	private int isSleepActive;
	private int backupRingMode;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		settingsAdapter = new SettingsAdapter(context);
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		settingsAdapter.open();
		isSleepActive = Integer.parseInt(settingsAdapter.fetchSetting("isSleepActive", "0"));
		backupRingMode = Integer.parseInt(settingsAdapter.fetchSetting("sleepRingModeB", "2")); // 2 = normal
	
		if (isSleepActive == 1) {
			audioManager.setRingerMode(backupRingMode);
			if (!settingsAdapter.updateSetting("isSleepActive", Integer.toString(0)))
	        	settingsAdapter.insertSetting("isSleepActive", Integer.toString(0));
		}
		
		this.settingsAdapter.close();
		
		Intent i = new Intent(context, Ringing.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		i.addFlags(Intent.FLAG_FROM_BACKGROUND);
    	context.startActivity(i);
	}

}
