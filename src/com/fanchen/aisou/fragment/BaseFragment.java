package com.fanchen.aisou.fragment;


import com.fanchen.aisou.MainActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
/**
 * baseFragment，所有的faragment都继承自该类
 * @author Administrator
 *
 */
public abstract class BaseFragment extends Fragment implements OnClickListener{
	

	protected MainActivity mainActivity;
/**
 *返回当前fragment所需要显示的view
 * @param inflater
 * @param container
 * @param savedInstanceState
 * @return
 */
	public abstract View getView(LayoutInflater inflater, ViewGroup container);
	/**
	 * 查找必要的控件
	 * @param v 当前fragment所需要显示的view
	 */
	public abstract void findView(View v);
	/**
	 * 为控件设置监听事件
	 */
	public abstract void setLinsener();
	/**
	 * 填充界面的数据
	 */
	public abstract void fillData(Bundle savedInstanceState);
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = getView( inflater,  container);
		findView(view);
		fillData(savedInstanceState);
		setLinsener();
		return view;
	}
	

	/**
	 * 显示吐司
	 * 已经做了处理，在子线程中也可以直接调用该方法
	 * @param c
	 */
	public void showToast(final CharSequence c){
		if(Thread.currentThread().getName().equals("main")){
			Toast.makeText(getActivity(), c, Toast.LENGTH_SHORT).show();
		}else{
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getActivity(), c, Toast.LENGTH_SHORT).show();					
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
			Toast.makeText(getActivity(), c, Toast.LENGTH_SHORT).show();
		}else{
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getActivity(), c, Toast.LENGTH_SHORT).show();					
				}
			});
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainActivity=(MainActivity) getActivity();
	}
	
}