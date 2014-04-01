package com.swat.onthespot.support;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.swat.onthespot.R;

public class SearchResultAdapter extends CursorAdapter {
	
	private Context mContext;

    static class ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView action;
        public TextView comment; 
        public Button button;
    }
    
    public SearchResultAdapter(Context context, Cursor cursor){
    	super(context, cursor, 0);
    	mContext = context;
    }
    
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();

		String name = cursor.getString(cursor.getColumnIndex(OTSDatabase.EXPS_KEY_NAME));
		String action = cursor.getString(cursor.getColumnIndex(OTSDatabase.EXPS_KEY_ACTION)) + " at";
		String comment = cursor.getString(cursor.getColumnIndex(OTSDatabase.EXPS_KEY_COMMENT));
		int eid = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
		
		holder.name.setText(name);
		holder.action.setText(action);
		holder.comment.setText(comment);
		holder.image.setImageResource(context.getResources().getIdentifier(
				cursor.getString(cursor.getColumnIndex(OTSDatabase.EXPS_KEY_IMAGE)), 
				"drawable", context.getPackageName()));
		holder.button.setTag(R.id.searchresultlist_plusbtn_eidtag, eid);
		holder.button.setTag(R.id.searchresultlist_plusbtn_nametag, name);
		holder.button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				SearchResultAddDialog dialog = new SearchResultAddDialog();
				Bundle args = new Bundle();
				args.putInt(SearchResultAddDialog.KEY_EID, (Integer)view.getTag(R.id.searchresultlist_plusbtn_eidtag));
				args.putString(SearchResultAddDialog.KEY_NAME, (String)view.getTag(R.id.searchresultlist_plusbtn_nametag));
				dialog.setArguments(args);
			    dialog.show(((Activity)SearchResultAdapter.this.mContext).getFragmentManager(), "missiles");
			}
		});

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
	    LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.list_item_searchresult,null,true);

	    ViewHolder holder = new ViewHolder();
	    holder.image = (ImageView) rowView.findViewById(R.id.searchresultlist_item_image);
	    holder.name = (TextView) rowView.findViewById(R.id.searchresultlist_item_name);
	    holder.action = (TextView) rowView.findViewById(R.id.searchresultlist_item_action);
	    holder.comment = (TextView) rowView.findViewById(R.id.searchresultlist_item_comment);
	    holder.button = (Button) rowView.findViewById(R.id.searchresultlist_plusbtn);
	    rowView.setTag(holder);

	    return rowView;
	}
	
	@Override
	public Cursor swapCursor(Cursor newCursor){
		return super.swapCursor(newCursor);
	}

}
