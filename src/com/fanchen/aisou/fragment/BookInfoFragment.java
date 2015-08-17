package com.fanchen.aisou.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import cn.bmob.v3.BmobUser;

import com.fanchen.aisou.R;
import com.fanchen.aisou.adapter.RecommendBookAdapter;
import com.fanchen.aisou.bean.DownloadLogBean;
import com.fanchen.aisou.bean.LightNovelBean;
import com.fanchen.aisou.bean.LightNovelInfoBean;
import com.fanchen.aisou.bean.UserBean;
import com.fanchen.aisou.db.CollectDao;
import com.fanchen.aisou.db.DownloadLogDao;
import com.fanchen.aisou.imagefetcher.ImageFetcher;
import com.fanchen.aisou.utils.DisplayUtil;
import com.fanchen.aisou.view.LoadingView;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressWarnings("unchecked")
public class BookInfoFragment extends BaseNetFragment implements OnItemClickListener{
	private ImageView mIcoImageView;
	private TextView mTitleTextView;
	private TextView mViewsTextView;
	private TextView mPressTextView;
	private TextView mAuthorTextView;
	private TextView mUpdateTextView;
	private TextView mInfoTextView;
	private TextView mDirTextView;
	private ImageButton mErrorImageButton;
	private Button mDownloadButton;
	private Button mReadButton;
	private Button mCollectButton;
	private LoadingView mLoadingView;
	private RelativeLayout mInfoRelativeLayout;
	private boolean infoUnfoldState;
	private boolean isBookAvailable;
	private RelativeLayout mRelativeLayout;
	private GridView mGridView;
	private RecommendBookAdapter mTempAdapter;
	private ImageFetcher mImageFetcher;
	private LightNovelInfoBean mCurrentLightNovelInfo;
	
	private CollectDao mCollectDao;
	private DownloadLogDao mDownloadLogDao;
	private boolean isCollect;
	private boolean isDownload;
	private DownloadManager mDownloadManager;
	
	private String chapterUrl;
	private UserBean loginUser;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_intro_abbr_indicator:
			LayoutParams layoutParams = mInfoTextView.getLayoutParams();
			if(!infoUnfoldState){
				layoutParams.height = LayoutParams.WRAP_CONTENT;
				infoUnfoldState = true;
			}else{
				layoutParams.height = DisplayUtil.dp2px(mainActivity, 60);
				infoUnfoldState = false;
			}
			mInfoTextView.setLayoutParams(layoutParams);
			break;
			
		case R.id.tv_directory_text:
			if(!isBookAvailable){
				showToast("本书已经下架");
				return;
			}
			if(!TextUtils.isEmpty(chapterUrl)){
				mainActivity.changeFragment(new ChapterFragment(), false, "bookInfo", "chapterUrl", chapterUrl);
			}else{
				showToast("获取目录连接失败");
			}
			break;
			
