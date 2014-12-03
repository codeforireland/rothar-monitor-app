package eu.appbucket.rothar.monitor.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.common.Settings.START_TASK;
import eu.appbucket.rothar.common.Settings.STOP_TASK;
import eu.appbucket.rothar.monitor.monitor.MonitorTask;
import eu.appbucket.rothar.monitor.update.UpdaterTask;

public class TaskManager extends BroadcastReceiver {

	private Context context;
	private AlarmManager alarmMgr;
	private PendingIntent updaterTask;
	//private PendingIntent monitorTask;
	private List<HourMinute> startTimes = new ArrayList<HourMinute>();
	private List<HourMinute> stopTimes = new ArrayList<HourMinute>();
	
	// Used only during intercepting device boot up
	public TaskManager() {
	}
	
	public TaskManager(Context context) {
		this.context = context;
		alarmMgr = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
		// updater
		Intent updaterIntent = new Intent(context, UpdaterTask.class);
		updaterTask = PendingIntent.getBroadcast(context, 0, updaterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// monitor
		startTimes.add(new HourMinute(8, 30));
		stopTimes.add(new HourMinute(23, 50));
		/*stopTimes.add(new HourMinute(9, 30));
		startTimes.add(new HourMinute(17, 00));
		stopTimes.add(new HourMinute(18, 00));*/
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
		if (alarmMgr == null) {
			return;
		}
	    alarmMgr.cancel(getMonitorTask());
	    for(HourMinute time: startTimes) {
	    	alarmMgr.cancel(getStartTaskForHourAndMinute(time.getHour(), time.getMinute()));
	    }
	    for(HourMinute time: stopTimes) {
	    	alarmMgr.cancel(getStopTaskForHourAndMinute(time.getHour(), time.getMinute()));
	    }
	}
	
	public PendingIntent getMonitorTask() {
		Intent monitorIntent = new Intent(context, MonitorTask.class);
		PendingIntent monitorTask = PendingIntent.getBroadcast(context, 0, monitorIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		return monitorTask;
	} 
	
	private void setupMonitorTask() {		
	    for(HourMinute time: startTimes) {
	    	startMonitorTaskAt(time.getHour(), time.getMinute());
	    }
	    for(HourMinute time: stopTimes) {
	    	stopMonitorTaskAt(time.getHour(), time.getMinute());
	    }		
	}
	
	private void startMonitorTaskAt(int hour, int minute) {
		alarmMgr.setInexactRepeating(
				AlarmManager.RTC_WAKEUP, getTriggerAtMillis(hour, minute), START_TASK.FREQUENCY, 
				getStartTaskForHourAndMinute(hour, minute));
	}
	
	private PendingIntent getStartTaskForHourAndMinute(int hour, int minute) {
		return getPedingIntentForBroadcastAtHourAndMinute(StartTask.class, hour, minute);
	}
	
	private PendingIntent getPedingIntentForBroadcastAtHourAndMinute(Class clazz, int hour, int minute) {
		Intent intent = new Intent(context, clazz);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, getCodeCode(hour, minute) , intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}
	
	private int getCodeCode(int hour, int minute) {
		return hour * 100 + minute;
	}
	
	private long getTriggerAtMillis(int hour, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		return calendar.getTimeInMillis();
	} 
	
	private void stopMonitorTaskAt(int hour, int minute) {		
		alarmMgr.setInexactRepeating(
				AlarmManager.RTC_WAKEUP, getTriggerAtMillis(hour, minute), STOP_TASK.FREQUENCY, 
				getStopTaskForHourAndMinute(hour, minute));
	}
	
	private PendingIntent getStopTaskForHourAndMinute(int hour, int minute) {
		return getPedingIntentForBroadcastAtHourAndMinute(StopTask.class, hour, minute);
	}
}
