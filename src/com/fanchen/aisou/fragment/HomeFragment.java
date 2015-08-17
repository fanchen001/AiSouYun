package com.fanchen.aisou.fragment;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.json.JSONArray;
import org.json.JSONException;


import com.fanchen.aisou.MainActivity;
import com.fanchen.aisou.R;
import com.fanchen.aisou.adapter.WordAdapter;
import com.fanchen.aisou.application.AisouApplication;
import com.fanchen.aisou.bean.HotWordBean;
import com.fanchen.aisou.db.HistoryDAO;
import com.fanchen.aisou.jni.HostURL;
import com.fanchen.aisou.services.WordLenovoService;
import com.fanchen.aisou.utils.DateUtil;
import com.fanchen.aisou.utils.KeyBoardUtil;
import com.fanchen.aisou.utils.NetworkStateUtil;
import com.fanchen.aisou.utils.XmlUtil;
import com.fanchen.aisou.view.KeywordsFlow;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 主界面
 * @author Administrator
 *
 */
public class HomeFragment extends BaseSearchFragment{
	
	private static final int FEEDKEY_START = 1;
	private int STATE = 1;
	private EditText mSearchEditText;//搜索输入框
	private LinearLayout mMainLinearLayout;//整个搜索热词所在的布局
	private ListView mAutoSearchWordListView;//词语联想的listview
	private List<String> mWordLists;//词语联想数据
	private WordAdapter mWordAdapter;//词语联想适配器
	private ImageButton mSearchImageButton;//搜索按钮
	private AisouApplication mAisouApplication;//应用程序上下文
	private HistoryDAO mHistoryDAO;//历史记录dao
	private SharedPreferences sp;//配置文件
	private KeywordsFlow mKeywordsFlow;
	private List<String> historyWord;
	private List<HotWordBean> hotwords =null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FEEDKEY_START:
				if(hotwords!=null){
					mKeywordsFlow.rubKeywords();
					feedKeywordsFlow(mKeywordsFlow, hotwords);
					mKeywordsFlow.go2Show(KeywordsFlow.ANIMATION_OUT);
					sendEmptyMessageDelayed(FEEDKEY_START, 8000);
				}
				break;
			}
		};
	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAisouApplication=(AisouApplication) getActivity().getApplication();
	}

	@Override
	public void onResult(JSONArray array) {
		//清空之前的数据
		mWordLists.clear();
		mWordAdapter.notifyDataSetChanged();
		int length = array.length();
		//如果数据长度大于8.截取
		length=length>8?8:length;
		for (int i = 0; i < length; i++) {
			try {
				mWordLists.add(array.getString(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		//显示
		mAutoSearchWordListView.setVisibility(View.VISIBLE);
		//通知数据改变
		mWordAdapter.notifyDataSetChanged();
	}


	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.searchButton:
			 String word = mSearchEditText.getText().toString();
			 startSearchFragment(word);
			 break;
		case R.id.ll_home_main:
			mAutoSearchWordListView.setVisibility(View.GONE);
			break;
		default:
			if(arg0 instanceof TextView && !(arg0 instanceof Button) && !(arg0 instanceof EditText)){
				String word1=((TextView)arg0).getText().toString().trim();
				startSearchFragment(word1);
			}
			break;
		}
	}

	/**
	 * 开启搜索fragment
	 * @param word 搜索词语
	 */
	private void startSearchFragment(String word) {
		mSearchEditText.setText("");
		if(!TextUtils.isEmpty(word)){
			 if(NetworkStateUtil.isNetWorkAvailable(getActivity())){
				 //隐藏输入法
				 KeyBoardUtil.closeKeybord(mSearchEditText, getActivity());
				 if(historyWord!=null||historyWord.size()>0){
					 //判断当前搜索word是否是今天已经搜索过的，如果是，不添加到记录
					if(! historyWord.contains(word)){
						mHistoryDAO.addHistoryChar(DateUtil.getTimeByCalendar(), word);
					}
				 }else{
					 //如果historyWord==null或者为0直接将词语添加到记录中
					 mHistoryDAO.addHistoryChar(DateUtil.getTimeByCalendar(), word);
				 }
				 mAutoSearchWordListView.setVisibility(View.GONE);
				 mainActivity.setTitle("搜索："+word);
				 changeFragment(new SearchFragment(),false,"search",word);
			 }else{
				 showToast("当前网络不可用");
			 }
		 }else{
			 showToast("请输入要搜索的资源名称");
		 }
	}
	
	/**
	 * 切换fragment
	 * @param f 需要切换的fragment
	 * @param init 是否添加到返回栈
	 * @param name 标示
	 * @param word 搜索词语
	 */
	public void changeFragment(Fragment f, boolean init,String name,String word) {
		if(MainActivity.fm==null)
			return;
		//切换动画
		FragmentTransaction ft = MainActivity.fm.beginTransaction().setCustomAnimations(
				R.anim.umeng_fb_slide_in_from_left,
				R.anim.umeng_fb_slide_out_from_left,
				R.anim.umeng_fb_slide_in_from_right,
				R.anim.umeng_fb_slide_out_from_right);
		Bundle bundle = new Bundle();
		bundle.putString("word", word);
		f.setArguments(bundle);
		ft.replace(R.id.fragment_layout, f);
		mAisouApplication.addFragment(f);
		if (!init)
			ft.addToBackStack(name);
		ft.commit();
	}


	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_home, null);
	}


	@Override
	public void findView(View view) {
		mKeywordsFlow = (KeywordsFlow) view.findViewById(R.id.keywordsflow);
		mMainLinearLayout=(LinearLayout) view.findViewById(R.id.ll_home_main);
		mSearchEditText=(EditText) view.findViewById(R.id.searchText);
		mAutoSearchWordListView=(ListView) view.findViewById(R.id.lv_auto_sreach_word);
		mSearchImageButton=(ImageButton) view.findViewById(R.id.searchButton);
	}


	@Override
	public void setLinsener() {
		mMainLinearLayout.setOnClickListener(this);
		mSearchImageButton.setOnClickListener(this);
		mSearchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				String word = mSearchEditText.getText().toString();
				if(!TextUtils.isEmpty(word)){
					WordLenovoService wordLenovoService = getWordLenovoService();
					if(wordLenovoService!=null){
						wordLenovoService.sendRequest(word);
					}
				}else{
					mAutoSearchWordListView.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
		mAutoSearchWordListView.setOnItemClickListener(this);
	}
	
	
	private void runGetRandomWord(){
		new Thread(){
			public void run() {
				try {
					URL url=new URL(HostURL.getHotWordUrl());
					HttpURLConnection conn=(HttpURLConnection) url.openConnection();
					conn.setReadTimeout(3*1000);
					conn.setConnectTimeout(3*1000);
					conn.setRequestMethod("GET");
					int code = conn.getResponseCode();
					if(code==200){
						Source src = new Source(conn.getInputStream());
						List<Element> allElements = src.getAllElements();
						final List<HotWordBean> wordlist=new ArrayList<HotWordBean>();
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
										wordlist.add(bean);
									}
								}
								//将热词缓存到本地文件
								XmlUtil.save(wordlist, "hotword.xml",getActivity());
								Editor edit = sp.edit();
								//更新热词的更新时间
								edit.putLong("time", System.currentTimeMillis());
								edit.commit();
								getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if(hotwords==null){
											hotwords=new ArrayList<HotWordBean>();
										}else{
											hotwords.clear();
										}
										//更新热词按钮
										hotwords.addAll(wordlist);
										mKeywordsFlow.setDuration(1000l);
										mKeywordsFlow.setOnItemClickListener(HomeFragment.this);
										feedKeywordsFlow(mKeywordsFlow, hotwords);
										mKeywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
										handler.sendEmptyMessage(FEEDKEY_START);
									}
								});
							}
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			};
		}.start();
	}
	

	@Override
	public void fillData(Bundle savedInstanceState) {
		
		sp=getActivity().getSharedPreferences("time", Context.MODE_PRIVATE);
		mWordLists=new ArrayList<String>();
		mHistoryDAO=new HistoryDAO(mainActivity);
		mWordAdapter=new WordAdapter(mainActivity);
		mWordAdapter.setData(mWordLists);
		mAutoSearchWordListView.setAdapter(mWordAdapter);
		try {
			//获取本地的热词信息
			hotwords = XmlUtil.getAll("hotword.xml", HotWordBean.class, getActivity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(hotwords!=null){
			//如果本地有热词缓存
			long time = sp.getLong("time", 0);
			long currentTime = System.currentTimeMillis();
			//如果热词缓存的时间超过了3天
			//那么重新从网络请求热词信息
			if(currentTime-time>(1000*60*60*24*3)){
				runGetRandomWord();
			}else{
				//如果没有超过一天
				//直接使用本地缓存的
				mKeywordsFlow.setDuration(1000l);
				mKeywordsFlow.setOnItemClickListener(this);
				feedKeywordsFlow(mKeywordsFlow, hotwords);
				mKeywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
				handler.sendEmptyMessage(FEEDKEY_START);
			}
		}else{
			//请求网络热词
			runGetRandomWord();
		}
		runGetHistoryBean();
		
	}
	
	
	private static void feedKeywordsFlow(KeywordsFlow keywordsFlow, List<HotWordBean> arr) {
		Random random = new Random();
		for (int i = 0; i < KeywordsFlow.MAX; i++) {
			int ran = random.nextInt(arr.size()-1);
			String tmp = arr.get(ran).getHotWord();
			keywordsFlow.feedKeyword(tmp);
		}
	}

	//词语联系条目的点击事件，点击条目直接
	//跳转到搜索结果界面
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		mAutoSearchWordListView.setVisibility(View.GONE);
		mSearchEditText.setText("");
		String word = mWordLists.get(arg2);
		startSearchFragment(word);
	}
	

/**************当页面切换到后台的时候，停止搜索热词飞入飞出效果，重新打开页面后继续************************/
	@Override
	public void onPause() {
		super.onPause();
		handler.removeMessages(FEEDKEY_START);
		STATE = 0;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (STATE == 0) {
			mKeywordsFlow.rubKeywords();
			handler.sendEmptyMessageDelayed(FEEDKEY_START, 3000);
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		handler.removeMessages(FEEDKEY_START);
		STATE = 0;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeMessages(FEEDKEY_START);
		STATE = 0;
	}
/********************************************************/
	
	
	/**
	 * 开启获取搜索记录词语的子线程
	 */
	private void runGetHistoryBean(){
		new Thread(){
			public void run() {
				historyWord = mHistoryDAO.getCharByDay(DateUtil.getTimeByCalendar());
			};
		}.start();
	}
	
}
