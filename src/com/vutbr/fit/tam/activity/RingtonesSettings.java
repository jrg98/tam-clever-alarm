/**
 * 
 */
package com.vutbr.fit.tam.activity;

import com.vutbr.fit.tam.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import com.vutbr.fit.tam.database.*;


/**
 * @author Zolex
 *
 */
public class RingtonesSettings extends Activity implements OnClickListener {
	
	final private String DEFAULT_RINGTONE = "content://settings/system/ringtone";
	
	private Button playRingtoneButton;
	private Button selectRingtoneButton;
	private Ringtone ringtone;
	private TextView ringtoneName;
	private Uri uri;
	private SeekBar seekBar;
	private EditText snoozeTimeBox;
	private AudioManager audioManager;
	private int ringVolume;
	private int snoozeTime; // in minutes
	private SettingsAdapter settingsAdapter;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ringtones_settings);

        this.settingsAdapter = new SettingsAdapter(this);
        
        this.playRingtoneButton = (Button) this.findViewById(R.id.playRingtone);
        this.playRingtoneButton.setOnClickListener(this);
        
        this.selectRingtoneButton = (Button) this.findViewById(R.id.selectRingtone2);
        this.selectRingtoneButton.setOnClickListener(this);
        
        this.ringtoneName = (TextView) this.findViewById(R.id.settingsRingtoneName);
        
        this.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
        this.seekBar = (SeekBar) this.findViewById(R.id.setVolumeBar);
        this.seekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        
        this.snoozeTimeBox = (EditText) this.findViewById(R.id.snoozeTime);
        
        this.load();
        
        this.snoozeTimeBox.setText(Integer.toString(snoozeTime));
        
        this.seekBar.setProgress(ringVolume);
        
        if (uri != null) {
        	ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
        	ringtoneName.setText(ringtone.getTitle(this));
        } else {
        	ringtone = null;
        	ringtoneName.setText(R.string.no_ringtone);
        }
    }
	
	/**
	 * Create menu
	 */
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = this.getMenuInflater();
    	inflater.inflate(R.menu.calendars_menu, menu);
    	return true;
    }

    /**
     * Handle menu items
     */
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case R.id.button_settings_menu_save:
    			this.save();
    	        this.finish();
    			break;
    		default:	
    			return super.onOptionsItemSelected(item);
    	}
    	
    	return true;
    }
    
    private void load() {
    	this.settingsAdapter.open();
    	this.ringVolume = Integer.parseInt(settingsAdapter.fetchSetting("volume", "0"));
    	this.snoozeTime = Integer.parseInt(settingsAdapter.fetchSetting("snoozetime", "5"));
        uri = Uri.parse(settingsAdapter.fetchSetting("uri", DEFAULT_RINGTONE));
        this.settingsAdapter.close();
    }
    
    private void save() {
    	this.settingsAdapter.open();
        if (uri != null) {
            if (!settingsAdapter.updateSetting("uri", uri.toString()));
        	    settingsAdapter.insertSetting("uri", uri.toString());	
        } else {
        	settingsAdapter.deleteSetting("key");
        }
        if (!this.settingsAdapter.updateSetting("volume", Integer.toString(seekBar.getProgress())))
        	this.settingsAdapter.insertSetting("volume", Integer.toString(seekBar.getProgress()));
        if (!this.settingsAdapter.updateSetting("snoozetime", snoozeTimeBox.getText().toString()));
        	this.settingsAdapter.insertSetting("snoozetime", snoozeTimeBox.getText().toString());
        this.settingsAdapter.close();
    }

	private void selectRingtone() {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER); 
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select alarm ringtone");
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
	    startActivityForResult(intent, 1);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
            uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
                ringtoneName.setText(ringtone.getTitle(this));
            } else {
            	ringtoneName.setText(R.string.no_ringtone);
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
	
	public void onPause() {
		this.save();
		super.onPause();
	}
}
