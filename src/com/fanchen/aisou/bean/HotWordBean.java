package com.fanchen.aisou.bean;
/**
 * 搜索热词实体
 * @author Administrator
 *
 */
public class HotWordBean {
	
	private int wordId;//保留的字段
	
	private String hotWord;//热词

	public String getHotWord() {
		return hotWord;
	}

	public void setHotWord(String hotWord) {
		this.hotWord = hotWord;
	}

	public int getWordId() {
		return wordId;
	}

	public void setWordId(int wordId) {
		this.wordId = wordId;
	}

	@Override
	public String toString() {
		return "HotWordBean [wordId=" + wordId + ", hotWord=" + hotWord + "]";
	}
	
	

}
