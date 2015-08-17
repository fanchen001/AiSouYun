package com.fanchen.aisou.adapter;

import java.util.List;

import com.fanchen.aisou.MainActivity;
import com.fanchen.aisou.R;
import com.fanchen.aisou.bean.LightNovelBean;
import com.fanchen.aisou.fragment.BookInfoFragment;
import com.fanchen.aisou.imagefetcher.ImageFetcher;
import com.fanchen.aisou.view.ScaleImageView;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BookListAdapter extends BaseListAdapter<LightNovelBean>{
	private ImageFetcher mImageFetcher;
	private OnLoadMoreData onLoadMoreData;
	private int layout = 0;
	
	public BookListAdapter(Context context){
		super(context);
		mImageFetcher = new ImageFetcher(context, 240);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo1);
	}
	public BookListAdapter(Context context,int layout){
		super(context);
		this.layout = layout;
		mImageFetcher = new ImageFetcher(context, 240);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo1);
	}
	public BookListAdapter(Context context, List<LightNovelBean> mList) {
		super(context, mList);
		mImageFetcher = new ImageFetcher(context, 240);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo1);
	}

	public void setOnLoadMoreData(OnLoadMoreData onLoadMoreData) {
		this.onLoadMoreData = onLoadMoreData;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(layout != 0){
			final LightNovelBean bean = mList.get(position);
			if(convertView == null){
				convertView = mLayoutInflater.inflate(layout, parent,false);
			}
			ScaleImageView mImageView = get(convertView, R.id.iv_book_recom_pic);
			TextView mTextView = get(convertView, R.id.tv_book_recom_title);
			mImageView.setImageWidth(80);
			mImageView.setImageHeight(120);
			mImageFetcher.loadImage(bean.getImage(), mImageView);
			mTextView.setText(bean.getTitle());
		}else{
			if(position==this.mList.size()){
				convertView = mLayoutInflater.inflate(R.layout.load_more_footer, null);
				final ProgressBar mProgressBar=(ProgressBar) convertView.findViewById(R.id.pull_to_refresh_progress);
				final TextView mTextView=(TextView) convertView.findViewById(R.id.tv_load_more);
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if(onLoadMoreData != null && mList.size() % 20 == 0){
							onLoadMoreData.loadMore(arg0);
							mProgressBar.setVisibility(View.VISIBLE);
							mTextView.setText("正在加载数据...");
						}else{
							mTextView.setText("没有更多数据");
						}
					}
				});
			}else{
				final LightNovelBean noveBean = mList.get(position);
				if (convertView == null || convertView instanceof FrameLayout) {
					convertView = mLayoutInflater.inflate(R.layout.item_book, parent,false);
				}
				ImageView imageView = get(convertView, R.id.iv_cover_image);
				TextView mStateTextView = get(convertView, R.id.tv_state_text);
				TextView mTitleTextView = get(convertView, R.id.tv_title_text);
				TextView mAuthorTextView = get(convertView, R.id.tv_author_text);
				TextView mTimeTextView = get(convertView, R.id.tv_update_text);
				mStateTextView.setText(noveBean.getState());
				mTitleTextView.setText(noveBean.getTitle());
				mAuthorTextView.setText(noveBean.getAuthor());
				mTimeTextView.setText(noveBean.getUpdateTime());
				mImageFetcher.loadImage(noveBean.getImage(), imageView);
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						((MainActivity) context).changeFragment(new BookInfoFragment(), false, "book", "url", noveBean.getUrl());
					}
				});
			}
		}
		return convertView;
	}
	
	@Override
	public int getCount() {
		if(layout != 0){
			return super.getCount();
		}else{
			return mList==null||mList.size()==0 ? 0 : mList.size()+1;
		}
	}
	
	public interface OnLoadMoreData{
		void loadMore(View v);
	}
}
