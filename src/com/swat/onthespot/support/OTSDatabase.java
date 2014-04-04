package com.swat.onthespot.support;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.swat.onthespot.MainActivity;


public class OTSDatabase extends SQLiteOpenHelper {
	private static final String TAG = "OTSDatabase";
	
    // All Static variables
    // Database Version
    public static final int DATABASE_VERSION = 1;
 
    // Database Name
    public static final String DATABASE_NAME = "OTSDatabase";
 
    // Contacts table name
    public static final String TABLE_USERS = "Users";
    public static final String TABLE_USERS_ITINS = "UsersItins";
    public static final String TABLE_ITINS = "Itins";
    public static final String TABLE_ITINS_EXPS = "ItinsExps";
    public static final String TABLE_EXPS = "Exps";
    
    // Users Table Column Names
    public static final String USERS_KEY_ID = "uid";
    public static final String USERS_KEY_NAME = "uname";
    
    // Users-Itins Column Names
    public static final String USERS_ITINS_KEY_USRID = "uid";
    public static final String USERS_ITINS_KEY_ITINID = "iid";
    public static final String USERS_ITINS_KEY_SECTION = "section";
    
    // Itins Table Column Names
    public static final String ITINS_KEY_ID = "iid";
    public static final String ITINS_KEY_NAME = "iname";
    public static final String ITINS_KEY_DATE = "date";
    public static final String ITINS_KEY_RATE = "rating";
    public static final String ITINS_KEY_COMMENT = "comment";
    public static final String ITINS_KEY_IMAGE = "imagename";
    
    // Itins-Exps Table Column Names
    public static final String ITINS_EXPS_KEY_ITINID = "iid";
    public static final String ITINS_EXPS_KEY_EXPID = "eid";
    public static final String ITINS_EXPS_KEY_SORT = "sortorder";
    
    // Experiences Table Column Names
    public static final String EXPS_KEY_ID = "eid";
    public static final String EXPS_KEY_NAME = "ename";
    public static final String EXPS_KEY_ADDR = "address";
    public static final String EXPS_KEY_ACTION = "action";
    public static final String EXPS_KEY_RATE = "rating";
    public static final String EXPS_KEY_COMMENT = "comment";
    public static final String EXPS_KEY_IMAGE = "imagename";
    
    // Error codes (for read / write method exceptions).
    public static final int ERR_NO_USER_NAME = -1;
    public static final int ERR_NO_ITIN_NAME = -2;
    public static final int ERR_NO_EXP_NAME = -3;
    public static final int ERR_MULTIPLE_USER_NAME = -4;
    public static final int ERR_MULTIPLE_ITIN_NAME = -5;
    public static final int ERR_MULTIPLE_EXP_NAME = -6;
 
    private static OTSDatabase sInstance = null;
    
    // Static factory method for correct management
    public static OTSDatabase getInstance(Context context) {
    	// Use the application context, which will ensure that you 
    	// don't accidentally leak an Activity's context.
    	// See this article for more information: http://bit.ly/6LRzfx
    	if (sInstance == null) {
    		sInstance = new OTSDatabase(context.getApplicationContext());
    	}
    	
    	return sInstance;
    }
    
