package eu.appbucket.rothar.ui.listener;

import java.util.List;

import eu.appbucket.rothar.web.domain.report.ReportData;

public interface ReportUpdateListener {
	
	void onReportUpdateSuccess(List<ReportData> reports);
	void onReportUpdateFailure();
}
