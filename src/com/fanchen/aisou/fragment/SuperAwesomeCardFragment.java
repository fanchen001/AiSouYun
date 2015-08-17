package com.fanchen.aisou.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import com.fanchen.aisou.R;
import com.fanchen.aisou.WebActivity;
import com.fanchen.aisou.adapter.BaseListAdapter;
import com.fanchen.aisou.bean.ResShakeBean;
import com.fanchen.aisou.bean.SearchBean;
import com.fanchen.aisou.bean.UserBean;
import com.fanchen.aisou.callback.OnTopStateListener;
import com.fanchen.aisou.utils.DateUtil;
import com.fanchen.aisou.utils.SearchTask;
import com.fanchen.aisou.view.LoadingView;
import com.fanchen.aisou.view.MaterialDialog;
/**
 * 搜索结果下方的fragment
 * @author Administrator
 *
 */
@SuppressWarnings("deprecation")
public  class SuperAwesomeCardFragment extends BaseFragment {
	private static final String ARG_POSITION = "position";
	private int position;
	private ListView mListView;//结果列表
	private LoadingView mLoadingView;//加载的等待视图
	private ImageView mNoFileImageView;//没有文件显示 的图片
	private ImageButton mLoadErrorImageView;//加载错误时候显示的图片
	private SearchListViewAdapter mListViewAdapter;//适配器
	private List<SearchBean> mFragmentSearchBeans=new ArrayList<SearchBean>();
	private static OnTopStateListener mTopStateListener;
	private static MaterialDialog materialDialog;
	private static  boolean searchMagnet;
	private static List<String> cloudType1;
	private UserBean loginUser;

