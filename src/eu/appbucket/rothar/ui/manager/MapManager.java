package eu.appbucket.rothar.ui.manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.ui.listener.MapUpdateListener;
import eu.appbucket.rothar.ui.task.UpdateMapTask;
import eu.appbucket.rothar.web.domain.report.ReportData;

import android.content.Context;
import android.widget.Toast;

public class MapManager {
	
	private Context context;
	private GoogleMap map;
	private MapUpdateListener listener;
	private List<ReportData> reports;
	private int dayIndex = 0;
	
	public MapManager(Context context, MapUpdateListener listener) {
		this.context = context;
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
	
    public GoogleMapOptions buildDefaultMapSettings() {
    	GoogleMapOptions options = new GoogleMapOptions();
    	CameraPosition camera = CameraPosition.fromLatLngZoom(Settings.MAP.DEFAULT_LOCATION, Settings.MAP.DEFAULT_ZOOM);
    	options.camera(camera);
    	return options;
    }
    
    public void loadMapSettings() {
		map.getUiSettings().setMapToolbarEnabled(false);
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
	
	public void loadBicycleReportsForDay(Date date) {
		UpdateMapTask.InputParameter inputParameter = new UpdateMapTask.InputParameter();
		inputParameter.setAssetId(37);
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
		LatLng mapCenterLocation = findFirstReportOrDefaultLocation();
		moveToMapLocation(mapCenterLocation);
	}
	
	private LatLng findFirstReportOrDefaultLocation() {
		if(reports.size() > 0) {
			ReportData firstReport = reports.get(0);
			return new LatLng(firstReport.getLatitude(), firstReport.getLongitude());
		} else {
			return Settings.MAP.DEFAULT_LOCATION;
		}
	}
	
	private void moveToMapLocation(LatLng mapLocation) {
		CameraPosition cameraPosition = CameraPosition.builder().target(mapLocation).zoom(Settings.MAP.DEFAULT_ZOOM).build();
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
