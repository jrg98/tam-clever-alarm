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

/**
 * @author Zolex
 *
 */
public class RingtonesSettings extends Activity implements OnClickListener {
	
	private Button selectRingtoneButton;
	private Button playRingtoneButton;
	private Ringtone ringtone;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ringtones_settings);

        this.selectRingtoneButton = (Button) this.findViewById(R.id.selectRingtone);
        this.selectRingtoneButton.setOnClickListener(this);
        
        this.playRingtoneButton = (Button) this.findViewById(R.id.playRingtone);
        this.playRingtoneButton.setOnClickListener(this);
        
    }

	private void selectRingtone() {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER); 
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select alarm tone");
	    startActivityForResult(intent, 1);
	}
	
	private void playRingtone() {
		if (ringtone != null) {
			ringtone.play();
		} else {
			
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
            }
}
	}
	
	public void onClick(View view) {
		
		switch (view.getId()) {
			case R.id.selectRingtone:
				this.selectRingtone();
				break;
			case R.id.playRingtone:
				this.playRingtone();
				break;
			}
			
		}

}
