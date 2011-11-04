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
	
	private Button playRingtoneButton;
	private Button selectRingtoneButton;
	private Ringtone ringtone;
	private TextView ringtoneName;
	private Uri uri;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ringtones_settings);

        this.playRingtoneButton = (Button) this.findViewById(R.id.playRingtone);
        this.playRingtoneButton.setOnClickListener(this);
        
        this.selectRingtoneButton = (Button) this.findViewById(R.id.selectRingtone2);
        this.selectRingtoneButton.setOnClickListener(this);
        
        this.ringtoneName = (TextView) this.findViewById(R.id.settingsRingtoneName);
        
        // TODO Load uri from database, default ringtone:
        uri = Uri.parse("content://settings/system/ringtone");
        
        if (uri != null) {
        	ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
        	ringtoneName.setText(ringtone.getTitle(this));
        } else {
        	ringtone = null;
        	ringtoneName.setText(R.string.no_ringtone);
        }
    }

	private void selectRingtone() {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER); 
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select alarm ringtone");
	    startActivityForResult(intent, 1);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
            uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
                ringtoneName.setText(ringtone.getTitle(this));
                // TODO Save uri.toString() to database
            } else {
            	ringtoneName.setText(R.string.no_ringtone);
                // TODO No ringtone selected (silence). Delete URI from database.
            }
}
	}
	
	public void onClick(View view) {
		
		switch (view.getId()) {
			case R.id.selectRingtone2:
				this.selectRingtone();
				break;
			case R.id.playRingtone:
				Intent intent = new Intent(this, Ringing.class);
		    	this.startActivityForResult(intent, 0);
				break;
			}
			
		}

}
