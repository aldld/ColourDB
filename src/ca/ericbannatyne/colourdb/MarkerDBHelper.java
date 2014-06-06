package ca.ericbannatyne.colourdb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MarkerDBHelper extends SQLiteOpenHelper {
	
	public static final String TAG = "MarkerDBHelper";
	
	private Context context;
	
	/*
	private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ "marker ("
			+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "code TEXT, "
			+ "family INTEGER, "
			+ "name TEXT, "
			+ "color INTEGER, "
			+ "wantIt INTEGER, "
			+ "haveIt INTEGER, "
			+ "needsRefill INTEGER);"; // FIXME: use contract class
			*/

	public MarkerDBHelper(Context context) {
		super(context, MarkerDBContract.DB_NAME, null, MarkerDBContract.DB_VERSION);
		this.context = context;
		// context.deleteDatabase(DB_NAME); // FIXME For debugging purposes
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(MarkerDBContract.Marker.CREATE_TABLE);
		Log.d(TAG, "onCreate called");
		
		AssetManager assetManager = context.getAssets();
		try {
			InputStream is = assetManager.open("copic-defaults.csv");
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
		// TODO Auto-generated method stub
	}

}
