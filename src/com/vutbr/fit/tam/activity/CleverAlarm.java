package com.vutbr.fit.tam.activity;

import java.util.Date;

import android.app.Activity;
import android.app.Notification;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.calendar.ChangeObserver;
import com.vutbr.fit.tam.calendar.Event;
import com.vutbr.fit.tam.database.EventsDatabase;
import com.vutbr.fit.tam.nofitication.NotificationHelper;
import com.vutbr.fit.tam.widget.WidgetUpdateServise;



public class CleverAlarm extends Activity implements OnClickListener {
	
	/**
	 * GUI elements in this activity
	 */
	TextView textView;
	Button queryButton;
	Button alarmButton;
	Button ringingButton;
	

	
	NotificationHelper notificationHelper;
	
	/**
	 * Initialize activity
	 */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);

        // initialize GUI elements
        this.textView = (TextView) this.findViewById(R.id.textView);
        
        if (this.textView == null)
        	Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();
        
        this.queryButton = (Button) this.findViewById(R.id.queryButton);
        this.alarmButton = (Button) this.findViewById(R.id.alarmButton);
        this.ringingButton = (Button) this.findViewById(R.id.ringingButton);

        
        // 	append onClickListener, which is this class
        this.queryButton.setOnClickListener(this);
        this.alarmButton.setOnClickListener(this);
        this.ringingButton.setOnClickListener(this);

        
        // TODO not sure this is working always as requested, eg. app is in background
        // maybe a service will be better
        this.registerCalendarChangeObserver();
        
        // NOTIFICATION EXAMPLE
        this.notificationHelper = new NotificationHelper(this);
        this.notificationHelper.setFlags(Notification.FLAG_NO_CLEAR);
        this.notificationHelper.show(
        		R.string.app_name, 
        		R.string.notification_example, 
        		NotificationHelper.TYPE_APPLICATION);

    }

    
    /**
     * Query calendars for events
     */
    private void query () {

 
    	EventsDatabase database = new EventsDatabase(this);
    	
    	Date from = new Date();
    	from.setTime(from.getTime() - DateUtils.DAY_IN_MILLIS);
    	Date to = new Date();
    	to.setTime(to.getTime() + DateUtils.DAY_IN_MILLIS);
    	
		for (Event event : database.getEvents(from, to, EventsDatabase.STATUS_AVAILABLE)) {

			int color = event.getColor();
			String title = event.getTitle();
			Date begin = event.getBeginDate();
			Date end = event.getEndDate();
			String allDay = event.isAllDayEvent() ? "Yes" : "No";
			String busy = event.isBusy() ? "Yes" : "No";
			
			String colorString = Color.red(color) + ", " + Color.green(color) + "," + Color.blue(color);
			String text = (String) textView.getText();
			text += "Title: " + title + "\nBegin: " + begin + "\nEnd: " + end +
					"\nAll Day: " + allDay + "\nBusy: " + busy + "\nColor: " + colorString + "\n\n";
			textView.setText(text);
		
		}
		
    	
    }
    

    
   
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.queryButton:
				this.query();
				break;
			case R.id.alarmButton:
				this.showAlarm();
				break;
			case R.id.ringingButton:
				this.showRinging();
				break;
		
		}
	}

    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = this.getMenuInflater();
    	inflater.inflate(R.menu.main_menu, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case R.id.button_main_menu_settings:
    			this.showSettings();
    			break;
    		case R.id.button_main_menu_quit:
    			this.quit();
    			break;
    		default:	
    			return super.onOptionsItemSelected(item);
    	}
    	
    	return true;
    }
    
    private void registerCalendarChangeObserver () {
        ChangeObserver observer = new ChangeObserver(new Handler(), this);
        ContentResolver cr = this.getApplicationContext().getContentResolver();
        
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
			cr.registerContentObserver(Uri.parse(ChangeObserver.CALENDAR_INSTANCES_URI_NEW), true, observer);
		} else{
			cr.registerContentObserver(Uri.parse(ChangeObserver.CALENDAR_INSTANCES_URI_OLD), true, observer);
		}
        
        
    }
    
    private void showSettings () {
    	Intent intent = new Intent(this, Settings.class);
    	this.startActivityForResult(intent, 0);
    }
    
    private void quit() {
    	this.notificationHelper.cancel(NotificationHelper.TYPE_APPLICATION);
    	this.finish();
    }
    
    private void showAlarm () {
    	Intent intent = new Intent(this, Alarm.class);
    	this.startActivityForResult(intent, 0);
    }
    
    private void showRinging () {
    	Intent intent = new Intent(this, Ringing.class);
    	this.startActivityForResult(intent, 0);
    }
    
}