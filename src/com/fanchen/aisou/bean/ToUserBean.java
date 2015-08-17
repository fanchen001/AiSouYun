package com.fanchen.aisou.bean;

import cn.bmob.v3.BmobObject;


/**
 * openid和用户名互相转换的类
 * @author fanchen
 *
 */
public class ToUserBean extends BmobObject{
	
	private static final long serialVersionUID = 1L;

	private String user_name;
	
	private String open_id;

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getOpen_id() {
		return open_id;
	}

	public void setOpen_id(String open_id) {
		this.open_id = open_id;
	}

	
}
