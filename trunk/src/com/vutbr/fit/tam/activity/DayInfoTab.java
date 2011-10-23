package com.vutbr.fit.tam.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.gui.Days;

public class DayInfoTab extends TabActivity {
	
	
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.day_info_tab);
	        
	        TabHost tabHost = this.getTabHost();
	        
	        Bundle bundle = this.getIntent().getExtras();
	        
	        // Tab for Photos
	        TabSpec dayGeneral = tabHost.newTabSpec("Day");
	        dayGeneral.setIndicator("Day", getResources().getDrawable(R.drawable.tab_day));
	        Intent dayIntent = new Intent(this, Day.class);
	        dayIntent.putExtras(bundle);
	        dayGeneral.setContent(dayIntent);
	        
	        // Tab for Songs
	        TabSpec alarm = tabHost.newTabSpec("Alarm");
	        // setting Title and Icon for the Tab
	        alarm.setIndicator("Alarm", getResources().getDrawable(R.drawable.tab_alarm));
	        Intent alarmIntent = new Intent(this, Alarm.class);
	        alarmIntent.putExtras(bundle);
	        alarm.setContent(alarmIntent);
	        
	        // Tab for Videos
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
}
