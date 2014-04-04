package com.swat.onthespot.support;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.swat.onthespot.R;

public class ItinDragDropList extends ListFragment {
	
	private final String TAG = "ItinDragDropList";
	
	// Main DragSortListView, Controller, and Adapter.
    private DragSortListView mDslv;
    private DragSortController mController;
    private ExpListAdapter mAdapter;

    // Needed Variables.
	private OTSDatabase mDatabase;
	private String mItinName;
	private Context mContext;
	
    // Configurations.
    private int dragStartMode = DragSortController.ON_DOWN;
    private boolean removeEnabled = true;
    private int removeMode = DragSortController.FLING_REMOVE;
    private boolean sortEnabled = true;
    private boolean dragEnabled = true;

            
    /**
     * Static factory method for ItinDragDroplist.
     * 
     * @param itinName Name of the itinerary.
     * @return A new instance of the ItinDragDropList.
     */
    public static ItinDragDropList newInstance(Context context, String itinName) {
        ItinDragDropList l = new ItinDragDropList();
    	l.mDatabase = OTSDatabase.getInstance(context);
    	l.mItinName = itinName;
    	l.mContext = context;
    	return l;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mDslv = (DragSortListView) inflater.inflate(R.layout.itinerary_dragdrop, container, false);
        mController = buildController(mDslv);
        
        mDslv.setFloatViewManager(mController);
        mDslv.setOnTouchListener(mController);
        mDslv.setDragEnabled(dragEnabled);

        return mDslv;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDslv = (DragSortListView) getListView(); 
        setListAdapter();
    }
     
    /**
     * 
     * @return Controller of the DragSortListView
     */
    public DragSortController getController() {
        return mController;
    }
    
    /**
     * Called from ItinDragDropList.onActivityCreated(). 
     */
    public void setListAdapter() {
		Cursor expsCursor = queryExpsInItin();
		mAdapter = new ExpListAdapter(mContext, expsCursor);
        
		// Notice the ExpListAdapter also implements dragListener, 
		// sortListener, and removeListener, so the DragSortListView 
		// will also set these listeners while setting adapter.
		setListAdapter(mAdapter); 
    }
    
    /**
     * 
     * @return A cursor containing experiences for the itinerary matching mItinName.
     */
    private Cursor queryExpsInItin(){
		String selection = OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ID + " AS _id , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_NAME + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ADDR + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ACTION + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_RATE + " , " + 
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_COMMENT + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_IMAGE + " , " +
				OTSDatabase.TABLE_ITINS_EXPS + "." + OTSDatabase.ITINS_EXPS_KEY_SORT + " , " +
				OTSDatabase.TABLE_ITINS_EXPS + "." + "ROWID";
		
		String expsQuery = 	"SELECT " + selection + " FROM " + 
				OTSDatabase.TABLE_EXPS + ", " + OTSDatabase.TABLE_ITINS_EXPS + " " +
				"WHERE " + OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ID + "=" + 
				OTSDatabase.TABLE_ITINS_EXPS + "." + OTSDatabase.ITINS_EXPS_KEY_EXPID + " AND " +
				OTSDatabase.TABLE_ITINS_EXPS + "." + OTSDatabase.ITINS_EXPS_KEY_ITINID + "=" + 
				mDatabase.ItinNameToIds(mItinName)[0] + " ORDER BY " + 
				OTSDatabase.TABLE_ITINS_EXPS + "." + OTSDatabase.ITINS_EXPS_KEY_SORT + " ASC";
		return mDatabase.rawQuery(expsQuery, null);
    }
    
    
    /**
     * Called in ItinDragDropList.onCreateView. 
     */
    public DragSortController buildController(DragSortListView dslv) {
        // defaults are
        //   dragStartMode = onDown
        //   removeMode = flingRight
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.explist_item_dragimage);
        controller.setClickRemoveId(R.id.click_remove);
        controller.setRemoveEnabled(removeEnabled);
        controller.setSortEnabled(sortEnabled);
        controller.setDragInitMode(dragStartMode);
        controller.setRemoveMode(removeMode);
        controller.setBackgroundColor(Color.WHITE);
        return controller;
    }
    
    public void writeBackChanges(){
    	
		// First get the changes stored in mappings:
		ArrayList<Integer> posToRowIdMapping = mAdapter.getPositionRowIdMap();
		ArrayList<Integer> posToSortMapping = mAdapter.getPositionSortMap();
		ArrayList<Integer> removedRowIds = mAdapter.getRemovedRowIds();
		
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
    
    /**
     * Write back changes in the old list, and query again.
     */
    public void updateList(){
    	Cursor newCursor = queryExpsInItin();
    	mAdapter.swapCursor(newCursor);
    }
    
    /**
     * Get the current order of Position to RowId mapping in the adapter.
     */
    /*
    public ArrayList<Integer> getPositionRowIdMap() {
        return mAdapter.getPositionRowIdMap();
    }
    */
    
    /**
     * Get the current order of Position to SortOrder mapping in the adapter.
     */
    /*
    public ArrayList<Integer> getPositionSortMap() {
        return mAdapter.getPositionSortMap();
    }
    */
    
    /**
     * Copy the removedRowIds list and return it.
     */
    /*
    public ArrayList<Integer> getRemovedRowIds() {
        return mAdapter.getRemovedRowIds();
    } 
    */  
 

    
}
