package com.vutbr.fit.tam.database;


import com.vutbr.fit.tam.calendar.Calendar;

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
public class CalendarAdapter {

	private Context context;
	private SQLiteDatabase database;
	private DatabaseHelper helper;

	/**
	 * Constructor
	 * @param context
	 */
	public CalendarAdapter(Context context) {
		this.context = context;
	}

	/**
	 * Open connection
	 * @return
	 * @throws SQLException
	 */
	public CalendarAdapter open() throws SQLException {
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
	 * Create a new todo If the todo is successfully created return the new
	 * rowId for that note, otherwise return a -1 to indicate failure.
	 */

	public long insertCalendar(Calendar c) {
		ContentValues initialValues = this.createContentValues(Integer.parseInt(c.getId()), c.getTitle(),c.isEnabled());
		//return this.database.insertWithOnConflict(CalendarTable.DATABASE_TABLE, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);
		return this.database.insert(CalendarTable.NAME, null, initialValues);
	}

	/**
	 * Update the calendars
	 */
	public boolean updateCalendar(int id, String title, boolean enabled) {
		ContentValues updateValues = this.createContentValues(id, title, enabled);
		return this.database.update(
				CalendarTable.NAME, 
				updateValues, 
				CalendarTable.KEY_CALENDAR_ID+ "=" + id,
				null
			) > 0;
	}

	/**
	 * Delete calendar
	 */
	public boolean deleteCalendar(long rowId) {
		return this.database.delete(CalendarTable.NAME, CalendarTable.KEY_CALENDAR_ID + "=" + rowId, null) > 0;
	}
	
	/**
	 * Delete all calendars
	 */
	public boolean deleteCalendars() {
		return this.database.delete(CalendarTable.NAME, null, null) > 0;
	}	

	/**
	 * Return a Cursor over the list of all calendars in the database
	 * 
	 * @return Cursor over all notes
	 */

	public Cursor fetchAllCalendars() {
		return this.database.query(CalendarTable.NAME, new String[] {
				CalendarTable.KEY_CALENDAR_ID, CalendarTable.KEY_TITLE, CalendarTable.KEY_ENABLED}, null, null, null,
				null, null);
	}

	/**
	 * Return a Cursor positioned at the defined calendar
	 */
	public Cursor fetchCalendars(int id) throws SQLException {
		Cursor mCursor = this.database.query(true, CalendarTable.NAME, new String[] {
				CalendarTable.KEY_CALENDAR_ID, CalendarTable.KEY_TITLE, CalendarTable.KEY_ENABLED },
				CalendarTable.KEY_CALENDAR_ID + "=" + id, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	private ContentValues createContentValues(int id, String title, boolean enabled) {
		ContentValues values = new ContentValues();
		values.put(CalendarTable.KEY_CALENDAR_ID, id);
		values.put(CalendarTable.KEY_TITLE, title);
		values.put(CalendarTable.KEY_ENABLED, enabled);
		return values;
	}
}
