package com.vutbr.fit.tam.nofitication;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.activity.CleverAlarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Notification helper class
 * 
 * @author Zsolt Horváth
 *
 */
public class NotificationHelper {

	/**
	 * Notification types
	 */
	public static final int TYPE_CALENDARS = 24000;
	public static final int TYPE_ALARM = 24001;
	public static final int TYPE_RINGER = 24002;
	public static final int TYPE_APPLICATION = 24001;
	
	private Context context;
	
	private NotificationManager notificationManager;
	
	private int icon;
	
	private int flags;
	
	private Class<?> cls;
	
	/**
	 * Construct
	 * 
	 * @param context
	 */
	public NotificationHelper (Context context) {
		this.context = context;
		this.icon = R.drawable.icon;
		this.flags = 0;
		this.cls = CleverAlarm.class;
		this.notificationManager = 
			(NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	/**
	 * Set notification icon
	 * 
	 * @param resource
	 */
	public void setIcon (int resource) {
		this.icon = resource;
	}
	
	/**
	 * Set notification flags
	 * 
	 * @param flags
	 */
	public void setFlags(int flags) {
		this.flags = flags;
	}
	
	/**
	 * Set intent class
	 * 
	 * @param cls
	 */
	public void setClass(Class<?> cls) {
		this.cls = cls;
	}
	
	/**
	 * Show notification
	 * 
	 * @param titleResource - title, string resource id
	 * @param messageResource - message, string resource id
	 * @param type - notification type
	 */
	public void show(int titleResource, int messageResource, int type) {
        String title = this.context.getString(titleResource);
        String message = this.context.getString(messageResource);
        
        Notification notification = new Notification(this.icon, title, System.currentTimeMillis());

        notification.flags = this.flags;
 
        Intent notificationIntent = new Intent(this.context, this.cls);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, notificationIntent, 0);
 
        notification.setLatestEventInfo(this.context, title, message, pendingIntent);
        this.notificationManager.notify(type, notification);
	}
	
	/**
	 * Cancel notification
	 * 
	 * @param type
	 */
	public void cancel(int type) {
		this.notificationManager.cancel(type);
	}
	
	/**
	 * Cancel all notifications
	 */
	public void canceAll() {
		this.notificationManager.cancelAll();
	}
	
}
