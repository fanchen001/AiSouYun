package com.fanchen.aisou.db;

import java.util.ArrayList;
import java.util.List;

import com.fanchen.aisou.bean.DownloadLogBean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DownloadLogDao {
	private DownloadLogHelper dbHelper;

	public DownloadLogDao(Context context) {
		dbHelper = new DownloadLogHelper(context);
	}

	private List<DownloadLogBean> getAllDownloadLog() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select bookTitle,imageUrl,bookPath,bookAuthor,bookFrom,bookState,bookUrl from download_log";
		Cursor cursor = db.rawQuery(sql, null);
		List<DownloadLogBean> mBeans = new ArrayList<DownloadLogBean>();
		while (cursor.moveToNext()) {
			String bookTitle = cursor.getString(0);
			String imageUrl = cursor.getString(1);
			String bookPath = cursor.getString(2);
			String bookAuthor = cursor.getString(3);
			String bookFrom = cursor.getString(4);
			int bookState = cursor.getInt(5);
			String bookUrl = cursor.getString(6);
			DownloadLogBean bean = new DownloadLogBean();
			bean.setAuthor(bookAuthor);
			bean.setFrom(bookFrom);
			bean.setImage(imageUrl);
			bean.setPath(bookPath);
			bean.setState(bookState);
			bean.setTitle(bookTitle);
			bean.setUrl(bookUrl);
			mBeans.add(bean);
		}
		cursor.close();
		db.close();
		return mBeans;
	}

	public List<DownloadLogBean> getAllDownloadSuccessLog() {
		List<DownloadLogBean> downloadLog = getAllDownloadLog();
		List<DownloadLogBean> downloadSuccessLog = new ArrayList<DownloadLogBean>();
		for (DownloadLogBean bean : downloadLog) {
			if (bean.getState() == 1) {
				downloadSuccessLog.add(bean);
			}
		}
		return downloadSuccessLog;
	}

	public List<DownloadLogBean> getAllDownloadingLog() {
		List<DownloadLogBean> downloadLog = getAllDownloadLog();
		List<DownloadLogBean> downloadingLog = new ArrayList<DownloadLogBean>();
		for (DownloadLogBean bean : downloadLog) {
			if (bean.getState() == 0) {
				downloadingLog.add(bean);
			}
		}
		return downloadingLog;
	}
	
	public DownloadLogBean getDownloadByUrl(String url){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select bookTitle,imageUrl,bookPath,bookAuthor,bookFrom,bookState,bookUrl from download_log where bookUrl=?";
		Cursor cursor = db.rawQuery(sql, new String[]{url});
		DownloadLogBean bean = null;
		while (cursor.moveToNext()) {
			String bookTitle = cursor.getString(0);
			String imageUrl = cursor.getString(1);
			String bookPath = cursor.getString(2);
			String bookAuthor = cursor.getString(3);
			String bookFrom = cursor.getString(4);
			int bookState = cursor.getInt(5);
			String bookUrl = cursor.getString(6);
			bean = new DownloadLogBean();
			bean.setAuthor(bookAuthor);
			bean.setFrom(bookFrom);
			bean.setImage(imageUrl);
			bean.setPath(bookPath);
			bean.setState(bookState);
			bean.setTitle(bookTitle);
			bean.setUrl(bookUrl);
		}
		return bean;
	}

	public void changeDownloadState(String url, int state) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "update download_log set bookState=? where bookUrl=?";
		db.execSQL(sql, new String[] { "" + state, url });
		db.close();
	}

	public void addDownload(DownloadLogBean bean) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "insert into download_log (bookUrl,bookTitle,imageUrl,bookPath,bookAuthor,bookFrom,bookState) values (?,?,?,?,?,?,?)";
		Object[] bindArgs = { bean.getUrl(), bean.getTitle(), bean.getImage(),
				bean.getPath(), bean.getAuthor(), bean.getFrom(),
				bean.getState() };
		database.execSQL(sql, bindArgs);
		database.close();
	}

	public void deleteDownloadByBookUrl(String url) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "delete from download_log where bookUrl=?";
		Object[] bindArgs = { url };
		db.execSQL(sql, bindArgs);
		db.close();
	}

	public void deleteCollect(DownloadLogBean bean) {
		deleteDownloadByBookUrl(bean.getUrl());
	}

	public void deleteAll() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "delete from download_log";
		db.execSQL(sql);
		db.close();
	}
}
