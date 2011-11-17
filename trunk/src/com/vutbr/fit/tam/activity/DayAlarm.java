package com.vutbr.fit.tam.activity;


import java.util.Date;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.alarm.Alarm;
import com.vutbr.fit.tam.database.AlarmAdapter;
import com.vutbr.fit.tam.gui.Days;

public class DayAlarm extends Activity implements OnClickListener, Days {

	private TimePicker alarmTime;
	private RadioButton alarmOn;
	private RadioButton alarmOff;
	private Button saveAlarm;
	
	private TextView actualDay;
	private LinearLayout dayBackground;
	private int day;
	
	private Alarm alarm;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.tab_alarm);

        this.alarmTime = (TimePicker) this.findViewById(R.id.timeAlarm);
        this.alarmTime.setIs24HourView(true);
        
        this.alarmOn = (RadioButton) this.findViewById(R.id.alarmOn);
        this.alarmOff = (RadioButton) this.findViewById(R.id.alarmOff);
        
        this.saveAlarm = (Button) this.findViewById(R.id.saveAlarm);
        this.saveAlarm.setOnClickListener(this);

        // Read choosed day and set action label
        Bundle bundle = this.getIntent().getExtras();
        this.day = bundle.getInt("day");        
        this.actualDay = (TextView) this.findViewById(R.id.tv_day);
        actualDay.setText(days[day]);
             
        Date date = new Date();
        if (date.getDay() == this.day ) {
        	this.dayBackground = (LinearLayout) this.findViewById(R.id.tab_day_background);
        	this.dayBackground.setBackgroundResource(R.drawable.tab_today_bg);
    	}
    
        //android:id="@+id/tab_day_background" 
        	    //android:background="@drawable/tab_day_bg"
        
    }
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		this.alarm = this.getDayAlarm(day);
		this.showAlarm();
		
	}
	
	private void setAlarmIndicatorOn(boolean value) {
		this.alarmOn.setChecked(value);
		this.alarmOff.setChecked(!value);		
	}
	
	private void setAlarmTimeIndicator(long value) {
		
		value /= 60000;
		int minutes = (int) value % 60;
		int hours = (int) ((value - minutes) / 60) + 1;
		
		alarmTime.setCurrentHour(hours);
		alarmTime.setCurrentMinute(minutes);
		
	}
	
	private Alarm getDayAlarm (int id) {
			
  		AlarmAdapter adapter;

  		Alarm alarm = new Alarm(id, false, 0, 0, 0);
	
  		try {
			adapter = new AlarmAdapter(this).open();
		
			Cursor cursorDAY = adapter.fetchAlarm(id);
		
			if (cursorDAY.moveToFirst()) {
			
				alarm.setEnabled(cursorDAY.getInt(0) > 0);
				alarm.setWakeUpOffset(cursorDAY.getLong(1));
				alarm.setWakeUpTimeout(cursorDAY.getLong(2));
				alarm.setSleepTime(cursorDAY.getLong(3));
								
			}
		
			adapter.close();
		
  		} catch (Exception ex) {
  			Log.e("DayAlarm", "AlarmAdapter error: "+ ex.toString());
  			return null;
  		}	  
  
  		return alarm;
  
  }
	
	
	private void showAlarm() {
		
		setAlarmIndicatorOn(alarm.isEnabled());
		setAlarmTimeIndicator(alarm.getWakeUpTimeout() - alarm.getWakeUpOffset());
		
	}
	
	public void saveAlarm() {
		
		this.alarm.setEnabled(this.alarmOn.isChecked());
		
		int hour = this.alarmTime.getCurrentHour();
		int minutes = this.alarmTime.getCurrentMinute();
		
		this.alarm.setWakeUpTimeout(((hour - 1) * 60 + minutes)* 60000);
		
		AlarmAdapter adapter;
		
		try {
			
			adapter = new AlarmAdapter(this).open();
			
			if (!adapter.updateAlarm(this.alarm)) {
				adapter.insertAlarm(this.alarm);
			}
			
			adapter.close();
			
			if (this.alarm.isEnabled()) {
				Toast.makeText(this, this.getResources().getString(R.string.alarm_set), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, this.getResources().getString(R.string.alarm_not_set), Toast.LENGTH_SHORT).show();
			}
			
			
		} catch (Exception ex) {
			Log.e("DayAlarm", "AlarmAdapter error: "+ ex.toString());
		}
		
		
	}
	   
	public void onClick(View view) {
			
		switch (view.getId()) {
			case R.id.saveAlarm:
				this.saveAlarm();
				break;
			}
			
		}
		
	
}
