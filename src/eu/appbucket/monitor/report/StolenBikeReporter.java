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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothClass.Device.Major;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import eu.appbucket.monitor.Constants;
import eu.appbucket.monitor.monitor.BikeRecord;
import eu.appbucket.monitor.update.StolenBikeDbHelper;
import eu.appbucket.rothar.web.domain.report.ReportData;

public class StolenBikeReporter {
	
	private static final String DEBUG_TAG = "StolenBikeReporter";
	
	public void report(Context context, BikeRecord beacon, Location location) {
		int assetId = lookupAssetIdByMajor(context, beacon.getMinor());
		if(assetId != -1) {
			postReport(context, assetId, location.getLatitude(), location.getLongitude());	
		}
	}
	
	private int lookupAssetIdByMajor(Context context, int minor) {
		int assetId = -1;
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
		if(cursor.getCount() > 0) {
			assetId = cursor.getInt(
				    cursor.getColumnIndexOrThrow(
				    		StolenBikeDbHelper.COLUMN_NAME_ASSET_ID));	
			Log.d(DEBUG_TAG, "Reporting found asset id: " + assetId);
		} else {
			Log.e(DEBUG_TAG, "Can't find asset id for minor: " + minor);
		}
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
		protected Void doInBackground(ReportData... reports) {
			try {
				postReport(reports[0]);
			} catch (IOException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		// Given a URL, establishes an HttpUrlConnection and retrieves
		// the web page content as a InputStream, which it returns as
		// a string.
		private void postReport(ReportData report) throws IOException, JSONException {
		        JSONObject jsonObj = new JSONObject();
				jsonObj.put("assetId", report.getAssetId());
				jsonObj.put("latitude", report.getLatitude());
				jsonObj.put("longitude", report.getLongitude());			
		        String payload = jsonObj.toString();
		        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		        HttpPost request = new HttpPost(Constants.SERVER_URL + "/v3/assets/" + report.getAssetId() + "/reports");
		        HttpResponse response = null;
		        request.setHeader( "Content-Type", "application/json" );
		        try {
		        	StringEntity se = new StringEntity(payload);
			        se.setContentEncoding("UTF-8");
			        se.setContentType("application/json");	        
			        request.setEntity(se);		        
			        response = client.execute(request);
		        } catch (IOException e) {
		            Log.d(DEBUG_TAG, "Can't send the report: " + e.getMessage());
		            e.printStackTrace();
		        } finally {
		        	client.close();
		        }
		}
	}
}
