package com.fanchen.aisou.bean;

import cn.bmob.v3.BmobObject;

public class ResShakeBean extends BmobObject {

	private static final long serialVersionUID = 1L;
	private String shakeUser;//分享者
	private String shakeTime;//分享时间
	private int praise;//赞
	private int trample;//踩
	private String fromDevice;//来自哪里
	private String title;//标题
	private String miniType;//文件类型
	private String url;//搜索结果实际url
	private String head;
	private String context;
	
	
	
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public String getShakeUser() {
		return shakeUser;
	}
	public void setShakeUser(String shakeUser) {
		this.shakeUser = shakeUser;
	}
	public String getShakeTime() {
		return shakeTime;
	}
	public void setShakeTime(String shakeTime) {
		this.shakeTime = shakeTime;
	}
	public int getPraise() {
		return praise;
	}
	public void setPraise(int praise) {
		this.praise = praise;
	}
	public int getTrample() {
		return trample;
	}
	public void setTrample(int trample) {
		this.trample = trample;
	}
	public String getFromDevice() {
		return fromDevice;
	}
	public void setFromDevice(String fromDevice) {
		this.fromDevice = fromDevice;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMiniType() {
		return miniType;
	}
	public void setMiniType(String miniType) {
		this.miniType = miniType;
	}
	
}
