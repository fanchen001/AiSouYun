package com.fanchen.aisou.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

@SuppressWarnings("rawtypes")
public class SqliteUtil {
	private String CREATE_TABLE_SQL;
	//java类型常量
	private static final ConcurrentHashMap<Class, String> columnType_columnConverter_map;
	static {
		columnType_columnConverter_map = new ConcurrentHashMap<Class, String>();

		columnType_columnConverter_map.put(boolean.class,
				boolean.class.getName());
		columnType_columnConverter_map.put(Boolean.class,
				Boolean.class.getName());

		columnType_columnConverter_map.put(byte.class, byte.class.getName());
		columnType_columnConverter_map.put(Byte.class, Byte.class.getName());

		columnType_columnConverter_map.put(char.class, char.class.getName());
		columnType_columnConverter_map.put(Character.class,
				Character.class.getName());

		columnType_columnConverter_map.put(Date.class, Date.class.getName());

		columnType_columnConverter_map
				.put(double.class, double.class.getName());
		columnType_columnConverter_map
				.put(Double.class, Double.class.getName());

		columnType_columnConverter_map.put(float.class, float.class.getName());
		columnType_columnConverter_map.put(Float.class, Float.class.getName());

		columnType_columnConverter_map.put(int.class, int.class.getName());
		columnType_columnConverter_map.put(Integer.class,
				Integer.class.getName());

		columnType_columnConverter_map.put(long.class, long.class.getName());
		columnType_columnConverter_map.put(Long.class, Long.class.getName());

		columnType_columnConverter_map.put(short.class, short.class.getName());
		columnType_columnConverter_map.put(Short.class, Short.class.getName());

		columnType_columnConverter_map
				.put(String.class, String.class.getName());

		columnType_columnConverter_map.put(URL.class, URL.class.getName());
	}

	//sqlite中的数据类型
	private String[] types = new String[] { "float", "double", "char(256)",
			"varchar(4000)", "data", "int", "boolean", "long", "short" };

	public SqliteOpenHelper mOpenHelper;

