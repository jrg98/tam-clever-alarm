package com.vutbr.fit.tam.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.alarm.Alarm;
import com.vutbr.fit.tam.database.AlarmAdapter;
import com.vutbr.fit.tam.gui.Days;

public class DayInfoTab extends TabActivity {
	
	
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.day_info_tab);
	        
	        TabHost tabHost = this.getTabHost();
	        
	        Bundle bundle = this.getIntent().getExtras();
        
	        
	        // Tab for day info
	        TabSpec dayGeneral = tabHost.newTabSpec("Day");
	        dayGeneral.setIndicator("Day", getResources().getDrawable(R.drawable.tab_day));
	        Intent dayIntent = new Intent(this, Day.class);
	        dayIntent.putExtras(bundle);
	
	        dayGeneral.setContent(dayIntent);
	        
	        // Tab for day alarm
	        TabSpec alarm = tabHost.newTabSpec("Alarm");
	        // setting Title and Icon for the Tab
	        alarm.setIndicator("Alarm", getResources().getDrawable(R.drawable.tab_alarm));
	        Intent alarmIntent = new Intent(this, DayAlarm.class);
	        alarmIntent.putExtras(bundle);
	        alarm.setContent(alarmIntent);
	        
	        // Tab start sleep mode
	        TabSpec sleepmode = tabHost.newTabSpec("Sleep mode");
	        sleepmode.setIndicator("Sleep mode", getResources().getDrawable(R.drawable.tab_sleepmode));
	        Intent sleepmodeIntent = new Intent(this, Sleepmode.class);
	        sleepmodeIntent.putExtras(bundle);
	        sleepmode.setContent(sleepmodeIntent);
	        
	        // Adding all TabSpec to TabHost
	        tabHost.addTab(dayGeneral);
	        tabHost.addTab(alarm);
	        tabHost.addTab(sleepmode);
	        	        
	        
	    }
	  

		@Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	    	MenuInflater inflater = this.getMenuInflater();
	    	inflater.inflate(R.menu.settings_menu, menu);
	    	return true;
	    }
		
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	switch(item.getItemId()) {
	    		case R.id.button_menu_settings:
	    			this.showSettings();
	    			break;
	    		default:	
	    			return super.onOptionsItemSelected(item);
	    	}
	    	
	    	return true;
	    }
	    
	    private void showSettings () {
	    	Intent intent = new Intent(this, Settings.class);
	    	this.startActivityForResult(intent, 0);
	    }
	    
}
