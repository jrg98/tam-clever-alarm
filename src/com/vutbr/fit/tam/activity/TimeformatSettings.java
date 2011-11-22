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




public class TimeformatSettings extends Activity {
	
	private final String[] timeFormat = {DateFormat.HOUR_OF_DAY + ":" + DateFormat.MINUTE + DateFormat.MINUTE,
										DateFormat.HOUR + ":" + DateFormat.MINUTE + DateFormat.MINUTE + " " + DateFormat.AM_PM};
	
	
	private RadioButton radioButtonAMPM;
	private RadioButton radioButton24;
	
	private SettingsAdapter settingsAdapter;
	/**
	 * Initialize activity
	 */
    public void onCreate(Bundle savedInstanceState) {
    	
    	this.settingsAdapter = new SettingsAdapter(this);
    	
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.timeformat_settings);
        
        this.radioButtonAMPM = (RadioButton) this.findViewById(R.id.radioPMAM);
        this.radioButton24 = (RadioButton) this.findViewById(R.id.radio24);
        
        Date date = new Date();
        
        this.radioButtonAMPM.setText(DateFormat.format(timeFormat[0], date.getTime()));
        this.radioButton24.setText(DateFormat.format(timeFormat[1], date.getTime()));
        		
        // LOAD
        this.load();
        
        
        
    }
	   
	private void load() {
		this.settingsAdapter.open();
    	String format = settingsAdapter.fetchSetting("timeformat", "0");
    	
    	if (format != null) {
    		this.radioButton24.setChecked(format.startsWith(timeFormat[1]));
    	}
        this.settingsAdapter.close();
	}
	
	private void save() {
		
		int index = 0;
		
		if (this.radioButton24.isChecked()) {
			index = 1;
		}

		this.settingsAdapter.open();
	    
		if (!this.settingsAdapter.updateSetting("timeformat", timeFormat[index]))
	        	this.settingsAdapter.insertSetting("timeformat",  timeFormat[index]);
		if (!this.settingsAdapter.updateSetting("is24hour", String.valueOf(index)))
        	this.settingsAdapter.insertSetting("is24hour", String.valueOf(index));
	        
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