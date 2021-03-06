package ca.ualberta.ece492.g9.biolock.customs;

import java.util.ArrayList;
import java.util.List;

import ca.ualberta.ece492.g9.biolock.types.LockInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "BioLock";
	private static final String TABLE_LOCKS = "LockInfo";
	private static final String KEY_ID = "ip";
	private static final String KEY_NAME = "name";
	private static final String KEY_ADMIN = "admin";
	private static final String KEY_USERPRINT = "userPrint";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOCKS_TABLE = "CREATE TABLE " + TABLE_LOCKS + "("
				+ KEY_ID + " STRING PRIMARY KEY, " + KEY_NAME + " TEXT, " + KEY_ADMIN +  " INTEGER, " + KEY_USERPRINT +  " INTEGER" + ")";
		db.execSQL(CREATE_LOCKS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCKS);

		// Create tables again
		onCreate(db);
	}

	// Adding new lock
	public long addLock(LockInfo lock) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, lock.getName()); // Lock Name
		values.put(KEY_ID, lock.getIP()); // Lock ip address
		values.put(KEY_ADMIN, lock.getAdmin()); // Check if this device belongs to admin
		values.put(KEY_USERPRINT, lock.getUserPrint()); // Check which user owns this device

		// Inserting Row
		long check = db.insert(TABLE_LOCKS, null, values);
		db.close(); // Closing database connection
		return check;
	}

	// Getting all locks
	public List<LockInfo> getAllLocks() {
		List<LockInfo> lockList = new ArrayList<LockInfo>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_LOCKS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				LockInfo lock = new LockInfo();
				lock.setIP(cursor.getString(0));
				lock.setName(cursor.getString(1));
				lock.setAdmin(cursor.getInt(2));
				lock.setUserPrint(cursor.getInt(3));
				// Adding contact to list
				lockList.add(lock);
			} while (cursor.moveToNext());
		}

		// return contact list
		return lockList;
	}

	// Getting locks count
	public int getLockCount() {
		String countQuery = "SELECT  * FROM " + TABLE_LOCKS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}

	// Deleting single lock
	public void deleteLock(LockInfo lock) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_LOCKS, KEY_ID + " = ?",
				new String[] { String.valueOf(lock.getIP()) });
		db.close();
	}
	
	// Getting single lock
	public LockInfo getLock(String ipAddress) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_LOCKS, new String[] { KEY_NAME, KEY_ADMIN, KEY_USERPRINT }, KEY_ID
				+ "=?", new String[] { ipAddress }, null, null, null, null);
		if (cursor.getCount() > 0){
			cursor.moveToFirst();
			LockInfo lock = new LockInfo(ipAddress, cursor.getString(0), cursor.getInt(1), cursor.getInt(2));
			// return lock
			return lock;
		} else {
			return null;
		}	
	}
	
	public int updateLock(LockInfo lock){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID, lock.getIP());
		values.put(KEY_NAME, lock.getName());
		values.put(KEY_ADMIN, lock.getAdmin());
		values.put(KEY_USERPRINT, lock.getUserPrint());
		
		return db.update(TABLE_LOCKS, values, KEY_ID + " = ?", new String[] { lock.getIP() });
	}
}
