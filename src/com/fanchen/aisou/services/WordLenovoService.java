package com.fanchen.aisou.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fanchen.aisou.callback.OnDataResultListener;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
/**
 * 词语联想服务
 * 主要是通过解析百度的json数据实现
 * @author Administrator
 *
 */
@SuppressWarnings("deprecation")
public class WordLenovoService extends Service {

	private MyBinder mBinder=new MyBinder();
	private OnDataResultListener mListener;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	/**
	 * 发送一个请求
	 * @param word
	 */
	public void sendRequest(String word){
		if(TextUtils.isEmpty(word)){
			return;
		}
		new RequestTask().execute(word);
	}
	/**
	 * 设置数据回调接口
	 * @param listener 数据回调接口
	 */
	public void setDataResultListener(OnDataResultListener listener){
		this.mListener=listener;
	}
	
	/**
	 * 返回一个联想服务
	 * @author Administrator
	 *
	 */
	public class MyBinder extends Binder{
		public WordLenovoService getService(){
			return WordLenovoService.this;
		}
	}
	
	/**
	 * 请求的异步任务
	 * @author Administrator
	 *
	 */
	public class RequestTask extends AsyncTask<String, Integer, JSONArray>{

		private static final int CONNECTION_TIME_OUT=500;//超时时间
		private static final int READ_TIME_OUT=500;
		public static final int SUCCESS=0x123;
		public static final int TIME_OUT=0x124;
		public static final int NETWORK_ERROR=0x125;
		public static final int FILE_NOTFOUND_ERROR=0x126;
		public static final int SERVICE_ERROR=0x127;
		public static final int PARSING_JSON_ERROR=0x128;
		public static final int UNKNOW_ERROR=0x0;
		private int requestState;
		
		@Override
		protected JSONArray doInBackground(String... arg0) {
			try {
				URL url=new URL("https://sp0.baidu.com/5a1Fazu8AA54nxGko9WTAnF6hhy/su?wd=" +
						URLEncoder.encode(arg0[0]) +
						"&json=1&p=3&sid=13457_1426_13151_13075_10211_12867_13322_12691_13411_8502_12722_12736_13085_13443_13324_13201_12836_13162_8498" +
						"&req=2&pbs=money&cb=jQuery110209462304923217744_1428735302208&_=1428735302226");
				HttpURLConnection conn=(HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(CONNECTION_TIME_OUT);
				conn.setReadTimeout(READ_TIME_OUT);
				conn.setRequestMethod("GET");
				int code = conn.getResponseCode();
				if(code==200){
					InputStream is = conn.getInputStream();
					StringBuilder sb=new StringBuilder();
					byte [] buff=new byte[1024];
					int len=-1;
					//读取返回的数据
					while((len=is.read(buff))!=-1){
						//这里需要用gbk编码，不然会乱码
						sb.append(new String(buff,0,len,"gbk"));
					}
					String temp = sb.toString();
					//替换掉一些多余的字符串
					temp=temp.replace("jQuery110209462304923217744_1428735302208(", "");
					temp=temp.replace(");", "");
					//封装成json
					JSONObject json=new JSONObject(temp);
					requestState=SUCCESS;
					return json.getJSONArray("s");
				}else if(code==404){
					requestState=FILE_NOTFOUND_ERROR;
				}else if(code==500){
					requestState=SERVICE_ERROR;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				requestState=UNKNOW_ERROR;
			} catch (ProtocolException e) {
				e.printStackTrace();
				requestState=UNKNOW_ERROR;
			} catch (IOException e) {
				e.printStackTrace();
				requestState=TIME_OUT;
			} catch (JSONException e) {
				e.printStackTrace();
				requestState=PARSING_JSON_ERROR;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONArray result) {
			//其他错误暂时不在处理
			switch (requestState) {
			case SUCCESS:
				if(mListener!=null&&result!=null){
					mListener.onResult(result);
				}
				break;
			case FILE_NOTFOUND_ERROR:
				
				break;
			case SERVICE_ERROR:
				
				break;
			case UNKNOW_ERROR:
				
				break;
			case TIME_OUT:
				
				break;
			case PARSING_JSON_ERROR:
				
				break;
				
			default:
				break;
			}
		}
	}
}
