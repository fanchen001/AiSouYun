package com.fanchen.aisou.adapter;

import java.util.List;

import com.fanchen.aisou.R;
import com.fanchen.aisou.WebActivity;
import com.fanchen.aisou.bean.ResShakeBean;
import com.fanchen.aisou.imagefetcher.ImageFetcher;
import com.fanchen.aisou.utils.ConstValue;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.ClipboardManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class ResShareAdapter extends BaseListAdapter<ResShakeBean> {
	private ImageFetcher mImageFetcher;
	private ClipboardManager mClipboardManager;// 剪切版
	private int max;
	private int current;
	private OnLoadMoreDate onLoadMoreDate;

	private boolean open_type;

	public ResShareAdapter(Context context) {
		super(context);
		mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		open_type = context.getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("open_type", true);
		mImageFetcher = new ImageFetcher(context, 240);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
	}

	public ResShareAdapter(Context context, List<ResShakeBean> mList) {
		super(context, mList);
		mClipboardManager = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		open_type = context
				.getSharedPreferences("config", Context.MODE_PRIVATE)
				.getBoolean("open_type", true);
		mImageFetcher = new ImageFetcher(context, 240);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
	}

	public void setOnLoadMoreDate(OnLoadMoreDate onLoadMoreDate) {
		this.onLoadMoreDate = onLoadMoreDate;
	}

	public void setMax(int max) {
		this.max = max;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == this.mList.size()) {
			convertView = mLayoutInflater.inflate(R.layout.load_more_footer,
					null);
			final ProgressBar mProgressBar = (ProgressBar) convertView
					.findViewById(R.id.pull_to_refresh_progress);
			final TextView mTextView = (TextView) convertView
					.findViewById(R.id.tv_load_more);
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (max <= mList.size()) {
						mTextView.setText("没有更多数据了");
						return;
					}
					if (max - current > 5) {
						if (onLoadMoreDate != null) {
							onLoadMoreDate.loadMore(5, current, false);
							current += 5;
						}
					} else {
						if (onLoadMoreDate != null) {
							onLoadMoreDate.loadMore(max - current, current,
									false);
							current += (max - current);
						}
					}
					mProgressBar.setVisibility(View.VISIBLE);
					mTextView.setText("正在加载数据...");
				}
			});
		} else {
			if (convertView == null || convertView instanceof FrameLayout) {
				convertView = mLayoutInflater.inflate(R.layout.res_share_item,
						parent, false);
			}
			ImageView mHeadImageView = get(convertView, R.id.share_user_head);
			ImageView mIcoImageView = get(convertView, R.id.share_ico);
			TextView mUserTextView = get(convertView, R.id.share_user);
			TextView mTimeTextView = get(convertView, R.id.share_time);
			TextView mTitleTextView = get(convertView, R.id.share_title);
			TextView mContextTextView = get(convertView, R.id.share_details);
			TextView mFromTextView = get(convertView, R.id.share_from);
			final TextView mPraiseTextView = get(convertView, R.id.share_praise);
			final TextView mTrampleTextView = get(convertView,R.id.share_trample);
			final ResShakeBean bean = mList.get(position);
			String head = bean.getHead();
			if (head == null || head.equals("")) {
				mHeadImageView.setImageResource(R.drawable.ic_launcher);
			} else {
				mImageFetcher.loadImage(head, mHeadImageView);
			}
			mContextTextView.setText(bean.getContext());
			mFromTextView.setText(bean.getFromDevice());
			mIcoImageView.setImageResource(bean.getMiniType() == null ? R.drawable.unknown : ConstValue.getMineType(bean.getMiniType()));
			mPraiseTextView.setText(bean.getPraise() + "");
			mPraiseTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPraiseTextView.setText(bean.getPraise() + 1 + "");
					bean.setPraise(bean.getPraise() + 1);
					bean.update(context);
				}
			});
			mTimeTextView.setText(bean.getShakeTime());
			mTitleTextView.setText(bean.getTitle());
			mTrampleTextView.setText(bean.getTrample() + "");
			mTrampleTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTrampleTextView.setText(bean.getTrample() + 1 + "");
					bean.setTrample(bean.getTrample() + 1);
					bean.update(context);
				}
			});
			mUserTextView.setText(bean.getShakeUser());
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String url = bean.getUrl();
					if (open_type) {
						if (url == null) {
							Toast.makeText(context, "该条目没有对应的种子文件", 0).show();
							return;
						}
						if (url.indexOf("http") == -1) {
							try {
								Intent mIntent = new Intent();
								mIntent.setAction("android.intent.action.VIEW");
								mIntent.setData(Uri.parse(bean.getUrl()));
								context.startActivity(mIntent);
							} catch (Exception e) {
								Toast.makeText(context,
										"抱歉，你的手机上没有安装支持磁链的应用，建议你去下载迅雷android版",
										1).show();
								e.printStackTrace();
							}
						} else {
							Intent mIntent = new Intent();
							mIntent.setAction("android.intent.action.VIEW");
							Uri data = Uri.parse(bean.getUrl());
							mIntent.setData(data);
							context.startActivity(mIntent);
						}
					} else {
						if (url.indexOf("http") == -1) {
							mClipboardManager.setText(bean.getUrl());
							Toast.makeText(context, "已将磁力链接复制到剪切板", 0).show();
						} else {
							Intent mIntent = new Intent(context,
									WebActivity.class);
							mIntent.putExtra("url", bean.getUrl());
							context.startActivity(mIntent);
						}
					}
				}
			});
		}
		return convertView;
	}

	@Override
	public int getCount() {
		return mList == null || mList.size() == 0 ? 0 : mList.size() + 1;
	}

	public interface OnLoadMoreDate {
		void loadMore(int i, int j, boolean b);
	}

}
