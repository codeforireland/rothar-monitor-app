package eu.appbucket.rothar.ui;

import java.util.List;

import eu.appbucket.rothar.web.domain.report.ReportData;

public interface MapUpdateListener {
	
	void onMapReportUpdate(List<ReportData> reports);
}
