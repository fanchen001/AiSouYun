package com.fanchen.aisou;


import com.fanchen.aisou.application.AisouApplication;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public abstract class BaseActivity extends FragmentActivity {
	 //整个应用程序的上下文
	protected AisouApplication mAisouApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//初始化自定义异常处理
		mAisouApplication=(AisouApplication) getApplicationContext();
		mAisouApplication.init();
		mAisouApplication.addActivity(this);
		init(savedInstanceState);
	}

	private void init(Bundle savedInstanceState) {

		int id = getResId();

		if (id <= 0)
			return;
		setContentView(id);

		findView();

		setListener();

		initData(savedInstanceState);
		
	}

	/**
	 * 返回当前界面需要显示的layout的id
	 * @return id
	 */
	public abstract int getResId();

	/**
	 * 查找必要的控件
	 */
	public abstract void findView();

	/**
	 * 为控件设置必要的监听器
	 */
	public abstract void setListener();

	/**
	 * 初始化界面其他的一些操作
	 * @param b 
	 */
	public abstract void initData(Bundle b);
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//界面退出的时候，从异常处理列表中移除当前界面
		mAisouApplication.removeActivity(this);
	}
	/**
	 * 淡入淡出效果
	 */
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.tran_pre_in,R.anim.tran_pre_out);
	}

	/** 通过Class跳转界面 **/
	public void startActivity(Class<?> cls) {
		startActivity(cls, null);
		overridePendingTransition( R.anim.tran_next_in,R.anim.tran_next_out);
	}

	/** 含有Bundle通过Class跳转界面 **/
	public void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
		overridePendingTransition( R.anim.tran_next_in,R.anim.tran_next_out);
	}

	/** 通过Action跳转界面 **/
	public void startActivity(String action) {
		startActivity(action, null);
		overridePendingTransition( R.anim.tran_next_in,R.anim.tran_next_out);
	}

	/** 含有Bundle通过Action跳转界面 **/
	public void startActivity(String action, Bundle bundle) {
		Intent intent = new Intent();
		intent.setAction(action);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
		overridePendingTransition( R.anim.tran_next_in,R.anim.tran_next_out);
	}
	
	/**
	 * 通过class开启服务
	 * @param cls 服务
	 */
	public void startService(Class<?> cls){
		Intent intent = new Intent();
		intent.setClass(this, cls);
		startService(intent);
	}
	
	/**
	 * 通过class和conn 绑定服务
	 * @param cls
	 * @param conn
	 * @return
	 */
	public boolean bindService(Class<?> cls, ServiceConnection conn) {
		Intent mIntent=new Intent();
		mIntent.setClass(this, cls);
		return bindService(mIntent, conn, Context.BIND_AUTO_CREATE);
	}
	
	/**
	 * 显示吐司
	 * 已经做了处理，在子线程中也可以直接调用该方法
	 * @param c
	 */
	public void showToast(final CharSequence c){
		if(Thread.currentThread().getName().equals("main")){
			Toast.makeText(this, c, Toast.LENGTH_SHORT).show();
		}else{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(BaseActivity.this, c, Toast.LENGTH_SHORT).show();					
				}
			});
		}
	}
	
	/**
	 * 显示吐司
	 * 已经做了处理，在子线程中也可以直接调用该方法
	 * @param c
	 */
	public void showToast(final int c){
		if(Thread.currentThread().getName().equals("main")){
			Toast.makeText(this, c, Toast.LENGTH_SHORT).show();
		}else{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(BaseActivity.this, c, Toast.LENGTH_SHORT).show();					
				}
			});
		}
	}
}
