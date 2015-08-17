package com.fanchen.aisou.fragment;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

import com.fanchen.aisou.R;
import com.fanchen.aisou.adapter.ResShareAdapter;
import com.fanchen.aisou.adapter.ResShareAdapter.OnLoadMoreDate;
import com.fanchen.aisou.bean.ResShakeBean;
import com.fanchen.aisou.view.LoadingView;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ResShareFragment extends BaseFragment implements CountListener,OnLoadMoreDate{
	
	private ListView mListView;
	private LoadingView mLoadingView;
	private ResShareAdapter mResShareAdapter;
	private BmobQuery<ResShakeBean> mBmobQuery;
	private ProgressBar mProgressBar;//进度条]
	private ImageButton mErrorImageButton;
	private TextView mTextView;//
	private Handler mHandler=new Handler();
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.ib_res_load_error:
			mErrorImageButton.setVisibility(View.GONE);
			mLoadingView.setVisibility(View.VISIBLE);
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mBmobQuery.count(mainActivity, ResShakeBean.class,ResShareFragment.this);
				}
			}, 1*1000);
			break;

		default:
			break;
		}
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_res_share, container,false);
	}

	@Override
	public void findView(View v) {
		mErrorImageButton = (ImageButton) v.findViewById(R.id.ib_res_load_error);
		mListView=(ListView) v.findViewById(R.id.lv_res_share);
		mLoadingView = (LoadingView) v.findViewById(R.id.loadingView_res);
	}

	@Override
	public void setLinsener() {
		mErrorImageButton.setOnClickListener(this);
	}

	@Override
	public void fillData(Bundle savedInstanceState) {
		mResShareAdapter = new ResShareAdapter(mainActivity);
		mResShareAdapter.setOnLoadMoreDate(this);
		mListView.setAdapter(mResShareAdapter);
		mBmobQuery=new BmobQuery<ResShakeBean>();
		mLoadingView.setVisibility(View.VISIBLE);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mBmobQuery.count(mainActivity, ResShakeBean.class,ResShareFragment.this);
			}
		}, 1*1000);
	}
	
	@Override
	public void onFailure(int arg0, String arg1) {
		mLoadingView.setVisibility(View.GONE);
		mErrorImageButton.setVisibility(View.VISIBLE);
	}

	@Override
	public void onSuccess(int arg0) {
		mResShareAdapter.setMax(arg0);
		loadMore(5, 0,true);
	}

	@Override
	public void loadMore(int limit,int skip,final boolean flag) {
		mBmobQuery.setLimit(limit);
		mBmobQuery.setSkip(skip);
		mBmobQuery.order("-createdAt");
		mBmobQuery.findObjects(mainActivity,new FindListener<ResShakeBean>() {
			@Override
			public void onSuccess(List<ResShakeBean> arg0) {
				mLoadingView.setVisibility(View.GONE);
				if(flag){
					mResShareAdapter.clear();
					mResShareAdapter.addAll(arg0);
				}else{
					mResShareAdapter.addAll(arg0);
				}
			}
			@Override
			public void onError(int arg0, String arg1) {
				showToast("网络连接错误"+arg1);
				if(mProgressBar!=null&&mTextView!=null){
					mProgressBar.setVisibility(View.GONE);//进度条
					mTextView.setText("加载更多数据");//
				}
				mLoadingView.setVisibility(View.GONE);
				mErrorImageButton.setVisibility(View.VISIBLE);
			}
		});
	}

}
