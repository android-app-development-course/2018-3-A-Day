package com.example.bottombar.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.bottombar.UserInfo;

public class SQLiteUtils {
	public static final String DATABASE_NAME = "a_day_db";

	public static final String DATETIME = "datetime";
    public static final String CONTENT = "content";
	
		public static DatabaseHelper createDBHelper(Context context) {
			DatabaseHelper dbHelper = new DatabaseHelper(context,DATABASE_NAME);
			return dbHelper;
		}

		public void insert(DatabaseHelper dbHelper,UserInfo user) {
			ContentValues values = new ContentValues();
			values.put("datetime", user.getDatetime());
			values.put("content",user.getContent());
			values.put("alerttime",user.getAlerttime());
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.insert("user", null, values);
			db.close();
		}
    

		public void update(DatabaseHelper dbHelper) {
			
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("content", "a_day");
			db.update("user", values, "id=?", new String[]{"1"});
			db.close();
		}

		public void delete(DatabaseHelper dbHelper,String datetime){
			
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			db.execSQL("DELETE FROM " + "user" + " WHERE datetime="+ datetime);
			db.close();
		}
}
