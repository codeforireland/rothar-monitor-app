package eu.appbucket.rothar.common;

@SuppressWarnings("unused")
public class Settings {
	
	public final static String SERVER_URL = "http://api.rothar.appbucket.eu";
	public final static String DONATION_URL = 
			"https://www.paypal.com/cgi-bin/webscr" +
			"?cmd=_donations&business=ZTA92F5X9P6A2&lc=IE&item_name=Rothar" +
			"&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted";
	
	public final class SYSTEM_IBEACON {
		public final static String UUID = "372295A7-CA90-6A84-7A29-5F472E4F7206";
		public final static int MAJOR = 372;	
	}
	
	public static final int ONE_SECOND = 1000;
	public static final int ONE_MINUTE = 60 * 1000;
	public static final int ONE_HOUR = 60 * ONE_MINUTE;
	
	public final static class START_TASK {
		public final static long FREQUENCY = 24 * ONE_HOUR;
	}
	
	public final static class STOP_TASK {
		public final static long FREQUENCY = 24 * ONE_HOUR;
	}
	
	public final static class MONITOR_TASK {
		public final static long DURATION = 10 * ONE_SECOND;
		private final static long FREQUENCY_PROD = 1 * ONE_MINUTE;
		private final static long FREQUENCY_DEV = 30 * ONE_SECOND;
		public final static long FREQUENCY = FREQUENCY_PROD;
	}
	
	public final static class UPDATER_TASK {
		private final static long FREQUENCY_PROD = 6 * ONE_HOUR;
		private final static long FREQUENCY_DEV = 1 * ONE_MINUTE;
		public final static long FREQUENCY = FREQUENCY_PROD;
	}
	
	public final static class REPORTER_TASK {
		public final static long DURATION = 10 * ONE_SECOND;
	}
	
	public final static String PREFERENCES_NAME = "rothar-preferences";
	
	public final static String SHOW_NOTIFICATIONS_PREF_NAME = "show-notifications";
	public final static boolean SHOW_NOTIFICATIONS_DEFAULT_VALUE = false;
	
	public final static String APPLICATION_UUID_PREF_NAME = "app-uuid";
	
	public final static String ASSET_ID_PREF_NAME = "asset-id";
	public final static String ASSET_STATUS_PREF_NAME = "asset-status";
	
	public final static class MAP {
		public final static float LOCATION_ZOOM = 17;
	}
	
}
