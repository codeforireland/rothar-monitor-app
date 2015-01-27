package eu.appbucket.rothar.ui;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import eu.appbucket.rothar.R;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.ui.listener.ReportUpdateListener;
import eu.appbucket.rothar.ui.manager.MapManager;
import eu.appbucket.rothar.ui.manager.TagManager;
import eu.appbucket.rothar.web.domain.report.ReportData;

public class MapActivity extends Activity implements OnMapReadyCallback, ReportUpdateListener {

	private MapManager mapManager;
	private TagManager tagManager;
	private ProgressDialog progress;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_map);
    	prepareProgressDialog();
    	mapManager = new MapManager(this, this);
        tagManager = new TagManager(this);
        linkActivityAsMapListener();
    }
    
    private void prepareProgressDialog() {
    	progress = new ProgressDialog(this);
    	progress.setIndeterminate(true);
		progress.setMessage("Searching reports ...");
		progress.setCancelable(false);
    }
    
    private void linkActivityAsMapListener() {
    	MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
    	mapFragment.getMapAsync(MapActivity.this);
    }
    
    @Override
	public void onMapReady(GoogleMap map) {
    	disableUserInteraction();
    	mapManager.setMap(map);
		mapManager.loadBicycleReportsForToday();
	}
    
	public void onReportUpdateSuccess(List<ReportData> reports) {
		mapManager.setReports(reports);
		mapManager.removerReportMarkersAndLineFromMap();
		mapManager.addReportMarkersAndLineToMap();
		mapManager.moveToReportOrDefaultLocation();
		showReportInformation();
		enableUserInteraction();
	}
	
	@Override
	public void onReportUpdateFailure() {
		showFailureInformation();
		enableUserInteraction();
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
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_next_day) {
			disableUserInteraction();
			mapManager.loadBicycleReportsForNextDay();
			return true;
		} else if (id == R.id.action_previous_day) {
			disableUserInteraction();
			mapManager.loadBicycleReportsForPreviousDay();
			return true;
		} else if (id == R.id.mark_stolen) {
			tagManager.updateBikeMarkStolen();
			return true;
		} else if (id == R.id.mark_recovered) {
			tagManager.updateBikeMarkRecovered();
			return true;
		} else if (id == R.id.mark_recovered) {
			tagManager.updateBikeMarkRecovered();
			return true;
		} else if (id == R.id.donate) {
			openDonationPage();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void disableUserInteraction() {
		progress.show();
	}
	
	private void enableUserInteraction() {
		progress.dismiss();
	}
	
	private void openDonationPage() {
		Uri uri = Uri.parse(Settings.DONATION_URL);
		startActivity(new Intent(Intent.ACTION_VIEW, uri));
	}
}
