package com.fanchen.aisou.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import com.fanchen.aisou.bean.SearchBean;
import com.fanchen.aisou.jni.HostURL;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;
@SuppressWarnings("deprecation")
public abstract class SearchTask extends AsyncTask<String,Integer,List<SearchBean> > {
	/**
	 * 用来记录支持的网盘和参数对应表
	 */
	private static Map<String, String> map=new HashMap<String, String>();
	//超时时间
	private static final int TIMEOUT=4*1000;
	public static final int TIME_OUT_ERROR=0X321;//连接超时
	public static final int NETWOEK_ERROR=0X432;//网络错误
	public static final int UNKNOW_ERROR=0X543;//未知错误
	public static final int SUCCESS=0X654;//完成
	public static final String SEARECH_TYPE_MAGNET="MAGNET";
	public static final String SEARECH_TYPE_CLOUD="CLOUD";
	private String SEARCH_URL=HostURL.getSearchUrl(0);
	private String MAGNET_URL=HostURL.getSearchUrl(1);
	private int state;//状态
	static{
		map.put("旋风", "wp=30&ty=gn&op=xuanfeng");
		map.put("百度", "wp=15&ty=gn&op=baipan");
		map.put("华为", "wp=6&ty=gn&op=dbank");
		map.put("115", "wp=1&ty=gn&op=115");
		map.put("迅雷", "wp=4&ty=gn&op=kuaichuan");
		map.put("金山", "wp=11&ty=gn&op=kuaipan");
		map.put("360", "wp=3&ty=gn&op=360yun");
		map.put("一木禾", "wp=5&ty=gn&op=yimuhe");
		map.put("千军万马", "wp=14&ty=gn&op=qjwm");
	}
	@Override
	protected List<SearchBean> doInBackground(String... params) {
		long startTime = System.currentTimeMillis();
		int startIndex=0;
		try{
			//获取分页数
			startIndex=Integer.parseInt(params[params.length-1]);
		}catch(Exception e){}
		URL searchurl = null;//请求url
		try {
			if(params[0].equals(SEARECH_TYPE_CLOUD)){
				searchurl = new URL(SEARCH_URL+map.get(params[1])+"&q="+URLEncoder.encode(params[2])+"&start="+startIndex);
			}else if(params[0].equals(SEARECH_TYPE_MAGNET)){
				searchurl = new URL(MAGNET_URL+URLEncoder.encode(params[2])+"&p="+startIndex+"&order=");
			}
			HttpURLConnection mconn = (HttpURLConnection) searchurl.openConnection();
			mconn.setConnectTimeout(TIMEOUT);
			mconn.setReadTimeout(TIMEOUT);
			if (mconn.getResponseCode() == 200) {
				InputStream is = mconn.getInputStream();
				Source src = new Source(is);
				List<Element> allElements = src.getAllElements();
				List<SearchBean> searchBeans=null;
				if(params[0].equals(SEARECH_TYPE_CLOUD)){
					searchBeans = getSearchBean(allElements);
				}else if(params[0].equals(SEARECH_TYPE_MAGNET)){
					searchBeans=getMagnetBean(allElements);
				}
				// 如果连接的时间太短，让线程睡一段时间
				long dTime = System.currentTimeMillis() - startTime;
				if (dTime < 1800)
					SystemClock.sleep(1800 - dTime);
				state = SUCCESS;
				return searchBeans;
			}
		} catch (MalformedURLException e) {
			state = TIME_OUT_ERROR;
			e.printStackTrace();
		} catch (IOException e) {
			state = NETWOEK_ERROR;
			e.printStackTrace();
		}
		//如果请求时间太短，线程睡眠一下
		long dTime = System.currentTimeMillis() - startTime;
		if (dTime < 1800)
			SystemClock.sleep(1800 - dTime);
		return null;
	}
	
	/**
	 * 获取关心得数据
	 * @param lists
	 * @return
	 */
	private List<SearchBean> getSearchBean(List<Element> lists) {
		if(lists==null)
			return null;
		List<SearchBean> mSearchBeans=new ArrayList<SearchBean>();
		SearchBean bean=null;
		for(Element ce:lists){
			String attributeValue = ce.getAttributeValue("class");
			if("cse-search-result_content_item_top_a".equals(attributeValue)){
//				List<Element> elements = ce.getAllElements();
//				for (Element el:elements) {
//					String value = el.getAttributeValue("class");
//					if("cse-search-result_content_item_top_a".equals(value)){
						String value2 = ce.getAttributeValue("href");//拿到url
						bean=new SearchBean();
						bean.setUrl(value2);
//					}
//				}
				String title = ce.getTextExtractor().toString();//拿到标题
				int type = ConstValue.getMineType(title);//通过标题判断文件类型（伪）
				bean.setMiniType(type);
				bean.setTitle(title);
			}else if("cse-search-result_content_item_mid".equals(attributeValue)){
				bean.setContent(ce.getTextExtractor().toString());//拿到内容
			}else if("cse-search-result_content_item_bottom".equals(attributeValue)){
				bean.setLinkContent(ce.getTextExtractor().toString());//拿到连接内容
				if(bean!=null){
					if(TextUtils.isEmpty(bean.getTitle())||TextUtils.isEmpty(bean.getContent())||TextUtils.isEmpty(bean.getLinkContent())||TextUtils.isEmpty(bean.getTitle())){
						
					}else{
						mSearchBeans.add(bean);
					}
				}
			}
		}
		
		
		return mSearchBeans;
	}
	
	
	public List<SearchBean> getMagnetBean(List<Element> lists){
		if(lists==null)
			return null;
		List<SearchBean> mSearchBeans=new ArrayList<SearchBean>();
		SearchBean bean=null;
		int count=0;
		for(Element ce:lists){
			String attributeValue = ce.getAttributeValue("class");
			if("torrent_name_tbl".equals(attributeValue)){
				if(count%2==0){
					bean=new SearchBean();
					bean.setTitle(ce.getTextExtractor().toString());
					bean.setMiniType(ConstValue.getMineType(bean.getTitle()));
					mSearchBeans.add(bean);
				}else{
					bean.setContent(ce.getTextExtractor().toString());
				}
				count++;
			}
			if("snippet".equals(attributeValue)){
				bean.setLinkContent(ce.getTextExtractor().toString());
			}
			String attributeValue1 = ce.getAttributeValue("class");
			
			if("torrent_name".equals(attributeValue1)){
				List<Element> temp = ce.getAllElements();
				if(temp==null){
				}else{
					String av = temp.get(1).getAttributeValue("href");
					if(av!=null){
						av=av.substring(av.indexOf("hash=")+5);
						bean.setUrl("magnet:?xt=urn:btih:"+av);
					}
				}
			}
		}
		return mSearchBeans;
	}
	
	@Override
	protected void onPostExecute(List<SearchBean> result) {
		if (result != null) {
			if (state == SUCCESS) {
				if(result.size()>0)
					onResult(result);
				else
					onNotData();
			}else if(state==NETWOEK_ERROR){
				onError(NETWOEK_ERROR);
			}else if(state == TIME_OUT_ERROR){
				onError(TIME_OUT_ERROR);
			}
		}else{
			onError(UNKNOW_ERROR);
		}
	}
	/**
	 * 出现错误
	 * @param errorCode 错误码
	 */
	public  abstract void onError(int errorCode);
	/**
	 * 没有数据的错误
	 */
	public  abstract void onNotData();
	/**
	 * 拿到数据
	 * @param result 数据
	 */ 
	public abstract void onResult(List<SearchBean> result);
	

}
