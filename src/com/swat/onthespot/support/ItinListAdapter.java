package com.swat.onthespot.support;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.swat.onthespot.R;

public class ItinListAdapter extends CursorAdapter {
	
    static class ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView date;
        public TextView comment; 
    }
    
    public ItinListAdapter(Context context, Cursor cursor){
    	super(context, cursor, 0);
    }
    
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
	    LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.list_item_itins,null,true);

	    ViewHolder holder = new ViewHolder();
	    holder.image = (ImageView) rowView.findViewById(R.id.itinlist_item_image);
	    holder.name = (TextView) rowView.findViewById(R.id.itinlist_item_name);
	    holder.date = (TextView) rowView.findViewById(R.id.itinlist_item_date);
	    holder.comment = (TextView) rowView.findViewById(R.id.itinlist_item_comment);
	    rowView.setTag(holder);

	    return rowView;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
       ViewHolder holder = (ViewHolder) view.getTag();

       String name = cursor.getString(cursor.getColumnIndex(OTSDatabase.ITINS_KEY_NAME));
       String date = cursor.getString(cursor.getColumnIndex(OTSDatabase.ITINS_KEY_DATE));
       String comment = cursor.getString(cursor.getColumnIndex(OTSDatabase.ITINS_KEY_COMMENT));
       holder.name.setText(name);
       holder.date.setText(date);
       holder.comment.setText(comment);
       holder.image.setImageResource(R.drawable.placeholder);
	}
	
}
