package com.fanchen.aisou.callback;

import java.util.Map;

/**
 * 版本更新回调的接口
 * @author Administrator
 *
 */
public interface OnUpdateListener {
	/**
	 * 发现新版本
	 * @param info 版本信息
	 */
	void onNewVersion(Map<String, String > info);
	/**
	 * 没有新版本
	 */
	void onNotNewVersion();
	/**
	 * 发生错误
	 * @param code 错误信息
	 */
	void onError(String code);
	
}
