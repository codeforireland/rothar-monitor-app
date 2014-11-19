package eu.appbucket.monitor;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import eu.appbucket.monitor.monitor.StolenBikeMonitor;
import eu.appbucket.monitor.update.StolenBikeUpdater;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startInLoop();
	}
	
	private void startInLoop() {
		startUpdaterInLoop();
		startMonitorInLoop();
	}
	
	private void startUpdaterInLoop() {
		Intent updaterIntent = new Intent(this, StolenBikeUpdater.class);
		PendingIntent updater = PendingIntent.getBroadcast(MainActivity.this, 0, updaterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmMgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME_WAKEUP , 0, Settings.UPDATER.UPDATE_FREQUENCY, updater);		
	}
	
	private void startMonitorInLoop() {
		Intent monitorIntent = new Intent(this, StolenBikeMonitor.class);
		PendingIntent monitor = PendingIntent.getBroadcast(MainActivity.this, 0, monitorIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmMgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME_WAKEUP , 0, Settings.MONITOR.SEARCH_FREQUENCY, monitor);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
