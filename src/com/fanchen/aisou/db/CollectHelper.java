package com.fanchen.aisou.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CollectHelper extends SQLiteOpenHelper {
	public CollectHelper(Context context) {
		super(context, "collect.db", null, 1);
	}

	public CollectHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public CollectHelper(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table collect_info(_id integer PRIMARY KEY AUTOINCREMENT,userName char, imageUrl char,bookUrl char,bookTitle char)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