		case R.id.bt_download_button:
			if(!isBookAvailable){
				showToast("本书已经下架");
				return;
			}
			if(!isDownload){
				DownloadLogBean bean = new DownloadLogBean();
				bean.setTitle(mCurrentLightNovelInfo.getTitle());
				bean.setAuthor(mCurrentLightNovelInfo.getAuthor());
				bean.setFrom(mCurrentLightNovelInfo.getFrom());
				bean.setImage(mCurrentLightNovelInfo.getImage());
				bean.setState(0);
				String string = getArguments().getString("url");
				bean.setUrl("http://dl.wenku8.com/down.php?type=txt&id="+string.substring(string.lastIndexOf("/")+1, string.length()-4));
				mDownloadLogDao.addDownload(bean);
				//创建下载请求  
                DownloadManager.Request down=new DownloadManager.Request (Uri.parse(bean.getUrl()));  
                //设置允许使用的网络类型，这里是移动网络和wifi都可以  
                down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);  
                //禁止发出通知，既后台下载  
                down.setShowRunningNotification(true);  
                //不显示下载界面  
                down.setVisibleInDownloadsUi(true);  
                //设置下载后文件存放的位置  
                down.setDestinationInExternalFilesDir(mainActivity, null, bean.getTitle()+".txt");  
                //将下载请求放入队列  
                mDownloadManager.enqueue(down); 
                showToast("开始下载...");
			}else{
				showToast("不需要重复下载");
			}
			break;
			
		case R.id.bt_begin_read_text:
			if(!isBookAvailable){
				showToast("本书已经下架");
				return;
			}
			if(!TextUtils.isEmpty(chapterUrl)){
				mainActivity.changeFragment(new ChapterFragment(), false, "bookInfo", "chapterUrl", chapterUrl);
			}else{
				showToast("获取目录连接失败");
			}
			break;
		case R.id.bt_subscribe_button:
			if(!isBookAvailable){
				showToast("本书已经下架");
				return;
			}
			if(loginUser == null){
				showToast("你还没用登录，不能进行书籍收藏");
				return;
			}
			if(isCollect){
				mCollectDao.deleteCollectByBookUrl(getArguments().getString("url"));
				mCollectButton.setText("加关注");
				mCollectButton.setPressed(false);
			}else{
				if(mCurrentLightNovelInfo != null){
					mCurrentLightNovelInfo.setUrl(getArguments().getString("url"));
					mCurrentLightNovelInfo.setAuthor(loginUser.getUsername());
					mCollectDao.addCollect(mCurrentLightNovelInfo);
					mCollectButton.setText("已收藏");
					mCollectButton.setPressed(true);
				}	
			}
			break;
		case R.id.ib_book_info_load_error:
			onFirstTimeLaunched();
			mLoadingView.setVisibility(View.VISIBLE);
			mErrorImageButton.setVisibility(View.GONE);
			break;

		default:
			break;
}
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_book_info, container,false);
	}

	@Override
	public void findView(View v) {
		mDownloadButton = (Button) v.findViewById(R.id.bt_download_button);
		mReadButton = (Button) v.findViewById(R.id.bt_begin_read_text);
		mCollectButton = (Button) v.findViewById(R.id.bt_subscribe_button);
		mIcoImageView = (ImageView) v.findViewById(R.id.iv_cover_image);
		mTitleTextView = (TextView) v.findViewById(R.id.tv_book_title_text);
		mViewsTextView = (TextView) v.findViewById(R.id.tv_views_text);
		mPressTextView = (TextView) v.findViewById(R.id.tv_press_text);
		mAuthorTextView = (TextView) v.findViewById(R.id.tv_author_text);
		mUpdateTextView = (TextView) v.findViewById(R.id.tv_update_text);
		mInfoTextView = (TextView) v.findViewById(R.id.tv_intro_text);
		mDirTextView = (TextView) v.findViewById(R.id.tv_directory_text);
		mErrorImageButton = (ImageButton) v.findViewById(R.id.ib_book_info_load_error);
		mGridView =(GridView) v.findViewById(R.id.gv_book_info);
		mInfoRelativeLayout = (RelativeLayout) v.findViewById(R.id.rl_book_info);
		mLoadingView =(LoadingView) v.findViewById(R.id.loadingView_book_info);
		mRelativeLayout = (RelativeLayout) v.findViewById(R.id.rl_intro_abbr_indicator);
	}

	@Override
	public void setLinsener() {
		mErrorImageButton.setOnClickListener(this);
		mRelativeLayout.setOnClickListener(this);
		mGridView.setOnItemClickListener(this);
		mDirTextView.setOnClickListener(this);
		mDownloadButton.setOnClickListener(this);
		mCollectButton.setOnClickListener(this);
		mReadButton.setOnClickListener(this);
	}

	
	@Override
	public void fillData(Bundle savedInstanceState) {
		mDownloadManager = (DownloadManager) mainActivity.getSystemService(Context.DOWNLOAD_SERVICE);
		mImageFetcher = new ImageFetcher(mainActivity, 240);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
		mCollectDao = new CollectDao(mainActivity);
		mDownloadLogDao = new DownloadLogDao(mainActivity);
		loginUser = BmobUser.getCurrentUser(mainActivity, UserBean.class);
		DownloadLogBean downloadLogBean = mDownloadLogDao.getDownloadByUrl(getArguments().getString("url"));
		if(downloadLogBean!=null){
			isDownload = true;
		}
		if(isDownload){
			mDownloadButton.setText("已添加下载");
			mDownloadButton.setPressed(true);
		}
		if(loginUser != null){
			LightNovelBean collect = mCollectDao.getCollectByUrlAndUser(getArguments().getString("url"),loginUser.getUsername());
			if(collect != null){
				isCollect = true;
			}
			if(isCollect){
				mCollectButton.setText("已收藏");
				mCollectButton.setPressed(true);
			}
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		
		LightNovelBean bean = (LightNovelBean) parent.getItemAtPosition(position);
		
		mainActivity.changeFragment(new BookInfoFragment(), false, "bookInfo", "url", bean.getUrl());
		
	}
	
	@Override
	protected void onSaveState(Bundle outState) {
		if(mCurrentLightNovelInfo != null){
			outState.putParcelable("LightNovelInfo", mCurrentLightNovelInfo);
			outState.putBoolean("isCollect", isCollect);
		}
		super.onSaveState(outState);
	}
	
	@Override
	protected void onRestoreState(Bundle savedInstanceState) {
		super.onRestoreState(savedInstanceState);
		if(savedInstanceState != null){
			mCurrentLightNovelInfo = savedInstanceState.getParcelable("LightNovelInfo");
			if(mCurrentLightNovelInfo != null){
				fillViewDate(mCurrentLightNovelInfo) ;
			}
			isCollect = savedInstanceState.getBoolean("isCollect");
			if(isCollect){
				mCollectButton.setText("已收藏");
				mCollectButton.setPressed(true);
			}
			
		}
	}
	
	@Override
	protected void onFirstTimeLaunched() {
		super.onFirstTimeLaunched();
		final String strUrl = getArguments().getString("url");
		ExecuteParams executeParams = new ExecuteParams();
		List<String> urls = new ArrayList<String>();
		urls.add(strUrl);
		executeParams.flag = 0;
		executeParams.params = urls;
		new ExecuteTask().execute(executeParams);
	}
	
	
	private void fillViewDate(LightNovelInfoBean bean) {
		chapterUrl = bean.getSectionBelowUrl();
		mTitleTextView.setText(bean.getTitle());
		mViewsTextView.setText(bean.getViews());
		mPressTextView.setText(bean.getFrom());
		
		mAuthorTextView.setText(bean.getAuthor());
		if(!isBookAvailable){
			mDirTextView.setText("本书已下架");
			mUpdateTextView.setText("本书已下架");
		}else{
			mDirTextView.setText(bean.getSectionBelow());
			mUpdateTextView.setText(bean.getUpdateTime());
		}
		mInfoTextView.setText(bean.getInfo());
		mImageFetcher.loadImage(bean.getImage(), mIcoImageView);
		mTempAdapter = new RecommendBookAdapter(mainActivity,bean.getmCorrelationBeans());
		mGridView.setAdapter(mTempAdapter);
		int size = bean.getmCorrelationBeans().size();
		DisplayMetrics dm = new DisplayMetrics();
		mainActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int allWidth = (int) (110 * size * density);
		int itemWidth = (int) (100 * density);
		@SuppressWarnings("deprecation")
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				allWidth, LinearLayout.LayoutParams.FILL_PARENT);
		mGridView.setLayoutParams(params);
		mGridView.setColumnWidth(itemWidth);
		mGridView.setHorizontalSpacing(10);
		mGridView.setStretchMode(GridView.NO_STRETCH);
		mGridView.setNumColumns(size);
		mLoadingView.setVisibility(View.GONE);
		mInfoRelativeLayout.setVisibility(View.VISIBLE);
	}
	
	@Override
	public ExecuteResult executeInBackground(ExecuteParams pa,ExecuteResult rs) {
		List<String> urls = (List<String>) pa.params;
		String strUrl = urls.get(0);
		byte[] bs = getResponseSource(strUrl);
		LightNovelInfoBean bean = null;
		if(bs != null && bs.length >0){
			String string = null;
			try {
				string = new String(bs,"gbk");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			Source src = new Source(string);
			bean = new LightNovelInfoBean();
			List<Element> allElements = src.getAllElements("table");
			if(allElements.get(0).getAllElements().size() < 20){
				isBookAvailable = false ;
				bean.setTitle(allElements.get(0).getAllElements().get(7).getTextExtractor().toString());
				bean.setFrom(allElements.get(0).getAllElements().get(12).getTextExtractor().toString().substring(5));
				bean.setAuthor(allElements.get(0).getAllElements().get(13).getTextExtractor().toString().substring(5));
				bean.setState(allElements.get(0).getAllElements().get(14).getTextExtractor().toString().substring(5));
				bean.setViews(allElements.get(0).getAllElements().get(15).getTextExtractor().toString().substring(5));
				bean.setInfo(allElements.get(2).getAllElements().get(15).getTextExtractor().toString());
				bean.setImage(allElements.get(2).getAllElements().get(3).getAttributeValue("src"));
				List<Element> allElements2 = allElements.get(3).getAllElements("div");
				List<LightNovelBean> mNovelBeans = new ArrayList<LightNovelBean>();
				for (int j = 0; j < allElements2.size(); j++) {
					if("float: left;text-align:center;width: 95px; height:155px;overflow:hidden;".equals(allElements2.get(j).getAttributeValue("style"))){
						LightNovelBean lightNovelBean = new LightNovelBean();
						String bookUrl = allElements2.get(j).getAllElements().get(1).getAttributeValue("href");
						String bookImageUrl = allElements2.get(j).getAllElements().get(2).getAttributeValue("src");
						String bookTitle = allElements2.get(j).getTextExtractor().toString();
						lightNovelBean.setImage(bookImageUrl);
						lightNovelBean.setTitle(bookTitle);
						lightNovelBean.setUrl(bookUrl);
						mNovelBeans.add(lightNovelBean);
					}
				}
				bean.setmCorrelationBeans(mNovelBeans);
			}else{
				isBookAvailable = true ;
				bean.setTitle(allElements.get(0).getAllElements().get(7).getTextExtractor().toString());
				bean.setFrom(allElements.get(0).getAllElements().get(13).getTextExtractor().toString().substring(5));
				bean.setViews(allElements.get(0).getAllElements().get(19).getTextExtractor().toString().substring(5));
				bean.setUpdateTime(allElements.get(0).getAllElements().get(18).getTextExtractor().toString().substring(5));
				bean.setState(allElements.get(0).getAllElements().get(15).getTextExtractor().toString().substring(5));
				bean.setUpdateIntroduction(allElements.get(2).getAllElements().get(4).getTextExtractor().toString());
				bean.setInfo(allElements.get(2).getAllElements().get(13).getTextExtractor().toString());
				bean.setSectionBelow(allElements.get(2).getAllElements().get(8).getTextExtractor()
						.toString().substring(5));
				bean.setAuthor(allElements.get(0).getAllElements().get(14).getTextExtractor()
						.toString().substring(5));
				String belowUrl = allElements.get(2).getAllElements().get(8).getAttributeValue("href");
				bean.setSectionBelowUrl(belowUrl.substring(0, belowUrl.lastIndexOf("/"))+"/index.htm");
				bean.setImage(allElements.get(2).getAllElements()
						.get(3).getAttributeValue("src"));
				List<Element> allElements2 = allElements.get(3).getAllElements("div");
				List<LightNovelBean> mNovelBeans = new ArrayList<LightNovelBean>();
				for (int j = 0; j < allElements2.size(); j++) {
					if("float: left;text-align:center;width: 95px; height:155px;overflow:hidden;".equals(allElements2.get(j).getAttributeValue("style"))){
						LightNovelBean lightNovelBean = new LightNovelBean();
						String bookUrl = allElements2.get(j).getAllElements().get(1).getAttributeValue("href");
						String bookImageUrl = allElements2.get(j).getAllElements().get(2).getAttributeValue("src");
						String bookTitle = allElements2.get(j).getTextExtractor().toString();
						lightNovelBean.setImage(bookImageUrl);
						lightNovelBean.setTitle(bookTitle);
						lightNovelBean.setUrl(bookUrl);
						mNovelBeans.add(lightNovelBean);
					}
				}
				bean.setmCorrelationBeans(mNovelBeans);
			}
			    rs.result = bean;
		}
		return rs;
	}
	
	@Override
	public void executePostExecute(ExecuteResult result) {
		mCurrentLightNovelInfo = (LightNovelInfoBean) result.result;
		mErrorImageButton.setVisibility(View.GONE);
		fillViewDate(mCurrentLightNovelInfo);
	}
	
	@Override
	public void executeError(String msg) {
		mLoadingView.setVisibility(View.GONE);
		mErrorImageButton.setVisibility(View.VISIBLE);
	}
	

}
