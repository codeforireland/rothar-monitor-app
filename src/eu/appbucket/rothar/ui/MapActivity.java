package eu.appbucket.rothar.ui;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;

import eu.appbucket.rothar.R;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.ui.listener.MapUpdateListener;
import eu.appbucket.rothar.ui.manager.MapManager;
import eu.appbucket.rothar.web.domain.report.ReportData;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, MapUpdateListener {

	private MapManager mapManager;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapManager = new MapManager(this, this);
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
    
    private void addMapToView() {
    	GoogleMapOptions mapSettings = mapManager.buildDefaultMapSettings();
    	createMapFragmentWithSettings(mapSettings);
    }
    
    private void recycleMapFromPreviousActivityLifeCycle() {
    	MapFragment mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
    	mapManager.setMap(mMapFragment.getMap());
    }
	
	public void onMapReportUpdateSuccess(List<ReportData> reports) {
		mapManager.setReports(reports);
		showReportInformation();
		mapManager.removerReportMarkersAndLineFromMap();
		mapManager.addReportMarkersAndLineToMap();
		mapManager.moveToReportOrDefaultLocation();
	}
	
	@Override
	public void onMapReportUpdateFailure() {
		showFailureInformation();	
	}
	
	private void showFailureInformation() {
		Date reportDate = mapManager.getDateForDayIndex();
		SimpleDateFormat formatter = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
		String formatterReportDate = formatter.format(reportDate);
		String message = "Can't retrieve report for: " + formatterReportDate;
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
    
	public void showReportInformation() {
		Date reportDate = mapManager.getDateForDayIndex();
		SimpleDateFormat formatter = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
		String message;
		String formatterReportDate = formatter.format(reportDate);
		if(mapManager.constainsReportsForCurrentDay()) {
			message = "Found report(s) for: " + formatterReportDate;
		} else {
			message = "No reports found for: " + formatterReportDate;
		}
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
		mapManager.setMap(map);
		mapManager.loadMapSettings();
		mapManager.loadBicycleReportsForToday();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_next_day) {
			mapManager.loadBicycleReportsForNextDay();
			return true;
		} else if (id == R.id.action_previous_day) {
			mapManager.loadBicycleReportsForPreviousDay();
			return true;
		} else if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
