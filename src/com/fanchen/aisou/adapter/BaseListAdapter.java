package com.fanchen.aisou.adapter;

import java.util.ArrayList;
import java.util.List;

import com.fanchen.aisou.utils.ViewUtil;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

/**
 * 自定义baseadapter,所有的listadapter都继承自该类
 * 
 * @author fanchen
 * 
 * @param <T>
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

	protected List<T> mList;// 需要显示的数据集

	protected LayoutInflater mLayoutInflater;// 布局填充器

	protected Context context;

	public BaseListAdapter(Context context) {
		this.context = context;
		this.mList =new ArrayList<T>();
		mLayoutInflater = LayoutInflater.from(context);
	}

	public BaseListAdapter(Context context, List<T> mList) {
		this.context = context;
		mLayoutInflater = LayoutInflater.from(context);
		this.mList = mList;
	}

	/**
	 * 设置数据，并自动通知刷新界面
	 * 
	 * @param list
	 */
	public void setData(List<T> list) {
		this.mList = list;
		notifyDataSetChanged();
	}

	private void add(T bean, boolean flag) {
		if (mList == null)
			return;
		this.mList.add(bean);
		if (flag) {
			notifyDataSetChanged();
		}
	}

	/**
	 * 清空数据
	 */
	public void clear() {
		if (mList == null)
			return;
		this.mList.clear();
		notifyDataSetChanged();
	}

	/**
	 * 添加一些列数据，并自动刷新
	 * 
	 * @param list
	 */
	public void addAll(List<T> list) {
		if (mList == null)
			return;
		for (int i = 0; i < list.size(); i++) {
			add(list.get(i), false);
		}
		notifyDataSetChanged();
	}

	/**
	 * 添加一条数据，并自动通知刷新界面
	 * 
	 * @param bean
	 */
	public void add(T bean) {
		add(bean, true);
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList == null ? null : mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return mList == null ? 0 : arg0;
	}
	
	@SuppressWarnings("hiding")
	public <T extends View> T get(View view, int id){
		return ViewUtil.get(view, id);
	}
	
	public void remove(T bean){
		mList.remove(bean);
		notifyDataSetChanged();
	}
	
	public void remove(int pos){
		mList.remove(pos);
		notifyDataSetChanged();
	}

}
