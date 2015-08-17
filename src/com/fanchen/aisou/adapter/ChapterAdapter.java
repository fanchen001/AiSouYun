package com.fanchen.aisou.adapter;

import java.util.List;

import com.fanchen.aisou.R;
import com.fanchen.aisou.bean.ChapterBean;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChapterAdapter extends BaseListAdapter<ChapterBean> {

	public ChapterAdapter(Context context) {
		super(context);
	}
	public ChapterAdapter(Context context, List<ChapterBean> mList) {
		super(context, mList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView =mLayoutInflater.inflate(R.layout.item_chapter, parent,false);
		}
		ChapterBean bean = mList.get(position);
		TextView mTextView = get(convertView, R.id.tv_chapter_title);
		mTextView.setText(bean.getTitle());
		return convertView;
	}

}
