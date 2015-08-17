package com.fanchen.aisou.bean;

import cn.bmob.v3.BmobObject;

public class SplashImgBean  extends BmobObject{
	
	private static final long serialVersionUID = 1L;

	private int version;
	
	private long startTime;
	
	private long endTime;
	
	private String imgUrl;
	
	private String path;
	

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
