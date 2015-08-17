package com.fanchen.aisou.adapter;

import java.util.List;

import com.fanchen.aisou.R;
import com.fanchen.aisou.bean.LightNovelBean;
import com.fanchen.aisou.imagefetcher.ImageFetcher;
import com.fanchen.aisou.view.ScaleImageView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RecommendBookAdapter extends BaseListAdapter<LightNovelBean> {

	private ImageFetcher mImageFetcher;
	public RecommendBookAdapter(Context context) {
		super(context);
		mImageFetcher = new ImageFetcher(context, 240);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo1);
	}
	public RecommendBookAdapter(Context context, List<LightNovelBean> mList) {
		super(context, mList);
		mImageFetcher = new ImageFetcher(context, 240);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo1);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final LightNovelBean bean = mList.get(position);
		if(convertView == null){
			convertView = mLayoutInflater.inflate(R.layout.item_recommend_book, parent,false);
		}
		ScaleImageView mImageView = get(convertView, R.id.iv_book_recom_pic);
		TextView mTextView = get(convertView, R.id.tv_book_recom_title);
		mImageView.setImageWidth(80);
		mImageView.setImageHeight(120);
		mImageFetcher.loadImage(bean.getImage(), mImageView);
		mTextView.setText(bean.getTitle());
		return convertView;
	}

}
