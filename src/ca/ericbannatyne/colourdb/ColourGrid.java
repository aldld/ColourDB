package ca.ericbannatyne.colourdb;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class ColourGrid extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private static String TAG = "ColourGrid";
	
	private Marker[] displayedMarkers;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_colour_list);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_all_colours);
			break;
		case 2:
			mTitle = getString(R.string.title_my_colours);
			break;
		case 3:
			mTitle = getString(R.string.title_need_refills);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.colour_list, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			// Log.d(TAG, "onCreateView called");

			View rootView = inflater.inflate(R.layout.fragment_colour_list,
					container, false);

			/*
			 * TextView textView = (TextView) rootView
			 * .findViewById(R.id.section_label);
			 * textView.setText(Integer.toString(getArguments().getInt(
			 * ARG_SECTION_NUMBER)));
			 */

			// Set up the colour grid.
			GridView colourGrid = (GridView) rootView
					.findViewById(R.id.colour_grid);
			if (colourGrid == null) {
				Log.d(TAG, "colourGrid is null");
			}
			registerForContextMenu(colourGrid);
			colourGrid.setAdapter(new ColourAdapter(rootView.getContext()));

			colourGrid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO: on click response
					/*
					 * Toast.makeText(getView().getContext(), "" + position,
					 * Toast.LENGTH_SHORT).show();
					 * view.setBackgroundColor(Color.RED);
					 */
					TextView textView = (TextView) view;
					Marker marker = MarkerDB.getInstance(view.getContext())
							.getAllMarkersArray()[position];
					if (marker.haveIt()) {
						// TODO: refactor to make this nicer
						marker.setHaveIt(false);
						textView.setBackgroundColor(Color.WHITE);
						textView.setTextColor(Color.LTGRAY);
					} else {
						marker.setHaveIt(true);
						textView.setBackgroundColor(marker.getColor());
						Marker.setTextColorFromBakground(marker, textView);
					}
				}

			});

			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((ColourGrid) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.colour_menu, menu);
		
		// TODO: work for other displays, not just all colours
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		Marker[] markersArray = MarkerDB.getInstance(getApplicationContext()).getAllMarkersArray();
		Marker marker = markersArray[info.position];
		menu.setHeaderTitle(marker.getCode() + ": " + marker.getName());
		
		menu.getItem(0).setChecked(marker.needsRefill());
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.needs_refill:
			item.setChecked(!item.isChecked());

			Marker[] markersArray = MarkerDB.getInstance(getApplicationContext()).getAllMarkersArray();
			Marker marker = markersArray[info.position];
			marker.setNeedsRefill(!marker.needsRefill());
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

}
