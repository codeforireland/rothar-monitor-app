package eu.appbucket.rothar.ui.task;

import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import eu.appbucket.rothar.R;
import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.ui.MapActivity;
import eu.appbucket.rothar.ui.listener.ReportUpdateListener;
import eu.appbucket.rothar.web.domain.report.ReportData;

public class ReportNotificationTask extends BroadcastReceiver implements ReportUpdateListener {
	private Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		Integer assetId = new ConfigurationManager(context).getAssetId();
		if(assetId == null) {
			return;
		}
		findReportsForToday();
	}

	private void findReportsForToday() {
		ReportUpdateTask.InputParameter inputParameter = new ReportUpdateTask.InputParameter();
		inputParameter.setAssetId((new ConfigurationManager(context)).getAssetId());
		Date today = new Date();
		inputParameter.setReportDate(today);
		new ReportUpdateTask(context, this).execute(inputParameter);
	}
	
	@Override
	public void onReportUpdateSuccess(List<ReportData> reports) {
		if(reports.isEmpty()) {
			return;
		}
		showNotification(buildNotification(reports.size()));
	}

	private Notification buildNotification(int numberOfReports) {

		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("Found your bike !")
		        .setContentText(numberOfReports + " locations of your stolen bike.")
				.setContentIntent(buildMapActivityPendingIntent())
				.setAutoCancel(true);
		return mBuilder.build();
	}
	
	private PendingIntent buildMapActivityPendingIntent() {
		Intent startMapActivity = new Intent(context, MapActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , startMapActivity, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}
	
	private void showNotification(Notification notificationToShow) {
		NotificationManager mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(0, notificationToShow);
	}
	
	@Override
	public void onReportUpdateFailure() {
	}
}
