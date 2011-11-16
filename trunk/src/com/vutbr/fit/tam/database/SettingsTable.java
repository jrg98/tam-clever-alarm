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
public class SettingsTable {

	/**
	 * Database table name
	 */
	protected static final String NAME = "settings";
	
	/**
	 * Column names in table
	 */
	protected static final String KEY = "key";
	protected static final String VALUE = "value";	
	
	/**
	 * Database definition
	 */
	protected static final String TABLE_CREATE = 
		"create table " + NAME + " (" +
		"	" + KEY + " text not null, " +
		"	" + VALUE + " text not null, " +
		");";


}
