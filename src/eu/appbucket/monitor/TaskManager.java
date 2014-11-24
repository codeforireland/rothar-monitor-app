package eu.appbucket.monitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.appbucket.monitor.monitor.MonitorTask;
import eu.appbucket.monitor.update.UpdaterTask;

public class TaskManager extends BroadcastReceiver {

	private Context context;
	private AlarmManager alarmMgr;
	private PendingIntent monitorTask;
	private PendingIntent updaterTask;
	
	public TaskManager() {
	}
	
	public TaskManager(Context context) {
		alarmMgr = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
		Intent monitorIntent = new Intent(context, MonitorTask.class);
		monitorTask = PendingIntent.getBroadcast(context, 0, monitorIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Intent updaterIntent = new Intent(context, UpdaterTask.class);
		updaterTask = PendingIntent.getBroadcast(context, 0, updaterIntent, PendingIntent.FLAG_UPDATE_CURRENT);		
	}
	
	// Used only for intercepting device boot up
	@Override
	public void onReceive(Context context, Intent intent) {	
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			new TaskManager(context).scheduleTasks();
        }
	}
	
	public void scheduleTasks() {
		cancelUpdaterTask();
		setupUpdaterTask();
		cancelMonitorTask();
		setupMonitorTask();
	}
	
	private void cancelUpdaterTask() {
		if (alarmMgr!= null) {
		    alarmMgr.cancel(updaterTask);
		}
	}

	private void setupUpdaterTask() {
		alarmMgr.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME , 
				0, 
				Settings.UPDATER_TASK.FREQUENCY, 
				updaterTask);		
	}
	
	private void cancelMonitorTask() {
		if (alarmMgr!= null) {
		    alarmMgr.cancel(monitorTask);
		}
	}
	
	private void setupMonitorTask() {		
		alarmMgr.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME_WAKEUP,
				0, 
				Settings.MONITOR_TASK.FREQUENCY,
				monitorTask);
	}
}
