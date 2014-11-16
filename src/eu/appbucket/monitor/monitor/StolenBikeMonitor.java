package eu.appbucket.monitor.monitor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import eu.appbucket.monitor.location.LocationReader;
import eu.appbucket.monitor.report.StolenBikeReporter;

public class StolenBikeMonitor extends BroadcastReceiver {

	private static final String DEBUG_TAG = "StolenBikeMonitor";
	private static final long SCAN_PERIOD = 5000;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;
	private BluetoothAdapter.LeScanCallback mLeScanCallback;
	private LocationReader locationUpdater;
	private Context context;
	private Set<Integer> foundBicyleMinorIds = new HashSet<Integer>();
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		final BluetoothManager bluetoothManager = (BluetoothManager) context
				.getSystemService(Context.BLUETOOTH_SERVICE);
		this.context = context;
		showToast("Scanning for stolen bikes and current location...");
		locationUpdater.start(context);
		mHandler = new Handler();
		mBluetoothAdapter = bluetoothManager.getAdapter();
		mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
			@Override
			public void onLeScan(BluetoothDevice device, int rssi,
					byte[] scanRecord) {
				int minor = BeaconRecordParser.parser(scanRecord);
				if(minor != -1) {
					foundBicyleMinorIds.add(minor);
				}				
			}
		};
		scanLeDevice(true);
	}

	private void showToast(String message) {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, message, duration);
		toast.show();
	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					locationUpdater.stop(context);
					for(int minor: foundBicyleMinorIds) {
						new StolenBikeReporter().report(context, minor, locationUpdater.getCurrentBestLocation());
					}
					Log.d(DEBUG_TAG, "Stopping scanner at: " + new Date());
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}
}
