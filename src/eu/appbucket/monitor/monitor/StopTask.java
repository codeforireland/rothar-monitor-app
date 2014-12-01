package eu.appbucket.monitor.monitor;

import eu.appbucket.monitor.scheduler.TaskManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopTask extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		stopMonitorTask(context);
	}
	
	private void stopMonitorTask(Context context) {
		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent monitorTask = new TaskManager(context).getMonitorTask();
	    alarmMgr.cancel(monitorTask);
	}
}
