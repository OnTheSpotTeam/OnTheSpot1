package com.swat.onthespot;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class SearchContract{
		
		/* Column Names */
		public static final String _ID = BaseColumns._ID;
		// Name is also used for suggestions.
		// See http://developer.android.com/guide/topics/search/adding-custom-suggestions.html#SuggestionTable
		public static final String COL_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1; 
		public static final String COL_TYPE = SearchManager.SUGGEST_COLUMN_TEXT_2; 
		
		/* Content Uri */
		public static final String AUTHORITY = "com.swat.onthespot.searchprovider";
		public static final String TABLENAME = "content";
		public static final Uri TABLEURI = Uri.parse("content://" + AUTHORITY + "/" + TABLENAME);

		/* MIME Types */
	    public static final String DIR_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/vnd.swat.onthespot.content";
	    public static final String ITEM_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/vnd.swat.onthespot.content";
	    
}