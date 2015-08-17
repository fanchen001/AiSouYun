package com.fanchen.aisou.jni;

public class BmobValue {

	public static native String getAccessKey();
	
	public static native String getApplicationID();
	
	public static native String getRESTAPIKey();
	
	public static native String getMasterKey();
	
	public static native String getSecretKey();
	
	static{
		try {
			System.loadLibrary("value");
		} catch (Exception e) {
		}
	}
}
