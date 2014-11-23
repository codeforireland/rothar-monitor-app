package eu.appbucket.monitor;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class ConfigurationManager {
	
	private Context context;
	
	public ConfigurationManager(Context context) {
		this.context = context;
	}
	
	public void enableNotifications() {
		Editor editor = context.getSharedPreferences(Settings.PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
		editor.putBoolean(Settings.SHOW_NOTIFICATIONS_PREF_NAME, true);
		editor.commit();
	}
	
	public void disableNotifications() {
		Editor editor = context.getSharedPreferences(Settings.PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
		editor.putBoolean(Settings.SHOW_NOTIFICATIONS_PREF_NAME, false);
		editor.commit();
	}
	
	public boolean isNotificationEnabled() {
		boolean showNotifcationsEnabled = 
				context
					.getSharedPreferences(Settings.PREFERENCES_NAME, Context.MODE_PRIVATE)
					.getBoolean(
							Settings.SHOW_NOTIFICATIONS_PREF_NAME, 
							Settings.SHOW_NOTIFICATIONS_DEFAULT_VALUE);
		return showNotifcationsEnabled;
	}
}