package com.fanchen.aisou.fragment;

import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fanchen.aisou.R;
import com.fanchen.aisou.adapter.PreviewAdapter;
import com.fanchen.aisou.bean.PreviewBean;
import com.fanchen.aisou.view.FlippingLoadingDialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MagnetFragment extends BaseFragment {
	
	private ListView mListView;
	private EditText mConvertEditText;
	private ImageButton mConvertImageButton;
	private PreviewAdapter mPreviewAdapter;
	private FlippingLoadingDialog mFlippingLoadingDialog;
	private LinearLayout mInfoLinearLayout;
	private Button mDownloadButton;
	private String hash;

	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0x123:
				@SuppressWarnings("unchecked")
				List<PreviewBean> beans=(List<PreviewBean>) msg.obj;
				if(beans!=null&&beans.size()>0){
					mPreviewAdapter.setData(beans);
					mFlippingLoadingDialog.dismiss();
					mInfoLinearLayout.setVisibility(View.VISIBLE);
					mConvertEditText.setText("");
				}else{
					mFlippingLoadingDialog.dismiss();
					mInfoLinearLayout.setVisibility(View.VISIBLE);
					mConvertEditText.setText("");
					showToast( "磁链转换成功，但没有搜索到种子内部文件信息");
				}
				break;
			case 0x124:
			case 0x125:
				mFlippingLoadingDialog.dismiss();
				showToast("查询出错");
				break;
			default:
				break;
			}
		};
	};
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.ib_convert:
			String magnet = mConvertEditText.getText().toString().trim();
			if(TextUtils.isEmpty(magnet)){
				showToast("请输入磁链");
				return;
			}
			if(magnet.indexOf("magnet:?xt=urn:btih:")!=0){
				showToast("请输入正确的磁链信息");
				return;
			}
			mFlippingLoadingDialog = new FlippingLoadingDialog(mainActivity, "请求提交中...");
			mFlippingLoadingDialog.show();
			hash=magnet.substring(magnet.indexOf("magnet:?xt=urn:btih:")+20);
			runPost(magnet);
			break;

		case R.id.fb_download:
			Intent mIntent=new Intent();
			mIntent.setAction("android.intent.action.VIEW");
			Uri data=Uri.parse("http://code.76lt.com/magnet-bt/api/torrent.php?hash="+hash);
			mIntent.setData(data);
			getActivity().startActivity(mIntent);
			break;
		default:
			break;
		}
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_magnet, null);
	}

	@Override
	public void findView(View v) {
		mListView=(ListView) v.findViewById(R.id.lv_preview);
		mInfoLinearLayout=(LinearLayout) v.findViewById(R.id.ll_magnet_info);
		mConvertEditText=(EditText) v.findViewById(R.id.ed_convert);
		mConvertImageButton=(ImageButton) v.findViewById(R.id.ib_convert);
		mDownloadButton=(Button) v.findViewById(R.id.fb_download);
	}

	@Override
	public void setLinsener() {
		mConvertImageButton.setOnClickListener(this);
		mDownloadButton.setOnClickListener(this);
	}

	@Override
	public void fillData(Bundle savedInstanceState) {
		mPreviewAdapter=new PreviewAdapter(mainActivity);
		mListView.setAdapter(mPreviewAdapter);
	}

	
	public void runPost(final String magnet) {
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost("http://code.76lt.com/magnet-bt/index.php");
				try {
					// 为httpPost设置HttpEntity对象
					List<NameValuePair> parameters = new ArrayList<NameValuePair>();
					parameters.add(new BasicNameValuePair("magnet",magnet));
					HttpEntity entity = new UrlEncodedFormEntity(parameters);
					post.setEntity(entity);
					// httpClient执行httpPost表单提交
					HttpResponse response = client.execute(post);
					// 得到服务器响应实体对象
					HttpEntity responseEntity = response.getEntity();
					if (responseEntity != null) {
						List<PreviewBean> beans=new ArrayList<PreviewBean>();
						Source src = new Source(EntityUtils.toString(responseEntity, "utf-8"));
						List<Element> all = src.getAllElements();
						for (Element e:all) {
							String value=e.getAttributeValue("class");
							String title=null;
							if ("table table-striped".equals(value)) {
								List<Element> allElements = e.getAllElements("tbody");
								if (allElements.get(0) != null) {
									for (Element child : allElements.get(0).getAllElements()) {
										List<Element> childElements2 = child.getChildElements();
										if (childElements2.size() >= 2) {
											if ("td".equals(childElements2.get(0).getName())) {
												PreviewBean bean = new PreviewBean();
												bean.setTitle(title == null ? "": title);
												bean.setFileName(childElements2.get(0).getTextExtractor().toString());
												bean.setSize(childElements2.get(1).getTextExtractor().toString());
												beans.add(bean);
											}
										}
									}
								}
							}
						}
						Message msg=Message.obtain();
						msg.what=0x123;
						msg.obj=beans;
						mHandler.sendMessage(msg);
						return;
					}
					mHandler.sendEmptyMessage(0x124);
				} catch (Exception e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(0x125);
				} finally {
					// 释放资源
					client.getConnectionManager().shutdown();
				}
			};
		}.start();
	}
}
