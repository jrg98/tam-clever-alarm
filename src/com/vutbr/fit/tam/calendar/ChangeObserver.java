package com.vutbr.fit.tam.calendar;

import com.vutbr.fit.tam.nofitication.NotificationHelper;
import com.vutbr.fit.tam.R;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;

/**
 * Observer class for calendar change events
 * 
 * @author Zsolt Horváth
 *
 */
public class ChangeObserver extends ContentObserver {

	private static final String CALENDAR_INSTANCES_URI_ECLAIR = "content://calendar/instances/";
	private static final String CALENDAR_INSTANCES_URI_FROYO = "content://com.android.calendar/instances/";
		
	Context context;
	
	public ChangeObserver(Handler handler, Context context) {
		super(handler);
		this.context = context;
	}

	public static String getCalendarURI() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
			return ChangeObserver.CALENDAR_INSTANCES_URI_FROYO;
		} else{
			return ChangeObserver.CALENDAR_INSTANCES_URI_ECLAIR;
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
