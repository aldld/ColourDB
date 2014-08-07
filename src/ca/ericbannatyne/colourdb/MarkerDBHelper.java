package ca.ericbannatyne.colourdb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MarkerDBHelper extends SQLiteOpenHelper {
	
	public static final String TAG = "MarkerDBHelper";
	public static int numInstances = 0;
	
	private Context context;
	private final String[] defaultsFileVersion = { null, "", "-1.0.1" };
	
	public MarkerDBHelper(Context context) {
		super(context, MarkerDBContract.DB_NAME, null, MarkerDBContract.DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(MarkerDBContract.Marker.CREATE_TABLE);
		String defaultsFileName = "copic-defaults"
				+ defaultsFileVersion[MarkerDBContract.DB_VERSION] + ".csv";
		
		AssetManager assetManager = context.getAssets();
		try {
			InputStream is = assetManager.open(defaultsFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			String line;
			int id = 0;
			while ((line = br.readLine()) != null) {
				String[] params = line.split(",");
				
				ContentValues values = new ContentValues();
				values.put(MarkerDBContract.Marker.COL_ID, id);
				values.put(MarkerDBContract.Marker.COL_CODE, params[0]);
				values.put(MarkerDBContract.Marker.COL_FAMILY, params[1]);
				values.put(MarkerDBContract.Marker.COL_NAME, params[2]);
				values.put(MarkerDBContract.Marker.COL_COLOR, params[3]);
				values.put(MarkerDBContract.Marker.COL_WANT_IT, params[4]);
				values.put(MarkerDBContract.Marker.COL_HAVE_IT, params[5]);
				values.put(MarkerDBContract.Marker.COL_NEEDS_REFILL, params[6]);
				
				db.insert(MarkerDBContract.Marker.TABLE_NAME, "null", values);
				
				id++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1 && newVersion == 2) {
			updateMarkerList(db, oldVersion, newVersion);
		}
	}
	
	/**
	 * Upgrade only the colours present in the markers database table and their
	 * order while retaining current data.
	 * 
	 * @param db the database
	 * @param oldVersion database version upgrading from
	 * @param newVersion database version upgrading to
	 */
	private void updateMarkerList(SQLiteDatabase db, int oldVersion, int newVersion) {
		String newDefaultsFileName = "copic-defaults"
				+ defaultsFileVersion[newVersion] + ".csv";
		
		// Store current list of markers (at this point they are identified by
		// code, not by ID)
		Map<String, Marker> current = new HashMap<String, Marker>();
		MarkerDB markerDB = MarkerDB.getInstance(context, db);
		Marker[] currentMarkersArray = markerDB.queryMarkers("", null);
		
		for (Marker marker : currentMarkersArray) {
			current.put(marker.getCode(), marker);
		}
		
		db.execSQL("DROP TABLE marker;");
		
		// TODO: Refactor this
		db.execSQL(MarkerDBContract.Marker.CREATE_TABLE);
		
		AssetManager assetManager = context.getAssets();
		try {
			InputStream is = assetManager.open(newDefaultsFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			String line;
			int id = 0;
			while ((line = br.readLine()) != null) {
				String[] params = line.split(",");
				
				ContentValues values = new ContentValues();
				values.put(MarkerDBContract.Marker.COL_ID, id);
				if (current.containsKey(params[0])) {
					Marker marker = current.get(params[0]);
					values.put(MarkerDBContract.Marker.COL_CODE, marker.getCode());
					values.put(MarkerDBContract.Marker.COL_FAMILY, marker.getFamily());
					values.put(MarkerDBContract.Marker.COL_NAME, marker.getName());
					values.put(MarkerDBContract.Marker.COL_COLOR, marker.getColor());
					values.put(MarkerDBContract.Marker.COL_WANT_IT, marker.wantIt());
					values.put(MarkerDBContract.Marker.COL_HAVE_IT, marker.haveIt());
					values.put(MarkerDBContract.Marker.COL_NEEDS_REFILL, marker.needsRefill());
				} else {
					values.put(MarkerDBContract.Marker.COL_CODE, params[0]);
					values.put(MarkerDBContract.Marker.COL_FAMILY, params[1]);
					values.put(MarkerDBContract.Marker.COL_NAME, params[2]);
					values.put(MarkerDBContract.Marker.COL_COLOR, params[3]);
					values.put(MarkerDBContract.Marker.COL_WANT_IT, params[4]);
					values.put(MarkerDBContract.Marker.COL_HAVE_IT, params[5]);
					values.put(MarkerDBContract.Marker.COL_NEEDS_REFILL, params[6]);
				}
				
				db.insert(MarkerDBContract.Marker.TABLE_NAME, "null", values);
				
				id++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
