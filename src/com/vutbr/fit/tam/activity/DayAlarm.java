package com.vutbr.fit.tam.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.gui.Days;

public class DayAlarm extends Activity implements Days {

	private TimePicker alarmTime;
	private RadioButton alarmOn;
	private RadioButton alarmOff;
	
	private TextView actualDay;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.tab_alarm);

        /*
        this.alarmTime = (TimePicker) this.findViewById(R.id.timeAlarm);
        this.alarmTime.setIs24HourView(true);
        
        this.alarmOn = (RadioButton) this.findViewById(R.id.alarmOn);
        this.alarmOff = (RadioButton) this.findViewById(R.id.alarmOff);

        Bundle bundle = this.getIntent().getExtras();
        int day = bundle.getInt("day");
        
        
        this.actualDay = (TextView) this.findViewById(R.id.tv_day);
        actualDay.setText(days[day]);
        // TODO: get info about alarm
   
        this.setAlarmOn(true);
        */

        
    }
	
	private void setAlarmOn(boolean value) {
		this.alarmOn.setChecked(value);
		this.alarmOff.setChecked(!value);
		// set alarm
		
	}
	
}
