package eu.appbucket.rothar.monitor.scheduler;

import eu.appbucket.rothar.monitor.monitor.MonitorTask;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StopTask extends BroadcastReceiver {

	private static final String LOG_TAG = "StopTask";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "Stopping monitor task.");
		stopMonitorTask(context);
	}
	
	private void stopMonitorTask(Context context) {
		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent monitorTask = new TaskManager(context).buildOperationForClass(MonitorTask.class);
	    alarmMgr.cancel(monitorTask);
	}
}
