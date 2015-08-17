package com.fanchen.aisou.callback;

import org.json.JSONArray;
/**
 * 词语联想服务回调的接口
 * @author Administrator
 *
 */
public interface OnDataResultListener {
	/**
	 * 如果服务拿到数据。回调该方法，填充数据
	 * @param data
	 */
	void onResult(JSONArray data);
}
