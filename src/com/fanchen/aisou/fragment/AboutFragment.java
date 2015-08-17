package com.fanchen.aisou.fragment;

import com.fanchen.aisou.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
/**
 * 关于
 * @author Administrator
 *
 */
public class AboutFragment extends BaseFragment {
	private static final String ABOUTPAGE = "about.html";//html文件
	private static final String ABOUTPAGE_URL = "file:///android_asset/";//路径
	private WebView mWebView;//用来显示的webview
	
	@Override
	public void onClick(View arg0) {
		
	}
	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_about, null);
	}
	@Override
	public void findView(View view) {
		mWebView = (WebView) view.findViewById(R.id.webview_about);
	}
	@Override
	public void setLinsener() {
		
	}
	@Override
	public void fillData(Bundle savedInstanceState) {
		//加载html
		mWebView.loadUrl(ABOUTPAGE_URL + ABOUTPAGE);
	}

}
