package eu.appbucket.rothar.common;

import java.util.UUID;

import eu.appbucket.rothar.web.domain.asset.AssetStatus;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class ConfigurationManager {
	
	private Context context;
	
	public ConfigurationManager(Context context) {
		this.context = context;
	}
	
	private int readIntiger(String nameOfItem, int defaultValue) {
		int value = context.getSharedPreferences(Settings.PREFERENCES_NAME, Context.MODE_PRIVATE)
				.getInt(nameOfItem, defaultValue);
		return value;		
	}

	private void saveInteger(String nameOfItem, int valueToSave) {
		Editor editor = context.getSharedPreferences(Settings.PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
		editor.putInt(nameOfItem, valueToSave);
		editor.commit();
	}
	
	private boolean readBoolean(String nameOfItem, boolean defaultValu) {
		boolean value = 
				context
					.getSharedPreferences(Settings.PREFERENCES_NAME, Context.MODE_PRIVATE)
					.getBoolean(
							nameOfItem, 
							defaultValu);
		return value;
	}
	
	private void saveBoolean(String nameOfItem, boolean valueToSave) {
		Editor editor = context.getSharedPreferences(Settings.PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
		editor.putBoolean(nameOfItem, valueToSave);
		editor.commit();
	}

	private String readString(String nameOfItem, String defaultValue) {
		String value = context.getSharedPreferences(Settings.PREFERENCES_NAME, Context.MODE_PRIVATE)
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
		Editor editor = context.getSharedPreferences(Settings.PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
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
}
