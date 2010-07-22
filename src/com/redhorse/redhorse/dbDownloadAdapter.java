package com.redhorse.redhorse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * DB format: _id, type, title, url
 */

/*
 * Database operator class, to create, open, use, and close DB. *
 */
public class dbDownloadAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_TYPE = "type";
	public static final String KEY_TITLE = "title";
	public static final String KEY_URL = "url";
	public static final String KEY_FILE = "file";
	public static final String KEY_STATUS = "status";
	private static final String TAG = "Download";
	private static final String DATABASE_NAME = "download";
	private static final String DATABASE_TABLE = "download";
	private static final int DATABASE_VERSION = 1;

	/*
	 * create table SQL
	 */
	private static final String DATABASE_CREATE = "create table download (_id integer primary key autoincrement, "
			+ "type text not null, title text not null, "
			+ "url text not null, file text not null, status text not null);";

	private final Context context;

	// DB assistant instance

	private DatabaseHelper DBHelper;

	// DB instance

	private SQLiteDatabase db;

	/*
	 * DBAdapter constructor
	 */
	public dbDownloadAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	/*
	 * DB help class, it is a DB assistant class You will need to override
	 * onCreate() and onUpgrade() method.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}
	}// end of DatabaseHelper

	/*****************************************************
	 * Below are all DBAdaptor method: create, open...
	 ****************************************************/

	/*
	 * Open DB
	 */
	public dbDownloadAdapter open() throws SQLException {
		// get a DB through DB assistant

		db = DBHelper.getWritableDatabase();
		return this;
	}

	/*
	 * close DB
	 */
	public void close() {
		// close DB through DB assistant

		DBHelper.close();
	}

	/*
	 * Insert one title
	 */
	public long insertTitle(String type, String title, String url, String file, String status) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TYPE, type);
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_URL, url);
		initialValues.put(KEY_FILE, file);
		initialValues.put(KEY_STATUS, status);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	/*
	 * Delete one title
	 */
	public boolean deleteTitle(String rowId) {
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/*
	 * Query all titles
	 */
	public Cursor getAllTitles() {
		return db.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TYPE,
				KEY_TITLE, KEY_URL, KEY_FILE, KEY_STATUS }, null, null, null, null, null);
	}

	/*
	 * Query a specified title
	 */
	public Cursor getTitle(long rowId) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_TYPE, KEY_TITLE, KEY_URL, KEY_FILE, KEY_STATUS }, KEY_ROWID + "="
				+ rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/*
	 * update a title
	 */
	public boolean updateTitle(long rowId, String type, String title, String url, String file, String status) {
		ContentValues args = new ContentValues();
		args.put(KEY_TYPE, type);
		args.put(KEY_TITLE, title);
		args.put(KEY_URL, url);
		args.put(KEY_FILE, file);
		args.put(KEY_STATUS, status);
		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
}