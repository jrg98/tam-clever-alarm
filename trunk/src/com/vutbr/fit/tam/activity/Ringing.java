package com.vutbr.fit.tam.activity;

import java.util.Calendar;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.alarm.AlarmLauncher;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class Ringing extends Activity implements OnClickListener {
	
	private AudioManager audioManager;
	private Ringtone ringtone;
	private Uri uri;
	private Button stopButton;
	private Button snoozeButton;
	private int systemVolume;
	private int systemRingMode;
	private int ringingVolume;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ringing);
        
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        if (!getIntent().getBooleanExtra("SCREEN_OFF", false)) {
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        
        this.stopButton = (Button) this.findViewById(R.id.stopButton);
        this.stopButton.setOnClickListener(this);
        
        this.snoozeButton = (Button) this.findViewById(R.id.snoozeButton);
        this.snoozeButton.setOnClickListener(this);
        
        // TODO Load uri from database, default ringtone:
        uri = Uri.parse("content://settings/system/ringtone");
        
        if (uri != null) {
        	ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
        }
        
        this.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // TODO Load ringingVolume from database... max volume:
        this.ringingVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        this.systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING); // Backup current volume
        this.systemRingMode = audioManager.getRingerMode();
        
        this.audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        this.audioManager.setStreamVolume(AudioManager.STREAM_RING, ringingVolume, 0);
        startRinging();
    }
	
	private void startRinging() {
		if (ringtone != null && ringtone.isPlaying() == false) {
			ringtone.play();
		}
	}
	
	private void snooze() {
		if (ringtone != null && ringtone.isPlaying() == true) {
			ringtone.stop();
		}
		Intent intent = new Intent(Ringing.this, AlarmLauncher.class);
        PendingIntent sender = PendingIntent.getBroadcast(Ringing.this,
                0, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);

        // Schedule the alarm!
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        this.audioManager.setStreamVolume(AudioManager.STREAM_RING, systemVolume, 0); // Restore system volume
        this.audioManager.setRingerMode(systemRingMode);
        finish();
	}
	
	private void stopRinging() {
		if (ringtone != null && ringtone.isPlaying() == true) {
			ringtone.stop();
		}
		this.audioManager.setStreamVolume(AudioManager.STREAM_RING, systemVolume, 0); // Restore system volume
		this.audioManager.setRingerMode(systemRingMode);
		finish();
	}

	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.stopButton:
			this.stopRinging();
			break;
		case R.id.snoozeButton:
			this.snooze();
			break;
		}
		
	}
	
	public void onPause() {
		if (ringtone != null && ringtone.isPlaying() == true) {
			ringtone.stop();
		}
		this.audioManager.setStreamVolume(AudioManager.STREAM_RING, systemVolume, 0); // Restore system volume
		this.audioManager.setRingerMode(systemRingMode);
		super.onPause();
	}
	
}