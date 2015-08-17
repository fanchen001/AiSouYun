package com.fanchen.aisou.fragment;

import java.util.Map;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.fanchen.aisou.R;
import com.fanchen.aisou.bean.UserBean;
import com.fanchen.aisou.utils.RegularUtil;
import com.fanchen.aisou.utils.SecurityUtil;
import com.fanchen.aisou.view.FlippingLoadingDialog;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ChagePwdFragment extends BaseFragment {
	
	private EditText mMailEditText;
	private EditText mOldEditText;
	private EditText mNewEditText;
	private Button mButton;
	private UserBean mUserBean;
	protected FlippingLoadingDialog mLoadingDialog;
	private Handler mHandler = new Handler();

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_change_pwd:
			final String mail = mMailEditText.getText().toString().trim();
			final String old = mOldEditText.getText().toString().trim();
			final String pwd = mNewEditText.getText().toString().trim();
			if(TextUtils.isEmpty(mail)){
				showToast("请输入邮箱");
				return;
			}
			if(TextUtils.isEmpty(old)){
				showToast("请输入旧密码");
				return;
			}
			if(TextUtils.isEmpty(pwd)){
				showToast("请输入新密码");
				return;
			}
			if(!RegularUtil.isEmail(mail)){
				showToast("请输入正确的邮箱");
				return;
			}
			if(pwd.length() < 6){
				showToast("新密码长度太短");
				return;
			}
			
			
			
			if(mail.equals(mUserBean.getEmail())){
				mLoadingDialog = new FlippingLoadingDialog(mainActivity, "正在请求数据...");
				mLoadingDialog.show();
				mUserBean.setPassword(SecurityUtil.encode(pwd));
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						UserBean bean = new UserBean();
						bean.setUsername(mUserBean.getUsername());
						bean.setPassword(SecurityUtil.encode(old));
						bean.login(mainActivity, new SaveListener() {
							@Override
							public void onSuccess() {
								mUserBean.update(mainActivity, new UpdateListener() {
									@Override
									public void onSuccess() {
										mLoadingDialog.dismiss();
										showToast("修改密码成功，请重新登录");
										BmobUser.logOut(mainActivity);
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
										showToast("修改密码失败");
									}
								});
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								mLoadingDialog.dismiss();
								showToast("修改密码失败");
							}
						});
					}
				}, 1000);
				
			}else{
				showToast("请检测是否输入了正确的邮箱");
			}
			

		default:
			break;
		}
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_change_pwd, container,false);
	}

	@Override
	public void findView(View v) {
		mMailEditText = (EditText) v.findViewById(R.id.ed_change_email);
		mOldEditText = (EditText) v.findViewById(R.id.ed_change_password);
		mNewEditText = (EditText) v.findViewById(R.id.ed_change_new_password);
		mButton = (Button) v.findViewById(R.id.bt_change_pwd);
	}

	@Override
	public void setLinsener() {
		
				mButton.setOnClickListener(this);
	}

	@Override
	public void fillData(Bundle savedInstanceState) {
		mUserBean = BmobUser.getCurrentUser(mainActivity, UserBean.class);
	}

}
