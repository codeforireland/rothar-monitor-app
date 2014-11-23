package eu.appbucket.monitor.update;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdaterTaskStopper extends BroadcastReceiver {

	private Context context;
	private AlarmManager alarmMgr;
	private PendingIntent updaterTask;
	
	public UpdaterTaskStopper() {
	}
	
	public UpdaterTaskStopper(Context context) {
		this.context = context;
		alarmMgr = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
		Intent updaterIntent = new Intent(context, UpdaterTask.class);
		updaterTask = PendingIntent.getBroadcast(context, 0, updaterIntent, PendingIntent.FLAG_UPDATE_CURRENT);		
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		new UpdaterTaskStopper(context).stop();
	}

	public void stop() {
		if (alarmMgr!= null) {
		    alarmMgr.cancel(updaterTask);
		}
	}

}
