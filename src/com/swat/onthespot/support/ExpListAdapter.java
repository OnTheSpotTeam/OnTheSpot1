package com.swat.onthespot.support;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.swat.onthespot.R;

public class ExpListAdapter extends CursorAdapter {
	
    static class ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView action;
        public TextView comment; 
    }
    
    public ExpListAdapter(Context context, Cursor cursor){
    	super(context, cursor, 0);
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
	
}

