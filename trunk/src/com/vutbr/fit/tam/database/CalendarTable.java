package com.vutbr.fit.tam.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Calendar data handler class
 * This class handler stores  settings in database of calendars,
 * which calendar must be monitored. If database version had
 * change automatically recreate new database table
 * 
 * 
 * @author Zsolt Horváth
 *
 */
public class CalendarTable {

	/**
	 * Database table name
	 */
	protected static final String NAME = "calendar_settings";
	
	/**
	 * Column names in table
	 */
	protected static final String KEY_CALENDAR_ID = "calendar_id";
	protected static final String KEY_TITLE = "title";
	protected static final String KEY_ENABLED = "enable";	
	
	/**
	 * Database definition
	 */
	protected static final String TABLE_CREATE = 
		"create table " + NAME + " (" +
		"	" + KEY_CALENDAR_ID + " int not null primary key, " +
		"	" + KEY_TITLE + " text not null, " +
		"	" + KEY_ENABLED + " bool not null" +
		");";


}
