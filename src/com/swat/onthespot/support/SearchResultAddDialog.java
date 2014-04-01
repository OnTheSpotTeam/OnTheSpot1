package com.swat.onthespot.support;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.swat.onthespot.MainActivity;
import com.swat.onthespot.ProfileFragmentItins;

public class SearchResultAddDialog extends DialogFragment {
	public static final String KEY_EID = "EID";
	public static final String KEY_NAME = "NAME";
	private OTSDatabase mDatabase;
	private CharSequence[] mItinNames = null;
	private int[] mItinIds = null;
	private ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();  // Where we track the selected items
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	mDatabase = OTSDatabase.getInstance(this.getActivity());
    	
    	// Get the experience id and name
    	int eid = getArguments().getInt(KEY_EID);
    	String expName = getArguments().getString(KEY_NAME);
    	
    	setItinNamesAndIds();
    	for (int i=0; i<mItinNames.length; i++){
    		Log.d("SearchResultDialog", (String)mItinNames[i]);
    	}
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add \"" + expName + "\" to Itinerary:")
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // FIRE ZE MISSILES!
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               })
               .setMultiChoiceItems(mItinNames, null, new DialogInterface.OnMultiChoiceClickListener() {
            	   @Override
            	   public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            		   if (isChecked) {
            			   // If the user checked the item, add it to the selected items
            			   mSelectedItems.add(mItinIds[which]);
            		   } else if (mSelectedItems.contains(mItinIds[which])) {
            			   // Else, if the item is already in the array, remove it 
            			   mSelectedItems.remove(Integer.valueOf(mItinIds[which]));
            		   }
            	   }
               });
        
        // Create the AlertDialog object and return it
        return builder.create();
    }
    
    private void setItinNamesAndIds(){
    	mItinNames = ProfileFragmentItins.sItinNames;
    	mItinIds = ProfileFragmentItins.sItinIds;
    	if (mItinNames == null || mItinIds == null){
    		// Query the Itineraries that a User has.
    		String selection = OTSDatabase.ITINS_KEY_ID + " AS _id , " +
    						   OTSDatabase.ITINS_KEY_NAME + " , " +
    						   OTSDatabase.ITINS_KEY_DATE + " , " +
    						   OTSDatabase.ITINS_KEY_RATE + " , " + 
    						   OTSDatabase.ITINS_KEY_COMMENT + " , " +
    						   OTSDatabase.ITINS_KEY_IMAGE; 						   
    		String itinQuery = 	"SELECT " + selection + " FROM " + OTSDatabase.TABLE_ITINS + " " +
    				"WHERE " + OTSDatabase.TABLE_ITINS + "." + OTSDatabase.ITINS_KEY_ID + " IN " + 
    				"(SELECT " + OTSDatabase.USERS_ITINS_KEY_ITINID + " FROM " + OTSDatabase.TABLE_USERS_ITINS + " " +
    				"WHERE " + OTSDatabase.TABLE_USERS_ITINS + "." + OTSDatabase.USERS_ITINS_KEY_USRID + " = " +
    				mDatabase.UserNameToIds(MainActivity.USER_NAME)[0] + ")";
    		Cursor cursor = mDatabase.rawQuery(itinQuery, null);
        	mItinNames = new CharSequence[cursor.getCount()];
        	mItinIds = new int[cursor.getCount()];
        	cursor.moveToFirst();
        	while (!cursor.isAfterLast()){
        		mItinNames[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(OTSDatabase.ITINS_KEY_NAME));
        		mItinIds[cursor.getPosition()] = cursor.getInt(cursor.getColumnIndex(OTSDatabase.ITINS_KEY_ID));
        	}
    	}
    }
}
