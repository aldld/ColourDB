package ca.ericbannatyne.colourdb;

import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

public class Marker {
	
	private static final String TAG = "Marker";
	
	public static final int BLACKS_BLENDER = 0;
	public static final int BLUE_GREEN = 1;
	public static final int BLUE_VIOLET = 2;
	public static final int BLUE = 3;
	public static final int COOL_GRAYS = 4;
	public static final int EARTH_TONES = 5;
	// TODO complete
	// FIXME worth changing to enum?
	
	public static String familyName(int family) {
		switch (family) {
		case BLACKS_BLENDER:
			return "Blacks & Blender";
		case BLUE_GREEN:
			return "Blue Green";
		case BLUE_VIOLET:
			return "Blue Violet";
		case BLUE:
			return "Blue";
		case COOL_GRAYS:
			return "Cool Grays";
		case EARTH_TONES:
			return "Earth Tones";
		}
		// TODO complete
		
		return null;
	}
	
	public static void setTextColorFromBakground(Marker marker, TextView textView) {
		float[] hsv = new float[3];
		Color.colorToHSV(marker.getColor(), hsv);
		
		if (hsv[2] > 0.5) {
			textView.setTextColor(Color.BLACK);
		} else {
			textView.setTextColor(Color.WHITE);
		}
	}
	
	private MarkerDB markerDB;
	private int id;
	private String code;
	private int family;
	private String name;
	private int color;
	private boolean wantIt;
	private boolean haveIt;
	private boolean needsRefill;

	/**
	 * @param code
	 * @param family
	 * @param name
	 * @param color
	 * @param wantIt
	 * @param haveIt
	 * @param needsRefill
	 */
	public Marker(MarkerDB markerDB, int id, String code, int family, String name, int color,
			boolean wantIt, boolean haveIt, boolean needsRefill) {
		this.markerDB = markerDB;
		this.id = id;
		this.code = code;
		this.family = family;
		this.name = name;
		this.color = color;
		this.wantIt = wantIt;
		this.haveIt = haveIt;
		this.needsRefill = needsRefill;
	}
	
	/**
	 * 
	 * @return the ID
	 */
	public int getID() {
		return id;
	}

	/**
	 * @return the wantIt
	 */
	public boolean wantIt() {
		return wantIt;
	}

	/**
	 * @param wantIt the wantIt to set
	 */
	public void setWantIt(boolean wantIt) {
		this.wantIt = wantIt;
	}

	/**
	 * @return the haveIt
	 */
	public boolean haveIt() {
		return haveIt;
	}

	/**
	 * @param haveIt the haveIt to set
	 */
	public void setHaveIt(boolean haveIt) {
		markerDB.setHaveIt(id, haveIt);
		this.haveIt = haveIt;
	}

	/**
	 * @return the needsRefill
	 */
	public boolean needsRefill() {
		return needsRefill;
	}

	/**
	 * @param needsRefill the needsRefill to set
	 */
	public void setNeedsRefill(boolean needsRefill) {
		this.needsRefill = needsRefill;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the family
	 */
	public int getFamily() {
		return family;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the color
	 */
	public int getColor() {
		return color;
	}

}
