package com.vutbr.fit.tam.activity;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.alarm.Alarm;
import com.vutbr.fit.tam.database.AlarmAdapter;
import com.vutbr.fit.tam.gui.Days;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;



public class Sleepmode extends Activity implements OnClickListener, Days {
	
	private TextView actualDay;
	private RadioButton sleepTimeOn;
	private RadioButton sleepTimeOff;
	private TimePicker sleepTime;
	private Button saveSleepMode;
	
	private int day;
	private Alarm alarm;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_sleepmode);
        
        this.sleepTime = (TimePicker) this.findViewById(R.id.timeSleep);
        this.sleepTime.setIs24HourView(true);
        
        this.saveSleepMode = (Button) this.findViewById(R.id.saveSleepMode);
        this.saveSleepMode.setOnClickListener(this);
        
        this.sleepTimeOn = (RadioButton) this.findViewById(R.id.sleepModeOn);
        this.sleepTimeOff = (RadioButton) this.findViewById(R.id.sleepModeOff);
        
        Bundle bundle = this.getIntent().getExtras();
        this.day = bundle.getInt("day");
        
        this.actualDay = (TextView) this.findViewById(R.id.tv_day);
        actualDay.setText(days[day]);
        
    }
    
    
    @Override
	protected void onResume() {
		
		super.onResume();
		
		this.alarm = this.getDayAlarm(day);
		this.showAlarm();
		
	}
	
	private void setSleepModeIndicatorOn(boolean value) {
		this.sleepTimeOn.setChecked(value);
		this.sleepTimeOff.setChecked(!value);		
	}
    
    
    private void setSleepModeTimeIndicator(long value) {
		
		value /= 60000;
		int minutes = (int) value % 60;
		int hours = (int) ((value - minutes) / 60) + 1;
		
		sleepTime.setCurrentHour(hours);
		sleepTime.setCurrentMinute(minutes);
		
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
		
		this.setSleepModeIndicatorOn(alarm.isEnabled());
		this.setSleepModeTimeIndicator(alarm.getWakeUpTimeout());
		
	}
	
	public void saveAlarm() {
		
		this.alarm.setEnabled(this.sleepTimeOn.isChecked());
		
		int hour = this.sleepTime.getCurrentHour();
		int minutes = this.sleepTime.getCurrentMinute();
		
		this.alarm.setSleepTime(((hour - 1) * 60 + minutes)* 60000);
		
		AlarmAdapter adapter;
		
		try {
			
			adapter = new AlarmAdapter(this).open();
			
			if (!adapter.updateAlarm(this.alarm)) {
				adapter.insertAlarm(this.alarm);
			}
				
			
			adapter.close();
			
			if (this.alarm.isEnabled()) {
				Toast.makeText(this, this.getResources().getString(R.string.sleepmode_set), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, this.getResources().getString(R.string.sleepmode_not_set), Toast.LENGTH_SHORT).show();
			}
			
			
			
		} catch (Exception ex) {
			Log.e("DayAlarm", "AlarmAdapter error: "+ ex.toString());
		}
		
		
	}


	public void onClick(View view) {
		
		switch (view.getId()) {
			case R.id.saveSleepMode:
				this.saveAlarm();
				break;
			}
			
	}
    
}

