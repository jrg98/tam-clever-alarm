package com.vutbr.fit.tam.database;

import com.vutbr.fit.tam.alarm.Alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

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
		return this.database.insert(AlarmTable.NAME, null, initialValues);
	}
	
	/**
	 * Update alarm for a specified day
	 */
	public boolean updateAlarm(Alarm alarm) {
		ContentValues updateValues = this.createContentValues(alarm, this.UPDATE);

		return this.database.update(
					AlarmTable.NAME, 
					updateValues,
					AlarmTable.KEY_ALARM_ID + "=" + alarm.getId(),
					null
				) > 0;
	}	

	public Cursor fetchAllAlarms() {
		return this.database.query(
					AlarmTable.NAME, 
					new String[] {
						AlarmTable.KEY_ALARM_ID, 
						AlarmTable.KEY_ENABLED, 
						AlarmTable.KEY_WAKEUP_OFFSET,
						AlarmTable.KEY_WAKEUP_TIMEOUT,
						AlarmTable.KEY_SLEEP_TIME,
						AlarmTable.KEY_SLEEP_ENABLED
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
				AlarmTable.NAME, 
				new String[] {
					AlarmTable.KEY_ENABLED, 
					AlarmTable.KEY_WAKEUP_OFFSET,
					AlarmTable.KEY_WAKEUP_TIMEOUT,
					AlarmTable.KEY_SLEEP_TIME,
					AlarmTable.KEY_SLEEP_ENABLED
				},
				AlarmTable.KEY_ALARM_ID + "=" + id,
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
			values.put(AlarmTable.KEY_ALARM_ID, alarm.getId());
		}
		
		values.put(AlarmTable.KEY_ENABLED, alarm.isEnabled());
		values.put(AlarmTable.KEY_WAKEUP_OFFSET, alarm.getWakeUpOffset());
		values.put(AlarmTable.KEY_WAKEUP_TIMEOUT, alarm.getWakeUpTimeout());
		values.put(AlarmTable.KEY_SLEEP_TIME, alarm.getSleepTime());
		values.put(AlarmTable.KEY_SLEEP_ENABLED, alarm.isSleepEnabled());		
		return values;
	}	
}