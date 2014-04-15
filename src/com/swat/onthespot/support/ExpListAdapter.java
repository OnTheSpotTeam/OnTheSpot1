package com.swat.onthespot.support;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;
import com.swat.onthespot.R;

public class ExpListAdapter extends CursorAdapter 
implements DragSortListView.DragSortListener{

	private static final String TAG = "ExpListAdapter";

	// Maps position of experience in itinerary to the ROWID column in Database.
	private SparseIntArray mPositionToRowId = new SparseIntArray();
	// Maps position of experience in itinerary to the SortOrder column in Database.
	private SparseIntArray mPositionToSortOrder = new SparseIntArray();
	// Maps ROWID of an experience to row index of the underlying cursor.
	private SparseIntArray mRowIdToCursorIndex = new SparseIntArray();
	// Stores removed rowids.
	private ArrayList<Integer> mRemovedRowIds = new ArrayList<Integer>();

	// Holder of the views in a list item.
	static class ViewHolder {
		public ImageView image;
		public TextView name;
		public TextView action;
		public TextView comment; 
	}

	/**
	 * Constructor of ExpListAdapter
	 * 
	 * @param context Context that holds the ListView
	 * @param cursor Cursor that contains the content.
	 */
	public ExpListAdapter(Context context, Cursor cursor){
		super(context, cursor, 0);
		resetMappings(cursor);
	}

	/**
	 * Swaps Cursor and clears list-Cursor mapping.
	 *
	 * @see android.widget.CursorAdapter#swapCursor(android.database.Cursor)
	 */
	@Override
	public Cursor swapCursor(Cursor newCursor) {
		Cursor old = super.swapCursor(newCursor);
		resetMappings(newCursor);
		return old;
	}

	/**
	 * Changes Cursor and clears list-Cursor mapping.
	 *
	 * @see android.widget.CursorAdapter#changeCursor(android.database.Cursor)
	 */
	@Override
	public void changeCursor(Cursor cursor) {
		super.changeCursor(cursor);
		resetMappings(cursor);
	}

	/**
	 * Reset the Mappings using the given cursor. 
	 * 
	 * @param cursor Must have column names "rowid" and OTSDatabase.ITINS_EXPS_KEY_SORT.
	 */
	private void resetMappings(Cursor cursor) {
		cursor.moveToFirst();
		mPositionToRowId.clear();
		mPositionToSortOrder.clear();
		mRowIdToCursorIndex.clear();
		mRemovedRowIds.clear();

		for (int i=0; i<cursor.getCount(); i++){
			mPositionToRowId.put(i, cursor.getInt(cursor.getColumnIndex("rowid")) );
			mPositionToSortOrder.put(i, cursor.getInt(cursor.getColumnIndex(OTSDatabase.ITINS_EXPS_KEY_SORT)) );
			mRowIdToCursorIndex.put(cursor.getInt(cursor.getColumnIndex("rowid")), i);
			cursor.moveToNext();
		}
		cursor.moveToFirst();
		//mRemovedCursorPositions.clear();
	}

	@Override
	public Object getItem(int position) {
		int rowid = mPositionToRowId.get(position, position);
		int cursorIndex = mRowIdToCursorIndex.get(rowid, rowid);
		return super.getItem(cursorIndex);
	}

	@Override
	public long getItemId(int position) {
		int rowid = mPositionToRowId.get(position, position);
		int cursorIndex = mRowIdToCursorIndex.get(rowid, rowid);
		return super.getItemId(cursorIndex);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		int rowid = mPositionToRowId.get(position, position);
		int cursorIndex = mRowIdToCursorIndex.get(rowid, rowid);
		return super.getDropDownView(cursorIndex, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int rowid = mPositionToRowId.get(position, position);
		int cursorIndex = mRowIdToCursorIndex.get(rowid, rowid);
		return super.getView(cursorIndex, convertView, parent);
	}


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_item_exps,null,true);

		ViewHolder holder = new ViewHolder();
		holder.image = (ImageView) rowView.findViewById(R.id.explist_item_image);
		holder.name = (TextView) rowView.findViewById(R.id.explist_item_name);
		holder.action = (TextView) rowView.findViewById(R.id.explist_item_action);
		holder.comment = (TextView) rowView.findViewById(R.id.explist_item_comment);
		rowView.setTag(holder);

		return rowView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();

		String name = cursor.getString(cursor.getColumnIndex(OTSDatabase.EXPS_KEY_NAME));
		String action = cursor.getString(cursor.getColumnIndex(OTSDatabase.EXPS_KEY_ACTION)) + " at";
		String comment = cursor.getString(cursor.getColumnIndex(OTSDatabase.EXPS_KEY_COMMENT));
		Log.d("Adapter", ""+holder);
		Log.d("Adapter", ""+holder.name);
		holder.name.setText(name);
		holder.action.setText(action);
		holder.comment.setText(comment);
		holder.image.setImageResource(context.getResources().getIdentifier(
				cursor.getString(cursor.getColumnIndex(OTSDatabase.EXPS_KEY_IMAGE)), 
				"drawable", context.getPackageName()));
	}

	/**
	 * On drop, this updates the mapping between Cursor positions
	 * and ListView positions. The Cursor is unchanged. Retrieve
	 * the current mapping with {@link getCursorPositions()}.
	 *
	 * @see DragSortListView.DropListener#drop(int, int)
	 */
	@Override
	public void drop(int from, int to) {
		if (from != to) {
			int rowidFrom = mPositionToRowId.get(from, from);
			//int sortorderFrom = mPositionToSortOrder.get(from, from);
			if (from > to) {
				for (int i = from; i > to; --i) {
					mPositionToRowId.put(i, mPositionToRowId.get(i-1,i-1));
					//mPositionToSortOrder.put(i, mPositionToSortOrder.get(i-1, i-1));
				}
			} else {
				for (int i = from; i < to; ++i) {
					mPositionToRowId.put(i, mPositionToRowId.get(i + 1, i + 1));
					//mPositionToSortOrder.put(i, mPositionToSortOrder.get(i + 1, i + 1));
				}
			}
			mPositionToRowId.put(to, rowidFrom);
			//mPositionToSortOrder.put(to, sortorderFrom);
			//cleanMapping();        
			notifyDataSetChanged();
		}		
	}

	/**
	 * Does nothing. Just completes DragSortListener interface.
	 */
	@Override
	public void drag(int from, int to) {
		// does nothing
	}

	@Override
	public void remove(int which) {
		int rowId = mPositionToRowId.get(which, which);
		if (!mRemovedRowIds.contains(rowId)) {
			mRemovedRowIds.add(rowId);
		}

		int newCount = getCount();
		for (int i = which; i < newCount; ++i) {
			mPositionToRowId.put(i, mPositionToRowId.get(i + 1, i + 1));
		}

		mPositionToRowId.delete(newCount);
		//cleanMapping();
		notifyDataSetChanged();		
	}

	@Override
	public int getCount() {
		return super.getCount() - mRemovedRowIds.size();
	}

	/**
	 * Get the current order of Position to RowId mapping in the adapter.
	 */
	public ArrayList<Integer> getPositionRowIdMap() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < getCount(); ++i) {
			result.add(mPositionToRowId.get(i, i));
		}
		return result;
	}

	/**
	 * Get the current order of Position to SortOrder mapping in the adapter.
	 */
	public ArrayList<Integer> getPositionSortMap() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < getCount(); ++i) {
			result.add(mPositionToSortOrder.get(i, i));
		}
		return result;
	}

	/**
	 * Copy the removedRowIds list and return it.
	 */
	public ArrayList<Integer> getRemovedRowIds() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < mRemovedRowIds.size(); ++i) {
			result.add(mRemovedRowIds.get(i));
		}
		return result;
	}    

}

