package com.vutbr.fit.tam.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.gui.Days;

public class Day extends Activity implements Days {

		private TextView actualDay;
	
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.tab_day);
	        
	        Bundle bundle = this.getIntent().getExtras();
	        int day = bundle.getInt("day");
				        
	        this.actualDay = (TextView) this.findViewById(R.id.tv_day);
	        actualDay.setText(days[day]);
	        

	    }
	
}
