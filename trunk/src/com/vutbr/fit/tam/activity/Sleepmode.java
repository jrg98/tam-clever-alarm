package com.vutbr.fit.tam.activity;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.gui.Days;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.TimePicker;



public class Sleepmode extends Activity implements Days {
	
	private TextView actualDay;
	
	private TimePicker sleepTime;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_sleepmode);
        
        this.sleepTime = (TimePicker) this.findViewById(R.id.timeSleep);
        this.sleepTime.setIs24HourView(true);
        
        Bundle bundle = this.getIntent().getExtras();
        int day = bundle.getInt("day");
        
        this.actualDay = (TextView) this.findViewById(R.id.tv_day);
        actualDay.setText(days[day]);
    }
}

