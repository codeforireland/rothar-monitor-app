package eu.appbucket.rothar.monitor.update;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import eu.appbucket.rothar.common.NotificationManager;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.monitor.monitor.BikeBeacon;

public class UpdaterTask extends BroadcastReceiver {

	private Context context;
	private static final String LOG_TAG = "UpdaterTask";
	
	private class UpdaterTaskCommunicationError extends RuntimeException {
		public UpdaterTaskCommunicationError(String errorMessage) {
			super(errorMessage);
		}
		public UpdaterTaskCommunicationError(String errorMessage, Throwable throwable) {
			super(errorMessage, throwable);
		}
	} 
	
	private class UpdaterTaskProcessingError extends RuntimeException {		
		public UpdaterTaskProcessingError(String errorMessage, Throwable throwable) {
			super(errorMessage, throwable);
		}
	} 
	
	@Override
	public void onReceive(Context context, Intent intent) {		
		this.context = context;		
		new ReportedBikesFetcher().execute();
	}
	
	private class ReportedBikesFetcher extends AsyncTask<Void, Void, String> {	
	
		@Override
		protected String doInBackground(Void... params) {
			return fetchReportedBikesInBackground();
		}
		
		@Override
		protected void onPostExecute(String notification) {
			new NotificationManager(context).showNotification(notification);
		}
	}
	
	private String fetchReportedBikesInBackground() {
		if(!isNetworkAvailable()) {
			Log.i(LOG_TAG, "Can't fetch reigstered bikes because network not availabe.");
			return "Can't fetch registered bikes - network disabled.";
		}
		try {
			String rawRecords = getRecordsRawData();
			Set<BikeBeacon> reportedBikes = convertRawRecordsToSet(rawRecords);
			resetReportedBikesDatabase();		
	        populateReportedBikesDatabase(reportedBikes);
	        return "Registered " + reportedBikes.size() + " stolen bikes.";
		} catch (UpdaterTaskCommunicationError e) {
			Log.e(LOG_TAG, "Communication error: ", e);
			return "Can't fetch reported bikes - communication problem.";
		} catch (UpdaterTaskProcessingError e) {
			Log.e(LOG_TAG, "Processing error: ", e);
			return "Can't fetch reported bikes - data problem.";
		}
	}
	
	private boolean isNetworkAvailable() {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    } else {
	        return false;
	    }
	}

	private String getRecordsRawData() throws UpdaterTaskCommunicationError, UpdaterTaskProcessingError {
		String recordsUrl = Settings.SERVER_URL + "/v3/assets?limit=100&offset=0&status=STOLEN";
		String recordsAsJsonString = "";
		int len = 15000;
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		HttpGet request = new HttpGet(recordsUrl);
		request.setHeader("Content-Type", "application/json");
		try {
			HttpResponse response = client.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();			
			if(responseCode == HttpURLConnection.HTTP_OK) {
				InputStream is = response.getEntity().getContent();
		        recordsAsJsonString = convertInputStreamToString(is, len);	
			} else {
				throw new UpdaterTaskCommunicationError("Server responded with error code: + " + responseCode);
			}
		} catch (IOException e) {
			throw new UpdaterTaskCommunicationError("Connection error", e);
		} finally {
			client.close();
		}
		return recordsAsJsonString;
	}
	
	public String convertInputStreamToString(InputStream stream, int len) throws UpdaterTaskProcessingError {
	    try {
	    	Reader reader = null;
		    reader = new InputStreamReader(stream, "UTF-8");        
		    char[] buffer = new char[len];
		    reader.read(buffer);
		    return new String(buffer);
		} catch (IOException e) {
			throw new UpdaterTaskProcessingError("Can't convert input data to string.", e);
		}		
	}
	
	private Set<BikeBeacon> convertRawRecordsToSet(String result) {
		Set<BikeBeacon> stolenBikes = new HashSet<BikeBeacon>(); 
		try {
			JSONArray jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
		        JSONObject explrObject = jsonArray.getJSONObject(i);
		        stolenBikes.add(
		        		new BikeBeacon(
		        				explrObject.getInt("assetId"), 
		        				explrObject.getString("uuid"), 
		        				explrObject.getInt("major"), 
		        				explrObject.getInt("minor")));			        
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Can't process stolen bikes");
		}
		return stolenBikes;
	}
	
	private void populateReportedBikesDatabase(Set<BikeBeacon> stolenBikes) {			
		for (BikeBeacon record : stolenBikes) {
			new StolenBikeDao().addStolenBikeRecord(context, record);
		}		
	}
	
	private void resetReportedBikesDatabase() {
		new StolenBikeDao().resetStolenBikes(context);
	}
}
