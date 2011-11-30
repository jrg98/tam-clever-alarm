package com.vutbr.fit.tam.alarm;

/**
 * 
 * @author Zsolt Horváth
 *
 */
public class Alarm {

	// HARD CODED constant id for saving actual alarm time
	public static int ACTUAL_ALARM_ID = 16254;
	
	private int id;
	
	private boolean enabled;
	
	private boolean sleepEnabled;
	
	private long wakeUpOffset;
	
	private long wakeUpTimeout;
	
	private long  sleepTime;
	
	public Alarm(int id, boolean enabled, long wakeUpOffset, long wakeUpTimeout, long sleepTime, boolean sleepEnabled) {
		this.id = id;
		this.enabled = enabled;
		this.sleepEnabled = sleepEnabled;
		this.wakeUpOffset = wakeUpOffset;
		this.wakeUpTimeout = wakeUpTimeout;
		this.sleepTime = sleepTime;
	}
	
	public int getId() {
		return this.id;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public boolean isSleepEnabled() {
		return this.sleepEnabled;
	}
	
	public long getWakeUpOffset()  {
		return this.wakeUpOffset;
	}
	
	public long getWakeUpTimeout() {
		return this.wakeUpTimeout;
	}
	
	public long getSleepTime() {
		return this.sleepTime;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setSleepEnabled(boolean enabled) {
		this.sleepEnabled = enabled;
	}
	
	public void setWakeUpOffset(long offset) {
		this.wakeUpOffset = offset;
	}
	
	public void setWakeUpTimeout(long timeout) {
		this.wakeUpTimeout = timeout;
	}
	
	public void setSleepTime(long time) {
		this.sleepTime = time;
	}
}
