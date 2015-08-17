package com.fanchen.aisou;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map; 

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.fanchen.aisou.bean.UserBean;
import com.fanchen.aisou.fragment.AboutFragment;
import com.fanchen.aisou.fragment.AcgFragment;
import com.fanchen.aisou.fragment.BookFragment;
import com.fanchen.aisou.fragment.BookInfoFragment;
import com.fanchen.aisou.fragment.BookListFragment;
import com.fanchen.aisou.fragment.CollectFragment;
import com.fanchen.aisou.fragment.HistoryFragment;
import com.fanchen.aisou.fragment.HomeFragment;
import com.fanchen.aisou.fragment.LoginFragment;
import com.fanchen.aisou.fragment.OpinionFragment;
import com.fanchen.aisou.fragment.ResShareFragment;
import com.fanchen.aisou.fragment.SearchFragment;
import com.fanchen.aisou.fragment.SettingFragment;
import com.fanchen.aisou.fragment.ShakeFragment;
import com.fanchen.aisou.fragment.UserInfoFragment;
import com.fanchen.aisou.jni.HostURL;
import com.fanchen.aisou.utils.DisplayUtil;
import com.fanchen.aisou.utils.NetworkStateUtil;
import com.fanchen.aisou.view.ActionBarDrawerToggle;
import com.fanchen.aisou.view.DrawerArrowDrawable;
import com.fanchen.aisou.view.MaterialDialog;
import com.fanchen.aisou.view.RoundImageView;
import com.fanchen.aisou.view.UISwitchButton;
import com.fanchen.aisou.view.toast.Crouton;
import com.fanchen.aisou.view.toast.Style;
import com.fanchen.aisou.view.toast.TipsToast;
import com.statusbar.systembartint.SystemBarTintManager;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 程序主界面。
 * 所有的用户操作基本上都是在这个界面里进行的
 * @author Administraotor
 *
 */
public class MainActivity extends BaseActivity{
	private static TipsToast mTipsToast;//网络状态的弹窗
	private ImageView mGprsImageView, mWifiImageView;//网络状态的弹窗上的图片
	private Button but_close;//网络状态改变的时候，弹窗上的关闭按钮
	private static AlertDialog mAlertDialog;//对话框
	private static MaterialDialog materialDialog;
	@SuppressWarnings("unused")
	private ConnectivityManager mConnectivityManager;//数据流量有关的Manager
	private UISwitchButton switchWifi, switchGprs;//网络状态的弹窗上的按钮
	private DrawerLayout mDrawerLayout;//实现侧滑的布局
	private ListView mDrawerList;//左侧布局的list
	private RelativeLayout rl;//anctivity中用来替换成fragment的那块布局
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerArrowDrawable drawerArrow;
	public static FragmentManager fm;//fragment的管理器
	private Boolean openOrClose = false;//用来标识左侧侧滑是否打开的的变量
	private RoundImageView mIcoRoundImageView;//用户登陆的按钮
	private NetworkBroadcastReceiver mNetworkBroadcastReceiver;//网络状态的广播接收者
	private SharedPreferences configSP;//应用程序对应的配置文件
	private Map<String, Fragment> mFragmentMap;//用来存储已经创建了的fragment的map，这样可以避免每次都重新创建新的fragment
	private long lastTime;//用来标记退出的时候的双击退出的变量（两次点击需要小于一个值，才可以退出程序）
	private String[] mTitles = new String[] { "网盘搜索","轻之文库","动漫美图", "搜索记录","资源摇摇", "分享软件", "意见反馈" ,"下载管理","资源墙","设置","关于"};
	
	/**
	 * 这个方法主要是开放给其他界面获取当前已经创建的fragment的map
	 * @return
	 */
	public Map<String, Fragment> getmFragmentMap() {
		return mFragmentMap;
	}

