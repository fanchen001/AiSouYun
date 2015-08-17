package com.fanchen.aisou.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.fanchen.aisou.R;
import com.fanchen.aisou.adapter.BookAdapter;
import com.fanchen.aisou.adapter.WordAdapter;
import com.fanchen.aisou.bean.LightNovelBean;
import com.fanchen.aisou.bean.LightNovelInfoBean;
import com.fanchen.aisou.jni.HostURL;
import com.fanchen.aisou.services.WordLenovoService;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class BookFragment extends BaseSearchFragment implements OnItemClickListener{
	
	private GridView mGridView;
	private BookAdapter mAdapter;
	private ListView mListView;
	private EditText mEditText;
	private ImageButton mImageButton;
	private WordAdapter mWordAdapter;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_book_search:
			String word = mEditText.getText().toString().trim();
			String encode = null;
			try {
				encode = URLEncoder.encode(word,"gbk");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			mainActivity.changeFragment(new BookListFragment(), false, "book", "bookListUrl", HostURL.getBookUrl(0)+encode+"&page=");
			break;

		default:
			break;
		}
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_book, container,false);
	}

	@Override
	public void findView(View v) {
		mGridView = (GridView) v.findViewById(R.id.gv_book);
		mListView = (ListView) v.findViewById(R.id.lv_book_auto_sreach_word);
		mEditText = (EditText) v.findViewById(R.id.ed_book_search);
		mImageButton = (ImageButton) v.findViewById(R.id.ib_book_search);
	}

	@Override
	public void setLinsener() {
		mGridView.setOnItemClickListener(this);
		mListView.setOnItemClickListener(this);
		mImageButton.setOnClickListener(this);
		mEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
				String word = mEditText.getText().toString();
				if(!TextUtils.isEmpty(word)){
					WordLenovoService wordLenovoService = getWordLenovoService();
					if(wordLenovoService!=null){
						wordLenovoService.sendRequest(word);
					}
				}else{
					mListView.setVisibility(View.GONE);
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
		mWordAdapter = new WordAdapter(mainActivity);
		List<LightNovelBean> mList = new ArrayList<LightNovelBean>();
		LightNovelBean bean1 = new LightNovelInfoBean();
		bean1.setTitle("最新更新");
		bean1.setImage(""+R.drawable.book_class_1);
		bean1.setUrl(HostURL.getBookUrl(1));
		LightNovelBean bean2 = new LightNovelInfoBean();
		bean2.setTitle("所有书籍");
		bean2.setImage(""+R.drawable.book_class_2);
		bean2.setUrl(HostURL.getBookUrl(2));
		LightNovelBean bean3 = new LightNovelInfoBean();
		bean3.setTitle("动画化");
		bean3.setImage(""+R.drawable.book_class_3);
		bean3.setUrl(HostURL.getBookUrl(3));
		LightNovelBean bean4 = new LightNovelInfoBean();
		bean4.setTitle("热门小说");
		bean4.setImage(""+R.drawable.book_class_4);
		bean4.setUrl(HostURL.getBookUrl(4));
		LightNovelBean bean5 = new LightNovelInfoBean();
		bean5.setTitle("最新上架");
		bean5.setImage(""+R.drawable.book_class_5);
		bean5.setUrl(HostURL.getBookUrl(5));
		LightNovelBean bean6 = new LightNovelInfoBean();
		bean6.setTitle("完结小说");
		bean6.setImage(""+R.drawable.book_class_6);
		bean6.setUrl(HostURL.getBookUrl(6));
		mList.add(bean1);
		mList.add(bean2);
		mList.add(bean3);
		mList.add(bean4);
		mList.add(bean5);
		mList.add(bean6);
		mAdapter = new BookAdapter(mainActivity, mList);
		mGridView.setAdapter(mAdapter);
		mListView.setAdapter(mWordAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Object object = parent.getItemAtPosition(position);
		if(object instanceof LightNovelBean){
			LightNovelBean bean = (LightNovelBean) parent.getItemAtPosition(position);
			mainActivity.changeFragment(new BookListFragment(), false, "book", "bookListUrl", bean.getUrl());
		}else if(object instanceof String){
			String encode = null;
			try {
				encode = URLEncoder.encode((String)object,"gbk");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			mainActivity.changeFragment(new BookListFragment(), false, "book", "bookListUrl", HostURL.getBookUrl(0)+encode+"&page=");
		}
	}

	@Override
	public void onResult(JSONArray data) {
		mWordAdapter.clear();
		List<String> datas = new ArrayList<String>();
		int length = data.length();
		//如果长度大于6，截取前面6个
		length=length>6?6:length;
		for (int i = 0; i < length; i++) {
			try {
				//添加到数据列表
				datas.add(data.getString(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		mWordAdapter.addAll(datas);
		//显示listview
		mListView.setVisibility(View.VISIBLE);
	}
}
