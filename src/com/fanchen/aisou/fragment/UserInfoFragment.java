package com.fanchen.aisou.fragment;

import java.io.File;
import java.util.Map;

import cn.bmob.v3.BmobUser;

import com.fanchen.aisou.R;
import com.fanchen.aisou.bean.UserBean;
import com.fanchen.aisou.view.RoundImageView;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserInfoFragment extends BaseFragment {
	
	private TextView mNameTextView;
	private TextView mBrithdayTextView;
	private TextView mNickTextView;
	private TextView mSexTextView;
	private RoundImageView mRoundImageView;
	private Button mButton;
	private RelativeLayout mCollectRelativeLayout;
	private RelativeLayout mChangePwdRelativeLayout;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_info_logout:
			File headFile = new File(Environment.getExternalStorageDirectory()+ "/android/data/com.fanchen.aisou/splash/head.jpg");
			if(headFile.exists()){
				headFile.delete();
			}
			BmobUser.logOut(mainActivity);   //清除缓存用户对象
			showToast("退出登录成功");
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
			break;
		case R.id.rl_book_collect:
			
			mainActivity.changeFragment(new CollectFragment(), false, "collect", "", "");
			mainActivity.setTitle("书籍收藏");
			break;
			
		case R.id.rl_change_password:
			
			mainActivity.changeFragment(new ChagePwdFragment(), false, "changePwd", "", "");
			mainActivity.setTitle("密码修改");
			
			break;

		default:
			break;
		}
	}
 
	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_user, container, false);
	}

	@Override
	public void findView(View v) {
		mNickTextView = (TextView) v.findViewById(R.id.tv_info_nickname);
		mSexTextView = (TextView) v.findViewById(R.id.tv_info_sex);
		mNameTextView = (TextView) v.findViewById(R.id.tv_info_username);
		mBrithdayTextView = (TextView) v.findViewById(R.id.tv_info_birthday);
		mRoundImageView = (RoundImageView) v.findViewById(R.id.riv_info_avatar);
		mButton = (Button) v.findViewById(R.id.bt_info_logout);
		mCollectRelativeLayout = (RelativeLayout) v.findViewById(R.id.rl_book_collect);
		mChangePwdRelativeLayout = (RelativeLayout) v.findViewById(R.id.rl_change_password);
	}

	@Override
	public void setLinsener() {
		mButton.setOnClickListener(this);
		mCollectRelativeLayout.setOnClickListener(this);
		mChangePwdRelativeLayout.setOnClickListener(this);
	}

	@Override
	public void fillData(Bundle savedInstanceState) {
		UserBean bmobUser = BmobUser.getCurrentUser(mainActivity, UserBean.class);
		mNameTextView.setText(bmobUser.getUsername());
		String birthday = bmobUser.getBirthday();
		String sex = bmobUser.getSex();
		String nick = bmobUser.getNickName();
		if(TextUtils.isEmpty(birthday)){
			birthday = "1990-01-01";
		}
		if(TextUtils.isEmpty(sex)){
			sex = "男";
		}
		if(TextUtils.isEmpty(nick)){
			nick = "无";
		}
		mSexTextView.setText(sex);
		mNickTextView.setText(nick);
		mBrithdayTextView.setText(birthday);
		File file = new File(Environment.getExternalStorageDirectory() + "/android/data/com.fanchen.aisou/head/head.jpg");
		if(file.exists()){
			mRoundImageView.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/android/data/com.fanchen.aisou/head/head.jpg"));
		}else{
			mRoundImageView.setImageResource(R.drawable.empty_photo);
		}
	}

}
