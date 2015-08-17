package com.fanchen.aisou.db;

import java.util.ArrayList;
import java.util.List;

import com.fanchen.aisou.bean.LightNovelBean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CollectDao {
	private CollectHelper dbHelper;

	public CollectDao(Context context) {
		dbHelper = new CollectHelper(context);
	}

	public List<LightNovelBean> getAllCollect() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select imageUrl,bookUrl,bookTitle,userName from collect_info";
		Cursor cursor = db.rawQuery(sql, null);
		List<LightNovelBean> mBeans = new ArrayList<LightNovelBean>();
		while (cursor.moveToNext()) {
			String imageUrl = cursor.getString(0);
			String bookUrl = cursor.getString(1);
			String bookTitle = cursor.getString(2);
			String user = cursor.getString(3);
			LightNovelBean bean = new LightNovelBean();
			bean.setUrl(bookUrl);
			bean.setImage(imageUrl);
			bean.setTitle(bookTitle);
			bean.setAuthor(user);
			mBeans.add(bean);
		}
		cursor.close();
		db.close();
		return mBeans;
	}

	public LightNovelBean getCollectByBookUrl(String url) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select imageUrl,bookUrl,bookTitle,userName from collect_info where bookUrl=?";
		Cursor cursor = db.rawQuery(sql, new String[] { url });
		LightNovelBean bean = null;
		while (cursor.moveToNext()) {
			bean = new LightNovelBean();
			String imageUrl = cursor.getString(0);
			String bookUrl = cursor.getString(1);
			String bookTitle = cursor.getString(2);
			String user = cursor.getString(3);
			bean.setUrl(bookUrl);
			bean.setAuthor(user);
			bean.setImage(imageUrl);
			bean.setTitle(bookTitle);
		}
		cursor.close();
		db.close();
		return bean;
	}
	
	public LightNovelBean getCollectByUrlAndUser(String url,String user){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select imageUrl,bookUrl,bookTitle,userName from collect_info where bookUrl=? and userName=?";
		Cursor cursor = db.rawQuery(sql, new String[] { url ,user});
		LightNovelBean bean = null;
		while (cursor.moveToNext()) {
			bean = new LightNovelBean();
			String imageUrl = cursor.getString(0);
			String bookUrl = cursor.getString(1);
			String bookTitle = cursor.getString(2);
			String userName = cursor.getString(3);
			bean.setUrl(bookUrl);
			bean.setAuthor(userName);
			bean.setImage(imageUrl);
			bean.setTitle(bookTitle);
		}
		cursor.close();
		db.close();
		return bean;
	}

	public void addCollect(LightNovelBean bean) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "insert into collect_info (imageUrl,bookUrl,bookTitle,userName) values (?,?,?,?)";
		Object[] bindArgs = { bean.getImage(), bean.getUrl(), bean.getTitle() ,bean.getAuthor()};
		database.execSQL(sql, bindArgs);
		database.close();
	}

	public void addAllCollect(List<LightNovelBean> beans) {
		for (int i = 0; i < beans.size(); i++) {
			addCollect(beans.get(i));
		}
	}

	public void deleteCollectByBookUrl(String url) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "delete from collect_info where bookUrl=?";
		Object[] bindArgs = { url };
		db.execSQL(sql, bindArgs);
		db.close();
	}

	public void deleteCollect(LightNovelBean bean) {
		deleteCollectByBookUrl(bean.getUrl());
	}

	public void deleteAll() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "delete from collect_info";
		db.execSQL(sql);
		db.close();
	}
}
