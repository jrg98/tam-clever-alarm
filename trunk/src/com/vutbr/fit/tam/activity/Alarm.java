package com.vutbr.fit.tam.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.vutbr.fit.tam.R;

public class Alarm extends Activity {

	private TimePicker alarmTime;
	private RadioButton alarmOn;
	private RadioButton alarmOff;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.alarm);

        
        this.alarmTime = (TimePicker) this.findViewById(R.id.timeAlarm);
        this.alarmTime.setIs24HourView(true);
        
        this.alarmOn = (RadioButton) this.findViewById(R.id.alarmOn);
        this.alarmOff = (RadioButton) this.findViewById(R.id.alarmOff);

        // TODO: get info about alarm
        this.setAlarmOn(true);
        

        
    }
	
	private void setAlarmOn(boolean value) {
		this.alarmOn.setChecked(value);
		this.alarmOff.setChecked(!value);
		// set alarm
		
	}
	
}
