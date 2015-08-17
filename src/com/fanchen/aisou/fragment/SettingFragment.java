package com.fanchen.aisou.fragment;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.update.BmobUpdateAgent;

import com.fanchen.aisou.R;
import com.fanchen.aisou.bean.CouldTypeBean;
import com.fanchen.aisou.utils.XmlUtil;
import com.fanchen.aisou.view.CustomMultiChoiceDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
/**
 * 设置
 * @author Administrator
 *
 */
public class SettingFragment extends BaseFragment{
	@SuppressWarnings("unused")
	private LinearLayout mDownLoadPathLinearLayout;
	private LinearLayout mCouldTypeLinearLayout;
	private LinearLayout mCheckUpdataPathLinearLayout;//检测更新
	private CheckBox mAutoUpdataCheckBox;// 自动检测更新按钮
	private CheckBox mWordLenovoCheckBox;//词语联想按钮
	private CheckBox mBrowserCheckBox;//是否打开浏览器按钮
	private CheckBox mMagnetCheckBox;
	private SharedPreferences mPreferences;//配置
	private static CustomMultiChoiceDialog.Builder multiChoiceDialogBuilder;
	private List<String> mTypeList;
	
	private List<Boolean> mChecks;
	
	private static String[] cloudType = {"百度","华为", "115", "旋风", "迅雷","金山","360","一木禾","千军万马"};
	
	@Override
	public void onClick(View arg0) {
		Editor edit = null;
		switch (arg0.getId()) {
		case R.id.cb_auto_updata:
			//自动更新设置
			edit = mPreferences.edit();
			edit.putBoolean("autoUpdata", mAutoUpdataCheckBox.isChecked());
			edit.commit();
			break;
		case R.id.cb_open_type:
			//打开方式设置
			edit = mPreferences.edit();
			edit.putBoolean("open_type", mBrowserCheckBox.isChecked());
			edit.commit();
			break;
		case R.id.cb_open_word_lenove:
			//词语联想设置
			edit = mPreferences.edit();
			edit.putBoolean("lenovo", mWordLenovoCheckBox.isChecked());
			edit.commit();
			break;
		case R.id.setup_checkupdata:
			//手动检测更
			showToast("正在检查更新...");
			BmobUpdateAgent.initAppVersion(mainActivity);
			BmobUpdateAgent.setUpdateOnlyWifi(false);
			BmobUpdateAgent.update(mainActivity);
			break;
		case R.id.cb_magnet:
			if(BmobUser.getCurrentUser(getActivity())==null){
				showToast("未登录用户不能使用磁链搜索服务");
				mMagnetCheckBox.setChecked(false);
				return;
			}
			edit = mPreferences.edit();
			edit.putBoolean("magnet", mMagnetCheckBox.isChecked());
			edit.commit();
			break;
			
		case R.id.setup_could_type:
			if(mTypeList == null && mChecks == null){
				mTypeList = new ArrayList<String>();
				mChecks = new ArrayList<Boolean>();
			}else{
				mTypeList.clear();
				mChecks.clear();
			}
			List<CouldTypeBean> couldList = null;
			try {
				couldList = XmlUtil.getAll("couldType.xml", CouldTypeBean.class, mainActivity);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(couldList != null && couldList.size() > 0){
				for (int i = 0; i < couldList.size(); i++) {
					mTypeList.add(couldList.get(i).getCouldType());
					mChecks.add(couldList.get(i).getCheck() == 0 ? false : true);
				}
			}else{
				for (int i = 0; i < cloudType.length; i++) {
					mTypeList.add(cloudType[i]);
					mChecks.add(true);
				}
			}
			multiChoiceDialogBuilder = new CustomMultiChoiceDialog.Builder(mainActivity);
			CustomMultiChoiceDialog multiChoiceDialog = multiChoiceDialogBuilder.setTitle("选择网盘")
					.setMultiChoiceItems(mTypeList, mChecks, null, true)
					.setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							List<Boolean> checkedItems = multiChoiceDialogBuilder.getCheckedItems();
							List<Boolean> checkeds = new ArrayList<Boolean>();
							for (Boolean b : checkedItems) {
								if(b){
									checkeds.add(b);
								}
							}
							List<CouldTypeBean> mCouldTypeBeans = new ArrayList<CouldTypeBean>();
							if( checkeds.size() <= 1){
								showToast("至少需要选择两个网盘哦，本次设置不生效");
								return;
							}
							for (int i = 0; i < checkedItems.size(); i++) {
								CouldTypeBean bean = new CouldTypeBean();
								bean.setCouldType(mTypeList.get(i));
								bean.setCheck(checkedItems.get(i) == false ? 0 : 1);
								mCouldTypeBeans.add(bean);
							}
							mCouldTypeBeans.add(new CouldTypeBean("磁力链",1));
							try {
								XmlUtil.save(mCouldTypeBeans, "couldType.xml", mainActivity);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).setNegativeButton("取消", null).create();
			multiChoiceDialog.show();
			
			break;
		default:
			break;
		}
		
	}
	
	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_setting, null);
	}
	@Override
	public void findView(View view) {
		mWordLenovoCheckBox=(CheckBox) view.findViewById(R.id.cb_open_word_lenove);
		mCheckUpdataPathLinearLayout=(LinearLayout) view.findViewById(R.id.setup_checkupdata);
		mDownLoadPathLinearLayout=(LinearLayout) view.findViewById(R.id.setup_downloadpath);
		mAutoUpdataCheckBox=(CheckBox) view.findViewById(R.id.cb_auto_updata);
		mBrowserCheckBox=(CheckBox) view.findViewById(R.id.cb_open_type);
		mMagnetCheckBox=(CheckBox) view.findViewById(R.id.cb_magnet);
		mCouldTypeLinearLayout=(LinearLayout) view.findViewById(R.id.setup_could_type);
	}
	@Override
	public void setLinsener() {
		mWordLenovoCheckBox.setOnClickListener(this);
		mAutoUpdataCheckBox.setOnClickListener(this);
		mBrowserCheckBox.setOnClickListener(this);
		mCheckUpdataPathLinearLayout.setOnClickListener(this);
		mMagnetCheckBox.setOnClickListener(this);
		mCouldTypeLinearLayout.setOnClickListener(this);
	}
	
	
	@Override
	public void fillData(Bundle savedInstanceState) {
		mPreferences=getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
		mAutoUpdataCheckBox.setChecked(mPreferences.getBoolean("autoUpdata", true));
		mBrowserCheckBox.setChecked(mPreferences.getBoolean("open_type", false));
		mWordLenovoCheckBox.setChecked(mPreferences.getBoolean("lenovo", true));
		mMagnetCheckBox.setChecked(mPreferences.getBoolean("magnet", false));
		
	}

}