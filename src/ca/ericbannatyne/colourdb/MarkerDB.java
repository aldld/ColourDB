package ca.ericbannatyne.colourdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Singleton implementation of a collection of markers.
 */
public class MarkerDB {
	
	public static final String TAG = "MarkerDB";
	
	private static MarkerDB instance = null;
	
	private MarkerDBHelper dbHelper;
	private SQLiteDatabase db;
	
	private Marker[] allMarkers;
	
	private MarkerDB(Context context) {
		dbHelper = new MarkerDBHelper(context);
		Log.d(TAG, "dbHelper created");
		db = dbHelper.getWritableDatabase();
		Log.d(TAG, "getWritableDatabase called");
		
		String[] projection = { "id", "code", "family", "name", "color",
				"wantIt", "haveIt", "needsRefill" };
		
		Cursor c = db.query("marker", projection, null, null, null, null, "id ASC");
		allMarkers = new Marker[c.getCount()];
		//c.moveToFirst();
		int i = 0;
		while (c.moveToNext()) {
			allMarkers[i] = new Marker(this, c.getInt(c.getColumnIndex("id")),
					c.getString(c.getColumnIndex("code")),
					c.getInt(c.getColumnIndex("family")),
					c.getString(c.getColumnIndex("name")),
					c.getInt(c.getColumnIndex("color")),
					c.getInt(c.getColumnIndex("wantIt")) > 0,
					c.getInt(c.getColumnIndex("haveIt")) > 0,
					c.getInt(c.getColumnIndex("needsRefill")) > 0
					);
			i++;
		}
	}
	
	public static MarkerDB getInstance(Context context) {
		if (instance == null) {
			instance = new MarkerDB(context.getApplicationContext());
		}
		
		return instance;
	}
	
	public Marker[] getAllMarkersArray() {
		return allMarkers;
	}
	
	public boolean setHaveIt(int id, boolean haveIt) {
		ContentValues values = new ContentValues();
		values.put("haveIt", haveIt ? 1 : 0);
		return db.update("marker", values, "id=?", new String[] { "" + id }) > 0;
	}
	
	public boolean setNeedsRefill(int id, boolean needsRefill) {
		ContentValues values = new ContentValues();
		values.put("needsRefill", needsRefill ? 1 : 0);
		return db.update("marker", values, "id=?", new String[] { "" + id }) > 0;
	}

}