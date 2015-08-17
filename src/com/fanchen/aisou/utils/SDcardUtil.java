package com.fanchen.aisou.utils;

import java.io.File;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

/**
 * SD卡相关操作工具类
 * @author fanchen
 *
 */
public class SDcardUtil {
	
	/**
	 * 获得文件路径的剩余大小
	 * 
	 * @param context
	 *            上下文
	 * @param fileDir
	 *            文件路径
	 * @return 剩余内存大小
	 */
	public static String getAvailSizeFromDir(Context context, File fileDir) {
		StatFs stat = new StatFs(fileDir.getPath());
		@SuppressWarnings("deprecation")
		long blockSize = stat.getBlockSize(); // 获得一个扇区的大小
		@SuppressWarnings("deprecation")
		long availableBlocks = stat.getAvailableBlocks(); // 获得可用的扇区数量
		return Formatter.formatFileSize(context, blockSize * availableBlocks);
	}

	/**
	 * 获得文件路径的全部大小
	 * 
	 * @param context
	 *            上下文
	 * @param fileDir
	 *            文件路径
	 * @return 剩余内存大小
	 */
	public static String getTotelSizeFromDir(Context context, File fileDir) {
		StatFs stat = new StatFs(fileDir.getPath());
		@SuppressWarnings("deprecation")
		long blockSize = stat.getBlockSize(); // 获得一个扇区的大小
		// long availableBlocks = stat.getTotalBytes(); // 获得可用的扇区数量
		return Formatter.formatFileSize(context, blockSize);
	}
	
	
	/**
	 * 判断SDCard是否可用
	 * 
	 * @return
	 */
	public static boolean isSDCardEnable(){
		
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

	}
	
	
}
