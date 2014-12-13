package eu.appbucket.rothar.ui;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.util.LangUtils;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

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
import eu.appbucket.rothar.ui.task.UpdateMapTask;
import eu.appbucket.rothar.web.domain.report.ReportData;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, MapUpdateListener {

	private List<ReportData> reports;
	private GoogleMap map;
	private int dayIndex;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadBicycleReportsForToday();
    }

	private void loadBicycleReportsForToday() {
		this.loadBicycleReportsForDay(getDateForToday());
	}
	
	private Date getDateForToday() {
		dayIndex = 0;
		return null;
	}
	
	private Date getDateForDayIndex() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, dayIndex);
		return cal.getTime();
	}
	
	private Date getDateForNextDay() {
		dayIndex++;
		return getDateForDayIndex();
	}
	
	private Date getDateForPreviousDay() {
		dayIndex--;
		return getDateForDayIndex();
	}
	
	public void loadBicycleReportsForDay(Date date) {
		new UpdateMapTask(this).equals(date);
	}
	
	public void onMapReportUpdate(List<ReportData> reports) {
		this.reports = reports;
		LatLng mapCenterLocation = findFirstReportOrDefaultLocation();
		createMapAtLocation(mapCenterLocation);
	}
	
	private LatLng findFirstReportOrDefaultLocation() {
		if(reports.size() > 0) {
			ReportData firstReport = reports.get(0);
			return new LatLng(firstReport.getLatitude(), firstReport.getLongitude());
		} else {
			return Settings.MAP_DEFAULT_LOCATION;
		}
	}
	
    private void createMapAtLocation(LatLng mapLocation) {
    	GoogleMapOptions options = new GoogleMapOptions();
    	ReportData firstReport = reports.get(0);
    	LatLng target = new LatLng(firstReport.getLatitude(), firstReport.getLongitude());
    	float zoom = 17;
    	CameraPosition camera = CameraPosition.fromLatLngZoom(target, zoom);
    	options.camera(camera);
		MapFragment mMapFragment = MapFragment.newInstance(options);
		mMapFragment.getMapAsync(this);
		FragmentTransaction fragmentTransaction =
		         getFragmentManager().beginTransaction();
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
		addReportMarkersOnMap();
	}
	
	private void addReportMarkersOnMap() {
		PolylineOptions locationsLine = new PolylineOptions();
		LatLng point;
		for(ReportData report: reports) {
			point = new LatLng(report.getLatitude(), report.getLongitude());
			map.addMarker(new MarkerOptions()
	        	.position(point)
	        	.title(report.getCreated().toString()));
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
