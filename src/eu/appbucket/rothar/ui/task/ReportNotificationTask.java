package eu.appbucket.rothar.ui.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import eu.appbucket.rothar.R;
import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.ui.MapActivity;

public class ReportNotificationTask extends BroadcastReceiver {
	private Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		Integer assetId = new ConfigurationManager(context).getAssetId();
		if(assetId == null) {
			return;
		}
		new ReportsChecker(assetId).execute();
	}
	
	private class ReportsChecker extends AsyncTask<Void, Void, Boolean> {

		private int assetId;
		
		public ReportsChecker(int assetId) {
			this.assetId = assetId;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			
			// TODO Auto-generated method stub
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean reportsExists) {
			if(reportsExists == false) {
				return;
			}
			showNotification(buildNotification());
		}
		
		private Notification buildNotification() {
			Intent startMapActivity = new Intent(context, MapActivity.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0 , startMapActivity, PendingIntent.FLAG_UPDATE_CURRENT);
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(context)
			        .setSmallIcon(R.drawable.ic_launcher)
			        .setContentTitle("Found your bike !")
			        .setContentText("Reports found showing location of your stolen bike.")
					.setContentIntent(pendingIntent);
			return mBuilder.build();
		}
		
		private void showNotification(Notification notificationToShow) {
			NotificationManager mNotificationManager =
				    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(0, notificationToShow);
		}
	}
}
