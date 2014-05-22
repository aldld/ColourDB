package ca.ericbannatyne.colourdb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MarkerDBHelper extends SQLiteOpenHelper {
	
	public static final String TAG = "MarkerDBHelper";
	
	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "MarkerDB.db";
	
	private Context context;
	
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

	public MarkerDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
		// context.deleteDatabase(DB_NAME); // FIXME For debugging purposes
		Log.d(TAG, "MarkerDBHelper constructor called.");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE);
		Log.d(TAG, "onCreate called");
		
		// Hack: insert row -1 to index rows from 0
		/*
		ContentValues initialValues = new ContentValues();
		initialValues.put("id", -1);
		db.insert("marker", "null", initialValues);
		*/
		
		AssetManager assetManager = context.getAssets();
		try {
			InputStream is = assetManager.open("copic-defaults.csv");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			String line;
			int id = 0;
			while ((line = br.readLine()) != null) {
				//Log.d(TAG, line);
				String[] params = line.split(",");
				
				ContentValues values = new ContentValues();
				values.put("id", id);
				values.put("code", params[0]);
				values.put("family", params[1]);
				values.put("name", params[2]);
				values.put("color", params[3]);
				values.put("wantIt", params[4]);
				values.put("haveIt", params[5]);
				values.put("needsRefill", params[6]);
				
				db.insert("marker", "null", values);
				
				id++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Delete row -1
		//db.delete("marker", "id=-1", null);
		
		/*
		String[] projection = { "id", "code", "name" };
		Cursor c = db.query("marker", projection, "code=\"0\"", null, null, null, null);
		c.moveToFirst();
		Log.d(TAG, "" + c.getInt(c.getColumnIndex("id")));
		Log.d(TAG, c.getString(c.getColumnIndex("code")));
		Log.d(TAG, c.getString(c.getColumnIndex("name")));
		*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
