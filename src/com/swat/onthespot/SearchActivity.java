package com.swat.onthespot;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

// Started every time the user submits a search query - see AndroidManifest.xml
public class SearchActivity extends Activity {

	// handle the search Intent every time this activity is created
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		handleIntent(getIntent());
		
		Log.i("SEARCHACTIVITY", "Activity started!");
	}
	
	// handle the search Intent every time a new query is passed in
	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		
		//gets the search query and does something with it
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			
			//search the data somehow... we'll just print the query for now to debug
			
			TextView queryDebugger = (TextView) findViewById(R.id.debug_query);
			queryDebugger.setText(query);
		}
		
	}
	
}

