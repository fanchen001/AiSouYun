package com.fanchen.aisou;


import java.io.File;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.fanchen.aisou.bean.SplashImgBean;
import com.fanchen.aisou.jni.BmobValue;
import com.fanchen.aisou.services.GeneralService;
import com.fanchen.aisou.utils.FileUtil;
import com.fanchen.aisou.utils.ImageUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
/**
 * 程序启动的欢迎界面
 * @author Administrator
 *
 */
public class SplashActivity extends BaseActivity {

	//加载完成
	private final static int LOAD_SUCCESS = 3;
	//加载顶部图片
	private final static int LOAD_TOP_IMAGE = 2;
	//顶部图片
	private ImageView mTopImageView;
	//配置
	private SharedPreferences sp;
	private SharedPreferences splashSp;

	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_SUCCESS:
				//时间到了，加载主界面
				loadMainUI();
				break;
			case LOAD_TOP_IMAGE:
				File file = new File(Environment.getExternalStorageDirectory()+ "/android/data/com.fanchen.aisou/splash/splash.jpg");
				if(file.exists()){
					mTopImageView.setBackground(ImageUtil.bitmapToDrawable(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+ "/android/data/com.fanchen.aisou/splash/splash.jpg")));
				}else{
					//加载顶部图片
					mTopImageView.setBackgroundResource(R.drawable.welcome_top);
				}
				break;
			default:
			}
		}
	};

	/**
	 * 载入主页面,如果是第一次安装程序，则先加载欢迎界面
	 */
	private void loadMainUI() {
		if (sp.getBoolean("isOne", true)) {
			// 如果是第一次进入页面
			// 加载引导界面
			startActivity(WelcomeActivity.class);
			Editor edit = sp.edit();
			edit.putBoolean("isOne", false);
			edit.commit();
		} else {
			// 如果不是，加载主界面
			startActivity(MainActivity.class);
		}
		finish();
	}

	/**
	 * 欢迎界面屏蔽掉back按键
	 */
	@Override
	public void onBackPressed() {
	}

	@Override
	public int getResId() {
		return R.layout.activity_splash;
	}

	@Override
	public void findView() {
		mTopImageView = (ImageView) findViewById(R.id.iv_top);
	}

	@Override
	public void setListener() {

	}

	@Override
	public void initData(Bundle b) {
		Bmob.initialize(this, BmobValue.getApplicationID());
		sp = getSharedPreferences("config", MODE_PRIVATE);
		splashSp = getSharedPreferences("splash", MODE_PRIVATE);
		// 延时1s加载top图片
		mHandler.sendEmptyMessageDelayed(LOAD_TOP_IMAGE, 1000);
		// 延时3s加载主界面
		mHandler.sendEmptyMessageDelayed(LOAD_SUCCESS,3000);
		// 拷贝应用图标到程序数据目录下
		FileUtil.copyFile(getApplicationContext(), "logo.png");
		BmobQuery<SplashImgBean> mBmobQuery = new BmobQuery<SplashImgBean>();
		mBmobQuery.findObjects(this, new FindListener<SplashImgBean>() {
			@Override
			public void onSuccess(List<SplashImgBean> arg0) {
				for (final SplashImgBean bean: arg0) {
					if(bean.getVersion() > splashSp.getInt("version", 0)){
						File file = new File(Environment.getExternalStorageDirectory()+ "/android/data/com.fanchen.aisou/splash/splash.jpg");
						if(file.exists()){
							file.delete();
						}
						Editor edit = splashSp.edit();
						edit.putInt("version", bean.getVersion());
						Intent mIntent = new Intent(SplashActivity.this,GeneralService.class);
						mIntent.putExtra("url", bean.getImgUrl());
						startService(mIntent);
						return;
					}
				}
			}
			@Override
			public void onError(int arg0, String arg1) {
				
			}
		});
	}
}
