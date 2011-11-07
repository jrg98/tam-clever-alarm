package com.vutbr.fit.tam.alarm;

import com.vutbr.fit.tam.activity.Ringing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmLauncher extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, Ringing.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		i.addFlags(Intent.FLAG_FROM_BACKGROUND);
    	context.startActivity(i);
	}

}
