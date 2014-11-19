package eu.appbucket.monitor;

public class Settings {
	
	public final static String SERVER_URL = "http://api.dev.rothar.appbucket.eu"; // development machine
	
	public final class IBEACON {
		public final static String IBEACON_UUID = "372295A7-CA90-6A84-7A29-5F472E4F7206";
		public final static int IBEACON_MAJOR = 372;	
	}
	
	public final class MONITOR {
		public final static long SEARCH_DURATION = 10 * 1000;
		public final static long SEARCH_FREQUENCY = 30 * 1000;	
	}
	

	public final class UPDATER {
		public final static long UPDATE_FREQUENCY = 60 * 1000;	
	}
}
