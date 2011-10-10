package com.vutbr.fit.tam.calendar;

import java.util.Date;

import android.graphics.Color;

/**
 * Class, for storing properties of event 
 * 
 * @author Zsolt Horváth
 * 
 */
public class Event {
	
	private String title;
	private Date begin;
	private Date end;
	private boolean allDay;
	private boolean busy;
	private int color;
	
	public Event () {
		this.title = "";
		this.begin = new Date();
		this.end = new Date();
		this.allDay = false;
		this.busy = false;
		this.color = Color.BLACK;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setBeginDate(Date begin) {
		this.begin = begin;
	}
	
	public void setEndDate(Date end) {
		this.end = end;
	}
	
	public void setAllDayEvent(boolean allDay) {
		this.allDay = allDay;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public Date getBeginDate() {
		return this.begin;
	}
	
	public Date getEndDate() {
		return this.end;
	}
	
	public boolean isAllDayEvent() {
		return this.allDay;
	}
	
	public boolean isBusy() {
		return this.busy;
	}
	
	public int getColor() {
		return this.color;
	}
}
