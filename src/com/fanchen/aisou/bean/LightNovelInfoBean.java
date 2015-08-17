package com.fanchen.aisou.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class LightNovelInfoBean extends LightNovelBean{
	
	private String views;
	private String from;
	private String sectionBelow;
	private String downloadUrl;
	private String sectionBelowUrl;
	private String updateIntroduction;
	private List<LightNovelBean> mCorrelationBeans;
	
	public LightNovelInfoBean(){
		
	}
	
	public LightNovelInfoBean(Parcel in){
		setImage(in.readString());
		setUrl(in.readString());
		setTitle(in.readString());
		setInfo(in.readString());
		setIntroduction(in.readString());
		setUpdateTime(in.readString());
		setState(in.readString());
		setAuthor(in.readString());
		views = in.readString();
		from = in.readString();
		sectionBelow = in.readString();
		downloadUrl = in.readString();
		sectionBelowUrl = in.readString();
		mCorrelationBeans = new ArrayList<LightNovelBean>();
		in.readTypedList(mCorrelationBeans, LightNovelBean.CREATOR);
	}
	
	public List<LightNovelBean> getmCorrelationBeans() {
		return mCorrelationBeans;
	}

	public void setmCorrelationBeans(List<LightNovelBean> mCorrelationBeans) {
		this.mCorrelationBeans = mCorrelationBeans;
	}

	public String getSectionBelowUrl() {
		return sectionBelowUrl;
	}

	public void setSectionBelowUrl(String sectionBelowUrl) {
		this.sectionBelowUrl = sectionBelowUrl;
	}

	public String getSectionBelow() {
		return sectionBelow;
	}

	public void setSectionBelow(String sectionBelow) {
		this.sectionBelow = sectionBelow;
	}

	public String getViews() {
		return views;
	}

	public void setViews(String views) {
		this.views = views;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getUpdateIntroduction() {
		return updateIntroduction;
	}

	public void setUpdateIntroduction(String updateIntroduction) {
		this.updateIntroduction = updateIntroduction;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getImage());
		dest.writeString(getUrl());
		dest.writeString(getTitle());
		dest.writeString(getInfo());
		dest.writeString(getIntroduction());
		dest.writeString(getUpdateTime());
		dest.writeString(getState());
		dest.writeString(getAuthor());
		dest.writeString(views);
		dest.writeString(from);
		dest.writeString(sectionBelow);
		dest.writeString(downloadUrl);
		dest.writeString(sectionBelowUrl);
		dest.writeString(updateIntroduction);
		dest.writeTypedList(mCorrelationBeans);
	}

	public static final Parcelable.Creator<LightNovelInfoBean> CREATOR = new Creator<LightNovelInfoBean>() {
		@Override
		public LightNovelInfoBean[] newArray(int size) {
			return new LightNovelInfoBean[size];
		}

		@Override
		public LightNovelInfoBean createFromParcel(Parcel in) {
			return new LightNovelInfoBean(in);
		}
	};

}
