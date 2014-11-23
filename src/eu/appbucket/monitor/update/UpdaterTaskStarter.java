package eu.appbucket.monitor.update;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.appbucket.monitor.Settings;

public class UpdaterTaskStarter extends BroadcastReceiver {

	private Context context;
	private AlarmManager alarmMgr;
	private PendingIntent updaterTask;
	
	public UpdaterTaskStarter() {
	}
	
	public UpdaterTaskStarter(Context context) {
		this.context = context;
		alarmMgr = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
		Intent updaterIntent = new Intent(context, UpdaterTask.class);
		updaterTask = PendingIntent.getBroadcast(context, 0, updaterIntent, PendingIntent.FLAG_UPDATE_CURRENT);	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		new UpdaterTaskStarter(context).start();
	}

	public void start() {
		alarmMgr.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME , 
				0, 
				Settings.UPDATER_TASK.FREQUENCY, 
				updaterTask);
	}
}
