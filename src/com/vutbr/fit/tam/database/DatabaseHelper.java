package com.vutbr.fit.tam.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.vutbr.fit.tam.database.*;

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
public class DatabaseHelper extends SQLiteOpenHelper {


	/**
	 * Constructor
	 * @param context
	 */
	public DatabaseHelper(Context context) {
		super(context, Database.NAME, null, Database.VERSION);
	}
	
	/**
	 * Method is called during creation of the database
	 * @param database
	 */
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CalendarTable.NAME);
		database.execSQL(AlarmTable.NAME);
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
			Database.NAME,
			"Upgrading database from version " + oldVersion + " to " +
			newVersion + ", which will destroy all old data"
		);
		database.execSQL("DROP TABLE IF EXISTS " + CalendarTable.NAME);
		database.execSQL("DROP TABLE IF EXISTS " + AlarmTable.NAME);
		this.onCreate(database);
	}

}
