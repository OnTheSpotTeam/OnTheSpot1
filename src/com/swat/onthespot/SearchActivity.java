package com.swat.onthespot;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

public class SearchActivity extends Activity
	implements LoaderManager.LoaderCallbacks<Cursor>{

	private static final String TAG = "OTS:SearchActivity";
	
	private static final int SEARCH_LOADER = 0;
	ListView mListView;
	SimpleCursorAdapter mAdapter; 
	private String mQuery; 
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("TAG", "Started Search Activity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Setup ListView and Adaptor.
		mListView = (ListView) findViewById(R.id.search_results_list);
		mAdapter = new SimpleCursorAdapter(
	            this,                // Current context
	            R.layout.searchresult_list_item,  // Layout for a single row
	            null,                // No Cursor yet
	            new String[] { SearchContract.COL_NAME, SearchContract.COL_TYPE }, // Cursor columns to use
                new int[] { R.id.searchresult_word, R.id.searchresult_definition }, // Layout fields to use
	            0                    // No flags
	    );
		mListView.setAdapter(mAdapter);
		
		// Handle user's search query.
	    handleIntent(getIntent());
	    
		// Start the query by initializing the cursor loader
		getLoaderManager().initLoader(SEARCH_LOADER, null, this);
		
		// Set transition animations:
		overridePendingTransition(0, 0);
	}
	
    @Override
    /**
     * In the Manifest file, I set the launchMode of this activity to be "singleTop",
     * which means there will only be one instance of the search activity. If we try
     * to launch it again (meaning we do a new search), android will prevent the 
     * launch, and this method will be called.
     */
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
        
        // Restart the loader to get content for the new query.
        getLoaderManager().restartLoader(SEARCH_LOADER, null, this);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            
            // Change the stored query to the new query, so the (new / restared)
            // CursorLoader can use this query.
            mQuery = query;
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // Handle a suggestions click (because the suggestions all use ACTION_VIEW)
            String query = intent.getDataString();
            mQuery = query;
        }
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
		getMenuInflater().inflate(R.menu.search, menu);
		
		//Setting up search configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		
		SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(info);

		// Change the maximum width of the searchView. Original one is not wide enough
		// setMaxWidth() function takes pixel value. Need to convert from 
		// resolution-independent unit dp;
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float dp = 1000f; // 6 inches in a 160 dpi screen is probably enough.
		int pixels = (int) (metrics.density * dp + 0.5f);
		searchView.setMaxWidth(pixels);
		
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
	/**
	 * Create a new CursorLoader 
	 */
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Log.d(TAG, "onCreateLoader");
		// TODO Auto-generated method stub
	    Uri baseUri = SearchContract.TABLEURI;
	    
	    String[] selectArgs = {mQuery};

	    // Peng:
	    // Using the constructor CursorLoader (Context, Uri, Projection, Selection, SelectionArgs, sortOrder)
	    // Here only the Uri and the selectionArgs matter, because the searchProvider's
	    // query function discards other arguments. 
	    // basedUri is used in Uri matcher (should match SEARCH_WORDS in this case)
	    // selectArgs[0] is user's query that will be passed into database.
	    // See SearchProvider's query() function for more details.
	    return new CursorLoader(this, baseUri, null, null, selectArgs, null);
	}


	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		
		Log.d(TAG, "onLoadFinished");
		
		// Now we have a Cursor returned by CursorLoader. Use it to create the new ListView.
		if(mAdapter!=null && cursor!=null){
			mAdapter.swapCursor(cursor); //swap the new cursor in.
		}
		else{
			Log.e(TAG,"OnLoadFinished: mAdapter or cursor is null");
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
	}
	
}
