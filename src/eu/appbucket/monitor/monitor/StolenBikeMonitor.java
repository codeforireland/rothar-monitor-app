package eu.appbucket.monitor.monitor;

import java.util.HashSet;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;
import eu.appbucket.monitor.Settings;
import eu.appbucket.monitor.report.StolenBikeReporter;
import eu.appbucket.monitor.update.StolenBikeDao;

public class StolenBikeMonitor extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		run(context);
	}
	
	private Context context;
	private static final String DEBUG_TAG = "StolenBikeMonitor";
	private BluetoothManager bluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothAdapter.LeScanCallback mLeScanCallback;
	private Handler mHandler;
	private boolean mScanning;
	private LocationReader locationUpdater;
	private Set<BikeBeacon> beaconsFound = new HashSet<BikeBeacon>();
	
	private void run(Context context) {
		setup(context);
		start();
	}

	private void setup(Context context) {
		this.context = context;
		locationUpdater = new LocationReader(context);
		locationUpdater.start(context);
		bluetoothManager = (BluetoothManager) 
				context.getSystemService(Context.BLUETOOTH_SERVICE);
		mHandler = new Handler();
		mBluetoothAdapter = bluetoothManager.getAdapter();
		mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
			@Override
			public void onLeScan(BluetoothDevice device, int rssi,
					byte[] scanRecord) {
				processRecord(scanRecord);
			}
		};
	}
	
	private void processRecord(byte[] scanRecord) {
		BeaconRecordParser parser = new BeaconRecordParser(scanRecord);
		if(parser.isRecordValid()) {
			BikeBeacon beacon = parser.parserRecordToBeacon();
			if(isSupportedBySystem(beacon)) {
				beacon = findInStolenBikes(beacon);
				if(beacon.getAssetId() != null) {
					beaconsFound.add(beacon);	
				}
			}
		}
	}
	
	private  boolean isSupportedBySystem(BikeBeacon beacon) {
		if(beacon.getUudi().equals(Settings.IBEACON.IBEACON_UUID) 
				&& beacon.getMajor() == Settings.IBEACON.IBEACON_MAJOR) {
			return true;
		}
		return false;
	}
	
	private BikeBeacon findInStolenBikes(BikeBeacon beacon) {
		beacon = new StolenBikeDao().findBikeRecordByIdentity(
				context, beacon.getUudi(), beacon.getMajor(), beacon.getMinor());
		return beacon;
	}
	
	private void start() {
		showToast("Searching stolen bikes ...");
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
					stop();
				}
			}, Settings.MONITOR.SEARCH_DURATION);
			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}
	
	private void stop() {
		mScanning = false;
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
		locationUpdater.stop(context);
		for(BikeBeacon beacon: beaconsFound) {
			new StolenBikeReporter(context).report(beacon, locationUpdater.getCurrentBestLocation());
		}
		showToast("Found: " + beaconsFound.size() + " stolen bikes.");
	}
}
