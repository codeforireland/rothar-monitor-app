package eu.appbucket.monitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class MainActivity extends Activity {
	
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
			setupShowNotificationCheckbox();
			runBackgroundTasks();	
		}	
	}

	private boolean isBluetoothEnabled() {
		BluetoothManager bluetoothManager = (BluetoothManager) 
				MainActivity.this.getSystemService(Context.BLUETOOTH_SERVICE);
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
	        	   MainActivity.this.finish();
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
	        	   MainActivity.this.finish();
	           }
	       });
	    AlertDialog dialog = builder.create();
	    dialog.show();
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
	
	private void runBackgroundTasks() {
		new TaskManager(MainActivity.this).scheduleTasks();
	}
}
