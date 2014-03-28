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
import android.text.BoringLayout.Metrics;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.swat.onthespot.support.OTSDatabase;
import com.swat.onthespot.support.OTSSearchProvider;
import com.swat.onthespot.support.SearchResultAdapter;

public class SearchActivity extends Activity
	implements LoaderManager.LoaderCallbacks<Cursor>{

	// Tag for LogCat
	private static final String TAG = "SearchActivity";
	
	// OTSDatabase Instance
	private OTSDatabase mDatabase;
	
	// Id of the CursorLoader
	private static final int SEARCH_LOADER = 0;

	// The List and Adapter
	private ListView mListView;
	private SearchResultAdapter mAdapter;
	
	// Method could be 0: search experience from string query (user hit search button)
	//                 1: retrieve experience given rowid in the table (user hit suggestion)
	private int mMethod;
	private final int METHOD_QUERY = 0;
	private final int METHOD_RETRIEVE = 1;
	private String mQuery;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("TAG", "Started Search Activity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Get the database instance.
		mDatabase = OTSDatabase.getInstance(this);
		
		// Setup ListView and Adaptor.
		mListView = (ListView) findViewById(R.id.search_results_list);
		mAdapter = new SearchResultAdapter(this, null);
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
            // Update Method and Query
            mMethod = METHOD_QUERY;
            mQuery = query;
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // Handle a suggestions click (because the suggestions all use ACTION_VIEW)
            String query = intent.getDataString();
            mMethod = METHOD_RETRIEVE;
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
	    Uri baseUri;
		switch(mMethod){
		case METHOD_QUERY:
			baseUri = Uri.parse("content://" + OTSSearchProvider.AUTHORITY + "/" + OTSSearchProvider.TABLE_SEARCH);
		    String[] selectArgs = {mQuery};
		    // Normal query. Query is the search word. 
		    // Put it into selectArgs[0].
		    return new CursorLoader(this, baseUri, null, null, selectArgs, null);
		case METHOD_RETRIEVE:
			// Retrieve an experience directly from rowid. mQuery is a complete Uri.
			// Rowid is the last segment of query.
			baseUri = Uri.parse(mQuery);
			return new CursorLoader(this, baseUri, null, null, null, null);
		default:
			return null;
		}
	}


	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		
		Log.d(TAG, "onLoadFinished");
		
		// Now we have a Cursor returned by CursorLoader. Use it to create the new ListView.
		if(mAdapter!=null && cursor!=null){
			mAdapter.swapCursor(cursor); //swap the new cursor in.
		}
		else{
			Log.e(TAG, "OnLoadFinished: mAdapter or cursor is null");
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
	}
	
}
