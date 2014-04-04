package com.swat.onthespot;

import java.util.ArrayList;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SearchView;
import android.widget.TextView;

import com.swat.onthespot.support.ItinDragDropList;
import com.swat.onthespot.support.OTSDatabase;

public class DummyItineraryActivity extends FragmentActivity {
	ArrayList<String> addresses;
	private OTSDatabase mDatabase;
	
	private static final String TAG = "ItineraryActivity";
	private static final String fragmentTAG = "DragSortFragment";
	public static final String INTENT_EXTRA = "Extra";
  
    private SearchView mSearchView = null;
    private MenuItem mSearchItem = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set the action bar color.
		//ActionBar bar = getActionBar();
		//bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#62a5d4")));
		
		setContentView(R.layout.activity_itinerary_dummy);
		
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Get the OTSDatabase instance
		mDatabase = OTSDatabase.getInstance(this);
		
		// Display the itinerary name
		final String itinName = getIntent().getStringExtra(MainActivity.INTENT_EXTRA);
		((TextView) findViewById(R.id.explist_itinName)).setText(itinName);
		
		// Set the "map view" button
		TextView mapViewBtn = (TextView)findViewById(R.id.explist_mapViewbtn);
		mapViewBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(DummyItineraryActivity.this, ItinMapFragment.class);
				intent.putExtra(INTENT_EXTRA, itinName);
				startActivityForResult(intent, 1);
			}
		});
		
		// Set the "share this" button
		TextView shareBtn = (TextView)findViewById(R.id.explist_sharebtn);
		shareBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Do nothing right now.
			}
		});
		
		// Instantiate the DragSortListView.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.itinerary_expList, 
            		getItinListFragment(itinName), fragmentTAG).commit();
        }
	}

	/**
	 * 
	 * @param itinName Name of the itinerary.
	 * @return The DragSortListView that populates the experience list.
	 */
    private Fragment getItinListFragment(String itinName) {
        ItinDragDropList f = ItinDragDropList.newInstance(this, itinName);
        return f;
    }
    
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.itinerary, menu);
		//Setting up search configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	   	mSearchItem = menu.findItem(R.id.menu_action_search);
		mSearchView = (SearchView)mSearchItem.getActionView();
		SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
		mSearchView.setSearchableInfo(info);

		// Change the maximum width of the searchView. Original one is not wide enough
		// setMaxWidth() function takes pixel value. Need to convert from 
		// resolution-independent unit dp;
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float dp = 1000f; // 6 inches in a 160 dpi screen is probably enough.
		int pixels = (int) (metrics.density * dp + 0.5f);
		mSearchView.setMaxWidth(pixels);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// Decide whether we go back to MainActivity, or stay in ItineraryActivity.
		if (requestCode == 1) {
			if(resultCode == RESULT_OK){      
				String result=data.getStringExtra("result");
				if (result.equals(ItinMapFragment.RESULT_MAIN)){
					finish();
				}
		    }
		    if (resultCode == RESULT_CANCELED) {    
		        //Write your code if there's no result
		    }
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		ItinDragDropList list = (ItinDragDropList) 
				getSupportFragmentManager().findFragmentByTag(fragmentTAG);
		list.writeBackChanges();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
        // If SearchView has focus, collapse it.
        if(mSearchView != null){
        	if(mSearchView.hasFocus()){
        		MenuItemCompat.collapseActionView(mSearchItem);
        	}
        }
        
		ItinDragDropList list = (ItinDragDropList) 
				getSupportFragmentManager().findFragmentByTag(fragmentTAG);
		list.updateList();
	}
}
