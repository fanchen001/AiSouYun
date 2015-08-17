package com.fanchen.aisou;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import com.fanchen.aisou.adapter.BookPageFactory;
import com.fanchen.aisou.services.GeneralService;
import com.fanchen.aisou.view.BookPageView;
import com.fanchen.aisou.view.LoadingDialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class ReadBookActivity extends Activity implements OnTouchListener {
	private BookPageView mBookPageView;
	private Bitmap mCurPageBitmap, mNextPageBitmap;
	private Canvas mCurPageCanvas, mNextPageCanvas;
	private BookPageFactory pagefactory;
	private DownloadReceiver mDownloadReceiver;
	long fileLenth = 1L;
	private LoadingDialog mLoadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDownloadReceiver = new DownloadReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.fanchen.aisou.download.txt.error");
		intentFilter.addAction("com.fanchen.aisou.download.txt.success");
		mLoadingDialog = LoadingDialog.createDialog(this, "正在加载数据...");
		mLoadingDialog.show();
		registerReceiver(mDownloadReceiver, intentFilter);
		Intent intent = getIntent();
		String textUrl = intent.getStringExtra("txtUrl");
		String name = intent.getStringExtra("name");
		Intent mIntent = new Intent(this,GeneralService.class);
		mIntent .putExtra("txtUrl", textUrl);
		mIntent.putExtra("name", URLEncoder.encode(name)+".txt");
		startService(mIntent);
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		boolean ret = false;
		if (v == mBookPageView) {
			if (e.getAction() == MotionEvent.ACTION_DOWN) {// 屏幕按下
				// 设置动画效果
				mBookPageView.abortAnimation();
				// 修改点击的坐标，从而判断是上一页还是下一页
				mBookPageView.calcCornerXY(e.getX(), e.getY());

				pagefactory.onDraw(mCurPageCanvas);
				if (mBookPageView.DragToRight()) {
					pagefactory.prePage();// 上一页
					// 如果是首页了，就不用更改进度框的信息
					if (pagefactory.isfirstPage()) {
						Toast.makeText(this, "首页", 1).show();
						return false;
					}
					pagefactory.onDraw(mNextPageCanvas);
				} else {
					pagefactory.nextPage();// 下一页
					// 如果是末页了，就不用更改进度框的信息
					if (pagefactory.islastPage()) {
						Toast.makeText(this, "末页", 1).show();
						return false;
					}
					pagefactory.onDraw(mNextPageCanvas);
				}
				// 把上一页和下一页的图片给自定义View
				mBookPageView.setBitmaps(mCurPageBitmap, mNextPageBitmap);
			}
			// 在activity和view中都有onTouchEvent方法
			// 如果在view中覆盖此方法并return
			// false则一次touch事件此方法只会被触发一次且触发完后会接着把这次的触发事件交给activity中的onTouchEvent方法，
			// 如果return true则所有的touch事件都会触发这个View的 onTouchEvent方法
			ret = mBookPageView.doTouchEvent(e);
			return ret;
		}
		return false;
	}

	class DownloadReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					"com.fanchen.aisou.download.txt.success")) {
					mLoadingDialog.dismiss();
				// Android获取屏幕宽度和高度：
				Display display = getWindowManager().getDefaultDisplay();
				int width = display.getWidth();
				int height = display.getHeight();
				// 实例化自定义View
				mBookPageView = new BookPageView(ReadBookActivity.this, width,
						height);
				setContentView(mBookPageView);
				// 创建当前的图片
				mCurPageBitmap = Bitmap.createBitmap(width, height,
						Bitmap.Config.ARGB_8888);
				// 创建下一页的图片
				mNextPageBitmap = Bitmap.createBitmap(width, height,
						Bitmap.Config.ARGB_8888);
				// 转化成画布类
				mCurPageCanvas = new Canvas(mCurPageBitmap);
				mNextPageCanvas = new Canvas(mNextPageBitmap);
				// 实例化工具类
				pagefactory = new BookPageFactory(ReadBookActivity.this, width,
						height);
				try {
					File dir =new File(Environment.getExternalStorageDirectory() + "/android/data/com.fanchen.aisou/cache/");
					File f = new File(dir,getIntent().getStringExtra("path")+".txt");
					pagefactory.openBook(f.getAbsolutePath());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				// 绘制进度百分比
				pagefactory.onDraw(mCurPageCanvas);
				// 设置自定义View的进度条上一页，下一页的图片
				mBookPageView.setBitmaps(mCurPageBitmap, mCurPageBitmap);
				// 设置自定义View的触屏事件
				mBookPageView.setOnTouchListener(ReadBookActivity.this);
			} else if (intent.getAction().equals(
					"com.fanchen.aisou.download.txt.error")) {
				mLoadingDialog.dismiss();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDownloadReceiver != null) {
			unregisterReceiver(mDownloadReceiver);
		}
	}

}
