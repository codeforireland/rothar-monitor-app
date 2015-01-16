package eu.appbucket.rothar.monitor.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import eu.appbucket.rothar.common.LocalFileLogger;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.monitor.monitor.MonitorTask;

public class StartTask extends BroadcastReceiver {

	private static final String LOG_TAG = "StartTask";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//Log.d(LOG_TAG, "Starting monitor task.");
		startMonitorTask(context);			
	}
	
	private void startMonitorTask(Context context) {
		LocalFileLogger.d(LOG_TAG, "Schedule monitor task.");
		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);	
		PendingIntent monitorTask = new TaskManager(context).buildOperationForClass(MonitorTask.class);		
		long triggerAtMillisDelay = SystemClock.elapsedRealtime() + 1000;
		alarmMgr.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME_WAKEUP,
				triggerAtMillisDelay, 
				Settings.MONITOR_TASK.FREQUENCY,
				monitorTask);
	}
}
