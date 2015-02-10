package eu.appbucket.rothar.ui;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.ui.listener.RepordDayChangeListener;
import eu.appbucket.rothar.ui.listener.ReportUpdateListener;
import eu.appbucket.rothar.ui.listener.TagUpdateListener;
import eu.appbucket.rothar.ui.manager.MapManager;
import eu.appbucket.rothar.ui.manager.TagManager;
import eu.appbucket.rothar.web.domain.asset.AssetData;
import eu.appbucket.rothar.web.domain.asset.AssetStatus;
import eu.appbucket.rothar.web.domain.report.ReportData;

public class MapActivity extends Activity 
	implements OnMapReadyCallback, ReportUpdateListener, TagUpdateListener, RepordDayChangeListener {

	private MapManager mapManager;
	private TagManager tagManager;
	private ProgressDialog progress;
	private boolean nextDayMenuIconEnabled = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_map);
    	prepareProgressDialog();
    	mapManager = new MapManager(this, this, this);
        tagManager = new TagManager(this, this);
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
			message = "Bike found on " + formatterReportDate;
		} else {
			message = "Bike not found on " + formatterReportDate;
		}
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		if(tagManager.isBikeStolen()) {
			menu.findItem(R.id.mark_stolen).setVisible(false);
			menu.findItem(R.id.mark_recovered).setVisible(true);
		} else {
			menu.findItem(R.id.mark_stolen).setVisible(true);
			menu.findItem(R.id.mark_recovered).setVisible(false);
		}
		if(nextDayMenuIconEnabled) {
			menu.findItem(R.id.action_next_day).setEnabled(true);
			menu.findItem(R.id.action_next_day).getIcon().setAlpha(255);
		} else {
			menu.findItem(R.id.action_next_day).setEnabled(false);
			menu.findItem(R.id.action_next_day).getIcon().setAlpha(64);
		}
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
		} else if (id == R.id.about) {
			startAboutActivity();
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
	
	/*private void startAboutActivity() {
		Intent intent = new Intent(this, AboutActivity.class);
		this.startActivity(intent);
	}*/
	
	private void startAboutActivity() {
		Intent intent = new Intent(this, SettingsActivity.class);
		this.startActivity(intent);
	}
	
	@Override
	public void onTagUpdateFailure(String cause) {
		Toast.makeText(this, "Bike status update failed: " + cause, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onTagUpdateSuccess(AssetData asset) {
		AssetStatus bikeStatus = asset.getStatus();
		new ConfigurationManager(this).setAssetStatus(bikeStatus);
		if(bikeStatus == AssetStatus.STOLEN) {
			bikeStatus = AssetStatus.STOLEN;
			invalidateOptionsMenu();
			showBikeStolenDialog();
		} else {
			bikeStatus = AssetStatus.RECOVERED;
			invalidateOptionsMenu();
			showBikeFoundDialog();
		}		
	}
	
	private void showBikeStolenDialog() {
		showTagStatusChangedDialog(R.string.marked_stolen_dialog_title, R.string.marked_stolen_dialog_message);
	}
	
	private void showTagStatusChangedDialog(int titleId, int messageId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(titleId).setMessage(messageId);
	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {}
	       });
	    AlertDialog dialog = builder.create();
	    dialog.show();
	}
	
	private void showBikeFoundDialog() {
		showTagStatusChangedDialog(R.string.marked_found_dialog_title, R.string.marked_found_dialog_message);
	}

	@Override
	public void onUpdate(int dayIndex) {
		if(isCurrentDayIndex(dayIndex)) {
			nextDayMenuIconEnabled = false;
		} else {
			nextDayMenuIconEnabled = true;	
		}
		invalidateOptionsMenu();
	}	
	
	private boolean isCurrentDayIndex(int index) {
		if(index == 0) {
			return true;
		}
		return false;
	}
}
