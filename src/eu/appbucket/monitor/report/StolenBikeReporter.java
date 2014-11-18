package eu.appbucket.monitor.report;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import eu.appbucket.monitor.Constants;
import eu.appbucket.monitor.monitor.BikeBeacon;
import eu.appbucket.monitor.update.StolenBikeDbHelper;
import eu.appbucket.rothar.web.domain.report.ReportData;

public class StolenBikeReporter {

	private static final String DEBUG_TAG = "StolenBikeReporter";

	public void report(Context context, BikeBeacon foundBacon,
			Location reportLocation) {
		ReportData report = new ReportData();
		report.setAssetId(foundBacon.getAssetId());
		report.setLatitude(reportLocation.getLatitude());
		report.setLongitude(reportLocation.getLongitude());
		new ReportStoleBikesTask().execute(report);
	}

	private class ReportStoleBikesTask extends
			AsyncTask<ReportData, Void, Void> {
		@Override
		protected Void doInBackground(ReportData... reports) {
			try {
				postReport(reports[0]);
			} catch (IOException | JSONException e) {
				Log.e(DEBUG_TAG, "Can't post the report: " + e.getMessage());
			}
			return null;
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
		HttpPost request = new HttpPost(Constants.SERVER_URL + "/v3/assets/"
				+ report.getAssetId() + "/reports");
		HttpResponse response = null;
		request.setHeader("Content-Type", "application/json");
		try {
			StringEntity se = new StringEntity(payload);
			se.setContentEncoding("UTF-8");
			se.setContentType("application/json");
			request.setEntity(se);
			response = client.execute(request);
			Log.e(DEBUG_TAG, "Report sent with response: "
					+ response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			Log.d(DEBUG_TAG, "Can't send the report: " + e.getMessage());
			e.printStackTrace();
		} finally {
			client.close();
		}
	}
}
