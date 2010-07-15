package com.redhorse.redhorse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbConfigKeyValueHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME="config";
	private final static int DATABASE_VERSION=1;
	private final static String TABLE_NAME="var";
	public final static String FIELD_ID="_id"; 
	public final static String FIELD_KEY="key";
	public final static String FIELD_VALUE="value";
	
	
	public dbConfigKeyValueHelper(Context context)
	{
		super(context, DATABASE_NAME,null, DATABASE_VERSION);
	}
	
	
	 
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql="Create table "+TABLE_NAME+"("+FIELD_ID+" integer primary key autoincrement,"
		+FIELD_KEY+" text unique, "+FIELD_VALUE+" text);";
		db.execSQL(sql);
		
		 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql=" DROP TABLE IF EXISTS "+TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	public Cursor select(String key)
	{
		SQLiteDatabase db=this.getReadableDatabase();
		String where=FIELD_KEY+"=?";
		String[] whereValue={key};
		Cursor cursor=db.query(TABLE_NAME, null, where, whereValue, null, null,  " _id desc");
		return cursor;
	}
	
	public long insert(String key, String value)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues cv=new ContentValues(); 
		cv.put(FIELD_KEY, key);
		cv.put(FIELD_VALUE, value);
		long row=db.insert(TABLE_NAME, null, cv);
		return row;
	}
	
	public void delete(String key)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		String where=FIELD_ID+"='?'";
		String[] whereValue={key};
		db.delete(TABLE_NAME, where, whereValue);
	}
	
	public void update(String key, String value)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		String where=FIELD_KEY+"='?'";
		String[] whereValue={key};
		ContentValues cv=new ContentValues(); 
		cv.put(FIELD_KEY, key);
		cv.put(FIELD_VALUE, value);
		db.update(TABLE_NAME, cv, where, whereValue);
	}
	
	
	
	
}
