package eu.appbucket.rothar.ui.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.monitor.monitor.BikeBeacon;
import eu.appbucket.rothar.ui.MapUpdateListener;
import eu.appbucket.rothar.ui.task.commons.OperationResult;
import eu.appbucket.rothar.ui.task.commons.TaskCommons;
import eu.appbucket.rothar.web.domain.report.ReportData;

class Result {
	protected List<ReportData> reports;
	protected boolean failure = true;
}

public class UpdateMapTask extends AsyncTask<Date, Void, Result> {

	private static final String LOG_TAG = "UpdateMapTask";
	private MapUpdateListener listener;
	private int assetId;
	
	public UpdateMapTask(Context context, MapUpdateListener listener) {
		this.listener = listener;
		assetId = 33;
	}

	@Override
	protected Result doInBackground(Date... params) {
		return readReportsInTheBackground(params[0]);
	}
	
	private Result readReportsInTheBackground(Date reportDate) {
		Result overallResult = new Result();
		overallResult.reports = new ArrayList<ReportData>();
		List<ReportData> currentRequestReports = new ArrayList<ReportData>();
		int offset = 0;
		int limit = 20;
		String urlPattern = Settings.SERVER_URL + "/v4/assets/%s/reports?sort=created&limit=%s&offset=%s";
		String currentRequestUrl;
		OperationResult lastRequestResult;
		do {
			currentRequestUrl = String.format(urlPattern, assetId, limit, offset);
			lastRequestResult = TaskCommons.getDataFromUrl(currentRequestUrl);
			Log.i(LOG_TAG, currentRequestUrl);
			if(lastRequestResult.isSuccess()) {
				currentRequestReports =  convertJsonStringToListOfReports(lastRequestResult.getPayload());
				overallResult.reports.addAll(currentRequestReports);
				overallResult.failure = false;
				offset = offset + limit;
			} else {
				overallResult.failure = true;
			}
		} while (currentRequestReports.size() > 0 && !overallResult.failure);
		return overallResult;
	}
	
	private List<ReportData> convertJsonStringToListOfReports(String jsonArrayAsString) {
		List<ReportData> reports = new ArrayList<ReportData>();
		ReportData report;
		try {
			JSONArray jsonArray = new JSONArray(jsonArrayAsString);
			for (int i = 0; i < jsonArray.length(); i++) {
		        JSONObject explrObject = jsonArray.getJSONObject(i);
		        report = new ReportData();
		        report.setAssetId(assetId);
		        report.setCreated(new Date(explrObject.getLong("created")));
		        report.setLatitude(explrObject.getDouble("latitude"));
		        report.setLongitude(explrObject.getDouble("longitude"));
		        reports.add(report);
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Can't process reports.");
		}
		return reports;
	}
	
	@Override
	protected void onPostExecute(Result result) {
		if(result.failure) {
			listener.onMapReportUpdateFailure();
		} else {
			listener.onMapReportUpdateSuccess(result.reports);	
		}
	}
}
