package com.fanchen.aisou.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import com.fanchen.aisou.R;
import com.fanchen.aisou.bean.HotWordBean;
import com.fanchen.aisou.db.HistoryDAO;
import com.fanchen.aisou.jni.HostURL;
import com.fanchen.aisou.utils.DateUtil;
import com.fanchen.aisou.utils.NetworkStateUtil;
import com.fanchen.aisou.utils.XmlUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

public class ShakeFragment extends BaseNetFragment {
	private List<String> historyWords;
	private ShakeListener mShakeListener = null;
	private Random mRandom;
	private Vibrator mVibrator;
	private RelativeLayout mImgUp;
	private RelativeLayout mImgDn;
	private HistoryDAO mHistoryDAO;//历史记录dao
	private SharedPreferences mSharedPreferences;
	private List<HotWordBean> randomWords;
	private  Handler mHandler =new Handler();
	private ScreenActionReceiver mScreenActionReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mScreenActionReceiver=new ScreenActionReceiver();
		IntentFilter filter = new IntentFilter(); 
        filter.addAction(Intent.ACTION_SCREEN_OFF); 
        filter.addAction(Intent.ACTION_SCREEN_ON); 
        mainActivity.registerReceiver(mScreenActionReceiver, filter);
	}
	
	@Override
	public void onClick(View v) {

	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_shake, container,false);
	}

	@Override
	public void findView(View v) {
		mImgUp = (RelativeLayout) v.findViewById(R.id.shakeImgUp);
		mImgDn = (RelativeLayout) v.findViewById(R.id.shakeImgDown);
	}

	@Override
	public void setLinsener() {
		mShakeListener = new ShakeListener(getActivity());
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				startAnim(); // 开始 摇一摇手掌动画
				mShakeListener.stop();
				startVibrato(); // 开始 震动
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (randomWords == null || randomWords.size() == 0) {
							showToast( "获取网络热搜词出现问题，请重试");
						} else {
							int index = mRandom.nextInt(randomWords.size() - 1);
							startSearchFragment(randomWords.get(index).getHotWord());
						}
						mVibrator.cancel();
						mShakeListener.start();
					}
				}, 2000);
			}
		});
	}

	@Override
	public void fillData(Bundle savedInstanceState) {
		mVibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		mHistoryDAO=new HistoryDAO(mainActivity);
		mRandom=new Random();
		mSharedPreferences=mainActivity.getSharedPreferences("time", Context.MODE_PRIVATE);
		long timeMillis = System.currentTimeMillis();
		if(timeMillis-mSharedPreferences.getLong("randomtime", 0)>24*60*60*1000){
			ExecuteParams executeParams = new ExecuteParams();
			List<String> ps = new ArrayList<String>();
			ps.add(HostURL.getHotWordUrl());
			executeParams.flag = 0;
			executeParams.params = ps;
			new ExecuteTask().execute(executeParams);
		}else{
			ExecuteParams executeParams = new ExecuteParams();
			executeParams.flag = 1;
			executeParams.params = null;
			new ExecuteTask().execute(executeParams);
		}
		ExecuteParams executeParams = new ExecuteParams();
		executeParams.flag = 2;
		executeParams.params = null;
		new ExecuteTask().execute(executeParams);
	}
	
	
	/**
	 * 开启搜索fragment
	 * @param word 搜索词语
	 */
	private void startSearchFragment(String word) {
		if(!TextUtils.isEmpty(word)){
			 if(NetworkStateUtil.isNetWorkAvailable(getActivity())){
				 if(historyWords!=null){
				 }else{
					 mHistoryDAO.addHistoryChar(DateUtil.getTimeByCalendar(), word);
				 }
				 mainActivity.setTitle("搜索："+word);
				 mainActivity.changeFragment(new SearchFragment(),false,"search","word",word);
			 }else{
				 showToast("当前网络不可用");
			 }
		 }else{
			 showToast( "请输入要搜索的资源名称");
		 }
	}
	
	
	public void startAnim () {   //定义摇一摇动画动画
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,
				Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);
		mytranslateanimup0.setDuration(1000);
		TranslateAnimation mytranslateanimup1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,
				Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);
		mytranslateanimup1.setDuration(1000);
		mytranslateanimup1.setStartOffset(1000);
		animup.addAnimation(mytranslateanimup0);
		animup.addAnimation(mytranslateanimup1);
		mImgUp.startAnimation(animup);
		
		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,
				Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);
		mytranslateanimdn0.setDuration(1000);
		TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,
				Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);
		mytranslateanimdn1.setDuration(1000);
		mytranslateanimdn1.setStartOffset(1000);
		animdn.addAnimation(mytranslateanimdn0);
		animdn.addAnimation(mytranslateanimdn1);
		mImgDn.startAnimation(animdn);	
	}
	
	public void startVibrato(){		//定义震动
		mVibrator.vibrate( new long[]{500,200,500,200}, -1); //第一个｛｝里面是节奏数组， 第二个参数是重复次数，-1为不重复，非-1则从pattern的指定下标开始重复
	}
	
	/** 一个检测手机摇晃的监听器 */
	public class ShakeListener implements SensorEventListener {
		
		// 速度阈值，当摇晃速度达到这值后产生作用
		private static final int SPEED_SHRESHOLD = 3000;
		// 两次检测的时间间隔
		private static final int UPTATE_INTERVAL_TIME = 70;
		// 传感器管理器
		private SensorManager sensorManager;
		// 传感器
		private Sensor sensor;
		// 重力感应监听器
		private OnShakeListener onShakeListener;
		// 上下文
		private Context mContext;
		// 手机上一个位置时重力感应坐标
		private float lastX;
		private float lastY;
		private float lastZ;
		// 上次检测时间
		private long lastUpdateTime;

		// 构造器
		public ShakeListener(Context c) {
			// 获得监听对象
			mContext = c;
			start();
		}

		// 开始
		public void start() {
			// 获得传感器管理器
			sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
			if (sensorManager != null) {
				// 获得重力传感器
				sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			}
			// 注册
			if (sensor != null) {
				sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
			}
		}

		// 停止检测
		public void stop() {
			sensorManager.unregisterListener(this);
		}

		// 设置重力感应监听器
		public void setOnShakeListener(OnShakeListener listener) {
			onShakeListener = listener;
		}

		// 重力感应器感应获得变化数据
		public void onSensorChanged(SensorEvent event) {
			// 现在检测时间
			long currentUpdateTime = System.currentTimeMillis();
			// 两次检测的时间间隔
			long timeInterval = currentUpdateTime - lastUpdateTime;
			// 判断是否达到了检测时间间隔
			if (timeInterval < UPTATE_INTERVAL_TIME)
				return;
			// 现在的时间变成last时间
			lastUpdateTime = currentUpdateTime;

			// 获得x,y,z坐标
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			// 获得x,y,z的变化值
			float deltaX = x - lastX;
			float deltaY = y - lastY;
			float deltaZ = z - lastZ;

			// 将现在的坐标变成last坐标
			lastX = x;
			lastY = y;
			lastZ = z;

			double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval * 10000;
			// 达到速度阀值，发出提示
			if (speed >= SPEED_SHRESHOLD) {
				onShakeListener.onShake();
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
		
	}

	// 摇晃监听接口
	public interface OnShakeListener {
		public void onShake();
	}
	
	
	@Override
	public void onStop() {
		super.onStop();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
    	if (mShakeListener != null) {
			mShakeListener.start();
		}
	}
	
	public class ScreenActionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			 String action = intent.getAction(); 
		        if (action.equals(Intent.ACTION_SCREEN_ON)) { 
		        } else if (action.equals(Intent.ACTION_SCREEN_OFF)) { 
		        	//锁屏的时候，关闭摇一摇功能
		        	if (mShakeListener != null) {
		    			mShakeListener.stop();
		    		}
		        } 
		} 
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mScreenActionReceiver!=null){
			mainActivity.unregisterReceiver(mScreenActionReceiver);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ExecuteResult executeInBackground(ExecuteParams pa,ExecuteResult rs) {
		switch (pa.flag) {
		case 0:
			List<String> ps = (List<String>) pa.params;
			byte[] bs = getResponseSource(ps.get(0));
			if(bs != null && bs.length > 0){
				Source src = new Source(new String(bs));
				List<Element> allElements = src.getAllElements();
				randomWords=new ArrayList<HotWordBean>();
				for (Element el:allElements) {
					String attributeValue = el.getAttributeValue("class");
					if("ToptenzRight".equals(attributeValue)){
						List<Element> allElements2 = el.getAllElements("a");
						for (Element e:allElements2) {
							String word = e.getTextExtractor().toString();
							if(!"TOP50>>".equals(word)){
								HotWordBean bean=new HotWordBean();
								bean.setHotWord(word);
								bean.setWordId(0);
								randomWords.add(bean);
							}
						}
						try {
							XmlUtil.save(randomWords, "randomword.xml",mainActivity);
							Editor edit = mSharedPreferences.edit();
							//更新热词的更新时间
							edit.putLong("randomtime", System.currentTimeMillis());
							edit.commit();
						} catch (Exception e) {
						}
					}
				}
			}
			rs.result = randomWords;
			break;
		case 1:
			try {
				randomWords = XmlUtil.getAll("randomword.xml",HotWordBean.class,mainActivity);
				if(randomWords != null && randomWords.size() > 0){
					rs.result = randomWords;
				}else{
					ExecuteParams executeParams = new ExecuteParams();
					List<String> pas = new ArrayList<String>();
					pas.add(HostURL.getHotWordUrl());
					executeParams.flag = 0;
					executeParams.params = pas;
					new ExecuteTask().execute(executeParams);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 2:
			historyWords = mHistoryDAO.getCharByDay(DateUtil.getTimeByCalendar());
			break;

		default:
			break;
		}
		return rs;
	}
}
