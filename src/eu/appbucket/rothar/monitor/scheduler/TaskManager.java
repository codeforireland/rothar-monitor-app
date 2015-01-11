package eu.appbucket.rothar.monitor.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.common.Settings.START_TASK;
import eu.appbucket.rothar.common.Settings.STOP_TASK;
import eu.appbucket.rothar.monitor.monitor.MonitorTask;
import eu.appbucket.rothar.monitor.update.UpdaterTask;

public class TaskManager extends BroadcastReceiver {

	private Context context;
	private AlarmManager alarmMgr;
	private List<HourMinute> startTimes = new ArrayList<HourMinute>();
	private List<HourMinute> stopTimes = new ArrayList<HourMinute>();
	
	// Used only during intercepting device boot up
	public TaskManager() {
	}
	
	// always used
	public TaskManager(Context context) {
		this.context = context;
		alarmMgr = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
		// monitor
		startTimes.add(new HourMinute(8, 30));
		stopTimes.add(new HourMinute(9, 30));
		startTimes.add(new HourMinute(17, 00));
		stopTimes.add(new HourMinute(18, 00));
	}
	
	// Used only for intercepting device boot up
	@Override
	public void onReceive(Context context, Intent intent) {
		boolean isBluetoothLeCapable = 
				new ConfigurationManager(context).isBluetoothLeCapable();
		boolean receivedBootCompletedEvent = 
				intent.getAction().equals("android.intent.action.BOOT_COMPLETED");
		if (receivedBootCompletedEvent && isBluetoothLeCapable) {
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
		alarmMgr.cancel(buildOperationForClass(UpdaterTask.class));
	}

	public PendingIntent buildOperationForClass(Class clazz) {
		return this.buildOperationWithIdForClass(0, clazz);
	}
	
	private PendingIntent buildOperationWithIdForClass(int id, Class clazz) {
		Intent intent = new Intent(context, clazz);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id , intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}

	private void setupUpdaterTask() {
		alarmMgr.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME , 
				0, 
				Settings.UPDATER_TASK.FREQUENCY, 
				buildOperationForClass(UpdaterTask.class));		
	}
	
	private void cancelMonitorTask() {
		int id = 0;
		for(HourMinute time: startTimes) {
			id = buildIdFromTime(time);
	    	alarmMgr.cancel(buildOperationWithIdForClass(id, StartTask.class));
	    }
	    for(HourMinute time: stopTimes) {
	    	id = buildIdFromTime(time);
	    	alarmMgr.cancel(buildOperationWithIdForClass(id, StopTask.class));
	    }
		alarmMgr.cancel(buildOperationForClass(MonitorTask.class));
	}
	
	private int buildIdFromTime(HourMinute time) {
		return time.getHour() * 100 + time.getMinute();
	}
		
	private void setupMonitorTask() {		
	    for(HourMinute time: startTimes) {
	    	scheduleMonitorTaskToStartAt(time);
	    }
	    for(HourMinute time: stopTimes) {
	    	schedulerMonitorTaskToStopAt(time);
	    }		
	}
	
	private void scheduleMonitorTaskToStartAt(HourMinute time) {
		alarmMgr.setInexactRepeating(
				AlarmManager.RTC_WAKEUP, getTimestampAtTime(time), START_TASK.FREQUENCY, 
				buildOperationWithIdForClass(buildIdFromTime(time), StartTask.class));
	}
	
	private long getTimestampAtTime(HourMinute time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, time.getHour());
		calendar.set(Calendar.MINUTE, time.getMinute());
		return calendar.getTimeInMillis();
	} 
	
	private void schedulerMonitorTaskToStopAt(HourMinute time) {		
		alarmMgr.setInexactRepeating(
				AlarmManager.RTC_WAKEUP, getTimestampAtTime(time), STOP_TASK.FREQUENCY, 
				buildOperationWithIdForClass(buildIdFromTime(time), StopTask.class));
	}
}
