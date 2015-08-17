package com.fanchen.aisou.bean;

public class ImageBean {

	private String abs;
	private String desc;
	private String image_url;
	private int image_width;
	private int image_height;
	private String thumbnail_url;
	private int thumbnail_width;
	private int thumbnail_height;

	public ImageBean() {

	}

	public ImageBean(String abs, String desc, String image_url,
			int image_width, int image_height, String thumbnail_url,
			int thumbnail_width, int thumbnail_height) {
		super();
		this.abs = abs;
		this.desc = desc;
		this.image_url = image_url;
		this.image_width = image_width;
		this.image_height = image_height;
		this.thumbnail_url = thumbnail_url;
		this.thumbnail_width = thumbnail_width;
		this.thumbnail_height = thumbnail_height;
	}

	public String getThumbnail_url() {
		return thumbnail_url;
	}

	public void setThumbnail_url(String thumbnail_url) {
		this.thumbnail_url = thumbnail_url;
	}

	public int getThumbnail_width() {
		return thumbnail_width;
	}

	public void setThumbnail_width(int thumbnail_width) {
		this.thumbnail_width = thumbnail_width;
	}

	public int getThumbnail_height() {
		return thumbnail_height;
	}

	public void setThumbnail_height(int thumbnail_height) {
		this.thumbnail_height = thumbnail_height;
	}

	public String getAbs() {
		return abs;
	}

	public void setAbs(String abs) {
		this.abs = abs;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public int getImage_width() {
		return image_width;
	}

	public void setImage_width(int image_width) {
		this.image_width = image_width;
	}

	public int getImage_height() {
		return image_height;
	}

	public void setImage_height(int image_height) {
		this.image_height = image_height;
	}

}
