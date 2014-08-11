package ca.ericbannatyne.colourdb;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ColourAdapter extends BaseAdapter {
	
	public static final int FILTER_ALL_COLOURS = 1;
	public static final int FILTER_MY_COLOURS = 2;
	public static final int FILTER_NEED_REFILLS = 3;
	public static final int FILTER_WISH_LIST = 4;

	private Context mContext;
	private MarkerDB markerDB;
	
	private int filter;
	private static final String[] filterWhereClause = {
			null,
			"", 
			MarkerDBContract.Marker.COL_HAVE_IT + "=1",
			MarkerDBContract.Marker.COL_NEEDS_REFILL + "=1",
			MarkerDBContract.Marker.COL_WANT_IT + "=1"
			};
	
	private Marker[] markerArray;

	public ColourAdapter(Context c, int filter) {
		mContext = c;
		markerDB = MarkerDB.getInstance(c);
		
		this.filter = filter;
		markerArray = markerDB.queryMarkers(filterWhereClause[filter], null);
	}
	
	public int getFilter() {
		return filter;
	}
	
	public void refresh() {
		markerArray = markerDB.queryMarkers(filterWhereClause[filter], null);
	}
	
	@Override
	public int getCount() {
		return markerArray.length;
	}

	@Override
	public Object getItem(int position) {
		return markerArray[position];
	}

	@Override
	public long getItemId(int position) {
		return markerArray[position].getID();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView;
		Marker marker = markerArray[position];

		if (convertView == null) {
			textView = new TextView(mContext);
			
			DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
			int displayWidth = metrics.widthPixels;

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					displayWidth / 5);

			textView.setLayoutParams(new GridView.LayoutParams(params));
			textView.setPadding(1, 1, 1, 1);
		} else {
			textView = (TextView) convertView;
		}

		textView.setText(marker.getCode());
		/*
		if (filter == FILTER_WISH_LIST) {
			textView.setBackgroundColor(marker.getColor());
		} else {
			textView.setBackgroundColor(marker.haveIt() ? marker.getColor()
					: Color.WHITE);
		}
		*/

		marker.setViewColor(textView, filter == FILTER_WISH_LIST);
		return textView;
	}

}
