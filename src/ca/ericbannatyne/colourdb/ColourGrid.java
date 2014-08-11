package ca.ericbannatyne.colourdb;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
						MainFragment.newInstance(position + 1)).commit();
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
		case 4:
			mTitle = getString(R.string.title_wish_list);
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

		return super.onOptionsItemSelected(item);
	}

	/**
	 * The fragment containing the main colour grid view.
	 */
	public static class MainFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static MainFragment newInstance(int sectionNumber) {
			MainFragment fragment = new MainFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public MainFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_colour_list,
					container, false);

			// Set up the colour grid.
			GridView colourGrid = (GridView) rootView
					.findViewById(R.id.colour_grid);
			
			
			int filter = getArguments().getInt(ARG_SECTION_NUMBER);

			TextView emptyView = (TextView) rootView.findViewById(
					R.id.empty_grid_view);
			
			// Messages to display when particular views are empty
			switch (filter) {
			case ColourAdapter.FILTER_MY_COLOURS:
				emptyView.setText(R.string.my_colours_empty);
				break;
			case ColourAdapter.FILTER_NEED_REFILLS:
				emptyView.setText(R.string.need_refills_empty);
				break;
			case ColourAdapter.FILTER_WISH_LIST:
				emptyView.setText(R.string.wish_list_empty);
				break;
			}

			colourGrid.setEmptyView(emptyView);

			registerForContextMenu(colourGrid);

			colourGrid.setAdapter(new ColourAdapter(rootView.getContext(), filter));
			colourGrid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					TextView textView = (TextView) view;
					ColourAdapter adapter = (ColourAdapter) parent.getAdapter();
					Marker marker = (Marker) adapter.getItem(position);
					
					if (adapter.getFilter() == ColourAdapter.FILTER_WISH_LIST) {
						// On the wish list display
						marker.setHaveIt(true);
						marker.setWantIt(false);

						adapter.refresh();
						adapter.notifyDataSetChanged();
						Toast.makeText(view.getContext(),
								R.string.toast_my_colours, Toast.LENGTH_SHORT)
								.show();
						return;
					}
					
					marker.setHaveIt(!marker.haveIt());
					marker.setViewColor(textView,  false);
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
		
		GridView gridView = (GridView) v;
		ColourAdapter adapter = (ColourAdapter) gridView.getAdapter();
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		Marker marker = (Marker) adapter.getItem(info.position);
		menu.setHeaderTitle(marker.getCode() + ": " + marker.getName());
		// TODO: Set menu header icon
		
		menu.getItem(0).setChecked(marker.needsRefill());
		menu.getItem(1).setTitle(
				marker.wantIt() ? R.string.remove_from_wish_list
						: R.string.add_to_wish_list);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		GridView gridView = (GridView) info.targetView.getParent();
		ColourAdapter adapter = (ColourAdapter) gridView.getAdapter();
		
		Marker marker = (Marker) adapter.getItem(info.position);
		
		switch (item.getItemId()) {
		case R.id.needs_refill:
			item.setChecked(!item.isChecked());
			
			marker.setNeedsRefill(!marker.needsRefill());
			if (adapter.getFilter() == ColourAdapter.FILTER_NEED_REFILLS) {
				adapter.refresh();
				adapter.notifyDataSetChanged();
			} else {
				if (marker.needsRefill())
					Toast.makeText(gridView.getContext(),
							R.string.toast_need_refills_added, Toast.LENGTH_SHORT)
							.show();
				else
					Toast.makeText(gridView.getContext(),
							R.string.toast_need_refills_removed, Toast.LENGTH_SHORT)
							.show();
			}
			
			return true;

		case R.id.wish_list:
			marker.setWantIt(!marker.wantIt());
			if (adapter.getFilter() == ColourAdapter.FILTER_WISH_LIST) {
				adapter.refresh();
				adapter.notifyDataSetChanged();
			} else {
				if (marker.wantIt())
					Toast.makeText(gridView.getContext(),
							R.string.toast_wish_list_added, Toast.LENGTH_SHORT)
							.show();
				else
					Toast.makeText(gridView.getContext(),
							R.string.toast_wish_list_removed, Toast.LENGTH_SHORT)
							.show();
			}
			
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

}
