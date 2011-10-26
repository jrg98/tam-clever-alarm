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
public class AlarmHelper extends SQLiteOpenHelper {

	/**
	 * Database table name
	 */
	protected static final String DATABASE_TABLE = "alarm_settings";
	
	/**
	 * Column name _id is the same as index of day in
	 * the week, first day is Monday
	 */
	public static final String KEY_ALARM_ID = "_id";
	/**
	 * Column name enabled represents if alarm is enabled for
	 * the day specified by _id
	 */
	public static final String KEY_ENABLED = "enabled";
	/**
	 * Column wakeup_offset represent minutes as sooner the
	 * alarms wakes you up before the first event current day
	 */
	public static final String KEY_WAKEUP_OFFSET = "wakeup_offset";
	/**
	 * Column wakeup_timeout represents time in minutes when alarm wakes
	 * you up if there are no events in that day
	 */
	public static final String KEY_WAKEUP_TIMEOUT = "wakeup_timeout";
	
	/**
	 * Column sleep_timeout represent time in minute when you don't want
	 * to be disturbed by ringing
	 */
	public static final String KEY_SLEEP_TIME = "sleep_time";
	
	/**
	 * Database definition
	 */
	private final String DATABASE_CREATE = 
		"create table " + DATABASE_TABLE + " (" +
		"	" + KEY_ALARM_ID + " int not null primary key, " +
		"	" + KEY_ENABLED + " bool not null, " +
		"	" + KEY_WAKEUP_OFFSET + " int not null," +
		"	" + KEY_WAKEUP_TIMEOUT + " int not null," +
		"	" + KEY_SLEEP_TIME + " int not null" +
		");";

	
	/**
	 * Constructor
	 * @param context
	 */
	public AlarmHelper(Context context) {
		super(context, Database.NAME, null, Database.VERSION);
	}
	
	/**
	 * Method is called during creation of the database
	 * @param database
	 */
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	/**
	 * Method is called during an upgrade of the database, e.g. if you increase
	 * the database version
	 * @param database
	 * @param oldVersion
	 * @param newVersion
	 */
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(
			CalendarHelper.class.getName(),
			"Upgrading database from version " + oldVersion + " to " +
			newVersion + ", which will destroy all old data"
		);
		database.execSQL("DROP TABLE IF EXISTS " + AlarmHelper.DATABASE_TABLE);
		this.onCreate(database);
	}

}