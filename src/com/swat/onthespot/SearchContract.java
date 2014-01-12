package com.swat.onthespot;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class SearchContract{
		// Column Names
		public static final String _ID = BaseColumns._ID;
		public static final String COL_NAME = "Name";
		public static final String COL_TYPE = "Type"; 
		
		// Content Uri
		public static final String AUTHORITY = "com.swat.onthespot.searchprovider";
		public static final String TABLENAME = "content";
		public static final Uri TABLEURI = Uri.parse("content://" + AUTHORITY + "/" + TABLENAME);

		// MIME Types
	    public static final String DIR_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/vnd.swat.onthespot.content";
	    public static final String ITEM_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/vnd.swat.onthespot.content";
	    
}