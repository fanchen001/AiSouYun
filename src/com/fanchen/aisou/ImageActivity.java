package com.fanchen.aisou;


import java.io.File;
import java.net.URLEncoder;

import com.fanchen.aisou.utils.FileUtil;
import com.fanchen.aisou.view.gesture.GestureImageView;
import com.statusbar.systembartint.SystemBarTintManager;

import android.annotation.TargetApi;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 *  显示图片的界面
 * 
 * 
 */
public class ImageActivity extends BaseActivity implements OnClickListener{

	private GestureImageView imageView; // 图片组件
	private ProgressBar progressBar; // 进度条
	private ImageView backBtn; // 回退按钮
	private ImageView downLoadBtn; // 下载按钮
	private TextView mTextView;
	private String Image_url;
	private Bitmap bitmap=null;
//	private ImageFetcher mImageFetcher;
	private SharedPreferences mSharedPreferences;
	private String dir;
	private String cacheImagePath;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.setting_wallpaper:
			showToast("正在设置壁纸");
			imageView.setDrawingCacheEnabled(true);
			bitmap = imageView.getDrawingCache();
			if (bitmap != null) {
				new Thread(){
					public void run() {
						WallpaperManager wallpaperManager = WallpaperManager.getInstance(ImageActivity.this);
						try {
							wallpaperManager.setBitmap(bitmap);
							showToast("设置壁纸成功");
						} catch (Exception e) {
						}finally{
							imageView.setDrawingCacheEnabled(false);
						}
					}
				}.start();
			}else{
				imageView.setDrawingCacheEnabled(false);
			}
			break;
			
		case R.id.download:
			
			if(!TextUtils.isEmpty(cacheImagePath)){
				new Thread(){
					public void run() {
						showToast("正在保存...");
						Bitmap decodeFile = BitmapFactory.decodeFile(cacheImagePath);
						if (FileUtil.writeSDCard(Image_url.substring(Image_url.lastIndexOf("/")),dir,decodeFile)) {
							showToast("保存成功:"+dir);
						} else {
							showToast("保存失败");
						}
					};
				}.start();
			}
			
//			mImageFetcher.loadImage(num, imageView)
			
//			mAsyncImageLoader.loadDrawable(Image_url, new OnImageCallback() {
//				
//				@Override
//				public void imageLoaded(Drawable image, String imageUrl) {
//					final Bitmap bitmap=ImageUtil.drawableToBitmap(image);
//					if(bitmap!=null){
//						new Thread(){
//							public void run() {
//								showToast("正在保存...");
//								if (FileUtil.writeSDCard(Image_url,dir,bitmap)) {
//									showToast("保存成功:"+dir);
//								} else {
//									showToast("保存失败");
//								}
//							};
//						}.start();
//					}else {
//						showToast("保存失败");
//					}
//				}
//			});
//			mAsyncImageLoader.loadDrawable(Image_url, new ImageCallback() {
//				@Override
//				public void imageLoaded(Drawable image, String imageUrl) {
//					final Bitmap bitmap=ImageUtil.drawableToBitmap(image);
//					if(bitmap!=null){
//						new Thread(){
//							public void run() {
//								showToast("正在保存...");
//								if (FileUtil.writeSDCard(Image_url,dir,bitmap)) {
//									showToast("保存成功:"+dir);
//								} else {
//									showToast("保存失败");
//								}
//							};
//						}.start();
//					}else {
//						showToast("保存失败");
//					}
//				}
//			});
			break;

		default:
			break;
		}
	}

	@Override
	public int getResId() {
		return R.layout.activity_image_page;
	}

	@Override
	public void findView() {
		imageView = (GestureImageView) findViewById(R.id.image);
		progressBar = (ProgressBar) findViewById(R.id.loading);
		downLoadBtn = (ImageView) findViewById(R.id.download);
		backBtn = (ImageView) findViewById(R.id.back);
		mTextView=(TextView) findViewById(R.id.setting_wallpaper);
	}

	@Override
	public void setListener() {
		mTextView.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		downLoadBtn.setOnClickListener(this);
	}

	@Override
	public void initData(Bundle b) {
		applyKitKatTranslucency();
		mSharedPreferences=getSharedPreferences("setting", Context.MODE_PRIVATE);
		dir=mSharedPreferences.getString("dir",Environment.getExternalStorageDirectory()+ "/aisou/image");
		Image_url = getIntent().getStringExtra("image");
		new ImageDownloadTask().execute(Image_url,"/android/data/com.fanchen.aisou/bigImage");
	}
	
	protected void applyKitKatTranslucency() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
			SystemBarTintManager mTintManager = new SystemBarTintManager(this);
			mTintManager.setStatusBarTintEnabled(true);
			mTintManager.setNavigationBarTintEnabled(true);
		}
	}
	
	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}
	
	@SuppressWarnings("deprecation")
	class ImageDownloadTask extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... params) {
			File file = new File(new File(Environment.getExternalStorageDirectory()+params[1]),URLEncoder.encode(params[0]));
			if(file.exists()){
				return file.getAbsolutePath();
			}
			return FileUtil.downloadFile(params[0], params[1], URLEncoder.encode(params[0]));
		}
		
		@Override
		protected void onPostExecute(String result) {
			cacheImagePath = result;
			progressBar.setVisibility(View.GONE);
			imageView.setImageBitmap(BitmapFactory.decodeFile(result));
			super.onPostExecute(result);
		}
		
	}

}
