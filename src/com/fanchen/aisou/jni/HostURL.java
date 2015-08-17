package com.fanchen.aisou.jni;

public class HostURL {
	
	public static native String getBookUrl(int id);
	public static native String getSearchUrl(int id);
	public static native String getImageUrl();
	public static native String getHotWordUrl();
	public static native String getShareUrl();
	
	static{
		try {
			System.loadLibrary("host");
		} catch (Exception e) {
		}
	}

}
