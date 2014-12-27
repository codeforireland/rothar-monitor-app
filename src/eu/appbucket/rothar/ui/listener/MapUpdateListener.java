package eu.appbucket.rothar.ui.listener;

import java.util.List;

import eu.appbucket.rothar.web.domain.report.ReportData;

public interface MapUpdateListener {
	
	void onMapReportUpdateSuccess(List<ReportData> reports);
	void onMapReportUpdateFailure();
}