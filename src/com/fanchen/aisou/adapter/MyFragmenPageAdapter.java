package com.fanchen.aisou.adapter;

import java.util.ArrayList;
import java.util.List;

import com.fanchen.aisou.bean.CouldTypeBean;
import com.fanchen.aisou.utils.XmlUtil;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * searchFragment里面，viewPager的适配器
 * @author Administrator
 *
 */
public class MyFragmenPageAdapter extends FragmentStatePagerAdapter{
	//标题
	private final String[] TITLES = {"磁力链", "百度云","华为网盘","115网盘","QQ旋风","迅雷快传","金山快盘","360云","一木禾","千军万马"};
	//要显示的fragemnt列表
	private List<Fragment> fragments; 
	private boolean isSearchMagent;
	
	private List<String> TITLES1;
	
	public MyFragmenPageAdapter(Context context,FragmentManager fm, List<Fragment> fragments,boolean isSearchMagent){
		super(fm);
		TITLES1 = new ArrayList<String>();
		List<CouldTypeBean> allCouldType = null;
		try {
			allCouldType = XmlUtil.getAll("couldType.xml", CouldTypeBean.class, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(allCouldType != null && allCouldType.size() > 0){
			TITLES1.add("磁力链");
			for (CouldTypeBean bean : allCouldType) {
				if(bean.getCheck() == 1){
					TITLES1.add(bean.getCouldType());
				}
			}
		}else{
			for (int i = 0; i < TITLES.length; i++) {
				TITLES1.add(TITLES[i]);
			}
		}
		
		this.isSearchMagent=isSearchMagent;
		this.fragments=fragments;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return isSearchMagent?TITLES1.get(position):TITLES1.get(position+1);
	}
	
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return fragments==null?0:fragments.size();
	}
	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}


}
