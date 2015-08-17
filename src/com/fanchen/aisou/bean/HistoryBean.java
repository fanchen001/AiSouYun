package com.fanchen.aisou.bean;

import java.util.List;
/**
 * 搜索记录实体
 * @author Administrator
 *
 */
public class HistoryBean {
	
	private String day;//日期
	private List<String> mChar;//搜索词
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public List<String> getmChar() {
		return mChar;
	}
	public void setmChar(List<String> mChar) {
		this.mChar = mChar;
	}
	@Override
	public String toString() {
		return "HistoryBean [day=" + day + ", mChar=" + mChar + "]";
	}
	
	

}
