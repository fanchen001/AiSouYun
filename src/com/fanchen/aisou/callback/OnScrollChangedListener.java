package com.fanchen.aisou.callback;
/**
 * 滚动改变监听器
 * @author Administrator
 *
 */
public abstract interface OnScrollChangedListener {
	//滚动改变回调此方法
	public abstract void onScrollChanged(int top, int oldTop);
}
