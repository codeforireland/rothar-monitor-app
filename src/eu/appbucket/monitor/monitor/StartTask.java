package eu.appbucket.monitor.monitor;

import eu.appbucket.monitor.Settings;
import eu.appbucket.monitor.scheduler.TaskManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartTask extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		startMonitorTask(context);			
	}
	
	private void startMonitorTask(Context context) {
		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);		
		PendingIntent monitorTask = new TaskManager(context).getMonitorTask();		
		alarmMgr.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME_WAKEUP,
				0, 
				Settings.MONITOR_TASK.FREQUENCY,
				monitorTask);
	}
}
