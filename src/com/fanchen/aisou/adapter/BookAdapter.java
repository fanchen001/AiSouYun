package com.fanchen.aisou.adapter;

import java.util.List;

import com.fanchen.aisou.R;
import com.fanchen.aisou.bean.LightNovelBean;
import com.fanchen.aisou.view.RoundImageView;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BookAdapter extends BaseListAdapter<LightNovelBean> {

	public BookAdapter(Context context) {
		super(context);
	}

	public BookAdapter(Context context, List<LightNovelBean> mList) {
		super(context, mList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_category,
					parent, false);
		}
		RoundImageView mImageView = get(convertView, R.id.riv_cover_image);
		mImageView.setImageBitmap(BitmapFactory.decodeResource(
				context.getResources(),
				Integer.valueOf(mList.get(position).getImage())));
		TextView mTextView = get(convertView, R.id.tv_category_text);
		mTextView.setText(mList.get(position).getTitle());
		return convertView;
	}
}
