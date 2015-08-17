package com.fanchen.aisou.callback;
/**
 * searchfragment页面，上方影藏的布局状态改变监听器
 * @author Administrator
 *
 */
public interface OnTopStateListener {
	/**
	 * 关闭上方隐藏的布局
	 */
	void onClose();
	/**
	 * 打开上方隐藏的布局
	 */
	void onOpen();
	/**
	 * 关闭上方的listview
	 */
	void onCloseListView();

}
