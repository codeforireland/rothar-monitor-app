package eu.appbucket.monitor.monitor;

import android.util.Log;

public class BeaconRecordParser {
	
	private int startByte = 2;
	private byte[] record;
	private static final String DEBUG_TAG = "BeaconRecordParser";
	private boolean isValidRecord = false;
	
	public BeaconRecordParser(byte[] record) {
		this.record = record;
	}
	
	public boolean isRecordValid() {
		while (startByte <= 5) {
			if (((int) record[startByte + 2] & 0xff) == 0x02 && 
				((int) record[startByte + 3] & 0xff) == 0x15) { 
				isValidRecord = true;
				break;
			}
			startByte++;
		}
		return isValidRecord;
	}
	
	public BikeBeacon parserRecordToBeacon() {
		if(isValidRecord == false)
			return null;
		// Convert to hex String
		byte[] uuidBytes = new byte[16];
		System.arraycopy(record, startByte + 4, uuidBytes, 0,
				16);
		String hexString = bytesToHex(uuidBytes);
		// Here is your UUID
		String uuid = hexString.substring(0, 8) + "-"
				+ hexString.substring(8, 12) + "-"
				+ hexString.substring(12, 16) + "-"
				+ hexString.substring(16, 20) + "-"
				+ hexString.substring(20, 32);
		// Here is your Major value
		int major = (record[startByte + 20] & 0xff) * 0x100
				+ (record[startByte + 21] & 0xff);
		// Here is your Minor value
		int minor = (record[startByte + 22] & 0xff) * 0x100
				+ (record[startByte + 23] & 0xff);
		Log.d(DEBUG_TAG, "Tag, uuid: " + uuid + ", major: " + major + ", minor " + minor);
		BikeBeacon record = new BikeBeacon(uuid, major, minor);
		return record;
	}
	
	static final char[] hexArray = "0123456789ABCDEF".toCharArray();

	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
