package com.vutbr.fit.tam.database;


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
public class SettingsAdapter {

	private Context context;
	private SQLiteDatabase database;
	private DatabaseHelper helper;

	/**
	 * Constructor
	 * @param context
	 */
	public SettingsAdapter(Context context) {
		this.context = context;
	}

	/**
	 * Open connection
	 * @return
	 * @throws SQLException
	 */
	public SettingsAdapter open() throws SQLException {
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
	public long insertSetting(String key, String value) {
		ContentValues initialValues = this.createContentValues(key, value);
		return this.database.insert(SettingsTable.NAME, null, initialValues);
	}

	/**
	 * Update the settings
	 */
	public boolean updateSetting(String key, String value) {
		ContentValues updateValues = this.createContentValues(key, value);
		return this.database.update(
				SettingsTable.NAME, 
				updateValues, 
				SettingsTable.KEY+ "=" + key,
				null
			) > 0;
	}

	/**
	 * Delete setting
	 */
	public boolean deleteSetting(String key) {
		return this.database.delete(SettingsTable.NAME, SettingsTable.KEY + "=" + key, null) > 0;
	}
		

	/**
	 * Return a Cursor positioned at the defined calendar
	 */
	public String fetchSetting(String key) throws SQLException {
		Cursor mCursor = this.database.query(true, SettingsTable.NAME, new String[] {
				SettingsTable.VALUE},
				SettingsTable.KEY + "=" + key, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
			return mCursor.getString(0);
		}
		return null;
	}

	private ContentValues createContentValues(String key, String value) {
		ContentValues values = new ContentValues();
		values.put(SettingsTable.KEY, key);
		values.put(SettingsTable.VALUE, value);
		return values;
	}
}
