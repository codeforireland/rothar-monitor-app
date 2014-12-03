package eu.appbucket.rothar.monitor.update;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import eu.appbucket.rothar.monitor.monitor.BikeBeacon;

public class StolenBikeDao {
	
	public void addStolenBikeRecord(Context context, BikeBeacon record) {
		StolenBikeDbHelper dbHelper = new StolenBikeDbHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(StolenBikeDbHelper.COLUMN_NAME_ASSET_ID, record.getAssetId());				
		values.put(StolenBikeDbHelper.COLUMN_NAME_UUID, record.getUudi());
		values.put(StolenBikeDbHelper.COLUMN_NAME_MAJOR, record.getMajor());
		values.put(StolenBikeDbHelper.COLUMN_NAME_MINOR, record.getMinor());
		try {
			db.insert(
					StolenBikeDbHelper.TABLE_NAME,
					null,
			        values);	
		} finally {
			db.close();
		}
		
	}
	
	public void resetStolenBikes(Context context) {
		StolenBikeDbHelper dbHelper = new StolenBikeDbHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.delete(StolenBikeDbHelper.TABLE_NAME, null, null);	
		} finally {
			db.close();
		}
		
	}
	
	public BikeBeacon findBikeRecordByIdentity(Context context, String uuid, int major, int minor) {
		BikeBeacon record = new BikeBeacon();
		StolenBikeDbHelper dbHelper = new StolenBikeDbHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String[] projection = {
			    StolenBikeDbHelper.COLUMN_NAME_ASSET_ID,
			    StolenBikeDbHelper.COLUMN_NAME_UUID,
			    StolenBikeDbHelper.COLUMN_NAME_MAJOR,
			    StolenBikeDbHelper.COLUMN_NAME_MINOR
		};
		String selection = 
				StolenBikeDbHelper.COLUMN_NAME_UUID + " like ? AND " +
				StolenBikeDbHelper.COLUMN_NAME_MAJOR + " = ? AND " +
				StolenBikeDbHelper.COLUMN_NAME_MINOR + " = ? ";
		String[] selectionArgs = {
				uuid,
				Integer.toString(major),
				Integer.toString(minor)};
		Cursor cursor = null;
		try {
			cursor = db.query(
					StolenBikeDbHelper.TABLE_NAME,	// The table to query
				    projection, 					// The columns to return
				    selection, 						// A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). 
				    								// Passing null will return all rows for the given table.
				    selectionArgs, 					// The values for the WHERE clause
				    "", 							// don't group the rows
				    "", 							// don't filter by row groups
				    "" 								// The sort order
			);
			cursor.moveToFirst();
			if(cursor.getCount() > 0) {
				record.setAssetId(cursor.getInt(
						cursor.getColumnIndexOrThrow(StolenBikeDbHelper.COLUMN_NAME_ASSET_ID)));
				record.setUudi(cursor.getString(
						cursor.getColumnIndexOrThrow(StolenBikeDbHelper.COLUMN_NAME_UUID)));
				record.setMajor(cursor.getInt(
						cursor.getColumnIndexOrThrow(StolenBikeDbHelper.COLUMN_NAME_MAJOR)));
				record.setMinor(cursor.getInt(
						cursor.getColumnIndexOrThrow(StolenBikeDbHelper.COLUMN_NAME_MINOR)));
			}	
		} finally {
			if(cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			db.close();
		}
		return record;
	}
}		