	public SqliteUtil(Context mContext, String dbName,Class[] clazz, String[] tableName) {
		List<String> fieldsNames = new ArrayList<String>();
		List<String> fieldsTypes = new ArrayList<String>();
		List<String> sqls = new ArrayList<String>();
		int l=clazz.length;
		for (int i = 0; i < l; i++) {
			Field[] fields = clazz[i].getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				String fieldName = field.getName();
				Class<?> fieldtype = field.getType();
				fieldsNames.add(fieldName);
				String type = getType(fieldtype);
				fieldsTypes.add(type);
			}
			CREATE_TABLE_SQL = "CREATE TABLE " + tableName[i]+ " (_id INTEGER primary key autoincrement,";
			StringBuilder sb = new StringBuilder(CREATE_TABLE_SQL);
			for (int j = 0; j < fieldsNames.size(); j++) {
				if (j != fieldsNames.size() - 1)
					sb.append(fieldsNames.get(j) + " " + fieldsTypes.get(j) + ",");
				else
					sb.append(fieldsNames.get(j) + " " + fieldsTypes.get(j));
			}
			sb.append(");");
			sqls.add(sb.toString());
			fieldsNames.clear();
			fieldsTypes.clear();
		}
		mOpenHelper = new SqliteOpenHelper(mContext, dbName,sqls);
	}
	

	/**
	 * 将class的类型转换成sqlite里面的类型
	 * @param clazztype
	 * @return
	 */
	private String getType(Class<?> clazztype) {
		String type = columnType_columnConverter_map.get(clazztype);
		if (type.equals(int.class.getName())
				|| type.equals(Integer.class.getName())) {
			return types[5];
		} else if (type.equals(boolean.class.getName())
				|| type.equals(Boolean.class.getName())) {
			return types[6];
		} else if (type.equals(char.class.getName())
				|| type.equals(Character.class.getName())) {
			return types[2];
		} else if (type.equals(Date.class.getName())) {
			return types[4];
		} else if (type.equals(double.class.getName())
				|| type.equals(Double.class.getName())) {
			return types[1];
		} else if (type.equals(float.class.getName())
				|| type.equals(Float.class.getName())) {
			return types[0];
		} else if (type.equals(long.class.getName())
				|| type.equals(Long.class.getName())) {
			return types[7];
		} else if (type.equals(short.class.getName())
				|| type.equals(Short.class.getName())) {
			return types[8];
		} else if (type.equals(String.class.getName())) {
			return types[3];
		}
		return null;
	}

	public class SqliteOpenHelper extends SQLiteOpenHelper {
		private static final int VERSION = 1;
		private List<String> CREATE_TABLE;

		public SqliteOpenHelper(Context context, String dbName,List<String> createTable) {
			super(context, dbName, null, VERSION);
			CREATE_TABLE = createTable;
		}

		@Override
		public void onCreate(SQLiteDatabase arg0) {
			for(String s:CREATE_TABLE){
				arg0.execSQL(s);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

		}

		/**
		 * 有where 条件的查询
		 * 
		 * @param tableName
		 * @param selection
		 * @param selectionArgs
		 * @param clazz
		 */
		public <T> List<T> queryWhere(String tableName, String selection,
				String[] selectionArgs, Class<T> clazz) throws Exception {
			List<String> names = new ArrayList<String>();
			List<Class<?>> types = new ArrayList<Class<?>>();
			List<T> lists = new ArrayList<T>();
			SQLiteDatabase database = this.getReadableDatabase();
			Cursor cursor = database.query(tableName, null, selection,
					selectionArgs, null, null, null);
			Field[] fields = clazz.getDeclaredFields();
			for (Field f : fields) {
				f.setAccessible(true);
				String fieldName = f.getName();
				Class<?> fieldtype = f.getType();
				names.add(fieldName);
				types.add(fieldtype);
			}
			while (cursor.moveToNext()) {
				T instance = clazz.newInstance();
				lists.add(instance);
				for (int i = 0; i < names.size(); i++) {
					Method setMethod = null;
					if (types.get(i) == boolean.class) {
						// boolean類型的方法特殊處理一下
						setMethod = getBooleanColumnSetMethod(clazz, fields[i]);
					} else {
						// 拼接出對應字段的set方法名稱
						String methodName = "set"
								+ names.get(i).substring(0, 1).toUpperCase()
								+ names.get(i).substring(1);
						try {
							// 拿到set方法
							setMethod = clazz.getDeclaredMethod(methodName,
									types.get(i));
						} catch (NoSuchMethodException e) {
						}
					}
					// 將數據轉換成對應的類型
					Object value = getValue(cursor.getString(i + 1),
							types.get(i));
					// 設置數值
					setMethod.invoke(instance, value);
				}
			}
			cursor.close();
			database.close();
			return lists;
		}

		
		public int getTableValueCount(String table){
			SQLiteDatabase database = this.getReadableDatabase();
			Cursor cursor = database.query(table, null, null, null, null, null, null, null);
			int count = cursor.getCount();
			database.close();
			return count;
		}

		/**
		 * 查询所有
		 * 
		 * @param tableName
		 * @param clazz
		 * @return
		 * @throws Exception
		 */
		public <T> List<T> queryAll(String tableName, Class<T> clazz)
				throws Exception {
			return queryWhere(tableName, null, null, clazz);
		}
		

		/**
		 * 快捷的将一个class存储到数据库中
		 * @param tableName 表名
		 * @param obj 需要存储的class
		 * @throws Exception
		 */
		public void insert(String tableName, Object obj) throws Exception {
			SQLiteDatabase database = this.getWritableDatabase();
			if(obj instanceof List){
				List<Object>lists=(List<Object>) obj;
				for(Object o:lists){
					saveObj(tableName,database, o);
				}
			}else{
				saveObj(tableName,database, obj);
			}
			database.close();
		}

		private void saveObj(String tableName, SQLiteDatabase database,Object obj)
				throws IllegalAccessException, InvocationTargetException {
			List<String> names = new ArrayList<String>();
			List<Object> values = new ArrayList<Object>();
			Class clazz = obj.getClass();
			Method[] Methods = clazz.getDeclaredMethods();
			for (Method m : Methods) {
				m.setAccessible(true);
				if ("getClass".equals(m.getName())) {
					break;
				}
				if (m.getName().indexOf("get") == 0) {
					String name = m.getName().substring(3, 4).toLowerCase()+ m.getName().substring(4);
					names.add(name);
					Object invoke = m.invoke(obj, null);
					values.add(invoke);
				} else if (m.getName().indexOf("is") == 0) {
					String name = m.getName().substring(2, 3).toLowerCase()+ m.getName().substring(3);
					names.add(name);
					Object invoke = m.invoke(obj, null);
					values.add(invoke);
				}
			}

			String sql = "INSERT INTO " + tableName + " (";
			StringBuilder sb = new StringBuilder(sql);
			for (int i = 0; i < names.size(); i++) {
				if (i != names.size() - 1){
					sb.append(names.get(i));
					sb.append(",");
				}else{
					sb.append(names.get(i));
					sb.append(")");
				}
			}
			sb.append(" values (");
			for (int i = 0; i < values.size(); i++) {
				if (i != names.size() - 1){
					if(values.get(i) instanceof String){
						sb.append("\"");
						sb.append(values.get(i));
						sb.append("\"");
						sb.append(",");
					}else{
						sb.append(values.get(i) + ",");
					}
				}
				else
					if(values.get(i) instanceof String){
						sb.append("\"");
						sb.append(values.get(i));
						sb.append("\"");
						sb.append(");");
					}else{
						sb.append(values.get(i) + ");");
					}
			}
			database.execSQL(sb.toString());
		}
		

		public boolean updata(String tableName,Object obj,String clause,String arg) throws Exception{
			SQLiteDatabase database = this.getWritableDatabase();
			List<String> newNames = new ArrayList<String>();
			List<Object> newValues = new ArrayList<Object>();
			Class clazz = obj.getClass();
			Method[] newMethods = clazz.getDeclaredMethods();
			for (Method m : newMethods) {
				m.setAccessible(true);
				if ("getClass".equals(m.getName())) {
					break;
				}
				if (m.getName().indexOf("get") == 0) {
					String name = m.getName().substring(3, 4).toLowerCase()+ m.getName().substring(4);
					newNames.add(name);
					Object invoke = m.invoke(obj, null);
					newValues.add(invoke);
				} else if (m.getName().indexOf("is") == 0) {
					String name = m.getName().substring(2, 3).toLowerCase()
							+ m.getName().substring(3);
					newNames.add(name);
					Object invoke = m.invoke(obj, null);
					newValues.add(invoke);
				}
			}
			ContentValues values=new ContentValues();
			for(int i=0;i<newNames.size();i++){
				values.put(newNames.get(i), newValues.get(i).toString());
			}
			
			StringBuilder sb=new StringBuilder(clause);
			sb.append("=?");
			String[] whereArgs=new String[]{arg};
			int i = database.update(tableName, values, sb.toString(), whereArgs);
			database.close();
			return i>0?true:false;
		}
		
		
		/**
		 * 快捷的从数据库更新一个class信息
		 * @param tableName 表名
		 * @param oleObj 需要修改存储的class
		 * @param newObj 新的class
		 * @return 是否更新成功
		 * @throws Exception
		 */
		public boolean updata(String tableName,Object oleObj,Object newObj) throws Exception{
			List<String> oldNames = new ArrayList<String>();
			List<Object> oldValues = new ArrayList<Object>();
			List<String> newNames = new ArrayList<String>();
			List<Object> newValues = new ArrayList<Object>();
			Class oldClazz = oleObj.getClass();
			Method[] Methods = oldClazz.getDeclaredMethods();
			for (Method m : Methods) {
				m.setAccessible(true);
				if ("getClass".equals(m.getName())) {
					break;
				}
				if (m.getName().indexOf("get") == 0) {
					String name = m.getName().substring(3, 4).toLowerCase()+ m.getName().substring(4);
					oldNames.add(name);
					Object invoke = m.invoke(oleObj, null);
					oldValues.add(invoke);
				} else if (m.getName().indexOf("is") == 0) {
					String name = m.getName().substring(2, 3).toLowerCase()
							+ m.getName().substring(3);
					oldNames.add(name);
					Object invoke = m.invoke(oleObj, null);
					oldValues.add(invoke);
				}
			}
			
			Class newClazz = newObj.getClass();
			Method[] newMethods = newClazz.getDeclaredMethods();
			for (Method m : newMethods) {
				m.setAccessible(true);
				if ("getClass".equals(m.getName())) {
					break;
				}
				if (m.getName().indexOf("get") == 0) {
					String name = m.getName().substring(3, 4).toLowerCase()+ m.getName().substring(4);
					newNames.add(name);
					Object invoke = m.invoke(oleObj, null);
					newValues.add(invoke);
				} else if (m.getName().indexOf("is") == 0) {
					String name = m.getName().substring(2, 3).toLowerCase()
							+ m.getName().substring(3);
					newNames.add(name);
					Object invoke = m.invoke(oleObj, null);
					newValues.add(invoke);
				}
			}
			
			StringBuilder sb=new StringBuilder();
			for (int i=0;i<oldNames.size();i++) {
				if(i!=oldNames.size()-1){
					sb.append(oldNames);
					sb.append("=? ");
				}else{
					sb.append(oldNames);
					sb.append("=?;");
				}
			}
			String[] whereArgs = new String[oldValues.size()];
			for (int i = 0; i < oldValues.size(); i++) {
				whereArgs[i]=oldValues.get(i).toString();
			}
			
			 ContentValues con=new ContentValues();
			 for(int i=0;i<newNames.size();i++){
				 con.put(newNames.get(i), newValues.get(i).toString());
			 }
			SQLiteDatabase database = this.getWritableDatabase();
			int updateLine = database.update(tableName, con, sb.toString(), whereArgs);
			database.close();
			return updateLine>0?true:false;
		}

		/**
		 * 快捷的从数据库中删除一个class
		 * @param tableName 表名
		 * @param obj 需要删除的class
		 * @return 是否删除成功
		 * @throws Exception
		 */
		public boolean delete(String tableName,Object obj) throws Exception{
			List<String> names = new ArrayList<String>();
			List<Object> values = new ArrayList<Object>();
			Class clazz = obj.getClass();
			Method[] Methods = clazz.getDeclaredMethods();
			for (Method m : Methods) {
				m.setAccessible(true);
				if ("getClass".equals(m.getName())) {
					break;
				}
				if (m.getName().indexOf("get") == 0) {
					String name = m.getName().substring(3, 4).toLowerCase()
							+ m.getName().substring(4);
					names.add(name);
					Object invoke = m.invoke(obj, null);
					values.add(invoke);
				} else if (m.getName().indexOf("is") == 0) {
					String name = m.getName().substring(2, 3).toLowerCase()
							+ m.getName().substring(3);
					names.add(name);
					Object invoke = m.invoke(obj, null);
					values.add(invoke);
				}
			}
			
			StringBuilder sb=new StringBuilder();
			for (int i=0;i<names.size();i++) {
				if(i!=names.size()-1){
					sb.append(names);
					sb.append("=? ");
				}else{
					sb.append(names);
					sb.append("=?;");
				}
			}
			String[] whereArgs = new String[values.size()];
			for (int i = 0; i < values.size(); i++) {
				whereArgs[i]=values.get(i).toString();
			}
			SQLiteDatabase database = this.getWritableDatabase();
			int deleteLine = database.delete(tableName, sb.toString(), whereArgs);
			database.close();
			return deleteLine>0?true:false;
		}
		
		public boolean delete(String tableName,String clause,String arg){
			SQLiteDatabase database = this.getWritableDatabase();
			StringBuilder sb=new StringBuilder(clause);
			sb.append("=?");
			String[] whereArgs=new String[]{arg};
			int i = database.delete(tableName, sb.toString(), whereArgs);
			database.close();
			return i>0?true:false;
		}
		
		public void deleteAll(String tableName){
			SQLiteDatabase database = this.getWritableDatabase();
			String sql="DELETE FROM "+tableName;
			database.execSQL(sql);
			database.close();
		}

		/**
		 * 拿到boolean类型set的方法
		 * @param entityType
		 * @param field
		 * @return
		 */
		private Method getBooleanColumnSetMethod(Class<?> entityType,
				Field field) {
			String fieldName = field.getName();
			String methodName = null;
			// 方法名的拼接
			if (isStartWithIs(field.getName())) {
				methodName = "set" + fieldName.substring(2, 3).toUpperCase()
						+ fieldName.substring(3);
			} else {
				methodName = "set" + fieldName.substring(0, 1).toUpperCase()
						+ fieldName.substring(1);
			}
			try {
				return entityType
						.getDeclaredMethod(methodName, field.getType());
			} catch (NoSuchMethodException e) {
			}
			return null;
		}

		/**
		 * 
		 * @param fieldName
		 * @return
		 */
		private boolean isStartWithIs(final String fieldName) {
			return fieldName != null && fieldName.startsWith("is");
		}

		/**
		 * 把string类型的数据转换成所需的数据类型
		 * @param obj
		 * @param clazztype
		 * @return
		 */
		@SuppressWarnings("deprecation")
		private Object getValue(String obj, Class<?> clazztype) {
			String type = columnType_columnConverter_map.get(clazztype);
			if (type.equals(int.class.getName())
					|| type.equals(Integer.class.getName())) {
				return Integer.valueOf(obj);
			} else if (type.equals(boolean.class.getName())
					|| type.equals(Boolean.class.getName())) {
				return Boolean.valueOf(obj);
			} else if (type.equals(byte.class.getName())
					|| type.equals(Byte.class.getName())) {
				return Byte.valueOf(obj);
			} else if (type.equals(char.class.getName())
					|| type.equals(Character.class.getName())) {
				return obj.toCharArray();
			} else if (type.equals(Date.class.getName())) {
				return new Date(obj);
			} else if (type.equals(double.class.getName())
					|| type.equals(Double.class.getName())) {
				return Double.valueOf(obj);
			} else if (type.equals(float.class.getName())
					|| type.equals(Float.class.getName())) {
				return Float.valueOf(obj);
			} else if (type.equals(long.class.getName())
					|| type.equals(Long.class.getName())) {
				return Long.valueOf(obj);
			} else if (type.equals(short.class.getName())
					|| type.equals(Short.class.getName())) {
				return Short.valueOf(obj);
			} else if (type.equals(String.class.getName())) {
				return obj;
			} else if (type.equals(URL.class.getName())) {
				try {
					return new URL(obj);
				} catch (MalformedURLException e) {
				}
			}
			return null;
		}

	}

}