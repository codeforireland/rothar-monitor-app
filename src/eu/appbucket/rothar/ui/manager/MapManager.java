package eu.appbucket.rothar.ui.manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.ui.listener.RepordDayChangeListener;
import eu.appbucket.rothar.ui.listener.ReportUpdateListener;
import eu.appbucket.rothar.ui.task.ReportUpdateTask;
import eu.appbucket.rothar.web.domain.report.ReportData;

public class MapManager {
	
	private Context context;
	private GoogleMap map;
	private ReportUpdateListener listener;
	private List<ReportData> reports;
	private int dayIndex = 0;
	
	public MapManager(Context context, ReportUpdateListener reportUpdateListener, RepordDayChangeListener repordDayChangeListener) {
		this.context = context;
		this.listener = reportUpdateListener;
	}

	public void setMap(GoogleMap map) {
		this.map = map;
	}
	
	public void setReports(List<ReportData> reports) {
		this.reports = reports;
	}
	
	public boolean constainsReportsForCurrentDay() {
		return !reports.isEmpty();
	}
    
	public void loadBicycleReportsForToday() {
		this.loadBicycleReportsForDay(getDateForToday());
	}

	public void loadBicycleReportsForNextDay() {
		Date nextDay = getDateForNextDay();
		loadBicycleReportsForDay(nextDay);
	}
	
	public void loadBicycleReportsForPreviousDay() {
		Date previousDay = getDateForPreviousDay();
		loadBicycleReportsForDay(previousDay);
	}
	
	private void loadBicycleReportsForDay(Date date) {
		ReportUpdateTask.InputParameter inputParameter = new ReportUpdateTask.InputParameter();
		inputParameter.setAssetId((new ConfigurationManager(context)).getAssetId());
		inputParameter.setReportDate(date);
		new ReportUpdateTask(context, listener).execute(inputParameter);
	}
	
	public void removerReportMarkersAndLineFromMap() {
		map.clear();
	}
	
	public void addReportMarkersAndLineToMap() {
		PolylineOptions locationsLine = new PolylineOptions();
		LatLng point;
		SimpleDateFormat formatter = new SimpleDateFormat("kk:mm EEEE, d MMMM yyyy", Locale.getDefault());
		Marker lastReportMarker = null;
		for(ReportData report: reports) {
			point = new LatLng(report.getLatitude(), report.getLongitude());
			lastReportMarker = map.addMarker(new MarkerOptions()
	        	.position(point)
	        	.title(formatter.format(report.getCreated())));
			locationsLine.add(point);
		}
		map.addPolyline(locationsLine);
		if(lastReportMarker != null) {
			lastReportMarker.showInfoWindow();
		}
	}
	
	public void moveToReportOrDefaultLocation() {
		LatLng lastReportLocation = findLastReportOrDefaultLocation();
		if(lastReportLocation != null) {
			moveMapToLocationAndZoom(lastReportLocation, Settings.MAP.LOCATION_ZOOM);	
		}
	}
	
	private LatLng findLastReportOrDefaultLocation() {
		if(reports.size() > 0) {
			ReportData lastReport = reports.get(reports.size() - 1);
			return new LatLng(lastReport.getLatitude(), lastReport.getLongitude());
		} else {
			return null;
		}
	}
	
	private void moveMapToLocationAndZoom(LatLng mapLocation, float zoom) {
		CameraPosition cameraPosition = CameraPosition.builder()
				.target(mapLocation)
				.zoom(zoom).build();
		map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	public Date getDateForToday() {
		dayIndex = 0;
		repordDayChangeListener ...
		return getDateForDayIndex();
	}
	
	public Date getDateForDayIndex() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, dayIndex);
		return cal.getTime();
	}
	
	public Date getDateForNextDay() {
		if(dayIndex < 0) {
			dayIndex++;
			repordDayChangeListener ...
		}
		return getDateForDayIndex();
	}
	
	public Date getDateForPreviousDay() {
		dayIndex--;	
		repordDayChangeListener ...
		return getDateForDayIndex();
	}
}