    private OTSDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create the Users Table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + USERS_KEY_ID + " INTEGER PRIMARY KEY, "
        		+ USERS_KEY_NAME + " TEXT"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);
        
        // Create the Users Itineraries Translation Table
        String CREATE_USERS_ITINS_TABLE = "CREATE TABLE " + TABLE_USERS_ITINS + "("
                + USERS_ITINS_KEY_USRID + " INTEGER, "
                + USERS_ITINS_KEY_ITINID + " INTEGER, "
                + USERS_ITINS_KEY_SECTION + " INTEGER, "
                + "PRIMARY KEY (" 
                  + USERS_ITINS_KEY_USRID + ", " + USERS_ITINS_KEY_ITINID+ "), " 
                + "FOREIGN KEY (" + USERS_ITINS_KEY_USRID + ") REFERENCES " + 
                  TABLE_USERS + "(" + USERS_KEY_ID + ") ON DELETE CASCADE, "
                + "FOREIGN KEY (" + USERS_ITINS_KEY_ITINID + ") REFERENCES " + 
                  TABLE_ITINS + "(" + ITINS_KEY_ID + ") ON DELETE NO ACTION" 
                + ")";
        
        // Create the Itineraries Table
        db.execSQL(CREATE_USERS_ITINS_TABLE);
        
        String CREATE_ITINS_TABLE = "CREATE TABLE " + TABLE_ITINS + "("
        		+ ITINS_KEY_ID + " INTEGER PRIMARY KEY, "
        		+ ITINS_KEY_NAME + " TEXT, "
        		+ ITINS_KEY_DATE + " TEXT, "
        		+ ITINS_KEY_RATE + " REAL, "
        		+ ITINS_KEY_COMMENT + " TEXT, "
        		+ ITINS_KEY_IMAGE + " TEXT"
        		+ ")";
        db.execSQL(CREATE_ITINS_TABLE);
        
        // Create the Itineraries - Experiences Translation Table
        String CREATE_ITINS_EXPS_TABLE = "CREATE TABLE " + TABLE_ITINS_EXPS + "("
                + ITINS_EXPS_KEY_ITINID + " INTEGER, "
                + ITINS_EXPS_KEY_EXPID + " INTEGER, "
                + ITINS_EXPS_KEY_SORT + " INTEGER, "
                + "PRIMARY KEY (" 
                  + ITINS_EXPS_KEY_ITINID + ", " + ITINS_EXPS_KEY_EXPID + "), " 
                + "FOREIGN KEY (" + ITINS_EXPS_KEY_ITINID + ") REFERENCES " + 
                  TABLE_ITINS + "(" + ITINS_KEY_ID + ") ON DELETE CASCADE, "
                + "FOREIGN KEY (" + ITINS_EXPS_KEY_EXPID + ") REFERENCES " + 
                  TABLE_EXPS + "(" + EXPS_KEY_ID + ") ON DELETE NO ACTION" 
                + ")";
        db.execSQL(CREATE_ITINS_EXPS_TABLE);
        
        // Create the Experiences Table
        String CREATE_EXPS_TABLE = "CREATE TABLE " + TABLE_EXPS + "("
        		+ EXPS_KEY_ID + " INTEGER PRIMARY KEY, "
        		+ EXPS_KEY_NAME + " TEXT, "
        		+ EXPS_KEY_ADDR + " TEXT, "
        		+ EXPS_KEY_ACTION + " TEXT, "
        		+ EXPS_KEY_RATE + " REAL, "
        		+ EXPS_KEY_COMMENT + " TEXT, "
        		+ EXPS_KEY_IMAGE + " TEXT"
        		+ ")";
        db.execSQL(CREATE_EXPS_TABLE);
        
        // Initialize Database
		addUser(db, MainActivity.USER_NAME);
   		addItinerary(db, "Houston Roadtrip", "20th Nov 2013", 3.5, 
				"Almost tripped on a tumbleweed.", "houston_roadtrip");
		addItinerary(db, "Exploring Philly 8th St", "8-9th Nov 2013", 4.5, 
				"Jim's Sticks was heaven in my belly.", "philly_8th_street");
		addItinerary(db, "New York or Bust!", "15th Dec 2013", 4.5, 
				"In all my years living on the East", "new_york_or_bust");
		addItinerary(db, "A Week in Madrid", "5th Jan 2014", 5, 
				"Spain is just drop-dead gorgeous, and ...", "a_week_in_madrid");
		sInstance.addItinForUser(db, MainActivity.USER_NAME, "Houston Roadtrip");
		sInstance.addItinForUser(db, MainActivity.USER_NAME, "Exploring Philly 8th St");
		sInstance.addItinForUser(db, MainActivity.USER_NAME, "New York or Bust!");
		sInstance.addItinForUser(db, MainActivity.USER_NAME, "A Week in Madrid");
		sInstance.addExperience(db, "King of Prussia mall", "160 N Gulph Rd, King of Prussia, PA 19406", 
				"Cologne Sampling", 4.5, "Lots of great fragrances with decent ...", "king_of_prussia");
		sInstance.addExperience(db, "One Liberty Place", "1625 Chestnut Street, Philadelphia, PA 19103", 
				"Watching the sunset", 5.0, "This is absolutely stunning!", "one_liberty_place");
		sInstance.addExperience(db, "Shake Shack", "2000 Sansom St, Philadelphia, PA 19103", 
				"Harlem Shaking", 3.0, "The locale was a little too approriate ...", "shake_shack");
		sInstance.addExperience(db, "Moo Tattoo", "513 South St #2, Philadelphia, PA 19147", 
				"Get Tattoo", 4.5, "Beautifully done. Couldn't have asked ...", "moo_tattoo");
		sInstance.addExpForItin(db, "Exploring Philly 8th St", "King of Prussia mall");
		sInstance.addExpForItin(db, "Exploring Philly 8th St", "One Liberty Place");
		sInstance.addExpForItin(db, "Exploring Philly 8th St", "Shake Shack");
		sInstance.addExpForItin(db, "Exploring Philly 8th St", "Moo Tattoo");
	}	
	
	// private helper methods for initializing database.
	private void addUser(SQLiteDatabase db, String Name){
		ContentValues values = new ContentValues();
		values.put(USERS_KEY_NAME, Name);
		db.insert(TABLE_USERS, null, values);
	}
	private void addItinerary(SQLiteDatabase db, String Name, String Date, double Rate, 
			String Comment, String ImageName){
		ContentValues values = new ContentValues();
		values.put(ITINS_KEY_NAME, Name);
		values.put(ITINS_KEY_DATE, Date);
		values.put(ITINS_KEY_RATE, Rate);
		values.put(ITINS_KEY_COMMENT, Comment);
		values.put(ITINS_KEY_IMAGE, ImageName);
		db.insert(TABLE_ITINS, null, values);
	}
	private void addExperience(SQLiteDatabase db, String Name, String Addr, String Action, 
			double Rate, String Comment, String ImageName){
		ContentValues values = new ContentValues();
		values.put(EXPS_KEY_NAME, Name);
		values.put(EXPS_KEY_ADDR, Addr);
		values.put(EXPS_KEY_ACTION, Action);
		values.put(EXPS_KEY_RATE, Rate);
		values.put(EXPS_KEY_COMMENT, Comment);
		values.put(EXPS_KEY_IMAGE, ImageName);
		db.insert(TABLE_EXPS, null, values);
	}
	private int addItinForUser(SQLiteDatabase db, String userName, String itinName){
		int[] uids = UserNameToIds(db, userName);
		int[] iids = ItinNameToIds(db, itinName);
		if (uids.length < 1){
			return ERR_NO_USER_NAME;
		}else if (iids.length < 1){
			return ERR_NO_ITIN_NAME;
		}else if (uids.length > 1){
			return ERR_MULTIPLE_USER_NAME;
		}else if (iids.length > 1){
			return ERR_MULTIPLE_ITIN_NAME;
		}else {
			ContentValues values = new ContentValues();
			values.put(USERS_ITINS_KEY_USRID, uids[0]);
			values.put(USERS_ITINS_KEY_ITINID, iids[0]);
			db.insertWithOnConflict(TABLE_USERS_ITINS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			return 0;
		}
	}
	private int addExpForItin(SQLiteDatabase db, String itinName, String expName){
		int[] iids = ItinNameToIds(db, itinName);
		int[] eids = ExpNameToIds(db, expName);
		if (iids.length < 1){
			return ERR_NO_ITIN_NAME;
		}else if (eids.length < 1){
			return ERR_NO_EXP_NAME;
		}else if (iids.length > 1){
			return ERR_MULTIPLE_ITIN_NAME;
		}else if (eids.length > 1){
			return ERR_MULTIPLE_EXP_NAME;
		}else {
			ContentValues values = new ContentValues();
			values.put(ITINS_EXPS_KEY_ITINID, iids[0]);
			values.put(ITINS_EXPS_KEY_EXPID, eids[0]);
			
			// Get the max sort-order, and add 1 to preserve uniqueness.
			String maxSortQuery = "SELECT IFNULL(MAX("+ ITINS_EXPS_KEY_SORT +"),0)+1 FROM " + 
					"(SELECT " + ITINS_EXPS_KEY_SORT + " FROM " + TABLE_ITINS_EXPS +
					" WHERE " + ITINS_EXPS_KEY_ITINID + "=" + String.valueOf(iids[0]) + ")";
			Cursor c = rawQuery(db, maxSortQuery, null);
			c.moveToFirst();
			
			//Peng: pay attention to the column name change!
			values.put(ITINS_EXPS_KEY_SORT, c.getInt(c.getColumnIndex("IFNULL(MAX("+ ITINS_EXPS_KEY_SORT +"),0)+1")));
			
			db.insertWithOnConflict(TABLE_ITINS_EXPS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			return 0;
		}
	}
	private int[] UserNameToIds(SQLiteDatabase db, String Name){
		Cursor cursor = db.query(TABLE_USERS, new String[]{USERS_KEY_ID},
				USERS_KEY_NAME + "=?", new String[]{Name}, null, null, null, null);
		
		if (cursor.moveToFirst()){
			int[] retval = new int[cursor.getCount()];
			int index = cursor.getColumnIndex(USERS_KEY_ID);
			while(!cursor.isAfterLast()) { // If you use c.moveToNext() here, you will bypass the first row, which is WRONG
				retval[cursor.getPosition()] = cursor.getInt(index);
				cursor.moveToNext();
			} 
			return retval;
		}
		else{
			return null;
		}
	}
	private int[] ItinNameToIds(SQLiteDatabase db, String Name){
		Cursor cursor = db.query(TABLE_ITINS, new String[]{ITINS_KEY_ID},
				ITINS_KEY_NAME + "=?", new String[]{Name}, null, null, null, null);
		
		if (cursor.moveToFirst()){
			int[] retval = new int[cursor.getCount()];
			int index = cursor.getColumnIndex(ITINS_KEY_ID);
			while(!cursor.isAfterLast()) { // If you use c.moveToNext() here, you will bypass the first row, which is WRONG
				retval[cursor.getPosition()] = cursor.getInt(index);
				cursor.moveToNext();
			} 
			return retval;
		}
		else{
			return null;
		}
	}
	private int[] ExpNameToIds(SQLiteDatabase db, String Name){
		Cursor cursor = db.query(TABLE_EXPS, new String[]{EXPS_KEY_ID},
				EXPS_KEY_NAME + "=?", new String[]{Name}, null, null, null, null);
		
		if (cursor.moveToFirst()){
			int[] retval = new int[cursor.getCount()];
			int index = cursor.getColumnIndex(EXPS_KEY_ID);
			while(!cursor.isAfterLast()) { // If you use c.moveToNext() here, you will bypass the first row, which is WRONG
				retval[cursor.getPosition()] = cursor.getInt(index);
				cursor.moveToNext();
			} 
			return retval;
		}
		else{
			return null;
		}
	}
	private Cursor rawQuery(SQLiteDatabase db, String query, String[] selectionArgs){
		return db.rawQuery(query, selectionArgs);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITINS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITINS_EXPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS_ITINS);
 
        // Create tables again
        onCreate(db);
	}
	
	public void addUser(String Name){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(USERS_KEY_NAME, Name);
		db.insert(TABLE_USERS, null, values);
		db.close();
	}
	
	public void addItinForUser(int UserId, int ItinId){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(USERS_ITINS_KEY_USRID, UserId);
		values.put(USERS_ITINS_KEY_ITINID, ItinId);
		db.insertWithOnConflict(TABLE_USERS_ITINS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		db.close();
	}
	
	public int addItinForUser(String userName, String itinName){
		int[] uids = UserNameToIds(userName);
		int[] iids = ItinNameToIds(itinName);
		if (uids.length < 1){
			return ERR_NO_USER_NAME;
		}else if (iids.length < 1){
			return ERR_NO_ITIN_NAME;
		}else if (uids.length > 1){
			return ERR_MULTIPLE_USER_NAME;
		}else if (iids.length > 1){
			return ERR_MULTIPLE_ITIN_NAME;
		}else {
			addItinForUser(uids[0], iids[0]);
			return 0;
		}
	}
	
	public void addItinerary(String Name, String Date, double Rate, String Comment, String ImageName){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ITINS_KEY_NAME, Name);
		values.put(ITINS_KEY_DATE, Date);
		values.put(ITINS_KEY_RATE, Rate);
		values.put(ITINS_KEY_COMMENT, Comment);
		values.put(ITINS_KEY_IMAGE, ImageName);
		db.insert(TABLE_ITINS, null, values);
		db.close();
	}
	
	public long addExpForItin(int ItinId, int ExpId){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ITINS_EXPS_KEY_ITINID, ItinId);
		values.put(ITINS_EXPS_KEY_EXPID, ExpId);
		
		// Get the max sort-order, and add 1 to preserve uniqueness.
		String maxSortQuery = "SELECT IFNULL(MAX("+ ITINS_EXPS_KEY_SORT +"),0)+1 FROM " + 
				"(SELECT " + ITINS_EXPS_KEY_SORT + " FROM " + TABLE_ITINS_EXPS +
				" WHERE " + ITINS_EXPS_KEY_ITINID + "=" + String.valueOf(ItinId) + ")";
		Cursor c = rawQuery(maxSortQuery, null);
		c.moveToFirst();
		
		//Peng: pay attention to the column name change!
		values.put(ITINS_EXPS_KEY_SORT, c.getInt(c.getColumnIndex("IFNULL(MAX("+ ITINS_EXPS_KEY_SORT +"),0)+1")));
		
		long retval;
		try{
			retval = db.insertWithOnConflict(TABLE_ITINS_EXPS, null, values, SQLiteDatabase.CONFLICT_ABORT);
		} catch (SQLiteConstraintException e){
			retval = -1;
		}
		db.close();
		return retval;
	}
	
	public int addExpForItin(String itinName, String expName){
		int[] iids = ItinNameToIds(itinName);
		int[] eids = ExpNameToIds(expName);
		if (iids.length < 1){
			return ERR_NO_ITIN_NAME;
		}else if (eids.length < 1){
			return ERR_NO_EXP_NAME;
		}else if (iids.length > 1){
			return ERR_MULTIPLE_ITIN_NAME;
		}else if (eids.length > 1){
			return ERR_MULTIPLE_EXP_NAME;
		}else {
			addExpForItin(iids[0], eids[0]);
			return 0;
		}
	}
	
	public void addExperience(String Name, String Addr, String Action, double Rate, String Comment, String ImageName){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(EXPS_KEY_NAME, Name);
		values.put(EXPS_KEY_ADDR, Addr);
		values.put(EXPS_KEY_ACTION, Action);
		values.put(EXPS_KEY_RATE, Rate);
		values.put(EXPS_KEY_COMMENT, Comment);
		values.put(EXPS_KEY_IMAGE, ImageName);
		db.insert(TABLE_EXPS, null, values);
		db.close();
	}
	
	public int[] UserNameToIds(String Name){
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_USERS, new String[]{USERS_KEY_ID},
				USERS_KEY_NAME + "=?", new String[]{Name}, null, null, null, null);
		
		if (cursor.moveToFirst()){
			int[] retval = new int[cursor.getCount()];
			int index = cursor.getColumnIndex(USERS_KEY_ID);
			while(!cursor.isAfterLast()) { // If you use c.moveToNext() here, you will bypass the first row, which is WRONG
				retval[cursor.getPosition()] = cursor.getInt(index);
				cursor.moveToNext();
			} 
			return retval;
		}
		else{
			return null;
		}
	}
	
	public int[] ItinNameToIds(String Name){
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_ITINS, new String[]{ITINS_KEY_ID},
				ITINS_KEY_NAME + "=?", new String[]{Name}, null, null, null, null);
		
		if (cursor.moveToFirst()){
			int[] retval = new int[cursor.getCount()];
			int index = cursor.getColumnIndex(ITINS_KEY_ID);
			while(!cursor.isAfterLast()) { // If you use c.moveToNext() here, you will bypass the first row, which is WRONG
				retval[cursor.getPosition()] = cursor.getInt(index);
				cursor.moveToNext();
			} 
			return retval;
		}
		else{
			return null;
		}
	}
	
	public int[] ExpNameToIds(String Name){
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_EXPS, new String[]{EXPS_KEY_ID},
				EXPS_KEY_NAME + "=?", new String[]{Name}, null, null, null, null);
		
		if (cursor.moveToFirst()){
			int[] retval = new int[cursor.getCount()];
			int index = cursor.getColumnIndex(EXPS_KEY_ID);
			while(!cursor.isAfterLast()) { // If you use c.moveToNext() here, you will bypass the first row, which is WRONG
				retval[cursor.getPosition()] = cursor.getInt(index);
				cursor.moveToNext();
			} 
			return retval;
		}
		else{
			return null;
		}
	}
	
	// Wrapper Functions.
	
	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, 
			String groupBy, String having, String orderBy, String limit){
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
	}
	
	public Cursor rawQuery(String query, String[] selectionArgs){
		SQLiteDatabase db = this.getReadableDatabase();
		return db.rawQuery(query, selectionArgs);
	}
	
	public int update(String table, ContentValues values, String whereClause, 
			String[] whereArgs){
		SQLiteDatabase db = this.getWritableDatabase();
		return db.update(table, values, whereClause, whereArgs);
	}
	
	public int updateWithOnConflict(String table, ContentValues values, String whereClause, 
			String[] whereArgs, int conflictAlgorithm){
		SQLiteDatabase db = this.getWritableDatabase();
		return db.updateWithOnConflict(table, values, whereClause, whereArgs, conflictAlgorithm);
	}
	
	public int delete(String table, String whereClause, String[] whereArgs){
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(table, whereClause, whereArgs);
	}
	
	public void execSQL (String sql){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(sql);
	}
	
	public void execSQL (String sql, Object[] bindArgs){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(sql, bindArgs);
	}
}
