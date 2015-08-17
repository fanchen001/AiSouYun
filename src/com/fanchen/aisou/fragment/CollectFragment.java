package com.fanchen.aisou.fragment;

import java.util.ArrayList;
import java.util.List;

import com.fanchen.aisou.R;
import com.fanchen.aisou.adapter.BookListAdapter;
import com.fanchen.aisou.bean.LightNovelBean;
import com.fanchen.aisou.db.CollectDao;
import com.fanchen.aisou.view.LoadingView;
import com.fanchen.aisou.view.MaterialDialog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

public class CollectFragment extends BaseStateFragment implements
		OnItemClickListener,OnItemLongClickListener {

	private static MaterialDialog materialDialog;
	private GridView mGridView;
	private BookListAdapter mListAdapter;
	private ArrayList<LightNovelBean> mLightNovelBeans;
	private CollectDao mCollectDao;
	private LoadingView mLoadingView;

	@Override
	public void onClick(View v) {

	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_collect, container, false);
	}

	@Override
	public void findView(View v) {
		mGridView = (GridView) v.findViewById(R.id.gv_collect);
		mLoadingView = (LoadingView) v.findViewById(R.id.loadingView_collect);
	}

	@Override
	public void setLinsener() {
		mGridView.setOnItemLongClickListener(this);
		mGridView.setOnItemClickListener(this);
	}

	@Override
	public void fillData(Bundle savedInstanceState) {
		mListAdapter = new BookListAdapter(mainActivity,R.layout.item_recommend_book);
		mGridView.setAdapter(mListAdapter);
		mCollectDao = new CollectDao(mainActivity);
	}
	
	@Override
	protected void onSaveState(Bundle outState) {
		if(mLightNovelBeans != null){
			outState.putParcelableArrayList("collect", mLightNovelBeans);
		}
	}
	
	@Override
	protected void onFirstTimeLaunched() {
		mLoadingView.setVisibility(View.VISIBLE);
		new CollectTask().execute("");
	}
	
	@Override
	protected void onRestoreState(Bundle savedInstanceState) {
		mLightNovelBeans = savedInstanceState.getParcelableArrayList("collect");
		if (mLightNovelBeans != null) {
			mListAdapter.addAll(mLightNovelBeans);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		LightNovelBean bean = (LightNovelBean) parent
				.getItemAtPosition(position);
		mainActivity.changeFragment(new BookInfoFragment(), false, "collect",
				"url", bean.getUrl());
	}
	
	class CollectTask extends AsyncTask<String, Integer, List<LightNovelBean>>{

		@Override
		protected List<LightNovelBean> doInBackground(String... params) {
			List<LightNovelBean> allCollect = mCollectDao.getAllCollect();
			return allCollect;
		}

		@Override
		protected void onPostExecute(List<LightNovelBean> result) {
			mLightNovelBeans = (ArrayList<LightNovelBean>) result;
			mListAdapter.addAll(mLightNovelBeans);
			mLoadingView.setVisibility(View.GONE);
		}
	}

	private void showMaterialDialog(String title,String msg,OnClickListener l1,OnClickListener l2){
		materialDialog = new MaterialDialog(mainActivity);
		materialDialog.setCanceledOnTouchOutside(false);
		materialDialog.setTitle(title).setMessage(msg)
				.setPositiveButton("确定", l1).setNegativeButton("取消", l2)
				.setCanceledOnTouchOutside(false)
				.setOnDismissListener(null).show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
		final LightNovelBean bean = (LightNovelBean) parent.getItemAtPosition(position);
		showMaterialDialog("提示", "是否删除<"+bean.getTitle()+">?", new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCollectDao.deleteCollect(bean);
				mListAdapter.remove(bean);
				materialDialog.dismiss();
				showToast("删除成功");
			}
		}, null);
		return true;
	}
}
