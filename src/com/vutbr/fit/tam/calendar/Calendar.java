package com.vutbr.fit.tam.calendar;

import android.graphics.Color;

/**
 * Class for storing properties of calendar 
 * 
 * @author Zsolt Horváth
 *
 */
public class Calendar {

	private String id;
	
	private String title;
	
	private boolean enabled;
	
	private int color;
	
	public Calendar (String id, String title, boolean enabled) {
		this.id = id;
		this.title = title;
		this.enabled = enabled;
		this.color = Color.WHITE;
	}
	
	public Calendar (String id, String title) {
		this(id, title, false);
	}	
	
	public String getId () {
		return this.id;
	}
	
	public String getTitle () {
		return this.title;
	} 
	
	public boolean isEnabled () {
		return this.enabled;
	}
	
	public int getColor () {
		return this.color;
	}	
	
	public void setId (String id) {
		this.id = id;
	}
	
	public void setTitle (String title) {
		this.title = title;
	}
	
	public void setEnabled (boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setColor (int color) {
		this.color = color;
	}
	
	public int hasCode () {
		return Integer.parseInt(this.id);
	}
	
}
