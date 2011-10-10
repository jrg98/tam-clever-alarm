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
public class CalendarHelper extends SQLiteOpenHelper {

	/**
	 * Database name
	 */
	private static final String DATABASE_NAME = "alarm_database";
	
	/**
	 * Database version number
	 */
	private static final int DATABASE_VERSION = 1;
	
	/**
	 * Database table name
	 */
	protected static final String DATABASE_TABLE = "calendar_settings";
	
	/**
	 * Column names in table
	 */
	public static final String KEY_CALENDAR_ID = "calendar_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_ENABLED = "enable";	
	
	/**
	 * Database definition
	 */
	private final String DATABASE_CREATE = 
		"create table " + DATABASE_TABLE + " (" +
		"	" + KEY_CALENDAR_ID + " int not null primary key, " +
		"	" + KEY_TITLE + " text not null, " +
		"	" + KEY_ENABLED + " bool not null" +
		");";

	
	/**
	 * Constructor
	 * @param context
	 */
	public CalendarHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
		database.execSQL("DROP TABLE IF EXISTS " + CalendarHelper.DATABASE_TABLE);
		this.onCreate(database);
	}

}
