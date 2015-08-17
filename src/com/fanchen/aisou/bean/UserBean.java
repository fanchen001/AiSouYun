package com.fanchen.aisou.bean;

import cn.bmob.v3.BmobUser;

//留着以后扩展
public class UserBean extends BmobUser {
	private static final long serialVersionUID = 1L;

	private String mHead;
	private String birthday;
	private String sex ;
	private String nickName;
	private String openId;// qq登陆的用户，通过这个来登陆
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}



	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getmHead() {
		return mHead;
	}

	public void setmHead(String mHead) {
		this.mHead = mHead;
	}

}
