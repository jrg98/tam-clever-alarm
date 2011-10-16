package com.vutbr.fit.tam.calendar;

import com.vutbr.fit.tam.nofitication.NotificationHelper;
import com.vutbr.fit.tam.R;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

/**
 * Observer class for calendar change events
 * 
 * @author Zsolt Horváth
 *
 */
public class ChangeObserver extends ContentObserver {

	//public static final String CALENDAR_INSTANCES_URI = "content://com.android.calendar/instances/";
	
	public static final String CALENDAR_INSTANCES_URI_NEW = "content://com.android.calendar/instances/";
	public static final String CALENDAR_INSTANCES_URI_OLD = "content://calendar/instances/";
		
	private String CALENDAR_INSTANCES_URI = null;	
			
	Context context;
	
	public ChangeObserver(Handler handler, Context context) {
		super(handler);
		this.context = context;
		this.setCalendarURI();
		
	}

	private void setCalendarURI() {
		
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		
		if (currentapiVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
			this.CALENDAR_INSTANCES_URI = this.CALENDAR_INSTANCES_URI_NEW;
		} else{
			this.CALENDAR_INSTANCES_URI = this.CALENDAR_INSTANCES_URI_OLD;
		}
	}
	
	public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }
 
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        
        // Show notification if calendar has been changed
        this.triggerNotification(); 
        
    }
    
    private void triggerNotification() {
        new NotificationHelper(this.context).show(
        	R.string.synchronizing, 
        	R.string.calendar_updated, 
        	NotificationHelper.TYPE_CALENDARS
        );
    }    
}
