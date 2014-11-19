package eu.appbucket.monitor.update;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import eu.appbucket.monitor.Settings;
import eu.appbucket.monitor.monitor.BikeBeacon;

public class StolenBikeUpdater extends BroadcastReceiver {

	private Context context;
	private static final String DEBUG_TAG = "StolenBikeUpdater";
	
	@Override
	public void onReceive(Context context, Intent intent) {		
		this.context = context;
		showToast("Updating reported bikes ...");
		if(isNetworkAvailable()) {
			fetchStolenBikeData();
		}
	}
	
	private void showToast(String message) {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, message, duration);
		toast.show();
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
	
	private void fetchStolenBikeData() {
		new DownloadStoleBikesTask(context).execute(Settings.SERVER_URL + "/v3/assets?limit=100&offset=0&status=STOLEN");		
	}	
	
	private class DownloadStoleBikesTask extends AsyncTask<String, Void, String> {
		
		private static final String DEBUG_TAG = "DownloadStoleBikesTask";
		private Context context;
		
		public DownloadStoleBikesTask(Context context) {
			this.context = context;
		}		
		
		@Override
		protected String doInBackground(String... urls) {
			try {
				return downloadUrl(urls[0]);	
			} catch (IOException e) {
				return "No results returned.";
			}			
		}
		
		// Given a URL, establishes an HttpUrlConnection and retrieves
		// the web page content as a InputStream, which it returns as
		// a string.
		private String downloadUrl(String myurl) throws IOException {
		    InputStream is = null;
		    // Only display the first 15000 characters of the retrieved
		    // web page content.
		    int len = 15000;
		        
		    try {
		        URL url = new URL(myurl);
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		        conn.setReadTimeout(20000 /* milliseconds */);
		        conn.setConnectTimeout(30000 /* milliseconds */);
		        conn.setRequestMethod("GET");
		        conn.setDoInput(true);
		        // Starts the query
		        conn.connect();
		        int response = conn.getResponseCode();
		        Log.d(DEBUG_TAG, "The response is: " + response);
		        is = conn.getInputStream();

		        // Convert the InputStream into a string
		        String contentAsString = readIt(is, len);
		        return contentAsString;
		        
		    // Makes sure that the InputStream is closed after the app is
		    // finished using it.
		    } finally {
		        if (is != null) {
		            is.close();
		        } 
		    }
		}
		
		// Reads an InputStream and converts it to a String.
		public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
		    Reader reader = null;
		    reader = new InputStreamReader(stream, "UTF-8");        
		    char[] buffer = new char[len];
		    reader.read(buffer);
		    return new String(buffer);
		}
		
		@Override
		protected void onPostExecute(String result) {
			cleanStolenBikeData();
			Set<BikeBeacon> stolenBikes = processResult(result);
	        storeStolenBikeData(stolenBikes);
		}
		
		private Set<BikeBeacon> processResult(String result) {
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
				Log.e(DEBUG_TAG, "Can't process stolen bikes");
			}
			return stolenBikes;
		}
		
		private void showToast(Context context, String message) {
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, message, duration);
			toast.show();
		}
		
		private void storeStolenBikeData(Set<BikeBeacon> stolenBikes) {			
			for (BikeBeacon record : stolenBikes) {
				new StolenBikeDao().addStolenBikeRecord(context, record);
			}
			showToast(context, "Reported " + stolenBikes.size() + " stolen bikes");		
		}
		
		private void cleanStolenBikeData() {
			new StolenBikeDao().resetStolenBikes(context);
		}
	}
}