	/**
	 * 设置状态栏透明
	 */
	private void applyKitKatTranslucency() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
			LayoutParams params =  mDrawerLayout.getLayoutParams();
			//因为设置状态栏透明后，程序上移，这里强制向下移动70dp
			if(params instanceof FrameLayout.LayoutParams){
				FrameLayout.LayoutParams  p=(android.widget.FrameLayout.LayoutParams) params;
				int dp2px = DisplayUtil.dp2px(getApplicationContext(), 70);
				p.topMargin=dp2px;
				mDrawerLayout.setLayoutParams(p);
			}else if(params instanceof LinearLayout.LayoutParams){
				LinearLayout.LayoutParams  p=(android.widget.LinearLayout.LayoutParams) params;
				int dp2px = DisplayUtil.dp2px(getApplicationContext(), 70);
				p.topMargin=dp2px;
				mDrawerLayout.setLayoutParams(p);
			} else if(params instanceof RelativeLayout.LayoutParams){
				RelativeLayout.LayoutParams  p=(android.widget.RelativeLayout.LayoutParams) params;
				int dp2px = DisplayUtil.dp2px(getApplicationContext(), 70);
				p.topMargin=dp2px;
				mDrawerLayout.setLayoutParams(p);
			}
		}

		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		//设置状态栏背景色
		tintManager.setStatusBarTintResource(R.color.actionbar_bg);
	}
	
	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}
	
	
	//自定义土司
    public void showCustomToast(String pMsg, int view_position) {
    	Crouton.makeText(this, pMsg, Style.CONFIRM, view_position).show();
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//点击topbar上面的标题时候调用这个方法
		//这里点击的时候用来打开和关闭侧滑菜单栏
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(rl)) {
				mDrawerLayout.closeDrawer(rl);
				openOrClose = false;
			} else {
				mDrawerLayout.openDrawer(rl);
				openOrClose = true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * 初始化fragment
	 */
	private void init() {
		fm = getSupportFragmentManager();
		Fragment homeFragment = mFragmentMap.get("home");
		if(homeFragment==null){
			homeFragment=new HomeFragment();
			mFragmentMap.put("home", homeFragment);
			changeFragment(homeFragment, false, "home", "url", "url");
		}
	}

	/**
	 * 更改fragment
	 * @param f fragment
	 * @param name 名字
	 */
	public void changeFragment(Fragment f,String name) {
		changeFragment(f, false,name);
	}

	/**
	 * 初始化fragemnt
	 * @param f 
	 */
	public void initFragment(Fragment f) {
		changeFragment(f, true,null);
	}
	
	/**
	 * 更改fragment
	 * @param f fragment
	 * @param init 是否添加返回站
	 * @param name 名字
	 */
	public void changeFragment(Fragment f, boolean init,String name) {
		FragmentTransaction ft = fm.beginTransaction().setCustomAnimations(
				R.anim.umeng_fb_slide_in_from_left,
				R.anim.umeng_fb_slide_out_from_left,
				R.anim.umeng_fb_slide_in_from_right,
				R.anim.umeng_fb_slide_out_from_right);
		;
		ft.replace(R.id.fragment_layout, f);
		if(mAisouApplication.getTopFragment() instanceof SearchFragment){
			mAisouApplication.popuFragment();
		}else{
			ft.addToBackStack(name);
		}
		mAisouApplication.addFragment(f);
		ft.commit();
	}
	
	
	
	/**
	 * 切换fragment
	 * @param f 需要切换的fragment
	 * @param init 是否添加到返回栈
	 * @param name 标示
	 * @param word 搜索词语
	 */
	public void changeFragment(Fragment f, boolean init,String name,String key,String word) {
		if(MainActivity.fm==null)
			return;
		//切换动画
		FragmentTransaction ft = MainActivity.fm.beginTransaction().setCustomAnimations(
				R.anim.umeng_fb_slide_in_from_left,
				R.anim.umeng_fb_slide_out_from_left,
				R.anim.umeng_fb_slide_in_from_right,
				R.anim.umeng_fb_slide_out_from_right);
		Bundle bundle = new Bundle();
		//添加搜索的词语
		bundle.putString(key, word);
		f.setArguments(bundle);
		//替换布局为fragment
		ft.replace(R.id.fragment_layout, f);
		//将当前fragment添加到Application列表里面
		mAisouApplication.addFragment(f);
		if (!init)
			ft.addToBackStack(name);
		ft.commit();
	}
	
	
	@Override
	public void onBackPressed() {
		//返回键的处理
		if (openOrClose == false) {
			//弹出最顶层的fragment
			Fragment popuFragment = mAisouApplication.popuFragment();
			//如果当前最顶层的是home
			//显示退出的土司
			//再按一次退出程序
				if(popuFragment != null&&popuFragment instanceof HomeFragment){
					showCustomToast(getString(R.string.back_exit_tips),R.id.fragment_layout);
					lastTime = System.currentTimeMillis();
					//清空fragment队列
					mAisouApplication.clearFragment();
				}else if(popuFragment==null){
					long time = System.currentTimeMillis();
					//如果两次间隔小于1.5s，退出应用
					if(time - lastTime<1500){
						finish();
					}else{
						Fragment fragment = mFragmentMap.get("home");
						mAisouApplication.addFragment(fragment);
					}
				}else{
					super.onBackPressed();
					//拿到顶层的fragment
					Fragment topFragment = mAisouApplication.getTopFragment();
					//设置标题
					if(topFragment instanceof HomeFragment){
						setTitle("爱搜云");
					}else if(topFragment instanceof ShakeFragment){
						setTitle("资源摇摇");
					}else if(topFragment instanceof SettingFragment){
						setTitle("设置");
					}else if(topFragment instanceof AboutFragment){
						setTitle("关于");
					}else if(topFragment instanceof HistoryFragment){
						setTitle("搜索记录");
					}else if(topFragment instanceof OpinionFragment){
						setTitle("意见反馈");
					}else if(topFragment instanceof ResShareFragment){
						setTitle("资源墙");
					}else if(topFragment instanceof AcgFragment){
						setTitle("动漫美图");
					}else if(topFragment instanceof UserInfoFragment){
						setTitle("个人信息");
					}else if(topFragment instanceof CollectFragment){
						setTitle("书籍收藏");
					}else if(topFragment instanceof BookFragment || topFragment instanceof BookListFragment || topFragment instanceof BookInfoFragment){
						setTitle("轻之文库");
					}
				}
		} else {
			mDrawerLayout.closeDrawers();
		}

	}
	
	private void showShare() {
		 ShareSDK.initSDK(this);
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
		 oks.disableSSOWhenAuthorize(); 
		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		 oks.setTitle(getString(R.string.share));
		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		 oks.setTitleUrl(HostURL.getShareUrl());
		 // text是分享文本，所有平台都需要这个字段
		 oks.setText("想要资源？快用爱搜云  --  (o゜▽゜)o☆");
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 oks.setImagePath("/data/data/com.fanchen.aisou/files/logo.png");//确保SDcard下面存在此张图片
		 // url仅在微信（包括好友和朋友圈）中使用
		 oks.setUrl(HostURL.getShareUrl());
		 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
		 oks.setComment("好用的资源搜索app  ----  (o゜▽゜)o☆[BINGO!]");
		 // site是分享此内容的网站名称，仅在QQ空间使用
		 oks.setSite(getString(R.string.app_name));
		 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
		 oks.setSiteUrl(HostURL.getShareUrl());

		// 启动分享GUI
		 oks.show(this);
		 }
	
	/**
	 * 网络状态的广播接收者
	 * @author Administrator
	 *
	 */
	class NetworkBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
				if (!NetworkStateUtil.isNetWorkAvailable(MainActivity.this)) {
					//没有网络的时候，显示网络无连接的对话框
					showDialog();
				}else{
					//网络变为可用的时候，如果对话框是显示状态，关闭
					if(mAlertDialog!=null&&mAlertDialog.isShowing())
						mAlertDialog.dismiss();
				}
			}
		}
	}
	
	
	private void showWarningDialog(String title,String msg,OnClickListener l1,OnClickListener l2,OnDismissListener d1){
		materialDialog = new MaterialDialog(MainActivity.this);
		materialDialog.setCanceledOnTouchOutside(false);
		materialDialog.setTitle(title).setMessage(msg)
				.setPositiveButton("确定", l1).setNegativeButton("取消", l2)
				.setCanceledOnTouchOutside(false)
				.setOnDismissListener(d1).show();
	}
	
	/**
	 * 自定义taost
	 * 
	 * @param iconResId
	 *            图片
	 * @param msgResId 
	 *            提示文字
	 */
	public void showTips(int iconResId, String tips) {
		if (mTipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				mTipsToast.cancel();
			}
		} else {
			mTipsToast = TipsToast.makeText(getApplication().getBaseContext(),tips, TipsToast.LENGTH_SHORT);
		}
		mTipsToast.show();
		mTipsToast.setIcon(iconResId);
		mTipsToast.setText(tips);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {// 根据反馈值判断当前网络状态
		case 1:
			String keyStr = data.getStringExtra("key");
			if ("-1".equals(keyStr)) {
				showTips(R.drawable.tips_error, "网络不可用...");
			} else {
				showTips(R.drawable.tips_smile, "网络已恢复正常...");
			}
			break;
		}
	}
	
	public void showDialog(){
		//如果当前已经有弹窗，不在弹出
		if(mAlertDialog!=null&&mAlertDialog.isShowing())
			return;
		AlertDialog.Builder mBuilder=new Builder(this);
		View view = View.inflate(this, R.layout.dialog_network, null);
		switchWifi = (UISwitchButton) view.findViewById(R.id.switch_wifi);
		switchGprs = (UISwitchButton) view.findViewById(R.id.switch_liuliang);
		mGprsImageView = (ImageView) view.findViewById(R.id.img_gprs);
		mWifiImageView = (ImageView) view.findViewById(R.id.img_wifi);
		but_close = (Button) view.findViewById(R.id.but_close);
		//设置按钮的点击事件
		but_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//关闭对话框的时候，检测网络是否已经连接
				checkNetwork();
			}
		});

		switchWifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if (isChecked) {
					toggleWiFi(true);
					mWifiImageView.setBackgroundResource(R.drawable.wuxianlanse);
					showTips(R.drawable.tips_smile, "正在打开WiFi网络...");
				} else {
					toggleWiFi(false);
					mWifiImageView.setBackgroundResource(R.drawable.wuxian1);
					showTips(R.drawable.tips_smile, "正在关闭WiFi网络...");
				}
			}
		});

		switchGprs.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if (isChecked) {
					setMobileNetEnable();
					mGprsImageView.setBackgroundResource(R.drawable.wuxianerlans);
					showTips(R.drawable.tips_smile, "正在打开数据网络...");
				} else {
					setMobileNetUnable();
					showTips(R.drawable.tips_smile, "正在关闭数据网络...");
					mGprsImageView.setBackgroundResource(R.drawable.wuxianer);
				}
			}
		});
		mAlertDialog = mBuilder.create();
		mAlertDialog.setView(view,0,0,0,0);
		mAlertDialog.show();
	}

	/**
	 * 设置是否启用WIFI网络
	 */
	public void toggleWiFi(boolean status) {
		WifiManager wifiManager = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		if (status == true && !wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);

		} else if (status == false && wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(false);
		}
	}

	/**
	 * 设置启用数据流量
	 */
	public final void setMobileNetEnable() {

		mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		Object[] arg = null;
		try {
			boolean isMobileDataEnable = invokeMethod("getMobileDataEnabled",
					arg);
			if (!isMobileDataEnable) {
				invokeBooleanArgMethod("setMobileDataEnabled", true);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 设置不启用数据流量
	 */
	public final void setMobileNetUnable() {
		mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		Object[] arg = null;
		try {
			boolean isMobileDataEnable = invokeMethod("getMobileDataEnabled",
					arg);
			if (isMobileDataEnable) {
				invokeBooleanArgMethod("setMobileDataEnabled", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//执行某个方法
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean invokeMethod(String methodName, Object[] arg)
			throws Exception {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		Class ownerClass = mConnectivityManager.getClass();
		Class[] argsClass = null;
		if (arg != null) {
			argsClass = new Class[1];
			argsClass[0] = arg.getClass();
		}
		Method method = ownerClass.getMethod(methodName, argsClass);
		Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);
		return isOpen;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object invokeBooleanArgMethod(String methodName, boolean value)
			throws Exception {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		Class ownerClass = mConnectivityManager.getClass();
		Class[] argsClass = new Class[1];
		argsClass[0] = boolean.class;
		Method method = ownerClass.getMethod(methodName, argsClass);
		return method.invoke(mConnectivityManager, value);
	}
	
	/**
	 * 检查网络
	 */
	private void checkNetwork() {
		if(mAlertDialog!=null&&mAlertDialog.isShowing()){
			if(NetworkStateUtil.isNetWorkAvailable(this)){
				showTips(R.drawable.tips_smile, "网络已恢复正常...");
			} else {
				showTips(R.drawable.tips_smile, "网络不可用...");
			}
			mAlertDialog.dismiss();
			mAlertDialog = null;
		}
	}
	
	
	/**
	 * 销毁的时候，取消注册广播
	 */
	@Override
	protected void onDestroy() {
		if(mNetworkBroadcastReceiver!=null){
			unregisterReceiver(mNetworkBroadcastReceiver);
		}
		super.onDestroy();
	}

	@Override
	public int getResId() {
		return R.layout.activity_main;
	}

	@Override
	public void findView() {
		mDrawerList = (ListView) findViewById(R.id.navdrawer);
		rl = (RelativeLayout) findViewById(R.id.rl);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_fragment);
		mIcoRoundImageView = (RoundImageView) findViewById(R.id.login_tv);
	}

	@Override
	public void setListener() {
		// 设置左侧菜单列表的监听事件
		mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressLint({ "ResourceAsColor", "Recycle" })
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				//切换到具体的fragment
				switch(position){
				case 0:
					Fragment homeFragment = mFragmentMap.get("home");
					if (homeFragment == null) {
						homeFragment = new HomeFragment();
						mFragmentMap.put("home", homeFragment);
						changeFragment(homeFragment, "home");
					} else {
						initFragment(homeFragment);
					}
					setTitle("网盘搜索");
					break;
				case 1:
					Fragment bookFragment = mFragmentMap.get("book");
					if (bookFragment == null) {
						bookFragment = new BookFragment();
						mFragmentMap.put("book", bookFragment);
						changeFragment(bookFragment, "book");
					} else {
						initFragment(bookFragment);
					}
					setTitle("轻之文库");
					break;
				case 2:
					Fragment acgFragment = mFragmentMap.get("acg");
					if (acgFragment == null) {
						acgFragment = new AcgFragment();
						mFragmentMap.put("acg", acgFragment);
						changeFragment(acgFragment, "acg");
					} else {
						initFragment(acgFragment);
					}
					setTitle("动漫美图");
					break;
				case 3:
					Fragment historyFragment = mFragmentMap.get("history");
					if (historyFragment == null) {
						historyFragment = new HistoryFragment();
						mFragmentMap.put("history", historyFragment);
						changeFragment(historyFragment, "history");
					} else {
						initFragment(historyFragment);
					}
					setTitle("搜索记录");
					break;
				case 4:
					Fragment shakeFragment = mFragmentMap.get("shake");
					if (shakeFragment == null) {
						shakeFragment = new ShakeFragment();
						mFragmentMap.put("shake", shakeFragment);
						changeFragment(shakeFragment,false, "shake","shake","");
					} else {
						initFragment(shakeFragment);
					}
					setTitle("资源摇一摇");
					break;
				case 5:
					showShare();
					break;
				case 6:
					Fragment opininFragment = mFragmentMap.get("opinion");
					if (opininFragment == null) {
						opininFragment = new OpinionFragment();
						mFragmentMap.put("opinion", opininFragment);
						changeFragment(opininFragment, "opinion");
					} else {
						initFragment(opininFragment);
					}
					setTitle("意见反馈");
					break;
				case 7:
					Intent mIntent = new Intent();
					mIntent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
					mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(mIntent);
					break;
				case 8 :
					Fragment resFragment = mFragmentMap.get("resshera");
					if (resFragment == null) {
						resFragment =new ResShareFragment();
						mFragmentMap.put("resshera", resFragment);
						changeFragment(resFragment, "resshera");
					} else {
						initFragment(resFragment);
					}
					setTitle("资源墙");
					break;
				case 9:
					Fragment settingFragment = mFragmentMap.get("setting");
					if (settingFragment == null) {
						settingFragment = new SettingFragment();
						mFragmentMap.put("setting", settingFragment);
						changeFragment(settingFragment, "setting");
					} else {
						initFragment(settingFragment);
					}
					setTitle("设置");
					break;
				case 10:
					Fragment aboutFragment = mFragmentMap.get("about");
					if (aboutFragment == null) {
						aboutFragment = new AboutFragment();
						mFragmentMap.put("about", aboutFragment);
						changeFragment(aboutFragment, "about");
					} else {
						initFragment(aboutFragment);
					}
					setTitle("关于");
					break;
				}
				mDrawerLayout.closeDrawers();
				openOrClose = false;
			}
		});
		mIcoRoundImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//判断当前用户是否已经登陆了
				BmobUser user = BmobUser.getCurrentUser(MainActivity.this);
				if (user == null) {
					changeFragment(new LoginFragment(), "login");
					setTitle("登录");
					mDrawerLayout.closeDrawers();
					openOrClose = false;
				} else {
					changeFragment(new UserInfoFragment(), "userInfo");
					setTitle("用户信息");
					mDrawerLayout.closeDrawers();
					openOrClose = false;
				}
			}
		});
	}

	@Override
	public void initData(Bundle b) {
		mFragmentMap=new HashMap<String, Fragment>();
		configSP=getSharedPreferences("config", MODE_PRIVATE);
		mNetworkBroadcastReceiver = new NetworkBroadcastReceiver();
		IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
		//注册网络改变的广播接收者
		registerReceiver(mNetworkBroadcastReceiver, filter);
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		applyKitKatTranslucency();
		drawerArrow = new DrawerArrowDrawable(this) {
			@Override
			public boolean isLayoutRtl() {
				return false;
			}
		};
		//左侧滑动视图
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,drawerArrow, R.string.drawer_open, R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				invalidateOptionsMenu();
				openOrClose = false;
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu();
				openOrClose = true;
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.item_text, mTitles);
		mDrawerList.setAdapter(adapter);
		init();
		
		if(configSP.getBoolean("warning", true)){
			
			showWarningDialog("警告", "使用该软件进行搜索，可能会含有一些令人不适的内容。请确定是否需要继续使用该软件？", new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Editor edit = configSP.edit();
					edit.putBoolean("warning", false);
					edit.commit();
					materialDialog.dismiss();
				}
			}, new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					materialDialog.dismiss();
					MainActivity.this.finish();
				}
			}, new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface arg0) {
					
				}
			});
			
		}
		BmobUpdateAgent.initAppVersion(this);
		//如果开启了自动检查更新
		if(configSP.getBoolean("autoUpdata", true)){
			BmobUpdateAgent.update(this);
		}
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		UserBean userBean = BmobUser.getCurrentUser(this, UserBean.class);
		if(userBean != null ){
			File headFile = new File(Environment.getExternalStorageDirectory()+ "/android/data/com.fanchen.aisou/head/head.jpg");
			if(headFile.exists()){
				mIcoRoundImageView.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+ "/android/data/com.fanchen.aisou/head/head.jpg"));
			}
		}
	}
}
