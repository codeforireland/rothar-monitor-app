package eu.appbucket.monitor;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	
	private static final long MINUTESx10 = 1000 * 60 * 10;
	private static final long SECONDSx10 = 1000 * 10;
	private static final long SECONDSx1 = 1000;
	private static final long SECONDSx2 = 2 * SECONDSx1;
	private static final long ALARM_INTERVAL = SECONDSx10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		scheduleStoleBikeUpdaterAlarm();
	}

	private void scheduleStoleBikeUpdaterAlarm() {		
		Intent intent = new Intent(this, StolenBikeUpdater.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmMgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP , 0, ALARM_INTERVAL, alarmIntent);
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
