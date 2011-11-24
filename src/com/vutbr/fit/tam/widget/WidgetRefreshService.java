package com.vutbr.fit.tam.widget;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WidgetRefreshService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate() {
		// POZOR !!!!!!!!!!!!! je treba po poslani zrusit servis !!!!!!!!!!!!!!!!!!!!!!!
		Intent intent=new Intent(getApplicationContext(),CleverAlarmWidgetProvider.class);
		this.sendBroadcast(intent);
		stopSelf();
	}

}
