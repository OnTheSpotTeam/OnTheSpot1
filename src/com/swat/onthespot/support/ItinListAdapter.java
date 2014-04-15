package com.swat.onthespot.support;

import android.content.Context;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.swat.onthespot.R;

public class ItinListAdapter extends CursorAdapter {

	private Boolean mHasCurrent;
	private Context mContext;

	static class ViewHolder {
		public int section;
		public TextView sectionName;
		public ImageView image;
		public TextView name;
		public TextView date;
		public TextView comment; 
	}

	public ItinListAdapter(Context context, Cursor cursor){
		super(context, cursor, 0);

		mContext = context;

		cursor.moveToFirst();
		mHasCurrent = false;
		while (!cursor.isAfterLast()){
			if (cursor.getInt(cursor.getColumnIndex(OTSDatabase.USERS_ITINS_KEY_SECTION)) == OTSDatabase.SECTION_CURRENT_CONTENT){ 
				mHasCurrent = true;
			}
			cursor.moveToNext();
		}
		cursor.moveToFirst();

	}

	@Override
	public int getViewTypeCount(){
		return 3;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		/*
		Log.d("newView", "name: " + cursor.getString(cursor.getColumnIndex(OTSDatabase.ITINS_KEY_NAME))
				+ "section: " + cursor.getInt(cursor.getColumnIndex(OTSDatabase.USERS_ITINS_KEY_SECTION)) );
		 */
		View rowView;
		LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder = new ViewHolder();


		int section = cursor.getInt(cursor.getColumnIndex(OTSDatabase.USERS_ITINS_KEY_SECTION));
		//int section = OTSDatabase.SECTION_CURRENT_CONTENT;
		holder.section = section;

		if (section == OTSDatabase.SECTION_CURRENT){
			rowView=inflater.inflate(R.layout.list_item_itinsection_current, null, true);
			rowView.setClickable(false);
			rowView.setFocusable(false);

			if (mHasCurrent){
				int sectionHeightDp = (int) mContext.getResources().getDimension(R.dimen.itinlist_sectionheight);
				rowView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, sectionHeightDp));
				View divider = rowView.findViewById(R.id.itinlist_section_divider);
				divider.setVisibility(View.GONE);
			}else{
				int sectionHeightDp = (int) mContext.getResources().getDimension(R.dimen.itinlist_sectionheightwithdivider);
				rowView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, sectionHeightDp));
				View divider = rowView.findViewById(R.id.itinlist_section_divider);
				divider.setVisibility(View.VISIBLE);
			}

			holder.sectionName=(TextView) rowView.findViewById(R.id.itinlist_sectionname);
		}
		else if (section == OTSDatabase.SECTION_PAST){
			rowView=inflater.inflate(R.layout.list_item_itinsection_past, null, true);
			rowView.setClickable(false);
			rowView.setFocusable(false);
			holder.sectionName=(TextView) rowView.findViewById(R.id.itinlist_sectionname);
		}
		else{
			rowView = inflater.inflate(R.layout.list_item_itins,null,true);
			holder.image = (ImageView) rowView.findViewById(R.id.itinlist_item_image);
			holder.name = (TextView) rowView.findViewById(R.id.itinlist_item_name);
			holder.date = (TextView) rowView.findViewById(R.id.itinlist_item_date);
			holder.comment = (TextView) rowView.findViewById(R.id.itinlist_item_comment);
		}
		rowView.setTag(holder);
		return rowView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();


		if (holder.section == OTSDatabase.SECTION_CURRENT){
			holder.sectionName.setText("CURRENT ITINERARY");
		}
		else if (holder.section == OTSDatabase.SECTION_PAST){
			holder.sectionName.setText("PAST ITINERARY");
		}
		else{
			String name = cursor.getString(cursor.getColumnIndex(OTSDatabase.ITINS_KEY_NAME));
			String date = cursor.getString(cursor.getColumnIndex(OTSDatabase.ITINS_KEY_DATE));
			String comment = cursor.getString(cursor.getColumnIndex(OTSDatabase.ITINS_KEY_COMMENT));
			holder.name.setText(name);
			holder.date.setText(date);
			holder.comment.setText(comment);

			holder.image.setImageResource(context.getResources().getIdentifier(
					cursor.getString(cursor.getColumnIndex(OTSDatabase.ITINS_KEY_IMAGE)), 
					"drawable", context.getPackageName()));
		}
	}


}
