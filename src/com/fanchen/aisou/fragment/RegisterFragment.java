package com.fanchen.aisou.fragment;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.listener.SaveListener;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.fanchen.aisou.R;
import com.fanchen.aisou.bean.UserBean;
import com.fanchen.aisou.jni.BmobValue;
import com.fanchen.aisou.utils.RegularUtil;
import com.fanchen.aisou.utils.SecurityUtil;
import com.fanchen.aisou.view.FlippingLoadingDialog;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterFragment extends BaseFragment implements UploadListener{
	
	private EditText mNameEditText;
	private EditText mPasswordEditText;
	private EditText mEmailEditText;
	private EditText mBrithdayEditText;
	private Button mRegisterButton;
	private ImageView mHeadImageView;
	private FlippingLoadingDialog mFlippingLoadingDialog;
	private TextView mTextView;
	private String mHeadPath;
	private String userName;
	private String passWord;
	private String brithday;
	private String email;
	private Handler mHandler=new Handler();
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.bt_register:
			userName = mNameEditText.getText().toString().trim();
			passWord = mPasswordEditText.getText().toString().trim();
			email = mEmailEditText.getText().toString().trim();
			brithday = mBrithdayEditText.getText().toString().trim();
			if(TextUtils.isEmpty(userName)){
				showToast("请输入用户名");
				return;
			}
			if(TextUtils.isEmpty(email)){
				showToast("请输入邮箱");
				return;	
			}
			if(TextUtils.isEmpty(passWord)){
				showToast( "请输入密码");
				return;
			}
			if(!isEmail(email)){
				showToast("请输入正确的邮箱");
				return;
			}
			if(TextUtils.isEmpty(brithday)){
				showToast("请输入生日");
				return;
			}
			if(!(userName.length() >= 6 && userName.length() <= 10)){
				showToast("用户名必须大于6个字符小于等于10个字符");
				return;
			}
			if(!(passWord.length() >= 6 && passWord.length() <= 10)){
				showToast("密码必须大于6个字符小于等于10个字符");
				return;
			}
			if(!RegularUtil.isStringOK(userName, 10)){
				showToast("用户名必须为字母数字下划线或者中文组合");
				return;
			}
			if(!RegularUtil.isStringOK(passWord, 10)){
				showToast("密码必须为字母数字下划线或者中文组合");
				return;
			}
			if(mHeadPath!=null){
				mFlippingLoadingDialog = new FlippingLoadingDialog(mainActivity, "请求提交中...");
				mFlippingLoadingDialog.show();
				BmobProFile.getInstance(mainActivity).upload(mHeadPath, this);
			}else {
				showToast("请选择用户头像...");
			}
			break;
		case R.id.go_login:
			goToLogin();
			break;
		case R.id.iv_head:
			pickFile();
			break;
			
		default:
			break;
		}
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_register, null);
	}

	@Override
	public void findView(View v) {
		mNameEditText=(EditText) v.findViewById(R.id.ed_nickname);
		mEmailEditText=(EditText) v.findViewById(R.id.ed_email);
		mPasswordEditText=(EditText) v.findViewById(R.id.ed_password);
		mRegisterButton=(Button) v.findViewById(R.id.bt_register);
		mTextView=(TextView) v.findViewById(R.id.go_login);
		mHeadImageView=(ImageView) v.findViewById(R.id.iv_head);
		mBrithdayEditText = (EditText) v.findViewById(R.id.ed_birthday);
	}

	@Override
	public void setLinsener() {
		mHeadImageView.setOnClickListener(this);
		mRegisterButton.setOnClickListener(this);
		mTextView.setOnClickListener(this);
	}

	@Override
	public void fillData(Bundle savedInstanceState) {
	}
	
	public boolean isEmail(String email){
		Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	private void goToLogin() {
		Map<String, Fragment> map = mainActivity.getmFragmentMap();
		Fragment loginFragment = map.get("login");
		if(loginFragment==null){
			loginFragment=new LoginFragment();
			map.put("login", loginFragment);
			mainActivity.changeFragment( loginFragment,"login");
		}else{
			mainActivity.initFragment(loginFragment);
		}
		mainActivity.setTitle("登录");
	}

//	@Override
//	public void onError(String arg0) {
//		showToast(arg0);
//	}
//
//	@Override
//	public void onFileChosen(ChosenFile file) {
//		choosedFile = file;
//		BTPFileResponse response = BmobProFile.getInstance(mainActivity).upload(choosedFile.getFilePath(), new UploadListener() {
//			@Override
//			public void onSuccess(String fileName,String url) {
//				//如果你想得到一个可以直接在客户端显示的图片地址，那么可以使用BmobProFile类的静态方法获取可访问的URL地址,且不建议开启URL签名认证
//				showToast("文件已上传成功："+URL);
//			}
//
//			@Override
//			public void onProgress(int ratio) {
//			}
//
//			@Override
//			public void onError(int statuscode, String errormsg) {
//				showToast("上传出错："+errormsg);
//			}
//		});
//	}

	
	public void pickFile() {
		 Intent intent = new Intent();  
         /* 开启Pictures画面Type设定为image */  
         intent.setType("image/*");  
         /* 使用Intent.ACTION_GET_CONTENT这个Action */  
         intent.setAction(Intent.ACTION_GET_CONTENT);   
         /* 取得相片后返回本画面 */  
         startActivityForResult(intent, 500); 
	}
	
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			// 拍照或者选择图片
			try {
				Uri uri = data.getData();
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor actualimagecursor = mainActivity.getContentResolver().query(uri, proj, null, null, null);
				int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				actualimagecursor.moveToFirst();
				mHeadPath = actualimagecursor.getString(actual_image_column_index);
				Bitmap bitmap = BitmapFactory.decodeFile(mHeadPath);
				mHeadImageView.setImageBitmap(bitmap);
			} catch (Exception e) {

			}
		}
    }

	@Override
	public void onError(int arg0, String arg1) {
		
	}

	@Override
	public void onProgress(int arg0) {
		
	}
	
	@Override
	public void onSuccess(String fileName,String url) {
		//如果你想得到一个可以直接在客户端显示的图片地址，那么可以使用BmobProFile类的静态方法获取可访问的URL地址,且不建议开启URL签名认证
		String headUrl = BmobProFile.getInstance(mainActivity).signURL(fileName,url,BmobValue.getAccessKey(),0,null);
		final UserBean bean=new UserBean();
		bean.setEmail(email);
		bean.setBirthday(brithday);
		bean.setUsername(userName);
		bean.setPassword(SecurityUtil.encode(passWord));
		bean.setmHead(headUrl);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				bean.signUp(mainActivity,new SaveListener() {
					@Override
					public void onSuccess() {
						mFlippingLoadingDialog.dismiss();
						goToLogin();
						showToast("注册成功");
					}
					@Override
					public void onFailure(int arg0, String arg1) {
						mFlippingLoadingDialog.dismiss();
						showToast("注册失败："+arg1);
					}
				});
			}
		}, 2*1000);
	}
	
	
}
