package eu.appbucket.monitor.report;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import eu.appbucket.monitor.Constants;
import eu.appbucket.monitor.update.StolenBikeDbHelper;
import eu.appbucket.rothar.web.domain.report.ReportData;

public class StolenBikeReporter {
	
	private static final String DEBUG_TAG = "StolenBikeReporter";
	
	public void report(Context context, int minor, Location location) {
		int assetId = lookupAssetIdByMajor(context, minor);
		postReport(context, assetId, location.getLatitude(), location.getLongitude());
	}
	
	private int lookupAssetIdByMajor(Context context, int minor) {
		StolenBikeDbHelper dbHelper = new StolenBikeDbHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] projection = {
			    StolenBikeDbHelper.COLUMN_NAME_ASSET_ID
		};
		String selection = StolenBikeDbHelper.COLUMN_NAME_MAJOR + " = ?";
		String[] selectionArgs = {
				Integer.toString(minor)};
		Cursor cursor = db.query(
				// String, String[], String, String[], String, String, String
				StolenBikeDbHelper.TABLE_NAME,  // The table to query
			    projection,                               // The columns to return
			    selection,                                // The columns for the WHERE clause
			    selectionArgs,                            // The values for the WHERE clause
			    "",                                     // don't group the rows
			    "",                                     // don't filter by row groups
			    ""                                 // The sort order
		);
		cursor.moveToFirst();
		int assetId = cursor.getInt(
		    cursor.getColumnIndexOrThrow(StolenBikeDbHelper.COLUMN_NAME_ASSET_ID)
		);
		Log.d(DEBUG_TAG, "Reporting found asset id: " + assetId);
		return assetId;
	}
	
	private void postReport(Context context, int assetId, double latitude, double longitude) {
		ReportData report = new ReportData();
		report.setAssetId(assetId);
		report.setLatitude(latitude);
		report.setLongitude(longitude);
		new ReportStoleBikesTask(context).execute(report);
	}
	
	private class ReportStoleBikesTask extends AsyncTask<ReportData, Void, Void> {
		
		private static final String DEBUG_TAG = "ReportStoleBikesTask";
		private Context context;
		
		public ReportStoleBikesTask(Context context) {
			this.context = context;
		}		
		
		@Override
		protected Void doInBackground(ReportData... report) {
			/*try {
				//return postReport(urls[0]);	
			} catch (IOException e) {
				// return "Can't file the report: ";
			}*/	
			return null;
		}
		
		// Given a URL, establishes an HttpUrlConnection and retrieves
		// the web page content as a InputStream, which it returns as
		// a string.
		private String postReport(ReportData report) throws IOException {
		    InputStream is = null;
		    // Only display the first 15000 characters of the retrieved
		    // web page content.
		    int len = 15000;
		        
		    try {
		        URL url = new URL(Constants.SERVER_URL + "/v3/assets/" + report.getAssetId() + "/reports");
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		        conn.setReadTimeout(20000 /* milliseconds */);
		        conn.setConnectTimeout(30000 /* milliseconds */);
		        conn.setRequestMethod("POST");
		        conn.setDoInput(true);
		        conn.setDoOutput(true);
		        conn.setRequestProperty("Content-Type", 
		                "application/x-www-form-urlencoded");
		        conn.setUseCaches(false);
		        // Starts the query
		        conn.connect();
		        
		        
		        // TODO: add JSON to the post //
		        
		        
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
