package com.vutbr.fit.tam.activity;

import com.vutbr.fit.tam.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;


public class Ringing extends Activity {
	
	private Ringtone ringtone;
	private Uri uri;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setContentView(R.layout.ringing);
        
        // TODO Load uri from database, default ringtone:
        uri = Uri.parse("content://settings/system/ringtone");
        
        if (uri != null) {
        	ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
        }
        
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
                        finish();
                   }
               });
        AlertDialog alert = builder.create();
        
        playRingtone();
        alert.show();
    }
	
	private void playRingtone() {
		if (ringtone != null && ringtone.isPlaying() == false) {
			ringtone.play();
		}
	}
	
	private void stopRingtone() {
		if (ringtone != null && ringtone.isPlaying() == true) {
			ringtone.stop();
		}
	}
	
}