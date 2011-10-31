/**
 * 
 */
package com.vutbr.fit.tam.activity;

import com.vutbr.fit.tam.R;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Zolex
 *
 */
public class RingtonesSettings extends Activity implements OnClickListener {
	
	private Button stopRingtoneButton;
	private Button playRingtoneButton;
	private Button selectRingtoneButton;
	private Ringtone ringtone;
	private TextView ringtoneName;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ringtones_settings);

        this.playRingtoneButton = (Button) this.findViewById(R.id.playRingtone);
        this.playRingtoneButton.setOnClickListener(this);
        
        this.stopRingtoneButton = (Button) this.findViewById(R.id.stopRingtone);
        this.stopRingtoneButton.setOnClickListener(this);
        
        this.selectRingtoneButton = (Button) this.findViewById(R.id.selectRingtone2);
        this.selectRingtoneButton.setOnClickListener(this);
        
        this.ringtoneName = (TextView) this.findViewById(R.id.settingsRingtoneName);
    }

	private void selectRingtone() {
		stopRingtone();
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER); 
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select alarm tone");
	    startActivityForResult(intent, 1);
	}
	
	private void playRingtone() {
		if (ringtone != null && ringtone.isPlaying() == false) {
			ringtone.play();
			playRingtoneButton.setEnabled(false);
			stopRingtoneButton.setEnabled(true);
		} else {
			
		}
	}
	
	private void stopRingtone() {
		if (ringtone != null && ringtone.isPlaying() == true) {
			ringtone.stop();
			playRingtoneButton.setEnabled(true);
			stopRingtoneButton.setEnabled(false);
		} else {
			
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
                ringtoneName.setText(ringtone.getTitle(getApplicationContext()));
                playRingtoneButton.setEnabled(true);
            } else {
            	ringtoneName.setText(R.string.not_set);
            	playRingtoneButton.setEnabled(false);
                stopRingtoneButton.setEnabled(false);
            }
}
	}
	
	public void onClick(View view) {
		
		switch (view.getId()) {
			case R.id.selectRingtone2:
				this.selectRingtone();
				break;
			case R.id.playRingtone:
				this.playRingtone();
				break;
			case R.id.stopRingtone:
				this.stopRingtone();
				break;
			}
			
		}

}
