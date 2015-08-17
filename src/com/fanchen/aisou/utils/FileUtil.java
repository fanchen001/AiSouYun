package com.fanchen.aisou.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

/**
 * 文件操作相关工具类
 * @author fanchen
 *
 */
public class FileUtil {
	 /** 默认下载图片文件目录. */
//	private static  String imageDownloadDir = null ;

	public static File updateDir = null;
	public static File updateFile = null;
	/***********保存升级APK的目录***********/
	public static final String APK_PATH = "aisou/updata";
	
	public static boolean isCreateFileSucess;
	 /**
     * 获取文件名（不含后缀）.
     *
     * @param url 文件地址
     * @return 文件名
     */
    public static String getCacheFileNameFromUrl(String url){
        if(TextUtils.isEmpty(url)){
            return null;
        }
        String name = null;
        try {
            name = SecurityUtil.encode(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }
    
    /**
	 * 获取文件后缀，本地和网络.
	 *
	 * @param url 文件地址
	 * @param response the response
	 * @return 文件后缀
	 */
    public static String getMIMEFromUrl(String url,HttpResponse response){
        
        if(TextUtils.isEmpty(url)){
            return null;
        }
        String mime = null;
        try {
            //获取后缀
            if(url.lastIndexOf(".")!=-1){
                mime = url.substring(url.lastIndexOf("."));
                 if(mime.indexOf("/")!=-1 || mime.indexOf("?")!=-1 || mime.indexOf("&")!=-1){
                     mime = null;
                 }
            }
            if(TextUtils.isEmpty(mime)){
                 //获取文件名  这个效率不高
                 String fileName = getRealFileName(response);
                 if(fileName!=null && fileName.lastIndexOf(".")!=-1){
                     mime = fileName.substring(fileName.lastIndexOf("."));
                 }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mime;
    }
    /**
	 * 获取真实文件名（xx.后缀），通过网络获取.
	 *
	 * @param response the response
	 * @return 文件名
	 */
    public static String getRealFileName(HttpResponse response){
        String name = null;
        try {
            if(response == null){
                return name;
            }
            //获取文件名
            Header[] headers = response.getHeaders("content-disposition");
            for(int i=0;i<headers.length;i++){
                 Matcher m = Pattern.compile(".*filename=(.*)").matcher(headers[i].getValue());
                 if (m.find()){
                     name =  m.group(1).replace("\"", "");
                 }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }
	/**
	 * 获取真实文件名（xx.后缀），通过网络获取.
	 * @param connection 连接
	 * @return 文件名
	 */
	public static String getRealFileName(HttpURLConnection connection){
		String name = null;
		try {
			if(connection == null){
				return name;
			}
			if (connection.getResponseCode() == 200){
				for (int i = 0;; i++) {
						String mime = connection.getHeaderField(i);
						if (mime == null){
							break;
						}
						if ("content-disposition".equals(connection.getHeaderFieldKey(i).toLowerCase())) {
							Matcher m = Pattern.compile(".*filename=(.*)").matcher(mime.toLowerCase());
							if (m.find()){
								return m.group(1).replace("\"", "");
							}
						}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
    }
    /**
	 * 获取文件名（.后缀），外链模式和通过网络获取.
	 *
	 * @param url 文件地址
	 * @param connection the connection
	 * @return 文件名
	 */
	public static String getCacheFileNameFromUrl(String url,HttpURLConnection connection){
		if(TextUtils.isEmpty(url)){
			return null;
		}
		String name = null;
		try {
			//获取后缀
			String suffix = getMIMEFromUrl(url,connection);
			if(TextUtils.isEmpty(suffix)){
				suffix = ".ab";
			}
			name = SecurityUtil.encode(url)+suffix;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
    }
	/**
	 * 获取文件后缀，本地.
	 *
	 * @param url 文件地址
	 * @param connection the connection
	 * @return 文件后缀
	 */
	public static String getMIMEFromUrl(String url,HttpURLConnection connection){
		
		if(TextUtils.isEmpty(url)){
			return null;
		}
		String suffix = null;
		try {
			//获取后缀
			if(url.lastIndexOf(".")!=-1){
				 suffix = url.substring(url.lastIndexOf("."));
				 if(suffix.indexOf("/")!=-1 || suffix.indexOf("?")!=-1 || suffix.indexOf("&")!=-1){
					 suffix = null;
				 }
			}
			if(TextUtils.isEmpty(suffix)){
				 //获取文件名  这个效率不高
				 String fileName = getRealFileName(connection);
				 if(fileName!=null && fileName.lastIndexOf(".")!=-1){
					 suffix = fileName.substring(fileName.lastIndexOf("."));
				 }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return suffix;
    }
    /**
	 * 获取文件名（.后缀），外链模式和通过网络获取.
	 *
	 * @param url 文件地址
	 * @param response the response
	 * @return 文件名
	 */
    public static String getCacheFileNameFromUrl(String url,HttpResponse response){
        if(TextUtils.isEmpty(url)){
            return null;
        }
        String name = null;
        try {
            //获取后缀
            String suffix = getMIMEFromUrl(url,response);
            if(TextUtils.isEmpty(suffix)){
                suffix = ".ab";
            }
            name = SecurityUtil.encode(url)+suffix;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }
	/**
 	 * 下载网络文件到SD卡中.如果SD中存在同名文件将不再下载
 	 *
 	 * @param url 要下载文件的网络地址
 	 * @param dirPath the dir path
 	 * @return 下载好的本地文件地址
 	 */
 	 public static String downloadFile(String url,String dir,String fileName){
 		 InputStream in = null;
 		 FileOutputStream fileOutputStream = null;
 		 HttpURLConnection connection = null;
 		 String downFilePath = null;
 		 File file = null;
 		 try {
 	    	if(!SDcardUtil.isSDCardEnable()){
 	    		return null;
 	    	}
             //先判断SD卡中有没有这个文件，不比较后缀部分比较
             String fileNameNoMIME  = getCacheFileNameFromUrl(url);
//             if(TextUtils.isEmpty(imageDownloadDir)){
//            	 imageDownloadDir = Environment.getExternalStorageDirectory()+ dir;
//             }
             File parentFile = new File(Environment.getExternalStorageDirectory()+ dir);
             if(!parentFile.exists()){
            	 parentFile.mkdirs();
             }
             File[] files = parentFile.listFiles();
             for(int i = 0; i < files.length; ++i){
//                  String fileName = files[i].getName();
                  String name = fileName.substring(0,fileName.lastIndexOf("."));
                  if(name.equals(fileNameNoMIME)){
                      //文件已存在
                      return files[i].getPath();
                  }
             } 
             
 			URL mUrl = new URL(url);
 			connection = (HttpURLConnection)mUrl.openConnection();
 			connection.connect();
            //获取文件名，下载文件
//            String fileName  = getCacheFileNameFromUrl(url,connection);
             
            file = new File(Environment.getExternalStorageDirectory()+ dir,fileName);
            downFilePath = file.getPath();
            if(!file.exists()){
                file.createNewFile();
            }else{
                //文件已存在
                return file.getPath();
            }
 			in = connection.getInputStream();
 			fileOutputStream = new FileOutputStream(file);
 			byte[] b = new byte[1024];
 			int temp = 0;
 			while((temp=in.read(b))!=-1){
 				fileOutputStream.write(b, 0, temp);
 			}
 		}catch(Exception e){
 			e.printStackTrace();
 			//检查文件大小,如果文件为0B说明网络不好没有下载成功，要将建立的空文件删除
 			if(file != null){
 				file.delete();
 			}
 			file = null;
 			downFilePath = null;
 		}finally{
 			try {
 				if(in!=null){
 					in.close();
 				}
 			} catch (Exception e) {
 				e.printStackTrace();
 			}
 			try {
 				if(fileOutputStream!=null){
 					fileOutputStream.close();
 				}
 			} catch (Exception e) {
 				e.printStackTrace();
 			}
 			try {
 				if(connection!=null){
 				    connection.disconnect();
 				}
 			} catch (Exception e) {
 				e.printStackTrace();
 			}
 		}
 		return downFilePath;
 	 }
	/** 
	* 方法描述：createFile方法
	* @param   String app_name
	* @return 
	* @see FileUtil
	*/
	public static void createFile(String app_name) {
		
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
			isCreateFileSucess = true;
			
			updateDir = new File(Environment.getExternalStorageDirectory()+ "/" + APK_PATH +"/");
			updateFile = new File(updateDir + "/" + app_name );

			if (!updateDir.exists()) {
				updateDir.mkdirs();
			}
			if (!updateFile.exists()) {
				try {
					updateFile.createNewFile();
				} catch (IOException e) {
					isCreateFileSucess = false;
					e.printStackTrace();
				}
			}

		}else{
			isCreateFileSucess = false;
		}
	}
	/**
	 * 文本的写入操作
	 * 
	 * @param filePath
	 *            文件路径。一定要加上文件名字 <br>
	 *            例如：../a/a.txt
	 * @param content
	 *            写入内容
	 */
	public static void write(String filePath, String content) {
		BufferedWriter bufw = null;
		try {
			bufw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath)));
			bufw.write(content);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (bufw != null) {
				try {
					bufw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 文本的读取操作
	 * 
	 * @param path
	 *            文件路径,一定要加上文件名字<br>
	 *            例如：../a/a.txt
	 * @return
	 */
	public static String read(String path) {
		BufferedReader bufr = null;
		try {
			bufr = new BufferedReader(new InputStreamReader(
					new FileInputStream(path)));
			StringBuffer sb = new StringBuffer();
			String str = null;
			while ((str = bufr.readLine()) != null) {
				sb.append(str);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufr != null) {
				try {
					bufr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 文本的读取操作
	 * 
	 * @param path
	 *            文件路径,一定要加上文件名字<br>
	 *            例如：../a/a.txt
	 * @return
	 */
	public static String read(InputStream is) {
		BufferedReader bufr = null;
		try {
			bufr = new BufferedReader(new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();
			String str = null;
			while ((str = bufr.readLine()) != null) {
				sb.append(str);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufr != null) {
				try {
					bufr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	private static final String TAG = "FileUtil";
	// 文件保存路径
		public static String filePath = android.os.Environment
				.getExternalStorageDirectory() + "/WWJBlog";

		public static String getFileName(String str) {
			// 去除url中的符号作为文件名返回
			str = str.replaceAll("(?i)[^a-zA-Z0-9\u4E00-\u9FA5]", "");
			return str + ".png";
		}

		/**
		 * 保存文件到SD卡中
		 * 
		 * @param filename
		 *            文件名
		 * @param inputStream
		 *            输入流
		 */
		public static void writeSDCard(String filename, InputStream inputStream) {
			try {
				File file = new File(filePath);
				if (!file.exists()) {
					file.mkdirs();
				}

				FileOutputStream fileOutputStream = new FileOutputStream(filePath
						+ "/" + filename);
				byte[] buffer = new byte[512];
				int count = 0;
				while ((count = inputStream.read(buffer)) > 0) {
					fileOutputStream.write(buffer, 0, count);// 写入缓冲区
				}
				fileOutputStream.flush();// 写入文件
				fileOutputStream.close();// 关闭文件输出流
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public static boolean writeSDCard(String fileName, Bitmap bmp) {
			try {
				File file = new File(filePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				InputStream is = bitmap2InputStream(bmp);

				FileOutputStream fileOutputStream = new FileOutputStream(filePath
						+ "/" + getFileName(fileName));
				byte[] buffer = new byte[512];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fileOutputStream.write(buffer, 0, count);
				}
				fileOutputStream.flush();
				fileOutputStream.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		/**
		 * Bitmap转换为byte[]
		 * 
		 * @param bm
		 * @return
		 */
		public static byte[] bitmap2Bytes(Bitmap bm) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			return baos.toByteArray();
		}

		/**
		 * Bitmap转换成InputStream
		 * 
		 * @param bm
		 * @return
		 */
		public static InputStream bitmap2InputStream(Bitmap bm) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			return is;
		}

		public static String getFileContent(Context context, String file) {
			String content = "";
			try {
				// 把数据从文件中读入内存
				InputStream is = context.getResources().getAssets().open(file);
				ByteArrayOutputStream bs = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int i = is.read(buffer, 0, buffer.length);
				while (i > 0) {
					bs.write(buffer, 0, i);
					i = is.read(buffer, 0, buffer.length);
				}
				content = new String(bs.toByteArray(), Charset.forName("utf-8"));
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
			return content;
		}
	/**
	 * 文件拷贝的方法，从应用程序包的assets目录将文件拷贝到data/data/包名/files目录下
	 * 使用新线程进行拷贝
	 * @param context 上下文
	 * @param oldFile 需要拷贝的文件名
	 */
	public static void copyFile(final Context context,final String oldFile){
		//得到文件
		File file = new File(context.getFilesDir(), oldFile);
		//如果文件存在，什么都不做
		if(file.exists()){
			Log.i(TAG, "文件已存在无需复制");
			return;
		}
		//开启新线程
		new Thread(){
			public void run() {
				FileOutputStream fos=null;
				InputStream is=null;
				try {
					//得到输入流
					is = context.getAssets().open(oldFile);
					File file = new File(context.getFilesDir(), oldFile);
					fos = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int len = 0;
					//开始复制
					while ((len = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}
					Log.i(TAG, "文件复制完成");
				} catch (Exception e) {
					e.printStackTrace();
					Log.i(TAG, "文件复制出错");
				}finally{
					if(fos!=null)
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					if(is!=null)
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			};
		}.start();
	}
	
	/** 
     * 获取文件夹大小 
     * （可能是个耗时操作，建议在子线程里面完成）
     * @param file File实例 
     * @return long 单位为M 
     * @throws Exception 
     */  
    public static long getFolderSize(File file)throws Exception{  
        long size = 0;  
        java.io.File[] fileList = file.listFiles();  
        for (int i = 0; i < fileList.length; i++)  
        {  
            if (fileList[i].isDirectory())  
            {  
                size = size + getFolderSize(fileList[i]);  
            } else  
            {  
                size = size + fileList[i].length();  
            }  
        }  
        return size/1048576;  
    }  
 
/** 
     * 删除指定目录下文件及目录 
     *  （可能是个耗时操作，建议在子线程里面完成）
     * @param deleteThisPath  要删除的目录的绝对路径
     * @param filepath 是否删除这个目录
     * @return 
      * @throws Exception 当指定的路径不是文件夹而是普通文件的时候抛出该异常
     */  
    public static void deleteFolderFile( String filePath,  boolean deleteThisPath)  
    		throws Exception {
		if (!TextUtils.isEmpty(filePath)) {
			File file = new File(filePath);
			deleteFolderFile(file,deleteThisPath);
		}
	};
	
	/** 
     * 删除指定目录下文件及目录 
     *  （可能是个耗时操作，建议在子线程里面完成）
     * @param deleteThisPath  要删除的目录
     * @param filepath 是否删除这个目录
     * @return 
	 * @throws Exception 当指定的路径不是文件夹而是普通文件的时候抛出该异常
     */ 
	 public static void deleteFolderFile( File dir,boolean deleteThisPath ) throws Exception{
			if (dir.isDirectory()) {// 处理目录
				File files[] = dir.listFiles();
				for (int i = 0; i < files.length; i++) {
					try {
						deleteFolderFile(files[i].getAbsolutePath(), true);
					} catch (IOException e) {
					}
				}
				if (deleteThisPath) {
					if (!dir.isDirectory()) {// 如果是文件，删除
						dir.delete();
					} else {// 目录
						if (dir.listFiles().length == 0) {// 目录下没有文件或者目录，删除
							dir.delete();
						}
					}
				}
			}else{
				dir.delete();
			}
	 }
	
	/** 
     * 删除指定目录下文件及目录 ,该操作不会删除当前目录，仅会删除目录内的文件或文件夹
     *  （可能是个耗时操作，建议在子线程里面完成）
     * @param deleteThisPath  要删除的目录的绝对路径
      * @throws Exception 当指定的路径不是文件夹而是普通文件的时候抛出该异常
     */  
	public static void deleteFolderFile(String filePath) throws Exception{
		deleteFolderFile(filePath,false);
	}
	
	/** 
     * 删除指定目录下文件及目录 ,该操作不会删除当前目录，仅会删除目录内的文件或文件夹
     *  （可能是个耗时操作，建议在子线程里面完成）
     * @param deleteThisPath  要删除的目录
      * @throws Exception 当指定的路径不是文件夹而是普通文件的时候抛出该异常
     */
	 public static void deleteFolderFile( File dir) throws Exception{
		 deleteFolderFile(dir,false);
	 }
	
	 /**
	  * 通过一个输入流来保存文件到指定的目录
	  * @param in 要保存文件的输入流
	  * @param file 保存文件的
	  */
	public static void saveFile(InputStream in,File file) {
		byte [] buff=new byte[1024];
		int len=-1;
		OutputStream os=null;
		try {
			os = new FileOutputStream(file);
			while((len=in.read(buff))!=-1){
				os.write(buff, 0, len);
			}
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(in!=null)
				try {
					in.close();
				} catch (IOException e) {
				}
			if(os!=null)
				try {
					os.close();
				} catch (IOException e) {
				}
		}
	}
	
	/**
	 * 创建根文件目录
	 * 
	 * @param fileName
	 * @return
	 */
	public static File CreateRootFile(String fileName) {

		// 得到SD卡根目录
		String path = Environment.getExternalStorageDirectory().getPath() 
						+ "/" + fileName;

		// 创建文件根路径
		File rootFile = new File(path);
		if (!rootFile.exists()) {
			rootFile.mkdirs();
		}

		return rootFile;
	}

	/**
	 * 创建二级文件目录
	 * 
	 * @param rootFileName
	 * @param subFileName
	 * @return
	 */
	public static File createSecondaryFile(String rootFileName,
			String SecondaryFileName) {

		File rootFile = FileUtil.CreateRootFile(rootFileName);

		// 创建文件子路径
		String subPath = rootFile + "/" + SecondaryFileName;
		File subFile = new File(subPath);
		if (!subFile.exists()) {
			subFile.mkdirs();
		}

		return subFile;
	}

	/**
	 * 遍历接收一个文件路径，然后把文件子目录中的所有文件遍历并输出来
	 * 
	 * @param root
	 * @return
	 */
	public static ArrayList<String> getAllFiles(File filePath) {

		File[] files = filePath.listFiles();
		ArrayList<String> paths = new ArrayList<String>();

		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					paths.addAll(getAllFiles(file));
				} else {
					paths.add(file.toString());
				}
			}
		}
		return paths;
	}
//	/**
//	 * 保存文件到SD卡中
//	 * 
//	 * @param filename
//	 *            文件名
//	 * @param inputStream
//	 *            输入流
//	 */
//	public static void writeSDCard(String filename, InputStream inputStream) {
//		try {
//			File file = new File(filePath);
//			if (!file.exists()) {
//				file.mkdirs();
//			}
//
//			FileOutputStream fileOutputStream = new FileOutputStream(filePath
//					+ "/" + filename);
//			byte[] buffer = new byte[512];
//			int count = 0;
//			while ((count = inputStream.read(buffer)) > 0) {
//				fileOutputStream.write(buffer, 0, count);// 写入缓冲区
//			}
//			fileOutputStream.flush();// 写入文件
//			fileOutputStream.close();// 关闭文件输出流
//			inputStream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

//	public static boolean writeSDCard(String fileName, Bitmap bmp) {
//		try {
//			File file = new File(filePath);
//			if (!file.exists()) {
//				file.mkdirs();
//			}
//			InputStream is = bitmap2InputStream(bmp);
//			FileOutputStream fileOutputStream = new FileOutputStream(filePath
//					+ "/" + getFileName(fileName));
//			byte[] buffer = new byte[512];
//			int count = 0;
//			while ((count = is.read(buffer)) > 0) {
//				fileOutputStream.write(buffer, 0, count);
//			}
//			fileOutputStream.flush();
//			fileOutputStream.close();
//			is.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
	
	public static boolean writeSDCard(String fileName,String folder, Bitmap bmp) {
		try {
			File dir=new File(folder);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			InputStream is = bitmap2InputStream(bmp);
			FileOutputStream fileOutputStream = new FileOutputStream(dir+ "/" + getFileName(fileName));
			byte[] buffer = new byte[512];
			int count = 0;
			while ((count = is.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, count);
			}
			fileOutputStream.flush();
			fileOutputStream.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
