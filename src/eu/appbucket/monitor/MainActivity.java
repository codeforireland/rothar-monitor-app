package eu.appbucket.monitor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class MainActivity extends Activity {
	
	private int REQUEST_ENABLE_BT = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		checkIfBluetoothIsEnabled();
		setupShowNotificationCheckbox();
	}
	
	private void checkIfBluetoothIsEnabled() {
		BluetoothManager bluetoothManager = (BluetoothManager) 
				MainActivity.this.getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			new TaskManager(MainActivity.this).scheduleTasks();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
	    if (requestCode == REQUEST_ENABLE_BT) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	        	new TaskManager(MainActivity.this).scheduleTasks();
	        } else {
	        	this.finish();
	        }
	    }		
	}
	
	private void setupShowNotificationCheckbox() {
		CheckBox showNotifcationsCheckbox = (CheckBox) findViewById(R.id.show_notifications);
		final ConfigurationManager configuration = new ConfigurationManager(this);
		showNotifcationsCheckbox.setChecked(configuration.isNotificationEnabled());
		showNotifcationsCheckbox.setOnClickListener(
				new OnClickListener() {					
					@Override
					public void onClick(View v) {
						if (((CheckBox) v).isChecked()) {
							configuration.enableNotifications();
						} else {
							configuration.disableNotifications();
						}
					}
			}
		);
	}
	

	
	/*@Override
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
	*/
}
