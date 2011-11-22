package com.vutbr.fit.tam.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.vutbr.fit.tam.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * 
 * @author Zsolt Horváth
 *
 */
public class Settings extends Activity implements OnItemClickListener {

	private enum Identifiers {
		TITLE, DESCRIPTION, ICON
	};

	private ListView settingsListView;
	private ArrayList<HashMap<String, String>> settingslistItems;

	/**
	 * Initialize activity
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.settings);
		this.initList();
		
		this.addListItem(
			R.string.button_calendars, 
			R.string.button_calendars_description,
			R.drawable.button_calendar
		);
		
		this.addListItem(
				R.string.button_settings_ringtones, 
				R.string.button_settings_ringtones_description,
				R.drawable.button_settings_ringtone
			);
		
		this.addListItem(
				R.string.button_settings_timeformat, 
				R.string.button_settings_timeformat_description,
				R.drawable.button_settings_timeformat
			);
		
		
		
		this.createList();

	}
	
	/**
	 * Initialize list
	 */
	private void initList () {
		this.settingsListView = (ListView) findViewById(R.id.settingsListView);
		this.settingslistItems = new ArrayList<HashMap<String, String>>();
	}
	
	/**
	 * Add new item to list
	 * Must call initList at first
	 * Must call before createList 
	 * 
	 * @param title string resource ID
	 * @param description string resource ID
	 * @param drawable drawable resource ID
	 */
	private void addListItem (int title, int description, int icon) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Identifiers.TITLE.toString(), this.getString(title));
		map.put(Identifiers.DESCRIPTION.toString(), this.getString(description));
		map.put(Identifiers.ICON.toString(), String.valueOf(icon));
		
		this.settingslistItems.add(map);
	}
	
	/**
	 * Create list with items
	 * Must call initList at first
	 */
	private void createList () {
		SimpleAdapter adapter = new SimpleAdapter(
				this.getBaseContext(),
				this.settingslistItems, 
				R.layout.settings_row, 
				new String[] {
					Identifiers.ICON.toString(),
					Identifiers.TITLE.toString(),
					Identifiers.DESCRIPTION.toString()
				}, 
				new int[] {
					R.id.settingsRowIcon,
					R.id.settingsRowTitle,
					R.id.settingsRowDescription
				}
			);

			this.settingsListView.setAdapter(adapter);
			this.settingsListView.setOnItemClickListener(this);
	}

	/**
	 * Compare item title with string resource
	 * 
	 * @param map
	 * @param resource
	 * @return true on match
	 */
	private boolean checkItem(HashMap<String, String> map, int resource) {
		return map.get(Identifiers.TITLE.toString()).equals(this.getString(resource));
	}
	
	/**
	 * Handle item click
	 */
	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
		HashMap<String, String> map = (HashMap<String, String>) this.settingsListView.getItemAtPosition(position);
		
		// handle items
		if (this.checkItem(map, R.string.button_calendars)) {
	    	Intent intent = new Intent(this, CalendarsSettings.class);
	    	this.startActivityForResult(intent, 0);
		}
		
		if (this.checkItem(map, R.string.button_settings_ringtones)) {
	    	Intent intent = new Intent(this, RingtonesSettings.class);
	    	this.startActivityForResult(intent, 0);
		}
		
		if (this.checkItem(map, R.string.button_settings_timeformat)) {
	    	Intent intent = new Intent(this, TimeformatSettings.class);
	    	this.startActivityForResult(intent, 0);
		}
	}

}
