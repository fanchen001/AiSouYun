package com.fanchen.aisou.fragment;

import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import com.fanchen.aisou.R;
import com.fanchen.aisou.ReadBookActivity;
import com.fanchen.aisou.adapter.ChapterAdapter;
import com.fanchen.aisou.bean.ChapterBean;
import com.fanchen.aisou.view.LoadingView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

@SuppressWarnings("unchecked")
public class ChapterFragment extends BaseNetFragment implements OnItemClickListener,OnRefreshListener{
	
	private ListView mListView;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private LoadingView mLoadingView;
	private ChapterAdapter mChapterAdapter;
	private ImageButton mErrorImageButton;
	private String chapterUrl;

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.ib_chapter_load_error:
			ExecuteParams executeParams = new ExecuteParams();
			List<String> urls = new ArrayList<String>();
			urls.add(chapterUrl);
			executeParams.flag = 0;
			executeParams.params = urls;
			new ExecuteTask().execute(executeParams);
			mLoadingView.setVisibility(View.VISIBLE);
			mErrorImageButton.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_chapter, container,false);
	}

	@Override
	public void findView(View v) {
		mListView = (ListView) v.findViewById(R.id.lv_chapter);
		mLoadingView = (LoadingView) v.findViewById(R.id.loadingView_chapter);
		mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_chapter);
		mErrorImageButton = (ImageButton) v.findViewById(R.id.ib_chapter_load_error);
	}

	@Override
	public void setLinsener() {
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mListView.setOnItemClickListener(this);
		mErrorImageButton .setOnClickListener(this);
	}

	@Override
	public void fillData(Bundle savedInstanceState) {
		mChapterAdapter = new ChapterAdapter(mainActivity);
		mListView.setAdapter(mChapterAdapter);
		chapterUrl = getArguments().getString("chapterUrl");
		mLoadingView.setVisibility(View.VISIBLE);
		ExecuteParams executeParams = new ExecuteParams();
		List<String> urls = new ArrayList<String>();
		urls.add(chapterUrl);
		executeParams.flag = 0;
		executeParams.params = urls;
		new ExecuteTask().execute(executeParams);
	}
	
	@Override
	public void onRefresh() {
		ExecuteParams executeParams = new ExecuteParams();
		List<String> urls = new ArrayList<String>();
		urls.add(chapterUrl);
		executeParams.flag = 0;
		executeParams.params = urls;
		new ExecuteTask().execute(executeParams);
	}
	
	@Override
	protected void onSaveState(Bundle outState) {
		super.onSaveState(outState);
	}
	
	@Override
	protected void onRestoreState(Bundle savedInstanceState) {
		super.onRestoreState(savedInstanceState);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		ChapterBean bean = (ChapterBean) parent.getItemAtPosition(position);
		Intent mIntent1 = new Intent(mainActivity,ReadBookActivity.class);
		String name = bean.getUrl().substring(bean.getUrl().lastIndexOf("/")+1)+".txt";
		mIntent1.putExtra("txtUrl", bean.getUrl());
		mIntent1.putExtra("name", name);
		mIntent1.putExtra("path", name);
		startActivity(mIntent1);
	}
	
	@Override
	protected void onFirstTimeLaunched() {
		super.onFirstTimeLaunched();
	}
	
	@Override
	public ExecuteResult executeInBackground(ExecuteParams pa,ExecuteResult rs) {
		List<String> urls = (List<String>) pa.params;
		byte[] bs = getResponseSource(urls.get(0));
		List<ChapterBean> mChapterBeans = new ArrayList<ChapterBean>();
		if(bs != null && bs.length > 0){
			String string = null;
			try {
				string = new String(bs,"gbk");
			} catch (Exception e) {
			}
			Source src = new Source(string);
			
			List<Element> allElements = src.getAllElements("table").get(0).getAllElements("td");
			for (int i = 0; i < allElements.size(); i++) {
				List<Element> allElements2 = allElements.get(i).getAllElements();
				if(allElements2.size() > 1){
					ChapterBean bean = new ChapterBean();
					bean.setTitle(allElements.get(i).getTextExtractor().toString());
					bean.setUrl(chapterUrl.substring(0, chapterUrl.lastIndexOf("/"))+"/"+allElements2.get(1).getAttributeValue("href"));
					mChapterBeans.add(bean);
				}
				
				
			}
		}
		rs.result = mChapterBeans;
		return rs;
	}
	
	@Override
	public void executePostExecute(ExecuteResult result) {
		List<ChapterBean> mChapterBeans = (List<ChapterBean>) result.result;
		mChapterAdapter.clear();
		mChapterAdapter.addAll(mChapterBeans);
		mLoadingView.setVisibility(View.GONE);
		mErrorImageButton.setVisibility(View.GONE);
		mSwipeRefreshLayout.setRefreshing(false);
	}
	
	@Override
	public void executeError(String msg) {
		mLoadingView.setVisibility(View.GONE);
		mSwipeRefreshLayout.setRefreshing(false);
		mErrorImageButton.setVisibility(View.VISIBLE);
	}
}
