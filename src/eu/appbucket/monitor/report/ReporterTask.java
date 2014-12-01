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
import eu.appbucket.monitor.ConfigurationManager;
import eu.appbucket.monitor.NotificationManager;
import eu.appbucket.monitor.Settings;
import eu.appbucket.monitor.monitor.BikeBeacon;
import eu.appbucket.monitor.monitor.LocationReader;
import eu.appbucket.rothar.web.domain.report.ReportData;

public class ReporterTask {

	private Context context;
	private static final String LOG_TAG = "ReporterTask";
	private final Set<BikeBeacon> foundBeacons = new HashSet<BikeBeacon>();
	private LocationReader locationUpdater;
	private Handler mHandler;
	private String applicationUuid;
	
	private class ReporterTaskProcessingError extends RuntimeException {		
		public ReporterTaskProcessingError(String errorMessage, Throwable throwable) {
			super(errorMessage, throwable);
		}
	} 
	
	private class ReporterTaskCommunicationError extends RuntimeException {		
		public ReporterTaskCommunicationError(String errorMessage, Throwable throwable) {
			super(errorMessage, throwable);
		}
	} 
	
	public ReporterTask(Context context) {
		this.context = context;
		locationUpdater = new LocationReader(context);
		locationUpdater.start();
		applicationUuid = new ConfigurationManager(context).getApplicationUuid();
	}
	
	public void store(BikeBeacon foundBacon) {
		Log.d(LOG_TAG, "Adding beacon: " + foundBacon.getMinor() + " to report " + Thread.currentThread().getId());
		this.foundBeacons.add(foundBacon);
	}

	public void report() {
		Log.d(LOG_TAG, "Finding current location ..." + Thread.currentThread().getId());
		mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				startReporting();
			}
		}, Settings.REPORTER_TASK.DURATION);
	}
	
	private void startReporting() {
		Log.d(LOG_TAG, "Starting reporting " +  + Thread.currentThread().getId());
		locationUpdater.stop();
		Location reportLocation = locationUpdater.getCurrentBestLocation();
		for(BikeBeacon beacon: foundBeacons) {
			Log.d(LOG_TAG, "Reporting beacon: " + beacon.getMinor() + "...");
			ReportData report = new ReportData();
			report.setAssetId(beacon.getAssetId());
			report.setLatitude(reportLocation.getLatitude());
			report.setLongitude(reportLocation.getLongitude());
			report.setReporterUuid(applicationUuid);
			new ReportStoleBikesTask().execute(report);
		}
	}
	
	private class ReportStoleBikesTask extends AsyncTask<ReportData, Void, String> {
		
		@Override
		protected String doInBackground(ReportData... reports) {
			return postStolenBikeReportInTheBackground(reports[0]);
		}
		
		@Override
		protected void onPostExecute(String message) {
			new NotificationManager(context).showNotification(message);
		}
	}
	
	private String postStolenBikeReportInTheBackground(ReportData report) throws ReporterTaskProcessingError {
		try {
			postStolenBikeReport(report);
			return "Report sent for bike id: " + report.getAssetId();
		} catch (ReporterTaskProcessingError e) {
			Log.e(LOG_TAG, "Processing report data failed.", e);
			return "Can't process report for bike id: " + report.getAssetId();
		} catch (ReporterTaskCommunicationError e) {
			Log.e(LOG_TAG, "Processing report data failed.", e);
			return "Can't sent report for bike id: " + report.getAssetId();
		}
	}
	
	private void postStolenBikeReport(ReportData report) throws ReporterTaskProcessingError, ReporterTaskCommunicationError {
		String payload = convertJsonToString(report);
		String url = Settings.SERVER_URL + "/v3/assets/" + report.getAssetId() + "/reports";
		postDataToUrl(payload, url);
	}
	
	private String convertJsonToString(ReportData report) throws ReporterTaskProcessingError {
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("assetId", report.getAssetId());
			jsonObj.put("latitude", report.getLatitude());
			jsonObj.put("longitude", report.getLongitude());
			jsonObj.put("reporter_uuid", report.getReporterUuid());
		} catch (JSONException e) {
			new ReporterTaskProcessingError("Can't convert report data to json object.", e);
		}
		return jsonObj.toString();
	}
	
	private void postDataToUrl(String payload, String url) throws ReporterTaskCommunicationError {
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		HttpPost request = new HttpPost(url);
		request.setHeader("Content-Type", "application/json");
		try {
			StringEntity se = new StringEntity(payload);
			se.setContentEncoding("UTF-8");
			se.setContentType("application/json");
			request.setEntity(se);
			client.execute(request);
		} catch (IOException e) {
			throw new ReporterTaskCommunicationError("Communication error. ", e);
		} finally {
			client.close();
		}
	}
}
