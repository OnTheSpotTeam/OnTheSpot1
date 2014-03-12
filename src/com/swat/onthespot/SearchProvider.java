package com.swat.onthespot;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class SearchProvider extends ContentProvider {
	
	private final String TAG = "OTS:SearchProvider";
	
	// The Database
    private SearchDatabase mDatabase;
    
	@Override
	public boolean onCreate() {
		// Implement this to initialize your content provider on startup.
        mDatabase = new SearchDatabase(getContext());
        return true;
	}
	
    // Uri Matcher Stuff
    private static final int SEARCH_WORDS = 0;
    private static final int GET_WORD = 1;
    private static final int SEARCH_SUGGEST = 2;
    private static final int REFRESH_SHORTCUT = 3;
    private static final UriMatcher sURIMatcher = buildUriMatcher();
	
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
        // to get definitions...
        matcher.addURI(SearchContract.AUTHORITY, SearchContract.TABLENAME, SEARCH_WORDS);
        matcher.addURI(SearchContract.AUTHORITY, SearchContract.TABLENAME + "/#", GET_WORD);
        
        // to get suggestions...
        matcher.addURI(SearchContract.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(SearchContract.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

        /* The following are unused in this implementation, but if we include
         * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions table, we
         * could expect to receive refresh queries when a shortcutted suggestion is displayed in
         * Quick Search Box, in which case, the following Uris would be provided and we
         * would return a cursor with a single item representing the refreshed suggestion data.
         */
        matcher.addURI(SearchContract.AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
        matcher.addURI(SearchContract.AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
        return matcher;
    }

	@Override
	public String getType(Uri uri) {
		// Implement this to handle requests for the MIME type of the data
		// at the given URI.
        switch (sURIMatcher.match(uri)) {
        case SEARCH_WORDS:
            return SearchContract.DIR_MIME_TYPE;
        case GET_WORD:
            return SearchContract.ITEM_MIME_TYPE;
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
            case SEARCH_SUGGEST:
                if (selectionArgs == null) {
                  throw new IllegalArgumentException(
                      "selectionArgs must be provided for the Uri: " + uri);
                }
                return getSuggestions(selectionArgs[0]);
            case SEARCH_WORDS:
                if (selectionArgs == null) {
                  throw new IllegalArgumentException(
                      "selectionArgs must be provided for the Uri: " + uri);
                }
                return search(selectionArgs[0]);
            case GET_WORD:
                return getWord(uri);
            case REFRESH_SHORTCUT:
                return refreshShortcut(uri);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
	}
	
    private Cursor getSuggestions(String query) {
        query = query.toLowerCase();
        String[] columns = new String[] {
            BaseColumns._ID,
            SearchContract.COL_NAME,
            SearchContract.COL_TYPE,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA
         /* SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
                          (only if you want to refresh shortcuts) */
            };

        Cursor results =  mDatabase.getWordMatches(query, columns);
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
        String[] columns = new String[] {
            BaseColumns._ID,
            SearchContract.COL_NAME,
            SearchContract.COL_TYPE};

        return mDatabase.getWordMatches(query, columns);
      }

      private Cursor getWord(Uri uri) {
        String rowId = uri.getLastPathSegment();
        String[] columns = new String[] {
            SearchContract.COL_NAME,
            SearchContract.COL_TYPE};

        return mDatabase.getWord(rowId, columns);
      }

      private Cursor refreshShortcut(Uri uri) {
        /* This won't be called with the current implementation, but if we include
         * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions table, we
         * could expect to receive refresh queries when a shortcutted suggestion is displayed in
         * Quick Search Box. In which case, this method will query the table for the specific
         * word, using the given item Uri and provide all the columns originally provided with the
         * suggestion query.
         */
        String rowId = uri.getLastPathSegment();
        String[] columns = new String[] {
            BaseColumns._ID,
            SearchContract.COL_NAME,
            SearchContract.COL_TYPE,
            SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};

        return mDatabase.getWord(rowId, columns);
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
