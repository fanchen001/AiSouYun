package com.fanchen.aisou.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DownloadLogHelper extends SQLiteOpenHelper {
	public DownloadLogHelper(Context context) {
		super(context, "download_log.db", null, 1);
	}

	public DownloadLogHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public DownloadLogHelper(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table download_log(_id integer PRIMARY KEY AUTOINCREMENT,bookUrl char,bookTitle char, imageUrl char,bookPath char,bookAuthor char,bookFrom char,bookState int)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
