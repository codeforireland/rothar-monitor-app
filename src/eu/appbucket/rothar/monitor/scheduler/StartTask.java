package eu.appbucket.rothar.monitor.scheduler;

import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.monitor.monitor.MonitorTask;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartTask extends BroadcastReceiver {

	private static final String LOG_TAG = "StartTask";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "Starting monitor task.");
		startMonitorTask(context);			
	}
	
	private void startMonitorTask(Context context) {
		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);		
		PendingIntent monitorTask = new TaskManager(context).buildOperationForClass(MonitorTask.class);		
		alarmMgr.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME_WAKEUP,
				0, 
				Settings.MONITOR_TASK.FREQUENCY,
				monitorTask);
	}
}
