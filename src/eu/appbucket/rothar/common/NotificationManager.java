package eu.appbucket.rothar.common;

import android.content.Context;
import android.widget.Toast;

public class NotificationManager {
	
	private Context context;
	private ConfigurationManager configuration;
	
	public NotificationManager(Context context) {
		this.context = context;
		 configuration = new ConfigurationManager(context);
	}
	
	public void showNotification(String notification) {
		if(configuration.isNotificationEnabled()) {
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, notification, duration);
			toast.show();	
		}
	}
}
