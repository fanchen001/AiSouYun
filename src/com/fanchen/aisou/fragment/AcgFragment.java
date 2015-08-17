package com.fanchen.aisou.fragment;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fanchen.aisou.R;
import com.fanchen.aisou.adapter.StaggeredAdapter;
import com.fanchen.aisou.bean.ImageBean;
import com.fanchen.aisou.jni.HostURL;
import com.fanchen.aisou.utils.BaseTask;
import com.fanchen.aisou.utils.SqliteUtil;
import com.fanchen.aisou.view.waterfall.XListView;
import com.fanchen.aisou.view.waterfall.XListView.onArriveBottom;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AcgFragment extends BaseFragment implements
		android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener {
	protected static final int SUCCESS = 0;
	private XListView listView;
	private	SwipeRefreshLayout swipe;
	private StaggeredAdapter mStaggeredAdapter;
	private int page = 0;
	private boolean isRefresh = false;
	private boolean isLoad = false;
	private SqliteUtil mSqliteUtil;
	private String table;
	private SharedPreferences mSharedPreferences;
	private boolean isDataOverdue=false;
	private String[] tables=new String[] {
			"STAR_PHOTO", "BEAUTIFUL_WOMAN", "COLLEGE_CAMPUS",
			"JAPANESE_ANIME", "ANIME_GIRLS", "LANDSCAPE", "COSPLAY",
			"PARTIS" };

	@Override
	public void onRefresh() {
		isRefresh = true;
		page = 0;
		String url = HostURL.getImageUrl();
		String type="browse";
		if("browse".equals(type)){
			new ImageBeanTask().execute(url + 0);
		}else{
			new SearchBeanTask().execute(url+0);
		}
	}

	@Override
	public void findView(View v) {
		listView = (XListView) v.findViewById(R.id.listView);
		swipe = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
	}

	@Override
	public void setLinsener() {
		swipe.setOnRefreshListener(this);
		listView.setArriveBottom(new onArriveBottom() {
			@Override
			public void onArriveBottom() {
				if (!isLoad) {
					isLoad = true;
					page++;
					isRefresh = false;
					String url = HostURL.getImageUrl();
					String type="browse";
					if("browse".equals(type)){
						new ImageBeanTask().execute(url + (page * 30));
					}else{
						new SearchBeanTask().execute(url + (page * 30));
					}
				}
			}
		});
	}


	class ImageBeanDBTask extends AsyncTask<String, Integer, LinkedList<ImageBean>> {
		@Override
		protected LinkedList<ImageBean> doInBackground(String... params) {
			List<ImageBean> all = null;
			try {
				all = mSqliteUtil.mOpenHelper.queryAll(params[0],ImageBean.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			LinkedList<ImageBean> beans = new LinkedList<ImageBean>();
			for (ImageBean b : all) {
				beans.add(b);
			}
			return beans;
		}

		@Override
		protected void onPostExecute(LinkedList<ImageBean> result) {
			if (result.size() > 0) {
				mStaggeredAdapter.addAll((LinkedList<ImageBean>) result);
			} else {
				String url = HostURL.getImageUrl();
				new ImageBeanTask().execute(url+page);
			}
			isLoad = false;
		}
	}

	class ImageBeanTask extends BaseTask<LinkedList<ImageBean>> {
		@Override
		public void obtainDataSuccess(LinkedList<ImageBean> result) {
			if (isRefresh) {
				mStaggeredAdapter.setData((LinkedList<ImageBean>) result);
				Editor edit = mSharedPreferences.edit();
				edit.putLong("time", System.currentTimeMillis());
				edit.commit();
				swipe.setRefreshing(false);
			} else {
				mStaggeredAdapter.addAll((LinkedList<ImageBean>) result);
			}
			isLoad = false;
		}

		@Override
		public void preObtainDataSuccess() {
			mainActivity.showToast("正在加载...");
		}

		@Override
		public void obtainDataError(String error) {
			isLoad = false;
			if (isRefresh) {
				swipe.setRefreshing(false);
			}
			mainActivity.showToast(error);
		}

		@Override
		protected LinkedList<ImageBean> doInBackground(String... params) {
			String url = params[0].toString();
			byte[] source = getResponseSource(url);
			LinkedList<ImageBean> beans = new LinkedList<ImageBean>();
			try {
				String string = new String(source, "utf-8");
				JSONObject obj = new JSONObject(string);
				JSONArray jsonArray = obj.getJSONArray("data");
				int length = jsonArray.length();
				for (int i = 0; i < length; i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					if (jsonObject.length() > 0) {
						ImageBean bean = new ImageBean();
						bean.setAbs(jsonObject.getString("abs"));
						bean.setDesc(jsonObject.getString("desc"));
						bean.setImage_url(jsonObject.getString("image_url"));
						bean.setImage_width(jsonObject.getInt("image_width"));
						bean.setImage_height(jsonObject.getInt("image_height"));
						bean.setThumbnail_url(jsonObject
								.getString("thumbnail_url"));
						bean.setThumbnail_width(jsonObject
								.getInt("thumbnail_width"));
						bean.setThumbnail_height(jsonObject
								.getInt("thumbnail_height"));
						beans.add(bean);
					}
				}
				if (beans.size() > 0) {
					if (table == null) {
						table = "BEAUTIFUL_WOMAN";
					}
					if(!isRefresh){
						int count = mSqliteUtil.mOpenHelper.getTableValueCount(table);
						if (count < 120) {
							mSqliteUtil.mOpenHelper.insert(table, beans);
						}
					}else{
						if(!isDataOverdue){
							mSqliteUtil.mOpenHelper.deleteAll(table);
							mSqliteUtil.mOpenHelper.insert(table, beans);
						}else{
							for (String t:tables) {
								mSqliteUtil.mOpenHelper.deleteAll(t);
							}
							mSqliteUtil.mOpenHelper.insert(table, beans);
						}
					}
				}
			} catch (Exception e) {
			}
			return beans;
		}
	}
	
	class SearchBeanTask extends BaseTask<LinkedList<ImageBean>>{

		@Override
		public void obtainDataSuccess(LinkedList<ImageBean> result) {
			if (isRefresh) {
				mStaggeredAdapter.setData((LinkedList<ImageBean>) result);
				Editor edit = mSharedPreferences.edit();
				edit.putLong("time", System.currentTimeMillis());
				edit.commit();
				swipe.setRefreshing(false);
			} else {
				mStaggeredAdapter.addAll((LinkedList<ImageBean>) result);
			}
			isLoad = false;
		}

		@Override
		public void preObtainDataSuccess() {
			mainActivity.showToast("正在加载...");
		}

		@Override
		public void obtainDataError(String error) {
			isLoad = false;
			if (isRefresh) {
				swipe.setRefreshing(false);
			}
			mainActivity.showToast(error);
		}

		@Override
		protected LinkedList<ImageBean> doInBackground(String... params) {
			byte[] source = getResponseSource(params[0].toString());
			LinkedList<ImageBean> beans = new LinkedList<ImageBean>();
			try {
				String string = new String(source, "utf-8");
				JSONObject obj = new JSONObject(string);
				JSONArray jsonArray = obj.getJSONArray("data");
				int length = jsonArray.length();
				for (int i = 0; i < length; i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					if (jsonObject.length() > 0) {
						ImageBean bean = new ImageBean();
						bean.setAbs(jsonObject.getString("fromPageTitleEnc"));
						bean.setImage_url(jsonObject.getString("objURL"));
						bean.setImage_width(jsonObject.getInt("width"));
						bean.setImage_height(jsonObject.getInt("height"));
						bean.setThumbnail_url(jsonObject.getString("objURL"));
						bean.setThumbnail_width(jsonObject.getInt("width"));
						bean.setThumbnail_height(jsonObject.getInt("height"));
						beans.add(bean);
					}
				}
			} catch (Exception e) {
			}
			return beans;
		}
	}

	@Override
	public void onClick(View arg0) {
		
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_acg, container,false);
	}

	@Override
	public void fillData(Bundle savedInstanceState) {
		mStaggeredAdapter = new StaggeredAdapter(mainActivity);
		listView.setAdapter(mStaggeredAdapter);
		// 顶部刷新的样式
		swipe.setColorSchemeResources(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		if (table == null) {
			table = "BEAUTIFUL_WOMAN";
		}
		mSharedPreferences=mainActivity.getSharedPreferences("LastUpdateTime", Context.MODE_PRIVATE);
		String type="browse";
		if("browse".equals(type)){
			mSqliteUtil = new SqliteUtil(mainActivity, "image", new Class[] {
					ImageBean.class, ImageBean.class, ImageBean.class,
					ImageBean.class, ImageBean.class, ImageBean.class,
					ImageBean.class, ImageBean.class }, tables);
			long timeMillis = System.currentTimeMillis();
			//如果数据库缓存的记录时间大约12h
			//从新从网络获取数据
			if(timeMillis-mSharedPreferences.getLong("time", 0)>(12*60*60*1000)){
				String url = HostURL.getImageUrl();
				isRefresh = true;
				isLoad=true;
				isDataOverdue=true;
				new ImageBeanTask().execute(url+page);
			}else{
				new ImageBeanDBTask().execute(table);
			}
		}else{
			String url = HostURL.getImageUrl();
			isRefresh = true;
			isLoad=true;
			isDataOverdue=true;
			new SearchBeanTask().execute(url+page);
		}
	}
}
