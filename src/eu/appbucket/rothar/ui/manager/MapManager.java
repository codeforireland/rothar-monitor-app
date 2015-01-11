package eu.appbucket.rothar.ui.manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.ui.listener.MapUpdateListener;
import eu.appbucket.rothar.ui.task.UpdateMapTask;
import eu.appbucket.rothar.web.domain.report.ReportData;

public class MapManager {
	
	private Context context;
	private GoogleMap map;
	private MapUpdateListener listener;
	private List<ReportData> reports;
	private int dayIndex = 0;
	
	public MapManager(Context context, MapUpdateListener listener) {
		this.context = context;
		this.listener = listener;
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
		UpdateMapTask.InputParameter inputParameter = new UpdateMapTask.InputParameter();
		inputParameter.setAssetId((new ConfigurationManager(context)).getAssetId());
		inputParameter.setReportDate(date);
		new UpdateMapTask(context, listener).execute(inputParameter);
	}
	
	public void removerReportMarkersAndLineFromMap() {
		map.clear();
	}
	
	public void addReportMarkersAndLineToMap() {
		PolylineOptions locationsLine = new PolylineOptions();
		LatLng point;
		SimpleDateFormat formatter = new SimpleDateFormat("kk:mm EEEE, d MMMM yyyy", Locale.getDefault());
		for(ReportData report: reports) {
			point = new LatLng(report.getLatitude(), report.getLongitude());
			map.addMarker(new MarkerOptions()
	        	.position(point)
	        	.title(formatter.format(report.getCreated())));
			locationsLine.add(point);
		}
		map.addPolyline(locationsLine);
	}
	
	public void moveToReportOrDefaultLocation() {
		LatLng mapFirstReportLocation = findFirstReportOrDefaultLocation();
		if(mapFirstReportLocation != null) {
			moveMapToLocationAndZoom(mapFirstReportLocation, Settings.MAP.LOCATION_ZOOM);	
		}
	}
	
	private LatLng findFirstReportOrDefaultLocation() {
		if(reports.size() > 0) {
			ReportData firstReport = reports.get(0);
			return new LatLng(firstReport.getLatitude(), firstReport.getLongitude());
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
		}
		return getDateForDayIndex();
	}
	
	public Date getDateForPreviousDay() {
		dayIndex--;	
		return getDateForDayIndex();
	}
}
