package com.swat.onthespot;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.swat.onthespot.support.ItinListAdapter;
import com.swat.onthespot.support.OTSDatabase;

public class ProfileFragmentItins extends Fragment {

	// Key for the new activity to retrieve extra message
	public static final String INTENT_EXTRA = "Extra Message";

	// OTSDatabase instance.
	private OTSDatabase mDatabase;

	// Store a cursor for itineraries.
	public static CharSequence[] sItinNames = null;
	public static int[] sItinIds = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.profile_tab_itins, container, false);

		mDatabase = OTSDatabase.getInstance(getActivity());
		String userName = MainActivity.USER_NAME;

		// Query the Itineraries that a User has.
		String selection = OTSDatabase.TABLE_ITINS + "." + OTSDatabase.ITINS_KEY_ID + " AS _id , " +
				OTSDatabase.TABLE_ITINS + "." + OTSDatabase.ITINS_KEY_NAME + " , " +
				OTSDatabase.TABLE_ITINS + "." + OTSDatabase.ITINS_KEY_DATE + " , " +
				OTSDatabase.TABLE_ITINS + "." + OTSDatabase.ITINS_KEY_RATE + " , " +
				OTSDatabase.TABLE_ITINS + "." + OTSDatabase.ITINS_KEY_COMMENT + " , " + 
				OTSDatabase.TABLE_ITINS + "." + OTSDatabase.ITINS_KEY_IMAGE + " , " +
				OTSDatabase.TABLE_USERS_ITINS + "." + OTSDatabase.USERS_ITINS_KEY_SECTION;


		String itinQuery = "SELECT " + selection + " FROM " + 
				OTSDatabase.TABLE_ITINS + ", " + OTSDatabase.TABLE_USERS_ITINS + " " +
				"WHERE " + OTSDatabase.TABLE_ITINS + "." + OTSDatabase.ITINS_KEY_ID + "=" + 
				OTSDatabase.TABLE_USERS_ITINS + "." + OTSDatabase.USERS_ITINS_KEY_ITINID + " AND " +
				OTSDatabase.TABLE_USERS_ITINS + "." + OTSDatabase.USERS_ITINS_KEY_USRID + "=" + 
				mDatabase.UserNameToIds(userName)[0] + " ORDER BY " + 
				OTSDatabase.TABLE_USERS_ITINS + "." + OTSDatabase.USERS_ITINS_KEY_SECTION + " ASC";


		/*
		String pastItinQuery = "SELECT " + selection + " FROM " + 
				OTSDatabase.TABLE_ITINS + ", " + OTSDatabase.TABLE_USERS_ITINS + " " +
				"WHERE " + OTSDatabase.TABLE_ITINS + "." + OTSDatabase.ITINS_KEY_ID + "=" + 
				OTSDatabase.TABLE_USERS_ITINS + "." + OTSDatabase.USERS_ITINS_KEY_ITINID + " AND " +
				OTSDatabase.TABLE_USERS_ITINS + "." + OTSDatabase.USERS_ITINS_KEY_USRID + "=" + 
				mDatabase.UserNameToIds(userName)[0] + " AND " +
				OTSDatabase.TABLE_USERS_ITINS + "." + OTSDatabase.USERS_ITINS_KEY_SECTION + "=" +
				OTSDatabase.SECTION_PAST;
		 */

		Cursor itinsCursor = mDatabase.rawQuery(itinQuery, null);
		storeItinNames(itinsCursor);
		ItinListAdapter itinsAdapter = new ItinListAdapter(getActivity(), itinsCursor);

		// Get and populate the listview
		ListView list = (ListView)rootView.findViewById(R.id.profile_itins_itinList);
		list.setAdapter(itinsAdapter);

		list.setClickable(true);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				TextView nameView = ((TextView)view.findViewById(R.id.itinlist_item_name));
				if (nameView != null){
					String itinName = nameView.getText().toString();
					Intent intent = new Intent(getActivity(), ItineraryActivity.class);
					intent.putExtra(INTENT_EXTRA, itinName);
					startActivity(intent);
				}
			}
		});
		return rootView;
	}

	private void storeItinNames(Cursor cursor){
		sItinNames = new CharSequence[cursor.getCount()];
		sItinIds = new int[cursor.getCount()];
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			sItinNames[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(OTSDatabase.ITINS_KEY_NAME));
			sItinIds[cursor.getPosition()] = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
			cursor.moveToNext();
		}
		cursor.moveToFirst();
	}
}