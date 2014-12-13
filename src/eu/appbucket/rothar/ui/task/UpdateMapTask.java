package eu.appbucket.rothar.ui.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.AsyncTask;
import eu.appbucket.rothar.ui.MapUpdateListener;
import eu.appbucket.rothar.web.domain.report.ReportData;

public class UpdateMapTask extends AsyncTask<Date, Void, List<ReportData>> {

	private MapUpdateListener listener;
	
	public UpdateMapTask(MapUpdateListener listener) {
		this.listener = listener;
	}

	@Override
	protected List<ReportData> doInBackground(Date... params) {
		return readReportsInTheBackgroupd(params[0]);
	}
	
	private List<ReportData> readReportsInTheBackgroupd(Date reportDate) {
		// - end -
		List<ReportData> reports = new ArrayList<ReportData>();
		ReportData report1 = new ReportData();
		report1.setLatitude(53.34 + (Math.random() / 100));
		report1.setLongitude(-6.26 + (Math.random() / 100));
		report1.setCreated(reportDate);
		reports.add(report1);
		ReportData report2 = new ReportData();
		report2.setLatitude(53.34 + (Math.random() / 100));
		report2.setLongitude(-6.25 + (Math.random() / 100));
		report2.setCreated(reportDate);
		reports.add(report2);
		return reports;
	}
	
	@Override
	protected void onPostExecute(List<ReportData> reports) {
		listener.onMapReportUpdate(reports);
	}
}
