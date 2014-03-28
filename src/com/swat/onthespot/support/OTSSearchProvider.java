package com.swat.onthespot.support;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class OTSSearchProvider extends ContentProvider {
	
	// Tag for LogCat
	private final String TAG = "OTSSearchProvider";
	
	// URI stuff
	public static final String AUTHORITY = "com.swat.onthespot.support.otssearchprovider";
	public static final String TABLE_SEARCH = "search";
	
	// MIME Types. In sample code, but don't see why it's useful.
    public static final String DIR_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "/vnd.swat.onthespot.search";
    public static final String ITEM_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
            "/vnd.swat.onthespot.search";
    
	// The Database Instance
    private OTSDatabase mDatabase;
    
    // Uri Matcher and Matching Types
    private static final int SEARCH_ITEM = 0;
    private static final int SEARCH_LIST = 1;
    private static final int SEARCH_SUGGEST = 2;
    private static final int REFRESH_SHORTCUT = 3;
    private static final UriMatcher sURIMatcher = buildUriMatcher();
    
    
	@Override
	public boolean onCreate() {
		// Implement this to initialize your content provider on startup.
        mDatabase = OTSDatabase.getInstance(getContext());
        return true;
	}
	
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
        
        // searching stuff ...
        matcher.addURI(AUTHORITY, TABLE_SEARCH, SEARCH_LIST);
        matcher.addURI(AUTHORITY, TABLE_SEARCH + "/#", SEARCH_ITEM);
        
        // to get suggestions ...
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

        /* The following are unused in this implementation, but if we include
         * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions table, we
         * could expect to receive refresh queries when a shortcutted suggestion is displayed in
         * Quick Search Box, in which case, the following Uris would be provided and we
         * would return a cursor with a single item representing the refreshed suggestion data.
         */
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
        return matcher;
    }
    
	@Override
	public String getType(Uri uri) {
		// Implement this to handle requests for the MIME type of the data
		// at the given URI.
        switch (sURIMatcher.match(uri)) {
        case SEARCH_LIST:
            return DIR_MIME_TYPE;
        case SEARCH_ITEM:
            return ITEM_MIME_TYPE;
        case SEARCH_SUGGEST:
            return SearchManager.SUGGEST_MIME_TYPE;
        case REFRESH_SHORTCUT:
            return SearchManager.SHORTCUT_MIME_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URL " + uri);
        }
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Implement this to handle query requests from clients.
        // Use the UriMatcher to see what kind of query we have and format the db query accordingly
        switch (sURIMatcher.match(uri)) {
        	// Uri indicates it's a search suggestion query. 
            case SEARCH_SUGGEST:
                if (selectionArgs == null) {
                  throw new IllegalArgumentException(
                      "selectionArgs must be provided for the Uri: " + uri);
                }
                return getSuggestions(selectionArgs[0]);
            
            // Uri indicates it's a search query for multiple items.
            case SEARCH_LIST:
                if (selectionArgs == null) {
                  throw new IllegalArgumentException(
                      "selectionArgs must be provided for the Uri: " + uri);
                }
                return search(selectionArgs[0]);
            
            // Uri indicates it's a search query for single item.
            case SEARCH_ITEM:
                return getSingleExperience(uri);
                
            // Uri indicates it's a request for refreshing global search shortcuts.
            case REFRESH_SHORTCUT:
            	throw new IllegalArgumentException("Currently doesn't support refreshing shortcut!");
                //return refreshShortcut(uri);
            
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
	}
	
    private Cursor getSuggestions(String query) {
        query = query.toLowerCase();
        
        String selection = 
        		OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ID + 
        			" AS " + BaseColumns._ID + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_NAME + 
					" AS " + SearchManager.SUGGEST_COLUMN_TEXT_1 + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ADDR + 
					" AS " + SearchManager.SUGGEST_COLUMN_TEXT_2 + " , " +
				OTSDatabase.TABLE_EXPS + "." + "ROWID" + 
        			" AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID;

		String SQL = "SELECT " + selection + " FROM " + OTSDatabase.TABLE_EXPS + " WHERE " + 
				"LOWER(" + OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_NAME + ")" 
					+ " LIKE '%" + query + "%'";
				/*
				+ " OR " +
				"LOWER(" + OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ACTION + ")" 
					+ " LIKE '%" + query + "%' OR " +
				"LOWER(" + OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ADDR + ")" 
					+ " LIKE '%" + query + "%'";
				*/
		
        Cursor results = mDatabase.rawQuery(SQL, null);
        /*
        String[] cols = results.getColumnNames();
        for (int i=0; i< cols.length; i++){
        	Log.d(TAG, "suggest result col " + i + " = " + cols[i]);
        }
        */
        return results;
    }
    
    private Cursor search(String query) {
        query = query.toLowerCase();
        
        String selection =	OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ID + 
    			" AS " + BaseColumns._ID + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_NAME + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ADDR + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ACTION + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_RATE + " , " + 
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_COMMENT + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_IMAGE;
        
		String SQL = "SELECT " + selection + " FROM " + OTSDatabase.TABLE_EXPS + " WHERE " + 
				"LOWER(" + OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_NAME + ")" 
					+ " LIKE '%" + query + "%'";
				/*
				+ " OR " +
				"LOWER(" + OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ACTION + ")" 
					+ " LIKE '%" + query + "%' OR " +
				"LOWER(" + OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ADDR + ")" 
					+ " LIKE '%" + query + "%'";
				*/
		
        return mDatabase.rawQuery(SQL, null);
    }
    
    private Cursor getSingleExperience(Uri uri) {
        String rowId = uri.getLastPathSegment();
        String selection =	OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ID + 
    			" AS " + BaseColumns._ID + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_NAME + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ADDR + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_ACTION + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_RATE + " , " + 
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_COMMENT + " , " +
				OTSDatabase.TABLE_EXPS + "." + OTSDatabase.EXPS_KEY_IMAGE;
        
		String SQL = "SELECT " + selection + " FROM " + OTSDatabase.TABLE_EXPS + 
				" WHERE ROWID = " + rowId;
		
		return mDatabase.rawQuery(SQL, null);
    }
    
	// Peng - Other methods are not yet implemented.
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// Implement this to handle requests to insert a new row.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Implement this to handle requests to delete one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// Implement this to handle requests to update one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
