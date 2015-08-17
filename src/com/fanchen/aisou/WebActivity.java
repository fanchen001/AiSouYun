package com.fanchen.aisou;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebActivity extends BaseActivity {
	private WebView webview;
	private ProgressBar load_pro;

	private int count = 0;
//	/**
//	 * Web视图
//	 * 
//	 * @author Administrator
//	 * 
//	 */
//	private class HelloWebViewClient extends WebViewClient {
//		@Override
//		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			count ++;
//			view.loadUrl(url);
//			return true;
//		}
//	}

	/**
	 * 点击监听
	 * 
	 * @param v
	 */
	public void MyOnclick(View v) {
		switch (v.getId()) {
		case R.id.iv_web_prev:
			if(count == 0){
				finish();
				return;
			}
			count --;
			webview.goBack(); // goBack()表示返回WebView的上一页面
			break;
		case R.id.iv_web_next:
			count ++;
			webview.goForward(); // goForward()表示返回WebView的下一页面
			break;
		case R.id.iv_web_refresh:
			webview.reload();
			break;
		}
	}

	@Override
	public int getResId() {
		return R.layout.activity_web;
	}

	@Override
	public void findView() {
		load_pro = (ProgressBar) findViewById(R.id.web_load_pro);

		webview = (WebView) findViewById(R.id.wb_web_webview);
	}

	@Override
	public void setListener() {

	}

	@Override
	public void initData(Bundle b) {
		// 设置WebView属性，能够执行Javascript脚本
		webview.getSettings().setJavaScriptEnabled(true);

		// 设置Web视图
//		webview.setWebViewClient(new HelloWebViewClient());

		// 如果页面中链接，如果希望点击链接继续在当前browser中响应，
		// 而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象。
		webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				count ++;
				view.loadUrl(url);
				return true;
			}
		});
		load_pro.setVisibility(View.VISIBLE);
		// 显示加载进度条
		webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					load_pro.setVisibility(View.GONE);
				} else {
					if (load_pro.getVisibility() == View.GONE)
						load_pro.setVisibility(View.VISIBLE);
					load_pro.setProgress(newProgress);
				}
			}
		});

		webview.setVisibility(View.VISIBLE);
		String url = getIntent().getStringExtra("url");
		if (url != null) {
			webview.loadUrl(url);
		}
	}
	
	
	@Override
	public void onBackPressed() {
		if(count == 0){
			finish();
			super.onBackPressed();
			return;
		}
		count -- ;
		webview.goBack(); // goBack()表示返回WebView的上一页面
		
		
	}

}
