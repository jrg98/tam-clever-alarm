package com.vutbr.fit.tam.gui;

import java.util.HashMap;
import java.util.List;

import com.vutbr.fit.tam.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class DaySimpleAdapter extends SimpleAdapter {
	
	int position;
	

	public DaySimpleAdapter(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to) {
		super(context, items, resource, from, to);
		
		this.position = this.getTodayIndex(items);
	
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  View view = super.getView(position, convertView, parent);

	  if (position == this.position) {
		  view.setBackgroundResource(R.drawable.menu_today);
	  }
	  else {
		  view.setBackgroundResource(R.drawable.menu_day);
	  }
	
	  return view;
	}
	
	
	private int getTodayIndex(List<HashMap<String, String>> items) {
		
		int index = -1;
		
		for (HashMap<String, String> item : items) {
			
			index++;
			
			if (item.get("TODAY").startsWith("true")) {
				return index;
			}
				
		}
		
		return index;
	}
	
}
