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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import eu.appbucket.rothar.R;
import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.monitor.scheduler.TaskManager;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		boolean isBluetootLeCapable = isBluetootLeCapable();
		if(isBluetootLeCapable && isBluetoothDisabled()) {
			showEnableBluetoothDialog();	
		} else if(isNetworkingDisable()) {
			showEnableNetworkinDialog();
		} else {
			if(isBluetootLeCapable) {
				runBackgroundTasks();	
			}
			startAppriopriateActivity();
		}	
	}

	private boolean isBluetootLeCapable() {
		return new ConfigurationManager(this).isBluetoothLeCapable();
	}
	
	private boolean isBluetoothDisabled() {
		return !this.isBluetoothEnabled();
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
	
	private boolean isNetworkingDisable() {
		return !this.isNetworkingEnabled();
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
	
	private void runBackgroundTasks() {
		new TaskManager(MainActivity.this).scheduleTasks();
	}
	
	private void startAppriopriateActivity() {
		if(new ConfigurationManager(this).getAssetId() == null) {
			startRegistrationActivity();
		} else {
			startMapActivity();
		}
	}
	private void startRegistrationActivity() {
		Intent intent = new Intent(this, RegisterActivity.class);
		this.startActivity(intent);
	}
	
	private void startMapActivity() {
		Intent intent = new Intent(this, MapActivity.class);
		this.startActivity(intent);
	}
}
