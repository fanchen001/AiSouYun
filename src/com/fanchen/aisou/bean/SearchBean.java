package com.fanchen.aisou.bean;
/**
 * 搜索结果实体
 * @author Administrator
 *
 */
public class SearchBean {
	private String title;//标题
	private String content;//内容
	private String linkContent;//下方显示的连接内容
	private int miniType;//文件类型
	private String url;//搜索结果实际url
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLinkContent() {
		return linkContent;
	}
	public void setLinkContent(String linkContent) {
		this.linkContent = linkContent;
	}
	public int getMiniType() {
		return miniType;
	}
	public void setMiniType(int miniType) {
		this.miniType = miniType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "SearchBean [title=" + title + ", content=" + content
				+ ", linkContent=" + linkContent + ", miniType=" + miniType
				+ ", url=" + url + "]";
	}

}
