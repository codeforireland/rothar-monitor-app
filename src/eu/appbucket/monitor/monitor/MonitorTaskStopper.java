package eu.appbucket.monitor.monitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MonitorTaskStopper extends BroadcastReceiver {

	private Context context;
	private AlarmManager alarmMgr;
	private PendingIntent monitorTask;
	
	public MonitorTaskStopper() {
	}
	
	public MonitorTaskStopper(Context context) {
		this.context = context;
		alarmMgr = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
		Intent monitorIntent = new Intent(context, MonitorTask.class);
		monitorTask = PendingIntent.getBroadcast(context, 0, monitorIntent, PendingIntent.FLAG_UPDATE_CURRENT);		
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		new MonitorTaskStopper(context).stop();
	}

	public void stop() {
		if (alarmMgr!= null) {
		    alarmMgr.cancel(monitorTask);
		}
	}

}
