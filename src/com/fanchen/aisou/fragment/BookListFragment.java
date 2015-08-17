package com.fanchen.aisou.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import com.fanchen.aisou.R;
import com.fanchen.aisou.adapter.BookListAdapter;
import com.fanchen.aisou.adapter.BookListAdapter.OnLoadMoreData;
import com.fanchen.aisou.bean.LightNovelBean;
import com.fanchen.aisou.view.LoadingView;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

@SuppressWarnings("unchecked")
public class BookListFragment extends BaseNetFragment implements OnRefreshListener,
		OnLoadMoreData {
	protected static final int SUCCESS = 0;
	public static final int LOADING_DATE_REFRESH = 100;
	public static final int LOADING_DATE = 200;
	private ListView listView;
	private LoadingView mLoadingView;
	private SwipeRefreshLayout swipe;
	private ImageButton mErrorImageButton;
	private BookListAdapter mBookAdapter;
	private int currentPage = 1;
	private int loadDataState = LOADING_DATE;
	ArrayList<LightNovelBean> mCurrentLightNovelBeans;
	private String bookListUrl;

	@Override
	public void onRefresh() {
		currentPage = 1;
		loadDataState = LOADING_DATE_REFRESH;
		List<String> urls = new ArrayList<String>();
		urls.add(bookListUrl);
		urls.add("" + (currentPage++));
		ExecuteParams executeParams = new ExecuteParams();
		executeParams.flag = 0;
		executeParams.params = urls;
		new ExecuteTask().execute(executeParams);
	}

	@Override
	public void findView(View v) {
		listView = (ListView) v.findViewById(R.id.listView_book);
		mLoadingView = (LoadingView) v.findViewById(R.id.loadingView_book);
		swipe = (SwipeRefreshLayout) v.findViewById(R.id.swipe_book);
		mErrorImageButton = (ImageButton) v.findViewById(R.id.ib_book_load_error);
	}

	@Override
	public void setLinsener() {
		swipe.setOnRefreshListener(this);
		mErrorImageButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ib_book_load_error:
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
		return inflater.inflate(R.layout.fragment_book_list, container, false);
	}

	@Override
	public void fillData(Bundle savedInstanceState) {
		mBookAdapter = new BookListAdapter(mainActivity);
		mBookAdapter.setOnLoadMoreData(this);
		listView.setAdapter(mBookAdapter);
		// 顶部刷新的样式
		swipe.setColorSchemeResources(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
	}

	@Override
	protected void onSaveState(Bundle outState) {
		if (mCurrentLightNovelBeans != null) {
			outState.putParcelableArrayList("LightNovel",
					mCurrentLightNovelBeans);
		}
		super.onSaveState(outState);
	}

	@Override
	protected void onFirstTimeLaunched() {
		super.onFirstTimeLaunched();
		mLoadingView.setVisibility(View.VISIBLE);
		bookListUrl = getArguments().getString("bookListUrl");
		if (bookListUrl != null) {
			loadDataState = LOADING_DATE;
			List<String> urls = new ArrayList<String>();
			urls.add(bookListUrl);
			urls.add("" + (currentPage++));
			ExecuteParams executeParams = new ExecuteParams();
			executeParams.flag = 0;
			executeParams.params = urls;
			new ExecuteTask().execute(executeParams);
		}
	}

	@Override
	protected void onRestoreState(Bundle savedInstanceState) {
		super.onRestoreState(savedInstanceState);
		if (savedInstanceState != null) {
			mCurrentLightNovelBeans = savedInstanceState
					.getParcelableArrayList("LightNovel");
			if (mCurrentLightNovelBeans != null) {
				mBookAdapter.addAll(mCurrentLightNovelBeans);
			}
		}
	}

	@Override
	public void loadMore(View v) {
		loadDataState = LOADING_DATE;
		List<String> urls = new ArrayList<String>();
		urls.add(bookListUrl);
		urls.add("" + (currentPage++));
		ExecuteParams executeParams = new ExecuteParams();
		executeParams.flag = 0;
		executeParams.params = urls;
		new ExecuteTask().execute(executeParams);
	}

	@Override
	public ExecuteResult executeInBackground(ExecuteParams pa,ExecuteResult rs) {
		List<String> urls = (List<String>) pa.params;
		byte[] bs = getResponseSource(urls.get(0) + urls.get(1));
		List<LightNovelBean> mLightNovelBeans = new ArrayList<LightNovelBean>();
		if (bs != null && bs.length > 0) {
			String string = null;
			try {
				string = new String(bs, "gbk");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			Source src = new Source(string);
			List<Element> allElements = src.getAllElements("div");
			for (int i = 0; i < allElements.size(); i++) {
				if ("width:373px;float:left;margin:5px 0px 5px 5px;"
						.equals(allElements.get(i).getAttributeValue("style"))) {
					List<Element> allElements2 = allElements.get(i)
							.getAllElements();
					LightNovelBean bean = new LightNovelBean();
					mLightNovelBeans.add(bean);
					for (int j = 0; j < allElements2.size(); j++) {
						bean.setTitle(allElements2.get(5).getTextExtractor()
								.toString());
						bean.setImage(allElements2.get(3).getAttributeValue(
								"src"));
						bean.setIntroduction(allElements2.get(8)
								.getTextExtractor().toString());
						bean.setUrl(allElements2.get(2).getAttributeValue(
								"href"));
						String allState = allElements2.get(7)
								.getTextExtractor().toString();
						String[] split = allState.split("/");
						bean.setAuthor(split[0]);
						bean.setState(split[2]);
						bean.setUpdateTime(split[1]);
					}
				}
			}
		}
		rs.result = mLightNovelBeans;
		return rs;
	}

	@Override
	public void executePostExecute(ExecuteResult result) {
		mLoadingView.setVisibility(View.GONE);
		mErrorImageButton.setVisibility(View.GONE);
		swipe.setRefreshing(false);
		mCurrentLightNovelBeans = (ArrayList<LightNovelBean>) result.result;
		if (loadDataState == LOADING_DATE) {
			mBookAdapter.addAll(mCurrentLightNovelBeans);
		} else if (loadDataState == LOADING_DATE_REFRESH) {
			mBookAdapter.clear();
			mBookAdapter.addAll(mCurrentLightNovelBeans);
		}
		
	}

	@Override
	public void executeError(String msg) {
		swipe.setRefreshing(false);
		mLoadingView.setVisibility(View.GONE);
		mErrorImageButton.setVisibility(View.VISIBLE);
		
	}

}
