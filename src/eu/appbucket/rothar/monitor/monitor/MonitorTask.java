package eu.appbucket.rothar.monitor.monitor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import eu.appbucket.rothar.common.NotificationManager;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.monitor.report.ReporterTask;
import eu.appbucket.rothar.monitor.update.StolenBikeDao;

public class MonitorTask extends BroadcastReceiver {
	
	private Context context;
	private static final String LOG_TAG = "MonitorTask";
	private BluetoothManager bluetoothManager;
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothAdapter.LeScanCallback scanCallback;
	private Handler delayedExecutionHandler;
	private Collection<BikeBeacon> foundBeacons = Collections.synchronizedCollection(new HashSet<BikeBeacon>());
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		this.context = context;
		scanForStolenBikes();
	}
	
	private void scanForStolenBikes() {
		setupScanner();
		startScanner();
	}

	private void setupScanner() {
		bluetoothManager = (BluetoothManager) 
				context.getSystemService(Context.BLUETOOTH_SERVICE);
		delayedExecutionHandler = new Handler();
		bluetoothAdapter = bluetoothManager.getAdapter();
		scanCallback = new BluetoothAdapter.LeScanCallback() {
			@Override
			public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
				processFoundBeaconRecord(scanRecord);
			}
		};
	}
	
	private void processFoundBeaconRecord(byte[] scanRecord) {
		BeaconRecordParser parser = new BeaconRecordParser(scanRecord);
		if(!parser.isRecordValid()) {
			return;
		}
		BikeBeacon beacon = parser.parserRecordToBeacon();
		if(isSupportedBySystem(beacon)) {
			beacon = findInStolenBikes(beacon);
			if(beacon.getAssetId() != null) {
				foundBeacons.add(beacon);
			}
		}
	}
	
	private  boolean isSupportedBySystem(BikeBeacon beacon) {
		if(beacon.getUudi().equals(Settings.SYSTEM_IBEACON.UUID) 
				&& beacon.getMajor() == Settings.SYSTEM_IBEACON.MAJOR) {
			return true;
		}
		return false;
	}
	
	private BikeBeacon findInStolenBikes(BikeBeacon beacon) {
		beacon = new StolenBikeDao().findBikeRecordByIdentity(
				context, beacon.getUudi(), beacon.getMajor(), beacon.getMinor());
		return beacon;
	}
	
	private void startScanner() {
		Log.d(LOG_TAG, "Starting scanner.");
		scanForBeacons();
	}
		
	private void scanForBeacons() {
		// Stops scanning after a pre-defined scan period.
		delayedExecutionHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				stopScanner();
			}
		}, Settings.MONITOR_TASK.DURATION);
		bluetoothAdapter.startLeScan(scanCallback);
	}
	
	private void stopScanner() {
		Log.d(LOG_TAG, "Stopping scanner.");
		bluetoothAdapter.stopLeScan(scanCallback);
		if(foundBeacons.size() == 0) {
			showToast("No stolen bikes found.");	
		} else {
			showToast("Found " + foundBeacons.size() + " stolen bike(s).");
			ReporterTask reporter = new ReporterTask(context);
			for(BikeBeacon beacon: foundBeacons) {
				reporter.store(beacon);
			}
			reporter.report();
		}
	}
	
	private void showToast(String message) {
		new NotificationManager(context).showNotification(message);
	}
}
