package com.fanchen.aisou.application;

import java.util.ArrayList;
import java.util.List;

import com.fanchen.aisou.exception.UnCeHandler;


import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;
/**
 * 整个aisou的应用程序上下文
 * @author Administrator
 *
 */
public class AisouApplication extends Application {
	//用来管理activity的列表,实现对程序整体异常的捕获
	private List<Activity> list = new ArrayList<Activity>(); 
	//当前应用程序fragment队列，主要是用来处理activity中的onBackPresseds事件
	private List<Fragment> mFragments=new ArrayList<Fragment>();
//	
//	private DownFileDao mDownFileDao = null;
//	private ArrayList<DownFile> mDownFileList1 = null;
//	private ArrayList<DownFile> mDownFileList2 = null;
//	private ArrayList<ArrayList<DownFile>> mGroupDownFileList = null;
//	
//	private ArrayList<ArrayList<DownFile>> mDownFileGroupList = null;
//	private String[] mDownFileGroupTitle = null;
//	public HashMap<String,AbFileDownloader> mFileDownloaders = null;
//	
//	/**
//	 * 描述：释放线程
//	 */
//	@SuppressWarnings("rawtypes")
//	public void releaseThread() {
//		 Iterator it = mFileDownloaders.entrySet().iterator();   
//		 AbFileDownloader mFileDownloader = null;
//		 while (it.hasNext()) {
//		    Map.Entry e = (Map.Entry) it.next(); 
//		    mFileDownloader = (AbFileDownloader)e.getValue();
//		    if(mFileDownloader!=null){
//		    	mFileDownloader.setFlag(false);
//		    	AbDownloadThread mDownloadThread = mFileDownloader.getThreads();
//				if(mDownloadThread!=null){
//					mDownloadThread.setFlag(false);
//					mDownloadThread = null;
//				}
//				mFileDownloader = null;
//			}
//		 }   
//	}
//	
//	
//	public void download(){
//		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//			return;
//		}
//		for (int i = 0; i < mDownFileList1.size(); i++) {
//			final DownFile mDownFile = mDownFileList1.get(i);
//			if(mDownFile.getState() == Constant.undownLoad || mDownFile.getState() == Constant.downLoadPause){
//	            //下载
//	        	AbTask mAbTask = new AbTask();
//				final AbTaskItem item = new AbTaskItem();
//				item.setListener(new AbTaskListener() {
//					@Override
//					public void update() {
//					}
//
//					@Override
//					public void get() {
//						try {
//							//检查文件总长度
//							int totalLength = AbFileUtil.getContentLengthFromUrl(mDownFile.getDownUrl());
//							mDownFile.setTotalLength(totalLength);
//							//开始下载文件
//							AbFileDownloader loader = new AbFileDownloader(AisouApplication.this,mDownFile,1);
//							mFileDownloaders.put(mDownFile.getDownUrl(), loader);
//							loader.download(null);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//				  };
//				});
//				mAbTask.execute(item);
//			}else if(mDownFile.getState()==Constant.downInProgress){
//				//暂停
//				mDownFile.setState(Constant.undownLoad);
//				AbFileDownloader mFileDownloader = mFileDownloaders.get(mDownFile.getDownUrl());
//				//释放原来的线程
//				if(mFileDownloader!=null){
//					mFileDownloader.setFlag(false);
//					AbDownloadThread mDownloadThread = mFileDownloader.getThreads();
//					if(mDownloadThread!=null){
//						mDownloadThread.setFlag(false);
//						mFileDownloaders.remove(mDownFile.getDownUrl());
//						mDownloadThread = null;
//					}
//					mFileDownloader = null;
//				}
//			}else if(mDownFile.getState()==Constant.downloadComplete){
//				//删除
//				mDownFileGroupList.get(0).remove(mDownFile);
//				mDownFile.setState(Constant.undownLoad);
//				mDownFileGroupList.get(1).add(mDownFile);
//			}
//		}
//		
//	}
//	
//	
    
    public void init(){  
        //设置该CrashHandler为程序的默认处理器    
        UnCeHandler catchExcep = new UnCeHandler(this);  
        Thread.setDefaultUncaughtExceptionHandler(catchExcep);   
    }  
    /** 
     *向fragment队列添加一个*/  
    public void addFragment(Fragment f){
    	mFragments.add(f);
    }
    
    /** 
     *获取fragment队列倒数第二个*/  
    public Fragment getTop2Fragment(){
    	if(mFragments.size()>=2)
    		return mFragments.remove(mFragments.size()-2);
    	return null;
    }
    /** 
     *弹出最上层的fragment*/  
    public Fragment popuFragment(){
    	if(mFragments.size()>=1)
    		return mFragments.remove(mFragments.size()-1);
    	return null;
    }
    /** 
     *获取最上层的fragment*/  
    public Fragment getTopFragment(){
    	if(mFragments.size()>=1)
    		return mFragments.get(mFragments.size()-1);
    	return null;
    }
    /** 
     *清空fragment队列*/  
    public void clearFragment(){
    	mFragments.clear();
    }
      
    /** 
     * Activity关闭时，删除Activity列表中的Activity对象*/  
    public void removeActivity(Activity a){  
        list.remove(a);  
    }  
      
    /** 
     * 向Activity列表中添加Activity对象*/  
    public void addActivity(Activity a){  
        list.add(a);   
    }  
      
    /** 
     * 关闭Activity列表中的所有Activity*/  
    public void finishActivity(){  
        for (Activity activity : list) {    
            if (null != activity) {    
                activity.finish();    
            }    
        }  
        //杀死该应用进程  
       android.os.Process.killProcess(android.os.Process.myPid());    
    }
}
