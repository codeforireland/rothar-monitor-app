package eu.appbucket.monitor.report;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import eu.appbucket.monitor.NotificationManager;
import eu.appbucket.monitor.Settings;
import eu.appbucket.monitor.monitor.BikeBeacon;
import eu.appbucket.monitor.monitor.LocationReader;
import eu.appbucket.rothar.web.domain.report.ReportData;

public class ReporterTask {

	private Context context;
	private static final String DEBUG_TAG = "ReporterTask";
	private final Set<BikeBeacon> foundBeacons = new HashSet<BikeBeacon>();
	private LocationReader locationUpdater;
	private Handler mHandler;
	
	public ReporterTask(Context context) {
		this.context = context;
		locationUpdater = new LocationReader(context);
		locationUpdater.start();
	}
	
	public void store(BikeBeacon foundBacon) {
		Log.d(DEBUG_TAG, "Adding beacon: " + foundBacon.getMinor() + " to report.");
		this.foundBeacons.add(foundBacon);
	}

	public void report() {
		Log.d(DEBUG_TAG, "Finding current location ...");
		mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				startReporting();
			}
		}, Settings.REPORTER_TASK.DURATION);
		
	}
	
	private void startReporting() {
		locationUpdater.stop();
		Location reportLocation = locationUpdater.getCurrentBestLocation();
		for(BikeBeacon beacon: foundBeacons) {
			Log.d(DEBUG_TAG, "Reporting beacon: " + beacon.getMinor() + "...");
			ReportData report = new ReportData();
			report.setAssetId(beacon.getAssetId());
			report.setLatitude(reportLocation.getLatitude());
			report.setLongitude(reportLocation.getLongitude());
			new ReportStoleBikesTask().execute(report);
		}
		Log.d(DEBUG_TAG, "Done");
	}
	
	private void showToast(String message) {
		new NotificationManager(context).showNotification(message);
	}
	
	private class ReportStoleBikesTask extends
			AsyncTask<ReportData, Void, Void> {
		ReportData report;
		@Override
		protected Void doInBackground(ReportData... reports) {
			try {
				report = reports[0];
				postReport(report);
			} catch (IOException | JSONException e) {
				Log.e(DEBUG_TAG, "Can't post the report: " + e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			showToast("Report sent for bike id: " + report.getAssetId());
		}
	}

	private void postReport(ReportData report) throws IOException,
			JSONException {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("assetId", report.getAssetId());
		jsonObj.put("latitude", report.getLatitude());
		jsonObj.put("longitude", report.getLongitude());
		String payload = jsonObj.toString();
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		HttpPost request = new HttpPost(Settings.SERVER_URL + "/v3/assets/"
				+ report.getAssetId() + "/reports");
		HttpResponse response = null;
		request.setHeader("Content-Type", "application/json");
		try {
			StringEntity se = new StringEntity(payload);
			se.setContentEncoding("UTF-8");
			se.setContentType("application/json");
			request.setEntity(se);
			response = client.execute(request);
			Log.d(DEBUG_TAG, "Report sent with response: "
					+ response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			Log.e(DEBUG_TAG, "Can't send the report: " + e.getMessage());
		} finally {
			client.close();
		}
	}
}
