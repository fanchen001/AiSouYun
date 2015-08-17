package com.fanchen.aisou.fragment;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.fanchen.aisou.R;
import com.fanchen.aisou.bean.ToUserBean;
import com.fanchen.aisou.bean.UserBean;
import com.fanchen.aisou.services.GeneralService;
import com.fanchen.aisou.utils.SecurityUtil;
import com.fanchen.aisou.view.FlippingLoadingDialog;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginFragment extends BaseFragment {
	
	private Tencent mTencent;
	private EditText mNameEditText;
	private EditText mPasswordEditText;
	private Button mLoginButton;
	protected FlippingLoadingDialog mLoadingDialog;
	private TextView mTextView;
	private TextView mLoginQQTextView;
	private Handler mHandler=new Handler();
	private BmobQuery<ToUserBean> mBmobQuery;
	private UserInfo userInfo;
	private String scope = "all";

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.bt_login:
			String name = mNameEditText.getText().toString().trim();
			String password = mPasswordEditText.getText().toString().trim();
			
			if(TextUtils.isEmpty(name)){
				showToast( "请输入用户名");
				return;
			}
			if(TextUtils.isEmpty(password)){
				showToast( "请输入密码");
				return;
			}
			final UserBean bean=new UserBean();
			bean.setUsername(name);
			bean.setPassword(SecurityUtil.encode(password));
			login(bean);
			break;
		case R.id.go_register:
			Map<String, Fragment> map = mainActivity.getmFragmentMap();
			Fragment registerFragment = map.get("register");
			if(registerFragment==null){
				registerFragment=new RegisterFragment();
				map.put("register", registerFragment);
				mainActivity.changeFragment( registerFragment,"register");
			}else{
				mainActivity.initFragment(registerFragment);
			}
			mainActivity.setTitle("注册");
			break;
		case R.id.tv_login_qq:
			if (!mTencent.isSessionValid()) {
				mTencent.login(mainActivity, scope, loginListener);
			}

			break;
		default:
			break;
		}
	}
	

	private void login(final UserBean bean) {
		mLoadingDialog = new FlippingLoadingDialog(mainActivity, "请求提交中...");
		mLoadingDialog.show();
//		mLoadToast=new LoadToast(mainActivity).setTranslationY(DisplayUtil.dp2px(mainActivity, 80)).setText("正在登陆...").show();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				bean.login(getActivity(), new SaveListener() {
					@Override
					public void onSuccess() {
						mLoadingDialog.dismiss();
						UserBean user = BmobUser.getCurrentUser(mainActivity, UserBean.class);
						if(!TextUtils.isEmpty(user.getmHead())){
							Intent mIntent = new Intent(mainActivity,GeneralService.class);
							mIntent.putExtra("url", user.getmHead());
							mIntent.putExtra("name", "head.jpg");
							mainActivity.startService(mIntent);
						}
						showToast( "登陆成功，你现在可以使用磁链转换功能了(｡･∀･)ﾉﾞ嗨");
						Map<String, Fragment> map = mainActivity.getmFragmentMap();
						Fragment homeFragment = map.get("home");
						if(homeFragment==null){
							homeFragment=new HomeFragment();
							map.put("register", homeFragment);
							mainActivity.changeFragment( homeFragment,"home");
						}else{
							mainActivity.initFragment(homeFragment);
						}
						mainActivity.setTitle("爱搜云");
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						mLoadingDialog.dismiss();
						showToast("登陆失败："+arg1);
					}
				});
			}
		}, 2*1000);
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_login, null);
	}

	@Override
	public void findView(View v) {
		mLoginButton=(Button) v.findViewById(R.id.bt_login);
		mNameEditText=(EditText) v.findViewById(R.id.ed_account);
		mPasswordEditText=(EditText) v.findViewById(R.id.ed_password);
		mTextView=(TextView) v.findViewById(R.id.go_register);
		mLoginQQTextView=(TextView) v.findViewById(R.id.tv_login_qq);
	}

	@Override
	public void setLinsener() {
		mLoginButton.setOnClickListener(this);
		mLoginQQTextView.setOnClickListener(this);
		mTextView.setOnClickListener(this);
	}

	@Override
	public void fillData(Bundle savedInstanceState) {
		mTencent=Tencent.createInstance("1104335299", mainActivity);
		mBmobQuery=new BmobQuery<ToUserBean>();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_API) {
			if (resultCode == Constants.RESULT_LOGIN) {
				Tencent.handleResultData(data, loginListener);
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	private IUiListener userInfoListener = new IUiListener() {
		@Override
		public void onError(UiError arg0) {
			
		}
		@Override
		public void onComplete(Object arg0) {
			try {
				JSONObject jo = (JSONObject) arg0;
				int ret = jo.getInt("ret");
				if(ret == 100030){
				}else{
					
					String nickName = jo.getString("nickname");
					String head = jo.getString("figureurl_qq_2");
					String sex = jo.getString("gender");
					final String openId = mTencent.getOpenId();
					final String username = "auto_"+ (System.currentTimeMillis() / 1000);
					final UserBean bean = new UserBean();
					bean.setUsername(username);
					bean.setBirthday("1990-01-01");
					bean.setPassword(SecurityUtil.encode("000000"));
					bean.setOpenId(openId);
					bean.setmHead(head);
					bean.setSex(sex);
					bean.setNickName(nickName);
					bean.signUp(mainActivity, new SaveListener() {
						
						@Override
						public void onSuccess() {
							ToUserBean toUserBean = new ToUserBean();
							toUserBean.setOpen_id(openId);
							toUserBean.setUser_name(username);
							toUserBean.save(mainActivity,new SaveListener() {
								@Override
								public void onSuccess() {
									login(bean);
								}

								@Override
								public void onFailure(int arg0,String arg1) {
									showToast("QQ授权登陆失败");
								}
							});
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							
						}
					});
				}
			} catch (Exception e) {
			}
		}
		
		@Override
		public void onCancel() {
			
		}
	};
	
	private IUiListener loginListener = new IUiListener() {

		@Override
		public void onError(UiError arg0) {

		}
		@Override
		public void onComplete(Object value) {
			try {
				JSONObject jo = (JSONObject) value;
				final String openID = jo.getString("openid");
				String accessToken = jo.getString("access_token");
				String expires = jo.getString("expires_in");
				mTencent.setOpenId(openID);
				mTencent.setAccessToken(accessToken, expires);
				
				mBmobQuery.addWhereEqualTo("open_id", openID);
				mBmobQuery.findObjects(mainActivity,new FindListener<ToUserBean>() {
					@Override
					public void onSuccess(List<ToUserBean> arg0) {
						if(arg0 == null || arg0.size() == 0){
							userInfo = new UserInfo(mainActivity, mTencent.getQQToken());
							userInfo.getUserInfo(userInfoListener);
					    }else{
					    	UserBean bean = new UserBean();
						    String user_name = arg0.get(0).getUser_name();
							bean.setUsername(user_name);
							bean.setPassword(SecurityUtil.encode("000000"));
							bean.setOpenId(openID);
							login(bean);
						}
					}

					@Override
					public void onError(int arg0, String arg1) {
						showToast("QQ授权登陆失败");
					}
				});
			} catch (Exception e) {
			}
		}

		@Override
		public void onCancel() {

		}
	};
}
