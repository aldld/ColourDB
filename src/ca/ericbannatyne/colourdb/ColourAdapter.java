package ca.ericbannatyne.colourdb;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ColourAdapter extends BaseAdapter {

	private static String TAG = "ColourAdapter";

	private Context mContext;
	private MarkerDB markerDB;

	public ColourAdapter(Context c) {
		mContext = c;
		markerDB = MarkerDB.getInstance(c);
	}

	@Override
	public int getCount() {
		return markerDB.getAllMarkersArray().length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView;
		Marker marker = markerDB.getAllMarkersArray()[position];

		if (convertView == null) {
			textView = new TextView(mContext);
			
			DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
			int displayWidth = metrics.widthPixels;

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					//LinearLayout.LayoutParams.MATCH_PARENT);
					displayWidth / 5);

			textView.setLayoutParams(new GridView.LayoutParams(params));
			textView.setPadding(1, 1, 1, 1);
		} else {
			textView = (TextView) convertView;
		}

		textView.setText(marker.getCode());
		textView.setBackgroundColor(marker.haveIt() ? marker.getColor() : Color.WHITE);
		if (marker.haveIt()) {
			textView.setBackgroundColor(marker.getColor());

			Marker.setTextColorFromBakground(marker, textView);
		} else {
			// TODO: Refactor to make this nicer
			textView.setBackgroundColor(Color.WHITE);
			textView.setTextColor(Color.LTGRAY);
		}
		return textView;
	}

}
