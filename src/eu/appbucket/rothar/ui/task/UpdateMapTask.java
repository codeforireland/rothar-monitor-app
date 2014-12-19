package eu.appbucket.rothar.ui.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.ui.MapUpdateListener;
import eu.appbucket.rothar.ui.task.UpdateMapTask.InputParameter;
import eu.appbucket.rothar.ui.task.commons.OperationResult;
import eu.appbucket.rothar.ui.task.commons.TaskCommons;
import eu.appbucket.rothar.web.domain.report.ReportData;

class Result {
	protected List<ReportData> reports;
	protected boolean failure = true;
}

public class UpdateMapTask extends AsyncTask<InputParameter, Void, Result> {
	
	public static class InputParameter {
		private Date reportDate;
		private int assetId;
		
		public void setAssetId(int assetId) {
			this.assetId = assetId;
		}
		
		public int getAssetId() {
			return assetId;
		}
		
		public void setReportDate(Date reportDate) {
			this.reportDate = reportDate;
		}
		
		public Date getReportDate() {
			return reportDate;
		}
	}
	
	private static final String LOG_TAG = "UpdateMapTask";
	private MapUpdateListener listener;
	private int assetId;
	
	public UpdateMapTask(Context context, MapUpdateListener listener) {
		this.listener = listener;
	}

	@Override
	protected Result doInBackground(InputParameter... params) {
		return readReportsInTheBackground(params[0]);
	}
	
	private Result readReportsInTheBackground(InputParameter inputParameter) {
		Result overallResult = new Result();
		overallResult.reports = new ArrayList<ReportData>();
		List<ReportData> lastReports = new ArrayList<ReportData>();
		int offset = 0;
		int limit = 20;
		String baseUrl = buildUrlForDate(inputParameter);
		do {
			String currentUrl = buildUrlForLimitAndOffset(baseUrl, limit, offset);
			OperationResult lastResult = TaskCommons.getDataFromUrl(currentUrl);
			if(lastResult.isSuccess()) {
				lastReports =  convertJsonStringToListOfReports(lastResult.getPayload());
				overallResult.reports.addAll(lastReports);
				overallResult.failure = false;
				offset = offset + limit;
			} else {
				overallResult.failure = true;
			}
		} while (lastReports.size() > 0 && !overallResult.failure);
		return overallResult;
	}
	
	private String buildUrlForDate(InputParameter inputParameter) {
		String urlPattern = Settings.SERVER_URL + "/v4/assets/%s/reports/%s/%s?sort=created&order=asc&";
		Long startTime = getTimeStampAtTheBeginningOfDay(inputParameter.getReportDate());
		Long endTime = getTimeStampAtTheEndOfDay(inputParameter.getReportDate());
		String url = String.format(urlPattern, inputParameter.getAssetId(), startTime, endTime);
		return url;
	}
	
	private String buildUrlForLimitAndOffset(String baseUrl, int limit, int offset) {
		String url = String.format(baseUrl + "limit=%s&offset=%s", limit, offset);
		return url;
	}
	
	private long getTimeStampAtTheBeginningOfDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}
	
	private long getTimeStampAtTheEndOfDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTimeInMillis();
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
