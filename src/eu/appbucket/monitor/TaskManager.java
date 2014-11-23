package eu.appbucket.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.appbucket.monitor.monitor.MonitorTaskStarter;
import eu.appbucket.monitor.monitor.MonitorTaskStopper;
import eu.appbucket.monitor.update.UpdaterTaskStarter;
import eu.appbucket.monitor.update.UpdaterTaskStopper;

public class TaskManager extends BroadcastReceiver {

	private MonitorTaskStarter monitorStarter;
	private MonitorTaskStopper monitorStopper;
	private UpdaterTaskStarter updaterStarter;
	private UpdaterTaskStopper updaterStopper;
	
	public TaskManager() {
	}
	
	public TaskManager(Context context) {
		monitorStarter = new MonitorTaskStarter(context);
		monitorStopper = new MonitorTaskStopper(context);
		updaterStarter = new UpdaterTaskStarter(context);
		updaterStopper = new UpdaterTaskStopper(context);
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
		updaterStopper.stop();
	}
	
	private void cancelMonitorTask() {
		monitorStopper.stop();
	}

	private void setupUpdaterTask() {
		updaterStarter.start();		
	}
	
	private void setupMonitorTask() {
		monitorStarter.startRepeating();
	}
}
