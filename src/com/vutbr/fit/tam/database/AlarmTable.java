package com.vutbr.fit.tam.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Alarm data handler class
 * This class handler stores settings in database for each day,
 * those must be monitored by alarm. If database version had
 * change automatically recreate new database table
 * 
 * 
 * @author Zsolt Horváth
 *
 */
public class AlarmTable {

	/**
	 * Database table name
	 */
	protected static final String NAME = "alarm_settings";
	
	/**
	 * Column name _id is the same as index of day in
	 * the week, first day is Monday
	 */
	protected static final String KEY_ALARM_ID = "_id";
	/**
	 * Column name enabled represents if alarm is enabled for
	 * the day specified by _id
	 */
	protected static final String KEY_ENABLED = "enabled";
	/**
	 * Column wakeup_offset represent minutes as sooner the
	 * alarms wakes you up before the first event current day
	 */
	protected static final String KEY_WAKEUP_OFFSET = "wakeup_offset";
	/**
	 * Column wakeup_timeout represents time in minutes when alarm wakes
	 * you up if there are no events in that day
	 */
	protected static final String KEY_WAKEUP_TIMEOUT = "wakeup_timeout";
	
	/**
	 * Column sleep_timeout represent time in minute when you don't want
	 * to be disturbed by ringing
	 */
	protected static final String KEY_SLEEP_TIME = "sleep_time";
	
	/**
	 * Database definition
	 */
	protected static final String TABLE_CREATE = 
		"create table " + NAME + " (" +
		"	" + KEY_ALARM_ID + " int not null primary key, " +
		"	" + KEY_ENABLED + " bool not null, " +
		"	" + KEY_WAKEUP_OFFSET + " unsigned big int not null," +
		"	" + KEY_WAKEUP_TIMEOUT + " unsigned big int not null," +
		"	" + KEY_SLEEP_TIME + " unsigned big int not null" +
		");";

	

}