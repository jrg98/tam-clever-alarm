package com.vutbr.fit.tam.activity;

import java.util.HashSet;
import java.util.Set;

import com.vutbr.fit.tam.R;
import com.vutbr.fit.tam.calendar.Calendar;
import com.vutbr.fit.tam.database.CalendarDatabase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
class Panel extends View {
    public Panel(Context context) {
        super(context);
        this.setMinimumHeight(32);
        this.setMinimumWidth(32);
    }

    @Override
    public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.FILL);
		RectF rectF = new RectF(0,0,100,100);
		canvas.drawRoundRect(rectF, 2.0f, 2.0f, paint);
		
    }
    
    
}    
/**
 * 
 * @author Zsolt Horváth
 *
 */
public class CalendarsSettings extends Activity {

	private CalendarDatabase database;
	
	/**
	 * Initialize activity
	 */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.calendars_settings);
        
        this.database = new CalendarDatabase(this);

        this.load();
        
    }
    
	/**
	 * Create menu
	 */
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = this.getMenuInflater();
    	inflater.inflate(R.menu.calendars_menu, menu);
    	return true;
    }

    /**
     * Handle menu items
     */
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case R.id.button_settings_menu_save:
    			this.save();
    			this.finish();
    			break;
    		default:	
    			return super.onOptionsItemSelected(item);
    	}
    	
    	return true;
    }
    
    /**
     * Load available calendars
     */
    private void load () {
    	
    	Set<Calendar> calendars = this.database.loadCalendars();
    	
    	TableLayout tableLayout = (TableLayout) this.findViewById(R.id.calendarTableLayout);
    	
		for (Calendar calendar : calendars) {
	
			TableRow tableRow = new TableRow(this);
			
			TextView textView = new TextView(this);
			textView.setText(calendar.getTitle());
			
			CheckBox checkBox = new CheckBox(this);
			checkBox.setTag(calendar);
			checkBox.setChecked(calendar.isEnabled());
			
			ImageView icon = new ImageView(this);
			icon.setMinimumHeight(32);
			icon.setMinimumWidth(32);
			icon.setBackgroundColor(calendar.getColor());
			
			TableLayout innerTableLayout = new TableLayout(this);
			innerTableLayout.setPadding(10,20,0,0);
			innerTableLayout.setColumnStretchable(0, true);
			
			TableRow innerTableRow = new TableRow(this);
			
			innerTableRow.addView(textView);
			innerTableRow.addView(icon);
			
			innerTableLayout.addView(innerTableRow);                                                                                         
			
			tableRow.addView(innerTableLayout);
			tableRow.addView(checkBox);
			
			tableLayout.addView(tableRow);
		}
    }
   
    /**
     * Save calendars' data
     */
    private void save () {
    	TableLayout layout = (TableLayout) this.findViewById(R.id.calendarTableLayout);

    	Set<Calendar> calendars = new HashSet<Calendar>();
    	
    	for (int i=0; i < layout.getChildCount(); i++) {
    		
    		TableRow row = (TableRow)layout.getChildAt(i);
    		for (int j=0; j < row.getChildCount(); j++) {
    			CheckBox checkbox = (CheckBox) row.getChildAt(1);
    			boolean enabled = checkbox.isChecked();
    			Calendar calendar = (Calendar) checkbox.getTag();
    			if (calendar != null) {
    				calendar.setEnabled(enabled);
    				calendars.add(calendar);
    			}
    		}
    	}
    	
    	this.database.saveCalendars(calendars);
    	
    	Intent intent = new Intent();
        this.setResult(RESULT_OK, intent);
    }
    
	public void onPause() {
		this.save();
		super.onPause();
	}
}
