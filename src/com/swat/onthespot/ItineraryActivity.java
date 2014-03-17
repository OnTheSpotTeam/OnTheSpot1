package com.swat.onthespot;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.swat.onthespot.support.ExpListAdapter;
import com.swat.onthespot.support.OTSDatabase;

public class ItineraryActivity extends Activity {
	ArrayList<String> addresses;
	private OTSDatabase mDatabase;
	private final String TAG = "ItineraryActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_itinerary);
		// Show the Up button in the action bar.
		setupActionBar();
		addresses = new ArrayList<String>();
		// Get the OTSDatabase instance
		mDatabase = OTSDatabase.getInstance(this);
		String itinName = getIntent().getStringExtra(ProfileFragmentItins.INTENT_EXTRA);
		((TextView) findViewById(R.id.explist_itinName)).setText(itinName);
		Button mapViewBtn = (Button)findViewById(R.id.explist_mapViewbtn);
		mapViewBtn.setText("Map View");
		mapViewBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(ItineraryActivity.this, ItinMapFragment.class);
				intent.putStringArrayListExtra("addr", addresses);
				startActivityForResult(intent, 1);
			}
		});
		// Query the Itineraries that a User has.
		String selection = OTSDatabase.EXPS_KEY_ID + " AS _id , " +
						   OTSDatabase.EXPS_KEY_NAME + " , " +
						   OTSDatabase.EXPS_KEY_ADDR + " , " +
						   OTSDatabase.EXPS_KEY_ACTION + " , " +
						   OTSDatabase.EXPS_KEY_RATE + " , " + 
						   OTSDatabase.EXPS_KEY_COMMENT; 						   
		String expsQuery = 	"SELECT " + selection + " FROM " + OTSDatabase.TABLE_EXPS + " " +
				"WHERE " + OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ID + " IN " + 
				"(SELECT " + OTSDatabase.ITINS_EXPS_KEY_EXPID + " FROM " + OTSDatabase.TABLE_ITINS_EXPS + " " +
				"WHERE " + OTSDatabase.TABLE_ITINS_EXPS + "." + OTSDatabase.ITINS_EXPS_KEY_ITINID + " = " +
				mDatabase.ItinNameToIds(itinName)[0] + ")";
		Cursor expsCursor = mDatabase.rawQuery(expsQuery, null);
		ExpListAdapter itinsAdapter = new ExpListAdapter(this, expsCursor);
		expsCursor.moveToFirst();
		int addCol = expsCursor.getColumnIndex("address");
		String address;
		while(!expsCursor.isAfterLast())
		{
			address = expsCursor.getString(addCol);
			addresses.add(address);
			expsCursor.moveToNext();
		}
		// Get and populate the listview
		ListView list = (ListView)findViewById(R.id.itinerary_expList);
		list.setAdapter(itinsAdapter);
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
	
}
