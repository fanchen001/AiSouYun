package com.fanchen.aisou.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 搜索记录的OpenHelper
 * @author Administrator
 *
 */
public class HistoryHelper extends SQLiteOpenHelper{

	public HistoryHelper(Context context) {
		super(context, "history.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String sql = "create table history_info(_id integer PRIMARY KEY AUTOINCREMENT, history_day char,history_char char)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}


	
}
