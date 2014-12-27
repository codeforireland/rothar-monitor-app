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

	private GoogleMap map;
	
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
    
    private void addMapToView() {
    	GoogleMapOptions mapSettings = buildDefaultMapSettings();
    	createMapFragmentWithSettings(mapSettings);
    }
    
    private void recycleMapFromPreviousActivityLifeCycle() {
    	MapFragment mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
    	map = mMapFragment.getMap();
    }
	
	public void onMapReportUpdateSuccess(List<ReportData> reports) {
		this.reports = reports;
		new MapManager(this, this).showReportInformation();
		new MapManager(this, this).removerReportMarkersAndLineFromMap();
		new MapManager(this, this).addReportMarkersAndLineToMap();
		new MapManager(this, this).moveToReportOrDefaultLocation();
	}
	
	@Override
	public void onMapReportUpdateFailure() {
		showFailureInformation();	
	}
	
	private void showFailureInformation() {
		Date reportDate = new MapManager(this, this).getDateForDayIndex();
		SimpleDateFormat formatter = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
		String formatterReportDate = formatter.format(reportDate);
		String message = "Can't retrieve report for: " + formatterReportDate;
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
		new MapManager(this, this).loadBicycleReportsForToday();
	}
	
	private void setMapSettings() {
		map.getUiSettings().setMapToolbarEnabled(false);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_next_day) {
			new MapManager(this, this).loadBicycleReportsForNextDay();
			return true;
		} else if (id == R.id.action_previous_day) {
			new MapManager(this, this).loadBicycleReportsForPreviousDay();
			return true;
		} else if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