	public static SuperAwesomeCardFragment newInstance(List<String> cloudType2,int position,String word,OnTopStateListener l,boolean isSearchMagnet) {
		cloudType1 = cloudType2;
		mTopStateListener=l;
		searchMagnet=isSearchMagnet;
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		b.putString("word", word);
		SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
		f.setArguments(b);
		return f;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loginUser = BmobUser.getCurrentUser(mainActivity, UserBean.class);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.load_error: 
			//加载出错时，点击该图片重新加载
			mLoadErrorImageView.setVisibility(View.GONE);
			mLoadingView.setVisibility(View.VISIBLE);
			position = getArguments().getInt(ARG_POSITION);
			String word = getArguments().getString("word");
			if(searchMagnet&&position==0){
				new SearchCloud().execute(SearchTask.SEARECH_TYPE_MAGNET,cloudType1.get(position), word);
			}else if(searchMagnet){
				new SearchCloud().execute(SearchTask.SEARECH_TYPE_CLOUD,cloudType1.get(position-1), word);
			}else{
				new SearchCloud().execute(SearchTask.SEARECH_TYPE_CLOUD,cloudType1.get(position), word);
			}
			break;

		default:
			break;
		}
	}


	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_search_bottom, null);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			//用户可见的时候，隐藏等待，错误，加载更多视图
			if (mLoadingView != null)
				mLoadingView.setVisibility(View.GONE);
			if (mNoFileImageView != null)
				mNoFileImageView.setVisibility(View.GONE);
			if (mLoadingView != null)
				mLoadErrorImageView.setVisibility(View.GONE);
			//如果没有数据，再次请求网络
			if (mFragmentSearchBeans == null|| mFragmentSearchBeans.size() == 0) {
				if (mLoadingView != null)
					mLoadingView.setVisibility(View.VISIBLE);
				position = getArguments().getInt(ARG_POSITION);
				String word = getArguments().getString("word");
				if(searchMagnet&&position==0){
					new SearchCloud().execute(SearchTask.SEARECH_TYPE_MAGNET,cloudType1.get(position), word);
				}else if(searchMagnet){
					new SearchCloud().execute(SearchTask.SEARECH_TYPE_CLOUD,cloudType1.get(position-1), word);
				}else{
					new SearchCloud().execute(SearchTask.SEARECH_TYPE_CLOUD,cloudType1.get(position), word);
				}
			}
		} else {

		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public void findView(View v) {
		mLoadingView = (LoadingView) v.findViewById(R.id.loadView);
		mNoFileImageView=(ImageView) v.findViewById(R.id.load_no_file);
		mLoadErrorImageView=(ImageButton) v.findViewById(R.id.load_error);
		mListView = (ListView) v.findViewById(R.id.lv_search_more);
	}

	@Override
	public void setLinsener() {
		mLoadErrorImageView.setOnClickListener(this);
		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {}
			
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				if(mTopStateListener!=null)
					mTopStateListener.onCloseListView();
				if(arg1==0){//当第一个条目可见的时候，打开上方隐藏的视图，让其可以下拉
					if(mTopStateListener!=null)
						mTopStateListener.onOpen();
				}else{
					if(mTopStateListener!=null)
						mTopStateListener.onClose();
				}
				
			}
		});
	}

	@Override
	public void fillData(Bundle savedInstanceState) {
		mListViewAdapter = new SearchListViewAdapter(getActivity());
		mListView.setAdapter(mListViewAdapter);
	}
	
	private void showMaterialDialog(String title,String msg,OnClickListener l1,OnClickListener l2){
		materialDialog = new MaterialDialog(mainActivity);
		materialDialog.setCanceledOnTouchOutside(false);
		materialDialog.setTitle(title).setMessage(msg)
				.setPositiveButton("确定", l1).setNegativeButton("取消", l2)
				.setCanceledOnTouchOutside(false)
				.setOnDismissListener(null).show();
	}
	
	/**
	 * 搜索条目的适配器
	 * @author Administrator
	 *
	 */
	class SearchListViewAdapter extends BaseListAdapter<SearchBean>{
		private ProgressBar mProgressBar;//进度条
		private TextView mTextView;//
		private int startIndex=0;//分页数
		private boolean flag;//标示
		private ClipboardManager mClipboardManager;//剪切版
		
		public SearchListViewAdapter(Context context) {
			super(context);
			mClipboardManager=(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			flag=context.getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("open_type", false);
		}

		public SearchListViewAdapter(Context context, List<SearchBean> mList) {
			super(context, mList);
			mClipboardManager=(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			flag=context.getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("open_type", false);
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			if(pos == this.mList.size()){
				convertView = mLayoutInflater.inflate(R.layout.load_more_footer, null);
				mProgressBar=(ProgressBar) convertView.findViewById(R.id.pull_to_refresh_progress);
				mTextView=(TextView) convertView.findViewById(R.id.tv_load_more);
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if(mFragmentSearchBeans.size()%10!=0){
							Toast.makeText(context, "没有更多数据了", 0).show();
							mProgressBar.setVisibility(View.GONE);
							mTextView.setText("没有更多数据了");
							return;
						}
						startIndex+=10;
						position = getArguments().getInt(ARG_POSITION);
						String word = getArguments().getString("word");
						mProgressBar.setVisibility(View.VISIBLE);
						mTextView.setText("正在加载数据...");
						if(searchMagnet&&position==0){
							new SearchCloud().execute(SearchTask.SEARECH_TYPE_MAGNET,cloudType1.get(position), word,""+startIndex);
						}else if(searchMagnet){
							new SearchCloud().execute(SearchTask.SEARECH_TYPE_CLOUD,cloudType1.get(position-1), word,""+startIndex);
						}else{
							new SearchCloud().execute(SearchTask.SEARECH_TYPE_CLOUD,cloudType1.get(position), word,""+startIndex);
						}
					}
				});
			}else{
				final SearchBean bean = this.mList.get(pos);
				//如果是搜索结果条目
				if (convertView == null||convertView instanceof FrameLayout) {
					convertView = mLayoutInflater.inflate(R.layout.search_item, null);
				} 
				ImageView mIcoImageView = get(convertView,R.id.search_ico);
				TextView mTitleTextView = get(convertView,R.id.search_title);
				TextView mLinkTextView = get(convertView,R.id.search_url);
				TextView mContentTextView = get(convertView,R.id.search_content);
				ImageView mShareImageView= get(convertView,R.id.iv_share_res);
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						String url = bean.getUrl();
						if(flag){
							if(url==null){
								showToast("该条目没有对应的种子文件");
								return;
							}
							if(url.indexOf("http")==-1){
								try {
									Intent mIntent=new Intent();
									mIntent.setAction("android.intent.action.VIEW");  
									mIntent.setData(Uri.parse(bean.getUrl()));
									startActivity(mIntent);
								} catch (Exception e) {
									showToast("抱歉，你的手机上没有安装支持磁链的应用，建议你去下载迅雷android版");
									e.printStackTrace();
								}
							}else{
								Intent mIntent=new Intent();
								mIntent.setAction("android.intent.action.VIEW");
								Uri data=Uri.parse(bean.getUrl());
								mIntent.setData(data);
								context.startActivity(mIntent);
							}
						}else{
							if(url.indexOf("http")==-1){
								mClipboardManager.setText(bean.getUrl());
								showToast("已将磁力链接复制到剪切板");
							}else{
								Intent mIntent=new Intent(mainActivity,WebActivity.class);
								mIntent.putExtra("url", bean.getUrl());
								startActivity(mIntent);
							}
						}
					}
				});
				//设置数据
				mIcoImageView.setImageResource(bean.getMiniType());
				mContentTextView.setText(bean.getContent());
				mTitleTextView.setText(bean.getTitle());
				mLinkTextView.setText(bean.getLinkContent());
				mShareImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(loginUser == null){
							showToast("你还没有登录，不能讲资源分享到资源墙");
							return;
						}
						showMaterialDialog("提示", "是否分享<"+bean.getTitle()+">",new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								materialDialog.dismiss();
								ResShakeBean sBean=new ResShakeBean();
								sBean.setHead(loginUser.getmHead());
								sBean.setFromDevice("Android客户端");
								sBean.setPraise(0);
								sBean.setShakeTime(DateUtil.getTimeByCalendar());
								String nickName = loginUser.getNickName();
								if(TextUtils.isEmpty(nickName)){
									nickName = loginUser.getUsername();
								}
								sBean.setShakeUser(nickName);
								sBean.setUrl(bean.getUrl());
								sBean.setTrample(0);
								sBean.setTitle(bean.getTitle());
								sBean.setContext(bean.getContent());
								sBean.setMiniType(bean.getTitle());
								sBean.save(mainActivity, new SaveListener() {
									@Override
									public void onSuccess() {
										showToast("分享成功...");
									}
									@Override
									public void onFailure(int arg0, String arg1) {
										showToast("分享失败"+arg1);
									}
								});
							}
						}, null);
					}
				});
			}
			return convertView;
		}
		
		@Override
		public int getCount() {
			return this.mList == null||this.mList.size()==0 ? 0 : this.mList.size()+1;
		}
	}

	/**
	 * 搜索异步任务
	 * @author Administrator
	 *
	 */
	class SearchCloud extends SearchTask {
		@Override
		public void onError(int errorCode) {
			mLoadingView.setVisibility(View.GONE);
			mNoFileImageView.setVisibility(View.GONE);
			mLoadErrorImageView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onResult(List<SearchBean> result) {
			mListViewAdapter.addAll(result);
			mFragmentSearchBeans.addAll(result);
			mLoadingView.setVisibility(View.GONE);
			mNoFileImageView.setVisibility(View.GONE);
			mLoadErrorImageView.setVisibility(View.GONE);
		}

		@Override
		public void onNotData() {
			mLoadingView.setVisibility(View.GONE);
			mNoFileImageView.setVisibility(View.VISIBLE);
			mLoadErrorImageView.setVisibility(View.GONE);
		}
	}
}