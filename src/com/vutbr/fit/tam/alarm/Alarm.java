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
	
	private int wakeUpOffset;
	
	private int wakeUpTimeout;
	
	private int  sleepTime;
	
	public Alarm(int id, boolean enabled, int wakeUpOffset, int wakeUpTimeout, int sleepTime) {
		this.id = id;
		this.enabled = enabled;
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
	
	public int getWakeUpOffset()  {
		return this.wakeUpOffset;
	}
	
	public int getWakeUpTimeout() {
		return this.wakeUpTimeout;
	}
	
	public int getSleepTime() {
		return this.sleepTime;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setWakeUpOffset(int offset) {
		this.wakeUpOffset = offset;
	}
	
	public void setWakeUpTimeout(int timeout) {
		this.wakeUpTimeout = timeout;
	}
	
	public void setSleepTime(int time) {
		this.sleepTime = time;
	}
}
