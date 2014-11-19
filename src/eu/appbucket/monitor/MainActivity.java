package eu.appbucket.monitor;

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
	
	private static final long MINUTESx10 = 1000 * 60 * 10;
	private static final long SECONDx1 = 1000;
	private static final long SECONDSx20 = SECONDx1 * 20;
	private static final long SECONDSx60 = 60 * SECONDx1 ;
	private static final long SECONDSx2 = 2 * SECONDx1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		scheduleStoleBikeUpdaterAlarm();
	}

	private void scheduleStoleBikeUpdaterAlarm() {
		startUpdater();
		/*startMonitor();*/
		/*startTest();*/
	}

	private void startTest() {
		Intent testerIntent = new Intent(this, TestUpdater.class);
		PendingIntent tester = PendingIntent.getBroadcast(MainActivity.this, 0, testerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmMgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP , 0, SECONDSx2, tester);
	}
	
	private void startUpdater() {
		Intent updaterIntent = new Intent(this, StolenBikeUpdater.class);
		PendingIntent updater = PendingIntent.getBroadcast(MainActivity.this, 0, updaterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmMgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP , 0, SECONDSx60, updater);		
	}
	
	private void startMonitor() {
		Intent monitorIntent = new Intent(this, StolenBikeMonitor.class);
		PendingIntent monitor = PendingIntent.getBroadcast(MainActivity.this, 0, monitorIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmMgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP , 0, SECONDSx20, monitor);
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
