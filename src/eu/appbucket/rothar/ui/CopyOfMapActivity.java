package eu.appbucket.rothar.ui;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import eu.appbucket.rothar.R;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.ui.listener.MapUpdateListener;
import eu.appbucket.rothar.ui.task.UpdateMapTask;
import eu.appbucket.rothar.web.domain.report.ReportData;

public class CopyOfMapActivity extends FragmentActivity implements OnMapReadyCallback, MapUpdateListener {

	private List<ReportData> reports;
	private GoogleMap map;
	private int dayIndex = 0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isActivityFirstTimeCreated(savedInstanceState)) {
        	addMapToView();	
        } else {
        	recycleMapFromPreviousActivityLifeCycle();
        }
    }

    private boolean isActivityFirstTimeCreated(Bundle savedInstanceState) {
    	if(savedInstanceState == null) {
    		return true;
    	}
    	return false;
    }
    
    private void recycleMapFromPreviousActivityLifeCycle() {
    	MapFragment mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
    	map = mMapFragment.getMap();
    }
    
	private void loadBicycleReportsForToday() {
		this.loadBicycleReportsForDay(getDateForToday());
	}
	
	private Date getDateForToday() {
		dayIndex = 0;
		return getDateForDayIndex();
	}
	
	private Date getDateForDayIndex() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, dayIndex);
		return cal.getTime();
	}
	
	private Date getDateForNextDay() {
		if(dayIndex < 0) {
			dayIndex++;	
		}
		return getDateForDayIndex();
	}
	
	private Date getDateForPreviousDay() {
		dayIndex--;	
		return getDateForDayIndex();
	}
	
	public void loadBicycleReportsForDay(Date date) {
		UpdateMapTask.InputParameter inputParameter = new UpdateMapTask.InputParameter();
		inputParameter.setAssetId(37);
		inputParameter.setReportDate(date);
		new UpdateMapTask(this, this).execute(inputParameter);
	}
	
	public void onMapReportUpdateSuccess(List<ReportData> reports) {
		this.reports = reports;
		showReportInformation();
		removerReportMarkersAndLineFromMap();
		addReportMarkersAndLineToMap();
		LatLng mapCenterLocation = findFirstReportOrDefaultLocation();
		moveToMapLocation(mapCenterLocation);
	}
	
	private void showReportInformation() {
		Date reportDate = getDateForDayIndex();
		SimpleDateFormat formatter = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
		String message;
		String formatterReportDate = formatter.format(reportDate);
		if(reports.size() > 0) {
			message = "Found " + reports.size() + " report(s) for: " + formatterReportDate;
		} else {
			message = "No reports found for: " + formatterReportDate;
		}
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onMapReportUpdateFailure() {
		showFailureInformation();	
	}
	
	private void showFailureInformation() {
		Date reportDate = getDateForDayIndex();
		SimpleDateFormat formatter = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
		String formatterReportDate = formatter.format(reportDate);
		String message = "Can't retrieve report for: " + formatterReportDate;
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	private void moveToMapLocation(LatLng mapLocation) {
		CameraPosition cameraPosition = CameraPosition.builder().target(mapLocation).zoom(Settings.MAP.DEFAULT_ZOOM).build();
		map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	private LatLng findFirstReportOrDefaultLocation() {
		if(reports.size() > 0) {
			ReportData firstReport = reports.get(0);
			return new LatLng(firstReport.getLatitude(), firstReport.getLongitude());
		} else {
			return Settings.MAP.DEFAULT_LOCATION;
		}
	}
    
    private void addMapToView() {
    	GoogleMapOptions mapSettings = buildDefaultMapSettings();
    	createMapFragmentWithSettings(mapSettings);
    }
    
    private GoogleMapOptions buildDefaultMapSettings() {
    	GoogleMapOptions options = new GoogleMapOptions();
    	CameraPosition camera = CameraPosition.fromLatLngZoom(Settings.MAP.DEFAULT_LOCATION, Settings.MAP.DEFAULT_ZOOM);
    	options.camera(camera);
    	return options;
    }
    
    private void createMapFragmentWithSettings(GoogleMapOptions options) {
    	MapFragment mMapFragment = MapFragment.newInstance(options);
    	mMapFragment.setRetainInstance(true);
		mMapFragment.getMapAsync(this);
		FragmentTransaction fragmentTransaction =
		         getFragmentManager().beginTransaction();
		fragmentTransaction.remove(mMapFragment);
		fragmentTransaction.add(R.id.map_fragment, mMapFragment);
		fragmentTransaction.commit();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tag, menu);
		return true;
	}
	
	@Override
	public void onMapReady(GoogleMap map) {
		this.map = map;
		setMapSettings();
		loadBicycleReportsForToday();
	}
	
	private void setMapSettings() {
		map.getUiSettings().setMapToolbarEnabled(false);
	}
	
	private void removerReportMarkersAndLineFromMap() {
		map.clear();
	}

	private void addReportMarkersAndLineToMap() {
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
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_next_day) {
			loadBicycleReportsForNextDay();
			return true;
		} else if (id == R.id.action_previous_day) {
			loadBicycleReportsForPreviousDay();
			return true;
		} else if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void loadBicycleReportsForNextDay() {
		Date nextDay = getDateForNextDay();
		loadBicycleReportsForDay(nextDay);
	}
	
	private void loadBicycleReportsForPreviousDay() {
		Date previousDay = getDateForPreviousDay();
		loadBicycleReportsForDay(previousDay);
	}
}
