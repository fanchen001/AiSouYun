package com.fanchen.aisou.adapter;

import java.util.List;

import com.fanchen.aisou.R;
import com.fanchen.aisou.bean.PreviewBean;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PreviewAdapter extends BaseListAdapter<PreviewBean> {

	public PreviewAdapter(Context context, List<PreviewBean> mList) {
		super(context, mList);
	}
	public PreviewAdapter(Context context) {
		super(context);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PreviewBean bean = mList.get(position);
		if(convertView == null){
			convertView=mLayoutInflater.inflate(R.layout.file_preview_item, parent,false);
		}
		TextView mFileNameTextView = get(convertView, R.id.tv_preview_name);
		TextView mSizeTextView = get(convertView, R.id.tv_preview_size);
		mFileNameTextView.setText(bean.getFileName());
		mSizeTextView.setText(bean.getSize());
		return convertView;
	}

}
