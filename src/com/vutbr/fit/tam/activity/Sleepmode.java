package com.vutbr.fit.tam.activity;

import java.util.Date;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.alarm.Alarm;
import com.vutbr.fit.tam.database.AlarmAdapter;
import com.vutbr.fit.tam.database.SettingsAdapter;
import com.vutbr.fit.tam.gui.Days;
import com.vutbr.fit.tam.widget.WidgetRefreshService;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;



public class Sleepmode extends Activity implements OnClickListener, Days {
	
	private TextView actualDay;
	private RadioButton sleepTimeOn;
	private RadioButton sleepTimeOff;
	private TimePicker sleepTime;
	private Button saveSleepMode;
	private LinearLayout dayBackground;
	private ScrollView scrollView;
	
	private boolean flag;
	private int day;
	private Alarm alarm;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_sleepmode);
        
        this.sleepTime = (TimePicker) this.findViewById(R.id.timeSleep);
        
        this.saveSleepMode = (Button) this.findViewById(R.id.saveSleepMode);
        this.saveSleepMode.setOnClickListener(this);
        
        this.sleepTimeOn = (RadioButton) this.findViewById(R.id.sleepModeOn);
        this.sleepTimeOff = (RadioButton) this.findViewById(R.id.sleepModeOff);
        
        Bundle bundle = this.getIntent().getExtras();
        this.day = bundle.getInt("day");
        
        this.actualDay = (TextView) this.findViewById(R.id.tv_day);
        actualDay.setText(days[day]);
        
        Date date = new Date();
        
        if (date.getDay() == this.day ) {
        	this.dayBackground = (LinearLayout) this.findViewById(R.id.tab_day_background);
        	this.dayBackground.setBackgroundResource(R.drawable.tab_today_bg);
    	}

        scrollView = (ScrollView) findViewById(R.id.scrollView3);
    }
    
    
    @Override
	protected void onResume() {
		
		super.onResume();
		
		this.alarm = this.getDayAlarm(day);
		this.showAlarm();
		
		this.sleepTime.setIs24HourView(is24hoursFormat());
		
	  	if (flag) {
  	  	    
	  		scrollView.post(new Runnable() {
	  			public void run() {
	  				scrollView.fullScroll(ScrollView.FOCUS_UP);
	  	    	}
	  	    });
	        
	  	 }
	  	 flag = true;
		
	}
	
	private void setSleepModeIndicatorOn(boolean value) {
		this.sleepTimeOn.setChecked(value);
		this.sleepTimeOff.setChecked(!value);		
	}
    
    
    private void setSleepModeTimeIndicator(long value) {
		
		value /= 60000;
		int minutes = (int) value % 60;
		int hours = (int) (value - minutes) / 60;
		
		sleepTime.setCurrentHour(hours);
		sleepTime.setCurrentMinute(minutes);
		
	}
	
    private boolean is24hoursFormat() {
    	
    	SettingsAdapter settingsAdapter = new SettingsAdapter(this);
    	settingsAdapter.open();
    	
    	final int is24hour = Integer.parseInt(settingsAdapter.fetchSetting("is24hour", "0"));
    	settingsAdapter.close();
    	
    	return (is24hour == 0 ? true : false);
    }
	
    private Alarm getDayAlarm (int id) {
		
  		AlarmAdapter adapter;

  		Alarm alarm = new Alarm(id, false, 0, 0, 0, false);
	
  		try {
			adapter = new AlarmAdapter(this).open();
		
			Cursor cursorDAY = adapter.fetchAlarm(id);
		
			if (cursorDAY.moveToFirst()) {
			
				alarm.setEnabled(cursorDAY.getInt(0) > 0);
				alarm.setWakeUpOffset(cursorDAY.getLong(1));
				alarm.setWakeUpTimeout(cursorDAY.getLong(2));
				alarm.setSleepTime(cursorDAY.getLong(3));
				alarm.setSleepEnabled(cursorDAY.getInt(4) > 0);
								
			}
		
		adapter.close();
		
  		} catch (Exception ex) {
  			Log.e("DayAlarm", "AlarmAdapter error: "+ ex.toString());
  			return null;
  		}	  
  
  		return alarm;
  
  }
	
	
	private void showAlarm() {
		
		this.setSleepModeIndicatorOn(alarm.isSleepEnabled());
		this.setSleepModeTimeIndicator(alarm.getSleepTime());
		
	}
	
	public void saveAlarm() {
		
		this.alarm.setSleepEnabled(this.sleepTimeOn.isChecked());
		
		int hour = this.sleepTime.getCurrentHour();
		int minutes = this.sleepTime.getCurrentMinute();
		
		this.alarm.setSleepTime((hour * 60 + minutes)* 60000);
		
		AlarmAdapter adapter;
		
		try {
			
			adapter = new AlarmAdapter(this).open();
			
			if (!adapter.updateAlarm(this.alarm)) {
				adapter.insertAlarm(this.alarm);
			}
				
			
			adapter.close();
			
			if (this.alarm.isSleepEnabled()) {
				Toast.makeText(this, this.getResources().getString(R.string.sleepmode_set), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, this.getResources().getString(R.string.sleepmode_not_set), Toast.LENGTH_SHORT).show();
			}
			
			
			
		} catch (Exception ex) {
			Log.e("DayAlarm", "AlarmAdapter error: "+ ex.toString());
		}
		
		// Actualize widget
		startService(new Intent(this.getApplicationContext(), WidgetRefreshService.class));
	}


	public void onClick(View view) {
		
		switch (view.getId()) {
			case R.id.saveSleepMode:
				this.saveAlarm();
				break;
			}
			
	}
    
}

