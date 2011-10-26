package com.vutbr.fit.tam.database;

import com.vutbr.fit.tam.alarm.Alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author Zsolt Horváth
 *
 */
public class AlarmAdapter {

	private Context context;
	private SQLiteDatabase database;
	private DatabaseHelper helper;
	
	private final int INSERT = 0;
	private final int UPDATE = 1;

	/**
	 * Constructor
	 * @param context
	 */
	public AlarmAdapter(Context context) {
		this.context = context;
	}

	/**
	 * Open connection
	 * @return
	 * @throws SQLException
	 */
	public AlarmAdapter open() throws SQLException {
		this.helper = new DatabaseHelper(this.context);
		this.database = this.helper.getWritableDatabase();
		return this;
	}

	/**
	 * Close connection
	 */
	public void close() {
		this.helper.close();
	}

	/**
	 * Insert alarm for a specified day
	 */
	public long insertAlarm(Alarm alarm) {
		ContentValues initialValues = this.createContentValues(alarm, this.INSERT);
		return this.database.insert(AlarmHelper.DATABASE_TABLE,	null, initialValues);
	}
	
	/**
	 * Update alarm for a specified day
	 */
	public boolean updateAlarm(Alarm alarm) {
		ContentValues updateValues = this.createContentValues(alarm, this.UPDATE);

		return this.database.update(
					AlarmHelper.DATABASE_TABLE, 
					updateValues,
					AlarmHelper.KEY_ALARM_ID + "=" + alarm.getId(),
					null
				) > 0;
	}	

	public Cursor fetchAllAlarms() {
		return this.database.query(
					AlarmHelper.DATABASE_TABLE, 
					new String[] {
						AlarmHelper.KEY_ALARM_ID, 
						AlarmHelper.KEY_ENABLED, 
						AlarmHelper.KEY_WAKEUP_OFFSET,
						AlarmHelper.KEY_WAKEUP_TIMEOUT,
						AlarmHelper.KEY_SLEEP_TIME
					}, 
					null, 
					null, 
					null,
					null,
					null
				);
	}

	/**
	 * Return a Cursor positioned at the defined alarm
	 */
	public Cursor fetchAlarm(int id) throws SQLException {
		Cursor mCursor = 
			this.database.query(
				true, 
				AlarmHelper.DATABASE_TABLE, 
				new String[] {
					AlarmHelper.KEY_ENABLED, 
					AlarmHelper.KEY_WAKEUP_OFFSET,
					AlarmHelper.KEY_WAKEUP_TIMEOUT,
					AlarmHelper.KEY_SLEEP_TIME
				},
				AlarmHelper.KEY_ALARM_ID + "=" + id,
				null, 
				null, 
				null, 
				null, 
				null
			);
		
		return mCursor;
	}
	
	private ContentValues createContentValues(Alarm alarm, int type) {
		ContentValues values = new ContentValues();
		
		if (type == this.INSERT) {
			values.put(AlarmHelper.KEY_ALARM_ID, alarm.getId());
		}
		
		values.put(AlarmHelper.KEY_ENABLED, alarm.isEnabled());
		values.put(AlarmHelper.KEY_WAKEUP_OFFSET, alarm.getWakeUpOffset());
		values.put(AlarmHelper.KEY_WAKEUP_TIMEOUT, alarm.getWakeUpTimeout());
		values.put(AlarmHelper.KEY_SLEEP_TIME, alarm.getSleepTime());
		return values;
	}	
}