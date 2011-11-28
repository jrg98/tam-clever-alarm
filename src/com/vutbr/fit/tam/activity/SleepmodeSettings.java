package com.vutbr.fit.tam.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.database.SettingsAdapter;




public class SleepmodeSettings extends Activity {
	
	private RadioButton radioSilent;
	private RadioButton radioVibrate;
	private String sleepMode;
	
	private SettingsAdapter settingsAdapter;
	/**
	 * Initialize activity
	 */
    public void onCreate(Bundle savedInstanceState) {
    	
    	this.settingsAdapter = new SettingsAdapter(this);
    	
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.sleepmode_settings);
        
        this.radioSilent = (RadioButton) this.findViewById(R.id.radioSilent);
        this.radioVibrate = (RadioButton) this.findViewById(R.id.radioVibrate);
        
        this.load();    
    }
	   
	private void load() {
		this.settingsAdapter.open();
    	sleepMode = settingsAdapter.fetchSetting("sleepRingMode", "0");
        this.settingsAdapter.close();
        if (sleepMode.compareTo("0") == 0) {
        	radioSilent.setChecked(true);
        	radioVibrate.setChecked(false);
        } else {
        	radioSilent.setChecked(false);
        	radioVibrate.setChecked(true);
        } 
	}
	
	private void save() {
		
		String mode;
		
		if (radioSilent.isChecked()) {
			mode = "0";
		} else {
			mode = "1"; 
		}

		this.settingsAdapter.open();
	    
		if (!this.settingsAdapter.updateSetting("sleepRingMode", mode))
	        	this.settingsAdapter.insertSetting("sleepRingMode",  mode);
	        
	    this.settingsAdapter.close();
	    

		
	}
	
	/**
	 * Create menu
	 */
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = this.getMenuInflater();
    	inflater.inflate(R.menu.timeformat_menu, menu);
    	return true;
    }

    /**
     * Handle menu items
     */
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case R.id.button_timeformat_menu_save:
    			this.save();
    			this.finish();
    			break;
    		default:	
    			return super.onOptionsItemSelected(item);
    	}
    	
    	return true;
    }
	
}