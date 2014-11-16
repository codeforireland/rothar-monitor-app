package eu.appbucket.monitor.update;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import eu.appbucket.monitor.Constants;

public class StolenBikeUpdater extends BroadcastReceiver {

	private Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		if(isNetworkAvailable()) {
			fetchStolenBikeData();
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
	
	private void fetchStolenBikeData() {
		new DownloadStoleBikesTask(context).execute(Constants.SERVER_URL + "/v3/assets?limit=100&offset=0&status=STOLEN");		
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
			Map<Integer, Integer> stolenBikesAssetIdToMajor = new HashMap<Integer, Integer>(); 
			try {
				JSONArray jsonArray = new JSONArray(result);
				for (int i = 0; i < jsonArray.length(); i++) {
			        JSONObject explrObject = jsonArray.getJSONObject(i);
			        stolenBikesAssetIdToMajor.put(explrObject.getInt("assetId"), explrObject.getInt("minor"));			        
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} 
			cleanStolenBikeData();
	        storeStolenBikeData(stolenBikesAssetIdToMajor);
		}
		
		private void showToast(Context context, String message) {
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, message, duration);
			toast.show();
		}
		
		private void storeStolenBikeData(Map<Integer, Integer> stolenBikes) {
			StringBuilder toastData = new StringBuilder("Stolen bike ids: ");
			StolenBikeDbHelper dbHelper = new StolenBikeDbHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			long newRowId;
			int major;
			for (int assetId : stolenBikes.keySet()) {
				major = stolenBikes.get(assetId); 
				ContentValues values = new ContentValues();
				values.put(StolenBikeDbHelper.COLUMN_NAME_ASSET_ID, assetId);				
				values.put(StolenBikeDbHelper.COLUMN_NAME_MAJOR, major);
				newRowId = db.insert(
						StolenBikeDbHelper.TABLE_NAME,
						null,
				        values);
				toastData.append(assetId + "," + major + "["+newRowId+"], ");
			}
			showToast(context, toastData.toString());		
		}
		
		private void cleanStolenBikeData() {
			StolenBikeDbHelper dbHelper = new StolenBikeDbHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.delete(StolenBikeDbHelper.TABLE_NAME, null, null);
		}
	}
}
