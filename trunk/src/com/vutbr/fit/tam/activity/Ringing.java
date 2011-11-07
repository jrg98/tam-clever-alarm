package com.vutbr.fit.tam.activity;

import java.util.Calendar;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.alarm.AlarmLauncher;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
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
	
	private Ringtone ringtone;
	private Uri uri;
	private Button stopButton;
	private Button snoozeButton;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ringing);
        
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        // Turn on the screen unless we are being launched from the AlarmAlert
        // subclass.
        if (!getIntent().getBooleanExtra("SCREEN_OFF", false)) {
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
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
        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Wake up you lazy bitch!")
               .setCancelable(false)
               .setPositiveButton("Stop", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   dialog.cancel();
                	   stopRingtone();
                	   finish();
                   }
               })
               .setNegativeButton("Snooze", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        stopRingtone();
                        // TODO Schedule next alarm in x minutes
                        Intent intent = new Intent(Ringing.this, AlarmLauncher.class);
                        PendingIntent sender = PendingIntent.getBroadcast(Ringing.this,
                                0, intent, 0);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.add(Calendar.SECOND, 70);

                        // Schedule the alarm!
                        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
                        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                        finish();
                   }
               });
        AlertDialog alert = builder.create();
        */
        startRinging();
        /*alert.show();*/
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
        finish();
	}
	
	private void stopRinging() {
		if (ringtone != null && ringtone.isPlaying() == true) {
			ringtone.stop();
		}
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
	
}