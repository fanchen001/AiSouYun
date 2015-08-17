package com.fanchen.aisou.utils;



import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 网络状态相关工具类
 * @author fanchen
 *
 */
public class NetworkStateUtil {
	/**
	 * 检查当前网络状态是否可用
	 * @param context 上下文
	 * @return 是否有网络连接
	 */
	public static boolean isNetWorkAvailable(Context context){
		ConnectivityManager cm=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
		return activeNetworkInfo!=null&&activeNetworkInfo.isConnected();
	}
	
	/**
	 * @author  获取当前的网络状态 -1：没有网络
	 *         1：WIFI网络2：wap网络3：net网络
	 * @param context
	 * @return
	 */
	public static int getAPNType(Context context) {
		int netType = -1;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			Log.e("networkInfo.getExtraInfo()",
					"networkInfo.getExtraInfo() is "
							+ networkInfo.getExtraInfo());
			if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
				netType = 3;
			} else {
				netType = 2;
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = 1;
		}
		return netType;
	}
	
	
	/**
	 * //  判断WIFI网络是否可用   
	 * @param mContext 上下文
	 * @return 是否是WiFi连接
	 */
	public static boolean isWifiConnected(Context mContext) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
        return mWiFiNetworkInfo.isConnected();  
    }  
  
   
	/**
	 *  //    判断MOBILE网络是否可用   
	 * @param context 上下文
	 * @return 是否是2/3G网络
	 */
	public static boolean isMobileConnected(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return mMobileNetworkInfo.isConnected();
	} 
	
	/**
	 * 打开手机网络设置界面
	 * @param mContext 上下文
	 */
	public static void openNetworkSetting(Context mContext) {
		Intent intent=null;
        //判断手机系统的版本  即API大于10 就是3.0或以上版本 
        if(android.os.Build.VERSION.SDK_INT>10){
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        }else{
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        mContext.startActivity(intent);
	}
	
}
