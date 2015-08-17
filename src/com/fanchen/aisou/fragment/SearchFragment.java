package com.fanchen.aisou.fragment;

import java.util.ArrayList;
import java.util.List;

import github.chenupt.dragtoplayout.DragTopLayout;

import org.json.JSONArray;
import org.json.JSONException;

import com.fanchen.aisou.R;
import com.fanchen.aisou.adapter.MyFragmenPageAdapter;
import com.fanchen.aisou.adapter.WordAdapter;
import com.fanchen.aisou.bean.CouldTypeBean;
import com.fanchen.aisou.callback.OnTopStateListener;
import com.fanchen.aisou.services.WordLenovoService;
import com.fanchen.aisou.utils.KeyBoardUtil;
import com.fanchen.aisou.utils.NetworkStateUtil;
import com.fanchen.aisou.utils.XmlUtil;
import com.fanchen.aisou.view.PagerSlidingTabStrip;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class SearchFragment extends BaseSearchFragment implements OnTopStateListener{
	private PagerSlidingTabStrip mPagerSlidingTabStrip;//开源控件
	private ViewPager mViewPager;//下方的viewpager
	private WordAdapter mWordAdapter; //联想词适配器
	private List<String> mWordLists;//联想词语数据
	private ListView mLenovoWordListView;//联想词listview
	private MyFragmenPageAdapter mPageAdapter;//viewpager的适配器
	private EditText mSearchEditText;//搜索输入框
	private ImageButton mSearchImageButton;//搜索按钮
	private DragTopLayout mDragTopLayout;//顶部布局
	private static String[] cloudType = { "百度","华为", "115", "旋风", "迅雷","金山","360","一木禾","千军万马"};
	private List<Fragment> fragments;//用来显示在viewpager的fragment
	private SharedPreferences sp;
	
	private List<String> cloudType1;

	@Override
	public void onResult(JSONArray array) {
		//先清空之前的数据
		mWordLists.clear();
		mWordAdapter.notifyDataSetChanged();
		int length = array.length();
		//如果长度大于6，截取前面6个
		length=length>6?6:length;
		for (int i = 0; i < length; i++) {
			try {
				//添加到数据列表
				mWordLists.add(array.getString(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		//显示listview
		mLenovoWordListView.setVisibility(View.VISIBLE);
		mWordAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View arg0) {
		
		switch (arg0.getId()) {
		case R.id.ib_search:
			String word = mSearchEditText.getText().toString();
			searchNewWord(word);
			break;

		default:
			break;
		}

	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_search, null);
	}

	@Override
	public void findView(View view) {
		mLenovoWordListView=(ListView) view.findViewById(R.id.lv_auto_sreach_word_searchfragment);
		mViewPager = (ViewPager) view.findViewById(R.id.pager_content);
		mDragTopLayout = (DragTopLayout) view.findViewById(R.id.drag_layout);
		mSearchEditText = (EditText) view.findViewById(R.id.ed_search);
		mSearchImageButton=(ImageButton) view.findViewById(R.id.ib_search);
		mPagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
	
	}

	@Override
	public void setLinsener() {
		mSearchImageButton.setOnClickListener(this);
		mLenovoWordListView.setOnItemClickListener(this);
		mSearchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
				String word = mSearchEditText.getText().toString();
				if(!TextUtils.isEmpty(word)){
					WordLenovoService wordLenovoService = getWordLenovoService();
					if(wordLenovoService!=null){
						wordLenovoService.sendRequest(word);
					}
				}else{
					mLenovoWordListView.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		
	}

	@Override
	public void fillData(Bundle savedInstanceState) {
		
		cloudType1 = new ArrayList<String>();
		
		List<CouldTypeBean> allCouldType = null;
		
		try{
			allCouldType = XmlUtil.getAll("couldType.xml", CouldTypeBean.class, mainActivity);
		}catch(Exception e){
			
		}
		
		if(allCouldType != null && allCouldType.size() > 0){
			
			for (CouldTypeBean bean : allCouldType) {
				if(bean.getCheck() == 1){
					cloudType1.add(bean.getCouldType());
				}
				
			}
		}else{
			for (int i = 0; i < cloudType.length; i++) {
				cloudType1.add(cloudType[i]);
			}
		}
	
		
		//关闭顶部布局
		mDragTopLayout.closeTopView(true);
		mWordLists=new ArrayList<String>();
		fragments = new ArrayList<Fragment>();
		sp=mainActivity.getSharedPreferences("config", Context.MODE_PRIVATE);
		mPageAdapter =new MyFragmenPageAdapter(mainActivity,getFragmentManager(), fragments,sp.getBoolean("magnet", false));
		//初始化viewpager里显示的fragment
		for (int i = 0; i < (sp.getBoolean("magnet", false)?cloudType1.size()+1:cloudType1.size()); i++) {
			SuperAwesomeCardFragment fragment = SuperAwesomeCardFragment.newInstance(cloudType1,i, getArguments().getString("word"),this,sp.getBoolean("magnet", false));
			fragments.add(fragment);
		}
		mViewPager.setAdapter(mPageAdapter);
		mWordAdapter=new WordAdapter(getActivity());
		mWordAdapter.setData(mWordLists);
		mLenovoWordListView.setAdapter(mWordAdapter);
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		mViewPager.setPageMargin(pageMargin);
		//设置缓存5个页面
		mViewPager.setOffscreenPageLimit(5);
		mPagerSlidingTabStrip.setViewPager(mViewPager);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String word = mWordLists.get(arg2);
		searchNewWord(word);
	}
	
	/**
	 * 发送一个新的搜索请求
	 * @param word
	 */
	private void searchNewWord(String word) {
		//如果网络可用的话
		boolean available = NetworkStateUtil.isNetWorkAvailable(mainActivity);
		if(!available){
			showToast("当前木有网络");
			return;
		}
		mainActivity.setTitle("搜索："+word);
		mLenovoWordListView.setVisibility(View.GONE);
		mSearchEditText.setText("");
		//关闭顶部布局
		mDragTopLayout.closeTopView(true);
		//关闭键盘
		KeyBoardUtil.closeKeybord(mSearchEditText, getActivity());
		//清空之前的数据
		fragments.clear();
		mPageAdapter.notifyDataSetChanged();
		//添加新的数据
		for (int i = 0; i < (sp.getBoolean("magnet", false)?cloudType.length+1:cloudType.length); i++) {
			SuperAwesomeCardFragment fragment = SuperAwesomeCardFragment.newInstance(cloudType1,i, word, this,sp.getBoolean("magnet", false));
			fragments.add(fragment);
		}
		mPageAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClose() {
		mDragTopLayout.setOpen(false);
	}

	@Override
	public void onOpen() {
		mDragTopLayout.setOpen(true);
	}

	@Override
	public void onCloseListView() {
		mLenovoWordListView.setVisibility(View.GONE);
	}
}
