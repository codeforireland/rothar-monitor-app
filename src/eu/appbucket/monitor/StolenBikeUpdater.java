package eu.appbucket.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class StolenBikeUpdater extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(isNetworkAvailable(context)) {
			fetchStolenBikeData(context);
		}
	}
	
	private boolean isNetworkAvailable(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
	private void fetchStolenBikeData(Context context) {
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
		        conn.setReadTimeout(10000 /* milliseconds */);
		        conn.setConnectTimeout(15000 /* milliseconds */);
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
			// JSONArray array = new JSONArray().
			try {
				JSONArray jsonArray = new JSONArray(result);
				for (int i = 0; i < jsonArray.length(); i++) {
			        JSONObject explrObject = jsonArray.getJSONObject(i);
			        explrObject.getString("assetId");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			showToast(context, "Stolen bikes list returned: " + result);
		}
				
		private void storeStolenBikeData(Context context, List<Integer> stolenBikes) {
			StringBuilder toastData = new StringBuilder("Stolen bike ids: ");
			for(Integer bikeId: stolenBikes) {
				toastData.append(bikeId + ", ");
			}
			showToast(context, toastData.toString());		
		}
		
		private void showToast(Context context, String message) {
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, message, duration);
			toast.show();
		}
	}
}
