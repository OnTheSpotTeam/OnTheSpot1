package com.swat.onthespot.support;

import com.swat.onthespot.MainActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class OTSDatabase extends SQLiteOpenHelper {
	
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
    
    // Itins Table Column Names
    public static final String ITINS_KEY_ID = "iid";
    public static final String ITINS_KEY_NAME = "iname";
    public static final String ITINS_KEY_DATE = "date";
    public static final String ITINS_KEY_RATE = "rating";
    public static final String ITINS_KEY_COMMENT = "comment";
    
    // Itins-Exps Table Column Names
    public static final String ITINS_EXPS_KEY_ITINID = "iid";
    public static final String ITINS_EXPS_KEY_EXPID = "eid";
    
    // Experiences Table Column Names
    public static final String EXPS_KEY_ID = "eid";
    public static final String EXPS_KEY_NAME = "ename";
    public static final String EXPS_KEY_ADDR = "address";
    public static final String EXPS_KEY_ACTION = "action";
    public static final String EXPS_KEY_RATE = "rating";
    public static final String EXPS_KEY_COMMENT = "comment";
    
    // Error codes (for read / write method exceptions).
    public static final int ERR_NO_USER_NAME = -1;
    public static final int ERR_NO_ITIN_NAME = -2;
    public static final int ERR_NO_EXP_NAME = -3;
    public static final int ERR_MULTIPLE_USER_NAME = -4;
    public static final int ERR_MULTIPLE_ITIN_NAME = -5;
    public static final int ERR_MULTIPLE_EXP_NAME = -6;
 
    private static OTSDatabase sInstance;
    
    // Static factory method for correct management
    public static OTSDatabase getInstance(Context context) {
        // Use the application context, which will ensure that you 
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
          sInstance = new OTSDatabase(context.getApplicationContext());
          sInstance.addUser(MainActivity.USER_NAME);
          sInstance.addItinerary("Houston Roadtrip", "20th Nov 2013", 3.5, "Almost tripped on a tumbleweed.");
          sInstance.addItinerary("Exploring Philly 8th St", "8-9th Nov 2013", 4.5, "Jim's Sticks was heaven in my belly.");
          sInstance.addItinerary("New York or Bust!", "15th Dec 2013", 4.5, "In all my years living on the East");
          sInstance.addItinerary("A Week in Madrid", "5th Jan 2014", 5, "Spain is just drop-dead gorgeous, and ...");
          sInstance.addItinForUser(MainActivity.USER_NAME, "Houston Roadtrip");
          sInstance.addItinForUser(MainActivity.USER_NAME, "Exploring Philly 8th St");
          sInstance.addItinForUser(MainActivity.USER_NAME, "New York or Bust!");
          sInstance.addItinForUser(MainActivity.USER_NAME, "A Week in Madrid");
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
                + "PRIMARY KEY (" 
                  + USERS_ITINS_KEY_USRID + ", " + USERS_ITINS_KEY_ITINID
                + "))";
        
        // Create the Itineraries Table
        db.execSQL(CREATE_USERS_ITINS_TABLE);
        
        String CREATE_ITINS_TABLE = "CREATE TABLE " + TABLE_ITINS + "("
        		+ ITINS_KEY_ID + " INTEGER PRIMARY KEY, "
        		+ ITINS_KEY_NAME + " TEXT, "
        		+ ITINS_KEY_DATE + " TEXT, "
        		+ ITINS_KEY_RATE + " REAL, "
        		+ ITINS_KEY_COMMENT + " TEXT"
        		+ ")";
        db.execSQL(CREATE_ITINS_TABLE);
        
        // Create the Itineraries - Experiences Translation Table
        String CREATE_ITINS_EXPS_TABLE = "CREATE TABLE " + TABLE_ITINS_EXPS + "("
                + ITINS_EXPS_KEY_ITINID + " INTEGER, "
                + ITINS_EXPS_KEY_EXPID + " INTEGER, "
                + "PRIMARY KEY (" 
                  + ITINS_EXPS_KEY_ITINID + ", " + ITINS_EXPS_KEY_EXPID
                + "))";
        db.execSQL(CREATE_ITINS_EXPS_TABLE);
        
        // Create the Experiences Table
        String CREATE_EXPS_TABLE = "CREATE TABLE " + TABLE_EXPS + "("
        		+ EXPS_KEY_ID + " INTEGER PRIMARY KEY, "
        		+ EXPS_KEY_NAME + " TEXT, "
        		+ EXPS_KEY_ADDR + " TEXT, "
        		+ EXPS_KEY_ACTION + " TEXT, "
        		+ EXPS_KEY_RATE + " REAL, "
        		+ EXPS_KEY_COMMENT + " TEXT"
        		+ ")";
        db.execSQL(CREATE_EXPS_TABLE);
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
	
	public void addItinerary(String Name, String Date, double Rate, String Comment){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ITINS_KEY_NAME, Name);
		values.put(ITINS_KEY_DATE, Date);
		values.put(ITINS_KEY_RATE, Rate);
		values.put(ITINS_KEY_COMMENT, Comment);
		db.insert(TABLE_ITINS, null, values);
		db.close();
	}
	
	public void addExpForItin(int ItinId, int ExpId){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ITINS_EXPS_KEY_ITINID, ItinId);
		values.put(ITINS_EXPS_KEY_EXPID, ExpId);
		db.insertWithOnConflict(TABLE_ITINS_EXPS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		db.close();
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
	
	public void addExperience(String Name, String Addr, String Action, double Rate, String Comment){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(EXPS_KEY_NAME, Name);
		values.put(EXPS_KEY_ADDR, Addr);
		values.put(EXPS_KEY_ACTION, Action);
		values.put(EXPS_KEY_RATE, Rate);
		values.put(EXPS_KEY_COMMENT, Comment);
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
	
	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, 
			String groupBy, String having, String orderBy, String limit){
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
	}
	
	public Cursor rawQuery(String query, String[] selectionArgs){
		SQLiteDatabase db = this.getReadableDatabase();
		return db.rawQuery(query, selectionArgs);
	}
	
}
