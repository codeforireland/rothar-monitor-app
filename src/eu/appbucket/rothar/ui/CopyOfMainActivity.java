package eu.appbucket.rothar.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import eu.appbucket.rothar.R;
import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.monitor.scheduler.TaskManager;

public class CopyOfMainActivity extends Activity {
	
	private static final String LOG_TAG = "MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(!isBluetoothEnabled()) {
			showEnableBluetoothDialog();	
		} else if(!isNetworkingEnabled()) {
			showEnableNetworkinDialog();
		} else {
			// setupShowNotificationCheckbox();
			runBackgroundTasks();
			startAppriopriateActicity();
		}	
	}

	private boolean isBluetoothEnabled() {
		BluetoothManager bluetoothManager = (BluetoothManager) 
				CopyOfMainActivity.this.getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
		if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
			return true;
		}
		return false;
	}
	
	private void showEnableBluetoothDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(R.string.bluetooth_dialog_message).setTitle(R.string.bluetooth_dialog_title);
	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   CopyOfMainActivity.this.finish();
	           }
	       });
	    AlertDialog dialog = builder.create();
	    dialog.show();
	}	
	
	private boolean isNetworkingEnabled() {
		ConnectivityManager connec = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);	
	    if (wifi.isConnected() || mobile.isConnected()) {
	    	return true;
	    }
	    return false;	    
	}
	
	private void showEnableNetworkinDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(R.string.networking_dialog_message).setTitle(R.string.networking_dialog_title);
	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   CopyOfMainActivity.this.finish();
	           }
	       });
	    AlertDialog dialog = builder.create();
	    dialog.show();
	}
	
	/*private void setupShowNotificationCheckbox() {
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
	}*/
	
	private void runBackgroundTasks() {
		new TaskManager(CopyOfMainActivity.this).scheduleTasks();
	}
	
	private void startAppriopriateActicity() {
		if(new ConfigurationManager(this).getAssetId() == null) {
			startRegistrationActivity();
		} else {
			startTagActivity();
		}
	}
	private void startRegistrationActivity() {
		Intent intent = new Intent(this, RegistrationActivity.class);
		this.startActivity(intent);
	}
	
	private void startTagActivity() {
		Intent intent = new Intent(this, TagActivity.class);
		this.startActivity(intent);
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