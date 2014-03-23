package com.swat.onthespot.support;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
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

    
    // OnTheSpot Database Instance
	private OTSDatabase mDatabase;
	private String mItinName;
	private Context mContext;
	
    // Configurations.
    private int dragStartMode = DragSortController.ON_DOWN;
    private boolean removeEnabled = false;
    private int removeMode = DragSortController.FLING_REMOVE;
    private boolean sortEnabled = true;
    private boolean dragEnabled = true;
    
    
    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                	Log.d(TAG, "on Drop from " + from + " to " + to);
                	/*
                    if (from != to) {
                        String item = mAdapter.getItem(from);
                        mAdapter.remove(item);
                        mAdapter.insert(item, to);
                    }
                    */
                }
            };

    private DragSortListView.RemoveListener onRemove = 
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                	Log.d(TAG, "on remove " + which );
                	/*
                    mAdapter.remove(mAdapter.getItem(which));
                	*/
                }
            };
            
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
        mDslv.setDropListener(onDrop);
        mDslv.setRemoveListener(onRemove);
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
				OTSDatabase.TABLE_ITINS_EXPS + "." + OTSDatabase.ITINS_EXPS_KEY_SORT;
		
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
        controller.setDragHandleId(R.id.explist_item_image);
        controller.setClickRemoveId(R.id.click_remove);
        controller.setRemoveEnabled(removeEnabled);
        controller.setSortEnabled(sortEnabled);
        controller.setDragInitMode(dragStartMode);
        controller.setRemoveMode(removeMode);
        controller.setBackgroundColor(Color.WHITE);
        return controller;
    }
    
 
    
}
