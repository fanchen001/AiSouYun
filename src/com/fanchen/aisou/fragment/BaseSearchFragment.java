package com.fanchen.aisou.fragment;

import com.fanchen.aisou.callback.OnDataResultListener;
import com.fanchen.aisou.services.WordLenovoService;
import com.fanchen.aisou.services.WordLenovoService.MyBinder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.AdapterView.OnItemClickListener;
/**
 * 和搜索有关的fragment都继承该BaseSearchFragment
 * 该fragment为其子类提供词语联系服务
 * @author Administrator
 *
 */
public abstract class BaseSearchFragment extends BaseFragment implements OnDataResultListener,OnItemClickListener{
	
	private WordLenovoService mWordLenovoService;//词语联想服务
	private SharedPreferences sp;//系统配置
	/**
	 * 获取词语联想服务
	 * 如果用户关闭了该功能
	 * 返回null
	 * @return
	 */
	public WordLenovoService getWordLenovoService() {
		if(!(sp.getBoolean("lenovo", true)))
			return null;
		//如果服务被关闭，重新启动
		if(mWordLenovoService==null){
			Intent mIntent=new Intent(getActivity(),WordLenovoService.class);
			getActivity().startService(mIntent);
			//绑定服务
			getActivity().bindService(mIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
		}
		return mWordLenovoService;
	}


	ServiceConnection mServiceConnection=new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}
		
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			MyBinder mBinder=(MyBinder) arg1;
			mWordLenovoService = mBinder.getService();
			//设置回调
			mWordLenovoService.setDataResultListener(BaseSearchFragment.this);
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//拿到设置
		sp=getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
		Intent mIntent=new Intent(getActivity(),WordLenovoService.class);
		getActivity().startService(mIntent);
		getActivity().bindService(mIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onStart() {
		if(mWordLenovoService!=null)
			mWordLenovoService.setDataResultListener(BaseSearchFragment.this);
		super.onStart();
	}
	
	/**
	 * 销毁时注销绑定的服务
	 */
	@Override
	public void onDestroy() {
		getActivity().unbindService(mServiceConnection);
		super.onDestroy();
	}
}
