package eu.appbucket.rothar.common;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;
import eu.appbucket.rothar.web.domain.asset.AssetStatus;

public class ConfigurationManager {
	
	private Context context;
	
	public ConfigurationManager(Context context) {
		this.context = context;
	}
	
	private int readIntiger(String nameOfItem, int defaultValue) {
		int value = getPreferences().getInt(nameOfItem, defaultValue);
		return value;		
	}

	private SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	private void saveInteger(String nameOfItem, int valueToSave) {
		Editor editor = getPreferences().edit();
		editor.putInt(nameOfItem, valueToSave);
		editor.commit();
	}
	
	private boolean readBoolean(String nameOfItem, boolean defaultValu) {
		boolean value = 
				getPreferences()
					.getBoolean(
							nameOfItem, 
							defaultValu);
		return value;
	}
	
	private void saveBoolean(String nameOfItem, boolean valueToSave) {
		Editor editor = getPreferences().edit();
		editor.putBoolean(nameOfItem, valueToSave);
		editor.commit();
	}

	private String readString(String nameOfItem, String defaultValue) {
		String value = getPreferences()
				.getString(nameOfItem, defaultValue);
		return value;		
	}
	
	public void enableNotifications() {
		saveBoolean(Settings.SHOW_NOTIFICATIONS_PREF_NAME, true);
	}
	
	public void disableNotifications() {
		saveBoolean(Settings.SHOW_NOTIFICATIONS_PREF_NAME, false);
	}
	
	public boolean isNotificationEnabled() {
		boolean showNotifcationsEnabled = readBoolean(
							Settings.SHOW_NOTIFICATIONS_PREF_NAME, 
							Settings.SHOW_NOTIFICATIONS_DEFAULT_VALUE);
		return showNotifcationsEnabled;
	}
		
	public String getApplicationUuid() {
		String applicationUuid = readString(Settings.APPLICATION_UUID_PREF_NAME, null);
		if(applicationUuid != null) {
			return applicationUuid;
		}
		applicationUuid = generateApplicationUuid();
		saveApplicationUuid(applicationUuid);
		return applicationUuid;
	}
	
	public String generateApplicationUuid() {
		return UUID.randomUUID().toString().toUpperCase();
	}
	
	public void saveApplicationUuid(String appUuid) {
		Editor editor = getPreferences().edit();
		editor.putString(Settings.APPLICATION_UUID_PREF_NAME, appUuid);
		editor.commit();
	}

	public void saveAssetId(int assetId) {
		saveInteger(Settings.ASSET_ID_PREF_NAME, assetId);
	}
	
	public Integer getAssetId() {
		int value = readIntiger(Settings.ASSET_ID_PREF_NAME, -1);
		return (value == -1 ? null : value);
	}
	
	public AssetStatus getAssetStatus() {
		int statusId = readIntiger(Settings.ASSET_STATUS_PREF_NAME, -1);
		return AssetStatus.getStatusEnumById(statusId);
	}
	
	public void setAssetStatus(AssetStatus status) {
		int statusId = status.getStatusId();
		saveInteger(Settings.ASSET_STATUS_PREF_NAME, statusId);
	}
	
	public boolean isBluetoothLeCapable() {
		if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && Build.VERSION.RELEASE.equalsIgnoreCase("4.4.4")) {
			return true;
		}
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
			return true;
		}
		return false;
	}
}
