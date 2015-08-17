package com.fanchen.aisou.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

/**
 * 快捷序列化工具类
 * 
 * 能方便的将一个class以xml的方式存储到本地
 * 
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class XmlUtil {
	public static final String TAG="XmlUtil";
	private static final ConcurrentHashMap<Class,String> columnType_columnConverter_map;
    static {
        columnType_columnConverter_map = new ConcurrentHashMap<Class,String>();

        columnType_columnConverter_map.put(boolean.class,boolean.class.getName());
        columnType_columnConverter_map.put(Boolean.class,Boolean.class.getName());

        columnType_columnConverter_map.put( byte.class,byte.class.getName());
        columnType_columnConverter_map.put(Byte.class,Byte.class.getName());

        columnType_columnConverter_map.put( char.class,char.class.getName());
        columnType_columnConverter_map.put( Character.class,Character.class.getName());

        columnType_columnConverter_map.put( Date.class,Date.class.getName());

        columnType_columnConverter_map.put( double.class,double.class.getName());
        columnType_columnConverter_map.put( Double.class,Double.class.getName());

        columnType_columnConverter_map.put( float.class,float.class.getName());
        columnType_columnConverter_map.put( Float.class,Float.class.getName());

        columnType_columnConverter_map.put( int.class,int.class.getName());
        columnType_columnConverter_map.put( Integer.class,Integer.class.getName());

        columnType_columnConverter_map.put( long.class,long.class.getName());
        columnType_columnConverter_map.put( Long.class,Long.class.getName());

        columnType_columnConverter_map.put( short.class,short.class.getName());
        columnType_columnConverter_map.put( Short.class,Short.class.getName());

        columnType_columnConverter_map.put( String.class,String.class.getName());
        
        columnType_columnConverter_map.put( URL.class,URL.class.getName());
    }
    
    /**
     * 快捷的將一個對象以xml的方式序列化到存儲器中
     * 該方法使用默認的路徑保存，即/data/data/包名/files路徑
     * @param obj 要保存的對象
     * @param filename 保存的文件名
     * @param mContext 上下午
     * @throws Exception
     */
    public static void save(Object obj,String filename,Context mContext) throws Exception{
    	File filesDir = mContext.getFilesDir();
		File file=new File(filesDir,filename);
		if(obj instanceof List){
			Log.i(TAG, "以list方式存储数据");
			save((List<Object>)obj,file);
		}else{
			save(obj,file);
			Log.i(TAG, "存储单个数据");
		}
    }
    /**
     * 快捷的將一個對象以xml的方式序列化到存儲器中
     * 該方法允許用戶指定存儲的路徑
     * @param obj 要保存的對象
     * @param filepath 文件的絕對路徑
     * @throws Exception
     */
    public static void save(Object obj,String filepath) throws Exception{
    	save(obj,new File(filepath));
    }
	
	private static void save(Object obj,File file) throws Exception{
		//拿到序列化器
		XmlSerializer ser = Xml.newSerializer();
		OutputStream os=null;
		try {
			//設置輸出流
			os = new FileOutputStream(file);
			//設置輸出格式
			ser.setOutput(os, "UTF-8");
			ser.startDocument("UTF-8", null);
			saveOneObject(ser, obj);
			ser.endDocument();
			ser.flush();
		}catch(Exception e){
			
		}finally {
			//關閉流
            if (os != null) {
                try {
                	os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
		}
	}
	
//	/**
//	 * 快捷的將一系列的數據以xml的方式保存到本地
//	 * /data/data/包名/files路徑
//	 * @param lists 數據集
//	 * @param filename 文件名
//	 * @param mContext 上下文
//	 * @throws Exception
//	 */
//	public static void save(Object lists,String filename,Context mContext) throws Exception{
//		File filesDir = mContext.getFilesDir();
//		File file=new File(filesDir,filename);
//		save((List<Object>)lists,file);
//	}
//	
//	/**
//	 *  快捷的將一系列的數據以xml的方式保存到本地
//	 *  可以由使用者指定保存的路徑
//	 * @param lists 數據集
//	 * @param filepath 文件的絕對路徑
//	 * @throws Exception
//	 */
//	public static void save(List<Object> lists,String filepath) throws Exception{
//		save(lists,new File(filepath));
//	}
	
	private static void save(List<Object> lists,File file) throws Exception{
		XmlSerializer ser = Xml.newSerializer();
		OutputStream os=null;
		try {
			os = new FileOutputStream(file);
			//xml格式
			ser.setOutput(os, "UTF-8");
			ser.startDocument("UTF-8", null);
			ser.startTag(null, "list");
			//遍歷每一個bean，寫入數據
			for(Object obj:lists){
				saveOneObject(ser, obj);
			}
			ser.endTag(null, "list");
			ser.endDocument();
			ser.flush();
		}catch(Exception e){
			
		}finally {
            if (os != null) {
                try {
                	os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
		}
	}
	
	/**
	 * 保存一個對象到序列化
	 * @param ser 序列化器
	 * @param obj 需要序列化的對象
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 */
	private static void saveOneObject(XmlSerializer ser, Object obj)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<? extends Object> clazz = obj.getClass();
		Method[] Methods = clazz.getMethods();
		ser.startTag(null, clazz.getName());
		for(Method method:Methods){
			method.setAccessible(true);
			if(method.getName().indexOf("get")==0||method.getName().indexOf("is")==0){
				String methodName=method.getName();
				if(methodName.contains("class")){
					return;
				}
				String name=methodName.substring(3,4).toLowerCase()+method.getName().substring(4);
				ser.startTag(null, name);//設置tag名字為字段的名字
				Object invoke = method.invoke(obj, null);
				ser.text(invoke.toString());
				ser.endTag(null, name);
			}
		}
		ser.endTag(null, clazz.getName());
	}
	
	/**
	 * 從所給的文件路徑讀取數據，并將數據封裝成對應的對象數組返回給使用者
	 * @param filePath 文件絕對路徑
	 * @param clazz 要封裝的javabean的class
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> getAll(String filePath,Class<T> clazz) throws Exception{
		return getAll(new File(filePath),clazz);
	}
	
	
	/**
	 * 從所給的文件路徑讀取數據，并將數據封裝成對應的對象數組返回給使用者
	 * @param filePath 文件絕對路徑
	 * @param clazz 要封裝的javabean的class
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> getAll(String fileName,Class<T> clazz,Context mContext) throws Exception{
		File filesDir = mContext.getFilesDir();
		return getAll(new File(filesDir,fileName),clazz);
	}
	
	/**
	 * 從所給的文件讀取數據，并將數據封裝成對應的對象數組返回給使用者
	 * @param file 文件
	 * @param clazz 要封裝的javabean的class
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> getAll(File file,Class<T> clazz) throws Exception{
		XmlPullParser parser = Xml.newPullParser();
		FileInputStream is=new FileInputStream(file);
		//格式
		parser.setInput(is, "utf-8");
		//tag類型
		int type = parser.getEventType();
		T newInstance=null;
		List<T> list=new ArrayList<T>(); 
		//開始遍歷xml
		while(type!=XmlPullParser.END_DOCUMENT){
			switch (type) {
			case XmlPullParser.START_TAG:
				//如果當前的名字和類的名字相同，則說明需要從新創建一個實例
				if(clazz.getName().equals(parser.getName())){
					newInstance = clazz.newInstance();
					//創建完成后添加到列表里
					list.add(newInstance);
					break;
				}
				Field[] fields = clazz.getDeclaredFields();
				for(Field field:fields){
					field.setAccessible(true);
					String fieldName = field.getName();
					Class<?> fieldtype = field.getType();
					if(fieldName.equals(parser.getName())){
						Method setMethod = null;
					    if (fieldtype == boolean.class) {
					    	//boolean類型的方法特殊處理一下
					        setMethod = getBooleanColumnSetMethod(clazz, field);
					    }else{
					    	//拼接出對應字段的set方法名稱
					    	String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					        try {
					        	//拿到set方法
					            setMethod = clazz.getDeclaredMethod(methodName, fieldtype);
					        } catch (NoSuchMethodException e) { 
					        }
					    }
					    //將數據轉換成對應的類型
					    Object value = getValue(parser.nextText(), fieldtype);
					    //設置數值
					    setMethod.invoke(newInstance, value);
					}
				}
				break;
			}

			type = parser.next();
		}
		return list;
	}
	
	/**
	 * 將從xml中讀取出來的string類型的數據轉換成方法所需要的參數類型
	 * @param obj 數據字符串
	 * @param clazztype 方法參數類型
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static Object getValue(String obj,Class<?> clazztype){
		 String type=columnType_columnConverter_map.get(clazztype);
		 if(type.equals(int.class.getName())||type.equals(Integer.class.getName())){
			 return Integer.valueOf(obj);
		 }else if(type.equals(boolean.class.getName())||type.equals(Boolean.class.getName())){
			 return Boolean.valueOf(obj);
		 }else if(type.equals(byte.class.getName())||type.equals(Byte.class.getName())){
			 return Byte.valueOf(obj);
		 }else if(type.equals(char.class.getName())||type.equals(Character.class.getName())){
			 return obj.toCharArray();
		 }else if(type.equals(Date.class.getName())){
			 return new Date(obj);
		 }else if(type.equals(double.class.getName())||type.equals(Double.class.getName())){
			 return Double.valueOf(obj);
		 }else if(type.equals(float.class.getName())||type.equals(Float.class.getName())){
			 return Float.valueOf(obj);
		 }else if(type.equals(long.class.getName())||type.equals(Long.class.getName())){
			 return Long.valueOf(obj);
		 }else if(type.equals(short.class.getName())||type.equals(Short.class.getName())){
			 return Short.valueOf(obj);
		 }else if(type.equals(String.class.getName())){
			 return obj;
		 }else if(type.equals(URL.class.getName())){
			try {
				return new URL(obj);
			} catch (MalformedURLException e) {
			}
		 }
		return null;
	 }
	
	
	private static Method getBooleanColumnSetMethod(Class<?> entityType, Field field) {
        String fieldName = field.getName();
        String methodName = null;
        //方法名的拼接
        if (isStartWithIs(field.getName())) {
            methodName = "set" + fieldName.substring(2, 3).toUpperCase() + fieldName.substring(3);
        } else {
            methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        }
        try {
            return entityType.getDeclaredMethod(methodName, field.getType());
        } catch (NoSuchMethodException e) {
        }
        return null;
    }
 
	private static boolean isStartWithIs(final String fieldName) {
		return fieldName != null && fieldName.startsWith("is");
	}
}