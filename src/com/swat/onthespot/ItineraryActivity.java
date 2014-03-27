package com.swat.onthespot;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.swat.onthespot.support.ItinDragDropList;
import com.swat.onthespot.support.OTSDatabase;

public class ItineraryActivity extends FragmentActivity {
	ArrayList<String> addresses;
	private OTSDatabase mDatabase;
	
	private static final String TAG = "ItineraryActivity";
	private static final String fragmentTAG = "DragSortFragment";
	public static final String INTENT_EXTRA = "Extra";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_itinerary);
		
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Get the OTSDatabase instance
		mDatabase = OTSDatabase.getInstance(this);
		
		// Display the itinerary name
		final String itinName = getIntent().getStringExtra(ProfileFragmentItins.INTENT_EXTRA);
		((TextView) findViewById(R.id.explist_itinName)).setText(itinName);
		
		// Set the "map view" button
		Button mapViewBtn = (Button)findViewById(R.id.explist_mapViewbtn);
		mapViewBtn.setText("Map View");
		mapViewBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(ItineraryActivity.this, ItinMapFragment.class);
				intent.putExtra(INTENT_EXTRA, itinName);
				startActivityForResult(intent, 1);
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
		
		// User may have dragged or removed the experiences list.
		// Write these changes back to the database.
		
		// First get the changes stored in mappings:
		ArrayList<Integer> posToRowIdMapping = list.getPositionRowIdMap();
		ArrayList<Integer> posToSortMapping = list.getPositionSortMap();
		ArrayList<Integer> removedRowIds = list.getRemovedRowIds();
		
		// Write back change of positions:
		for (int i=0; i<posToRowIdMapping.size(); i++){
			ContentValues values = new ContentValues();
			values.put(OTSDatabase.ITINS_EXPS_KEY_SORT, posToSortMapping.get(i));
			String whereClause = "ROWID=" + posToRowIdMapping.get(i); 
			int rowsAffected = mDatabase.updateWithOnConflict(OTSDatabase.TABLE_ITINS_EXPS, 
					values, whereClause, null, SQLiteDatabase.CONFLICT_ROLLBACK);
			if (rowsAffected!=1){
				Log.e(TAG, "Not exactly one rows updated");
			}
		}
		
		// Write back deletes:
		for (int i=0; i<removedRowIds.size(); i++){
			String whereClause = "ROWID=" + removedRowIds.get(i);
			int rowsDeleted = mDatabase.delete(OTSDatabase.TABLE_ITINS_EXPS, whereClause, null);
			if (rowsDeleted!=1){
				Log.e(TAG, "Not exactly one row deleted");
			}
		}
	}
	
}
