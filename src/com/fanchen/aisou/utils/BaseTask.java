package com.fanchen.aisou.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.util.ByteArrayBuffer;

import android.os.AsyncTask;

public abstract class BaseTask<Result> extends AsyncTask<String,Integer,Result> {
	
	public static final int TIME_OUT=5*1000;
	
	public static final int SUCCESS=200;
	public static final int ERROR_TIME_OUT=0x123;
	public static final int ERROR_FILE_NOT_FOUND=404;
	public static final int ERROR_SERVICE_ERROR=500;
	public static final int ERROR_UNKNOW=0x126;
	private int state;
	
	public static Map<Integer, String> map;
	static{
		map=new HashMap<Integer, String>();
		map.put(ERROR_TIME_OUT, "连接超时");
		map.put(ERROR_UNKNOW, "未知错误");
		map.put(ERROR_FILE_NOT_FOUND, "文件未找到");
		map.put(ERROR_SERVICE_ERROR, "服务器错误");
	}

	@Override
	protected void onPreExecute() {
		preObtainDataSuccess();
	}


	
	@Override
	protected void onPostExecute(Result result) {
		switch (state) {
		case SUCCESS:
			obtainDataSuccess(result);
			break;
		case ERROR_TIME_OUT:
			obtainDataError(map.get(ERROR_TIME_OUT));
			break;
		case ERROR_FILE_NOT_FOUND:
			obtainDataError(map.get(ERROR_FILE_NOT_FOUND));
			break;
		case ERROR_SERVICE_ERROR:
			obtainDataError(map.get(ERROR_SERVICE_ERROR));
			break;
		case ERROR_UNKNOW:
			obtainDataError(map.get(ERROR_UNKNOW));
			break;
		default:
			break;
		}
	}



	public abstract void obtainDataSuccess(Result result);

	public abstract void preObtainDataSuccess();
	
	public abstract void obtainDataError(String error);
	

	
	public byte[] getResponseSource(String s){
		InputStream is=null;
		try {
			URL url=new URL(s);
			HttpURLConnection conn=(HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setRequestMethod("GET");
			int code = conn.getResponseCode();
			if(code==SUCCESS){
				is = conn.getInputStream();
				ByteArrayBuffer bab=new ByteArrayBuffer(32);
				byte [] buff=new byte[1024];
				int len=-1;
				while ((len=is.read(buff))!=-1) {
					bab.append(buff,0,len);
				}
				state=SUCCESS;
				return bab.toByteArray();
			}else if(code==ERROR_FILE_NOT_FOUND){
				state=ERROR_FILE_NOT_FOUND;
			}else if(code==ERROR_SERVICE_ERROR){
				state=ERROR_SERVICE_ERROR;
			}
		} catch (SocketTimeoutException e) {
			state=ERROR_TIME_OUT;
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		state=ERROR_UNKNOW;
		return null;
	}
}
