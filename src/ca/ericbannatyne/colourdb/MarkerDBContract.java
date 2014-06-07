package ca.ericbannatyne.colourdb;

import android.provider.BaseColumns;

/**
 * Contract class defining the marker database schema.
 */
public final class MarkerDBContract {
	
	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "MarkerDB.db";

	// Prevent others from instantiating this class
	private MarkerDBContract() {
	}
	
	public static abstract class Marker implements BaseColumns {
		
		public static final String TABLE_NAME = "marker";
		
		public static final String COL_ID = "id";
		public static final String COL_CODE = "code";
		public static final String COL_FAMILY = "family";
		public static final String COL_NAME = "name";
		public static final String COL_COLOR = "color";
		public static final String COL_WANT_IT = "wantIt";
		public static final String COL_HAVE_IT = "haveIt";
		public static final String COL_NEEDS_REFILL = "needsRefill";
		
		public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME + " ("
				+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ COL_CODE + " TEXT, "
				+ COL_FAMILY + " INTEGER, "
				+ COL_NAME + " TEXT, "
				+ COL_COLOR + " INTEGER, "
				+ COL_WANT_IT + " INTEGER, "
				+ COL_HAVE_IT + " INTEGER, "
				+ COL_NEEDS_REFILL + " INTEGER);"; 
		
	}
	
}
