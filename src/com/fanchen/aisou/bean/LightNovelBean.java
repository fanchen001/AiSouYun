package com.fanchen.aisou.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class LightNovelBean implements Parcelable {

	private String image;
	private String url;
	private String title;
	private String info;
	private String introduction;
	private String updateTime;
	private String state;
	private String author;

	public LightNovelBean(){
	}
	
	public LightNovelBean(Parcel in) {
		image = in.readString();
		url = in.readString();
		title = in.readString();
		info = in.readString();
		introduction = in.readString();
		updateTime = in.readString();
		state = in.readString();
		author = in.readString();
	}
	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(image);
		dest.writeString(url);
		dest.writeString(title);
		dest.writeString(info);
		dest.writeString(introduction);
		dest.writeString(updateTime);
		dest.writeString(state);
		dest.writeString(author);
	}

	public static final Parcelable.Creator<LightNovelBean> CREATOR = new Creator<LightNovelBean>() {
		@Override
		public LightNovelBean[] newArray(int size) {
			return new LightNovelBean[size];
		}

		@Override
		public LightNovelBean createFromParcel(Parcel in) {
			return new LightNovelBean(in);
		}
	};

}
