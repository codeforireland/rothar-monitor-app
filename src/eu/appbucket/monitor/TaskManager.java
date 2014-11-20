package eu.appbucket.monitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.appbucket.monitor.monitor.StolenBikeMonitor;
import eu.appbucket.monitor.update.StolenBikeUpdater;

public class TaskManager extends BroadcastReceiver {
	
	private Context context;
	
	public TaskManager() {
	}
	
	public TaskManager(Context context) {
		this.context = context;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		new TaskManager(context).startInLoop();
	}
	
	public void startInLoop() {		
		startUpdaterInLoop();
		startMonitorInLoop();
	}
	
	private void startUpdaterInLoop() {
		Intent updaterIntent = new Intent(context, StolenBikeUpdater.class);
		PendingIntent updater = PendingIntent.getBroadcast(context, 0, updaterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME_WAKEUP , 0, Settings.UPDATER.UPDATE_FREQUENCY, updater);		
	}
	
	private void startMonitorInLoop() {
		Intent monitorIntent = new Intent(context, StolenBikeMonitor.class);
		PendingIntent monitor = PendingIntent.getBroadcast(context, 0, monitorIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME_WAKEUP , 0, Settings.MONITOR.SEARCH_FREQUENCY, monitor);
	}


}
