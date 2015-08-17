package com.fanchen.aisou.utils;


import com.fanchen.aisou.R;

public class ConstValue {

	public static final int MINE_TYPE_ZIP = R.drawable.zip;
	public static final int MINE_TYPE_PICTURE = R.drawable.picture;
	public static final int MINE_TYPE_MUSIC = R.drawable.music;
	public static final int MINE_TYPE_VIDEO = R.drawable.video;
	public static final int MINE_TYPE_DOC = R.drawable.word;
	public static final int MINE_TYPE_PDF = R.drawable.pdf;
	public static final int MINE_TYPE_ECXEL = R.drawable.excel;
	public static final int MINE_TYPE_TEXT = R.drawable.text;
	public static final int MINE_TYPE_UNKNOW = R.drawable.unknown;
	public static final int MINE_TYPE_FOLDER = R.drawable.folder1;
	public static final int MINE_TYPE_PPT = R.drawable.ppt;

	public static final String SD_DIR_PATH = "";

	public static int getMineType(String substring) {
		substring = substring.toLowerCase();
		if (substring.indexOf("mp3") != -1 || substring.indexOf("m4a") != -1
				|| substring.indexOf("flac") != -1
				|| substring.indexOf("wma") != -1
				|| substring.indexOf("ogg") != -1) {
			return MINE_TYPE_MUSIC;
		} else if (substring.indexOf("mp4") != -1
				|| substring.indexOf("rmvb") != -1
				|| substring.indexOf("avi") != -1
				|| substring.indexOf("rm") != -1) {
			return MINE_TYPE_VIDEO;
		} else if (substring.indexOf("rar") != -1
				|| substring.indexOf("zip") != -1
				|| substring.indexOf("7z") != -1) {
			return MINE_TYPE_ZIP;
		} else if (substring.indexOf("doc") != -1
				|| substring.indexOf("docx") != -1) {
			return MINE_TYPE_DOC;
		} else if (substring.indexOf("xls") != -1
				|| substring.indexOf("xlsx") != -1) {
			return MINE_TYPE_ECXEL;
		} else if (substring.indexOf("txt") != -1
				|| substring.indexOf("java") != -1
				|| substring.indexOf("css") != -1
				|| substring.indexOf("html") != -1
				|| substring.indexOf("js") != -1
				|| substring.indexOf("cpp") != -1) {
			return MINE_TYPE_TEXT;
		} else if (substring.indexOf("ppt") != -1
				|| substring.indexOf("pptx") != -1) {
			return MINE_TYPE_PPT;
		} else if (substring.indexOf("gif") != -1
				|| substring.indexOf("png") != -1
				|| substring.indexOf("jpg") != -1
				|| substring.indexOf("bmp") != -1) {
			return MINE_TYPE_PICTURE;
		} else if (substring.indexOf("pdf") != -1) {
			return MINE_TYPE_PDF;
		} else if (substring.indexOf("文件夹") != -1) {
			return MINE_TYPE_FOLDER;
		} else {
			return MINE_TYPE_UNKNOW;
		}
	}
	
}
