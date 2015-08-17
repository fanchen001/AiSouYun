package com.fanchen.aisou.adapter;

import java.util.List;

import com.fanchen.aisou.ImageActivity;
import com.fanchen.aisou.MainActivity;
import com.fanchen.aisou.R;
import com.fanchen.aisou.bean.ImageBean;
import com.fanchen.aisou.imagefetcher.ImageFetcher;
import com.fanchen.aisou.view.ScaleImageView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class StaggeredAdapter extends BaseListAdapter<ImageBean>  implements OnClickListener {
	private ImageFetcher mImageFetcher;
	public StaggeredAdapter(Context context, List<ImageBean> mList) {
		super(context, mList);
		mImageFetcher = new ImageFetcher(context, 240);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
	}
	public StaggeredAdapter(Context context) {
		super(context);
		mImageFetcher = new ImageFetcher(context, 240);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ImageBean imageBean = mList.get(position);
		if(convertView == null){
			convertView = mLayoutInflater.inflate(R.layout.infos_list, parent,false);
		}
		
		ScaleImageView imageView = get(convertView, R.id.news_pic);
		TextView contentView = get(convertView, R.id.news_title);
		imageView.setImageWidth(imageBean.getThumbnail_width());
		imageView.setImageHeight(imageBean.getThumbnail_height());
		contentView.setText("" + imageBean.getAbs());
		mImageFetcher.loadImage(imageBean.getThumbnail_url(), imageView);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("image", imageBean.getImage_url());
				((MainActivity) context).startActivity(ImageActivity.class,bundle);
			}
		});
		return convertView;
	}

	@Override
	public void onClick(View v) {
		
	}
}
