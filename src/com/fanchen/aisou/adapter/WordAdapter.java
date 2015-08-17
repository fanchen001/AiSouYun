package com.fanchen.aisou.adapter;

import java.util.List;

import com.fanchen.aisou.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
/**
 * 词语联想适配器
 * @author Administrator
 *
 */
public class WordAdapter extends BaseListAdapter<String> {

	public WordAdapter(Context context) {
		super(context);
	}
	
	public WordAdapter(Context context,List<String> list) {
		super(context,list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.auto_search_wrod_item, null);
			
		}
		TextView mTextView = (TextView) get(convertView, R.id.tv_auto_word);
		mTextView.setText(mList.get(position));
		return convertView;
	}
}
