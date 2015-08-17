package com.fanchen.aisou.bean;

public class CouldTypeBean {
	
	private String couldType;
	
	private int  check;

	public CouldTypeBean(){
		
	}
	
	public CouldTypeBean(String couldType,int  check){
		this.check=check;
		this.couldType=couldType;
	}
	
	public String getCouldType() {
		return couldType;
	}

	public void setCouldType(String couldType) {
		this.couldType = couldType;
	}

	public int getCheck() {
		return check;
	}

	public void setCheck(int check) {
		this.check = check;
	}

	
	

}
