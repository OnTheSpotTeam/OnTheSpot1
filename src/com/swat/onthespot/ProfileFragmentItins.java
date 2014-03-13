package com.swat.onthespot;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.swat.onthespot.support.ItinListAdapter;
import com.swat.onthespot.support.OTSDatabase;

public class ProfileFragmentItins extends Fragment {
	
	private OTSDatabase mDatabase;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.profile_tab_itins, container, false);
        
		mDatabase = OTSDatabase.getInstance(getActivity());
		String userName = MainActivity.USER_NAME;
		
		// Query the Itineraries that a User has.
		String selection = OTSDatabase.ITINS_KEY_ID + " AS _id , " +
						   OTSDatabase.ITINS_KEY_NAME + " , " +
						   OTSDatabase.ITINS_KEY_DATE + " , " +
						   OTSDatabase.ITINS_KEY_RATE + " , " + 
						   OTSDatabase.ITINS_KEY_COMMENT; 						   
		String itinQuery = 	"SELECT " + selection + " FROM " + OTSDatabase.TABLE_ITINS + " " +
				"WHERE " + OTSDatabase.TABLE_ITINS + "." + OTSDatabase.ITINS_KEY_ID + " IN " + 
				"(SELECT " + OTSDatabase.USERS_ITINS_KEY_ITINID + " FROM " + OTSDatabase.TABLE_USERS_ITINS + " " +
				"WHERE " + OTSDatabase.TABLE_USERS_ITINS + "." + OTSDatabase.USERS_ITINS_KEY_USRID + " = " +
				mDatabase.UserNameToIds(userName)[0] + ")";
		
		Cursor itinsCursor = mDatabase.rawQuery(itinQuery, null);
		ItinListAdapter itinsAdapter = new ItinListAdapter(getActivity(), itinsCursor);
		
		// Get and populate the listview
		ListView list = (ListView)rootView.findViewById(R.id.profile_itins_itinList);
		list.setAdapter(itinsAdapter);
        
        return rootView;
    }
}