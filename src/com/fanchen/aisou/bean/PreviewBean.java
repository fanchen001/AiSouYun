package com.fanchen.aisou.bean;

public class PreviewBean {

	private String title;
	private String fileName;
	private String size;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String toString() {
		return "PreviewBean [title=" + title + ", fileName=" + fileName
				+ ", size=" + size + "]";
	}
	
	
	
}
