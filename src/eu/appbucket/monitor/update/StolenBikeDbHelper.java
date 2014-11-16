package eu.appbucket.monitor.update;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StolenBikeDbHelper extends SQLiteOpenHelper {
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "StoleBikes.db";
    public static final String TABLE_NAME = "records";
    private static final String INTEGER_TYPE = " INTEGER";
    public static final String COLUMN_NAME_ASSET_ID = "asset_id";
    public static final String COLUMN_NAME_MAJOR = "major";
    
    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + TABLE_NAME + " (" +
        COLUMN_NAME_ASSET_ID + INTEGER_TYPE + "," +
        COLUMN_NAME_MAJOR + INTEGER_TYPE + ")";

    private static final String SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + TABLE_NAME;
    
    public StolenBikeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
