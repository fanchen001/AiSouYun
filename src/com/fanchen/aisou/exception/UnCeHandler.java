package com.fanchen.aisou.exception;
import java.io.File;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import com.fanchen.aisou.R;
//import com.fanchen.aisou.SplashActivity;
import com.fanchen.aisou.application.AisouApplication;
import com.fanchen.aisou.utils.SDcardUtil;
import com.fanchen.aisou.view.toast.TipsToast;

//import android.app.AlarmManager;
//import android.app.PendingIntent;
import android.content.Context;
//import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
//import android.util.Log;

public class UnCeHandler implements UncaughtExceptionHandler {
	
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	public final String TAG = "CatchExcep";
	AisouApplication application;
	private static TipsToast mTipsToast;

	public UnCeHandler(AisouApplication application) {
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		this.application = application;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			SystemClock.sleep(3000);
//			Intent intent = new Intent(application.getApplicationContext(),SplashActivity.class);
//			PendingIntent restartIntent = PendingIntent.getActivity(application.getApplicationContext(), 0, intent,
//					Intent.FLAG_ACTIVITY_NEW_TASK);
			// 退出程序
//			AlarmManager mgr = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
			// 2秒钟后重启应用
//			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000,restartIntent); 
			application.finishActivity();
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		//将异常信息写入到sd卡
		if (SDcardUtil.isSDCardEnable()) {
			try {
				File folder = new File(Environment.getExternalStorageDirectory() + "/aisou/error");
				if (!folder.exists())
					folder.mkdirs();
				File file = new File( folder, "aisou_error_"+ System.currentTimeMillis() + ".txt");
				if (!file.exists())
					file.createNewFile();
				PrintStream ps = new PrintStream(file, "utf-8");
				ex.printStackTrace(ps);
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				showTips(application.getApplicationContext(),R.drawable.tips_error,"抱歉,程序异常.请将/aisou/error的日志提交开发者");
				Looper.loop();
			}
		}.start();
		return true;
	}

	/**
	 * 自定义taost
	 * 
	 * @param iconResId
	 *            图片
	 * @param msgResId 
	 *            提示文字
	 */
	public void showTips(Context context,int iconResId, String tips) {
		if (mTipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				mTipsToast.cancel();
			}
		} else {
			mTipsToast = TipsToast.makeText(context,tips, TipsToast.LENGTH_LONG);
		}
		mTipsToast.show();
		mTipsToast.setIcon(iconResId);
		mTipsToast.setText(tips);
	}
}
