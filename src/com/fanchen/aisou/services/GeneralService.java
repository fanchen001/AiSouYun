package com.fanchen.aisou.services;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import com.fanchen.aisou.utils.BaseTask;
import com.fanchen.aisou.utils.FileUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;

public class GeneralService extends Service {
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent !=null && intent.getStringExtra("url") != null && intent.getStringExtra("name") != null){
			String url = intent.getStringExtra("url");
			String name = intent.getStringExtra("name");
			downloadImage(url,"/android/data/com.fanchen.aisou/head/",name);
		}else if(intent !=null && intent.getStringExtra("url") != null){
			String url = intent.getStringExtra("url");
			downloadImage(url,"/android/data/com.fanchen.aisou/splash/","splash.jpg");
		}else if(intent !=null && intent.getStringExtra("txtUrl") != null && intent.getStringExtra("name") != null){
			String txtUrl = intent.getStringExtra("txtUrl");
			String name = intent.getStringExtra("name");
			new DownloadTxtTask().execute(txtUrl,name);
		}
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void downloadImage(final String s,final String dir,final String name){
		new Thread(){
			public void run() {
				if(TextUtils.isEmpty(s)){
					return;
				}
				File fileDir = new File(Environment.getExternalStorageDirectory() + dir);
				if(!fileDir.exists()){
					fileDir.mkdirs();
				}
				File file = new File(Environment.getExternalStorageDirectory() + dir +name+".temp");
				if(file.exists()){
					file.delete();
				}
				FileUtil.downloadFile(s,dir, name);
				file.renameTo(new File(Environment.getExternalStorageDirectory() + dir +name));
			};
			
		}.start();
	}
	
	class DownloadTxtTask extends BaseTask<Boolean>{

		@Override
		public void obtainDataSuccess(Boolean result) {
			if(result){
				Intent mIntent = new Intent("com.fanchen.aisou.download.txt.success");
				sendBroadcast(mIntent);
			}
		}

		@Override
		public void preObtainDataSuccess() {
			
		}

		@Override
		public void obtainDataError(String error) {
			
		}

		@Override
		protected Boolean doInBackground(String... params) {
			byte[] bs = getResponseSource(params[0]);
			String string = null;
			if(bs!=null&&bs.length>0){
				try {
					string = new String(bs,"gbk");
				} catch (Exception e) {
				}
				Source source = new Source(string);
				List<Element> allElements = source.getAllElements("div");
				for (int i = 0; i < allElements.size(); i++) {
					if("content".equals(allElements.get(i).getAttributeValue("id"))){
						try {
							File dir = new File(Environment.getExternalStorageDirectory() + "/android/data/com.fanchen.aisou/cache/");
							if(!dir.exists()){
								dir.mkdirs();
							}
							File file = new File(dir,(String)params[1]);
							if(!file.exists()){
								file.createNewFile();
							}else{
								return true;
							}
							FileOutputStream fos = new FileOutputStream(file);
							String context = allElements.get(i).getTextExtractor().toString();
							context = context.replace("    ", "\n  ");
							byte[] bytes = context.getBytes();
							fos.write(bytes);
							fos.flush();
							fos.close();
							return true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			return false;
		}
	}
}
