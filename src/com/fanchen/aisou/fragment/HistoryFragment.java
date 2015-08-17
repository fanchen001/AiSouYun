package com.fanchen.aisou.fragment;

import java.util.ArrayList;
import java.util.List;

import com.fanchen.aisou.R;
import com.fanchen.aisou.adapter.StatusExpandAdapter;
import com.fanchen.aisou.bean.HistoryBean;
import com.fanchen.aisou.db.HistoryDAO;
import com.fanchen.aisou.entity.ChildStatusEntity;
import com.fanchen.aisou.entity.GroupStatusEntity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
/**
 * 搜索记录
 * @author Administrator
 *
 */
public class HistoryFragment extends BaseFragment {
	private ExpandableListView expandlistView;
	private StatusExpandAdapter statusAdapter;
	
	private List<HistoryBean> mHistoryBean;
	private TextView mClearTextView;
	private HistoryDAO dao;
	
	private List<GroupStatusEntity> getListData() {
		List<GroupStatusEntity> groupList;
		dao = new HistoryDAO(getActivity());
		//拿到所有的搜索记录
		mHistoryBean=dao.getAllHistory();
		groupList = new ArrayList<GroupStatusEntity>();
		for(int i=0;i<mHistoryBean.size();i++){
			//一级。代表日期
			GroupStatusEntity groupStatusEntity = new GroupStatusEntity();
			groupStatusEntity.setGroupName(mHistoryBean.get(i).getDay());
			List<ChildStatusEntity> childList = new ArrayList<ChildStatusEntity>();
			for(int j=0;j<mHistoryBean.get(i).getmChar().size();j++){
				//二级，代表搜索词
				ChildStatusEntity childStatusEntity = new ChildStatusEntity();
				//添加到视图上面去
				childStatusEntity.setCompleteTime(mHistoryBean.get(i).getmChar().get(j));
				childStatusEntity.setIsfinished(true);
				childList.add(childStatusEntity);
			}
			groupStatusEntity.setChildList(childList);
			groupList.add(groupStatusEntity);
		}
		return groupList;
	}


	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tv_history_clear:
			dao.deleteAll();
			fillData(null);
			break;

		default:
			break;
		}
	}


	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_history, null);
	}


	@Override
	public void findView(View view) {
		expandlistView=(ExpandableListView) view.findViewById(R.id.expandlist);
		mClearTextView=(TextView) view.findViewById(R.id.tv_history_clear);
	}


	@Override
	public void setLinsener() {
		mClearTextView.setOnClickListener(this);
	}


	@Override
	public void fillData(Bundle savedInstanceState) {
		statusAdapter = new StatusExpandAdapter(getActivity(), getListData());
		expandlistView.setAdapter(statusAdapter);
		expandlistView.setGroupIndicator(null); // 去掉默认带的箭头
		expandlistView.setSelection(0);// 设置默认选中项
		// 遍历所有group,将所有项设置成默认展开
		int groupCount = expandlistView.getCount();
		for (int i = 0; i < groupCount; i++) {
			expandlistView.expandGroup(i);
		}
	}

}
