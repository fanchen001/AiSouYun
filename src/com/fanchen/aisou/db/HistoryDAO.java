package com.fanchen.aisou.db;

import java.util.ArrayList;
import java.util.List;

import com.fanchen.aisou.bean.HistoryBean;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * 搜索记录的dao
 * @author Administrator
 *
 */
public class HistoryDAO {
	private HistoryHelper dbHelper;

	public HistoryDAO(Context context)
	{
		dbHelper = new HistoryHelper(context);
	}
	
	/**
	 * 添加一条搜索记录
	 * @param historyDay 日期
	 * @param historyChar 词语
	 */
	public void addHistoryChar(String historyDay,String historyChar){
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql="insert into history_info (history_day,history_char) values (?,?)";
		Object[] bindArgs =
		{ historyDay,historyChar };
		database.execSQL(sql, bindArgs);
		database.close();
	}
	/**
	 * 返回所有的搜索记录
	 * @return
	 */
	public List<HistoryBean>  getAllHistory(){
		
		List<HistoryBean> list=new ArrayList<HistoryBean>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select history_day,history_char from history_info";
		Cursor cursor = db.rawQuery(sql,null);
		String oldDay="";
		List<String> mChar=null;
		HistoryBean mHistoryBean=null;
		while (cursor.moveToNext())
		{
			String day = cursor.getString(0);
			if(oldDay.endsWith(day)){
				mChar.add(cursor.getString(1));
			}else{
				mHistoryBean=new HistoryBean();
				mChar=new ArrayList<String>();
				mChar.add(cursor.getString(1));
				mHistoryBean.setmChar(mChar);
				mHistoryBean.setDay(day);
				list.add(mHistoryBean);
				oldDay=day;
			}
		}
		cursor.close();
		db.close();
		return list;
	}
	
	
	/**
	 * 获取指定日期的搜索词列表
	 * @param day
	 * @return
	 */
	public List<String> getCharByDay(String day){
		List<String> list=new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("history_info", new String[]{"history_char"}, "history_day=?", new String[]{day}, null, null, null);
		while (cursor.moveToNext()){
			String mchar = cursor.getString(0);
			list.add(mchar);
		}
		cursor.close();
		db.close();
		return list;
	}
	
	
	public  void deleteAll(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "delete from history_info";
		db.execSQL(sql);
		db.close();
	}
}
