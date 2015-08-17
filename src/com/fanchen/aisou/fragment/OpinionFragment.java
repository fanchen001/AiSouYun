package com.fanchen.aisou.fragment;


import com.fanchen.aisou.R;
import com.fanchen.aisou.mail.MultiMailsender;
import com.fanchen.aisou.mail.MultiMailsender.MultiMailSenderInfo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class OpinionFragment extends BaseFragment {
	
	private EditText mContentEditText;//意见内容输入框
	private EditText mMoreEditText;//更多信息输入框
	private Button mSendButton;//发送按钮
	private Button mClearButton;//清空按钮
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.fb_mail_clear:
			//清空信息
			mContentEditText.setText("");
			mMoreEditText.setText("");
			break;
		case R.id.fb_mail_send:
			//发送邮件
			String content = mContentEditText.getText().toString().trim();
			String more = mMoreEditText.getText().toString().trim();
			if(TextUtils.isEmpty(content)){
				showToast("请输入你的建议或者意见");
				return;
			}
			showToast( "正在发送，请稍后...");
			if(!TextUtils.isEmpty(content)){
				sendMail("\t"+content+"\n\n\t联系方式："+more);
				return;
			}
			sendMail("\t"+content);
			break;
		default:
			break;
		}
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_opinion, null);
	}

	@Override
	public void findView(View v) {
		mSendButton=(Button) v.findViewById(R.id.fb_mail_send);
		mClearButton=(Button) v.findViewById(R.id.fb_mail_clear);
		mContentEditText=(EditText) v.findViewById(R.id.ed_opinion_content);
		mMoreEditText=(EditText) v.findViewById(R.id.ed_opinion_more);
	}

	@Override
	public void setLinsener() {
		mSendButton.setOnClickListener(this);
		mClearButton.setOnClickListener(this);
	}

	@Override
	public void fillData(Bundle savedInstanceState) {
		
	}
	
	/**
	 * 发送邮件
	 * 使用新线程
	 * @param word 邮件内容
	 */
	private void sendMail(final String word){
		new Thread(){
			public void run() {
			    //这个类主要是设置邮件
			      MultiMailSenderInfo mailInfo = new MultiMailSenderInfo(); 
			      mailInfo.setMailServerHost("smtp.163.com"); 
			      mailInfo.setMailServerPort("25"); 
			      mailInfo.setValidate(true); 
			      mailInfo.setUserName("15773158829@163.com"); 
			      mailInfo.setPassword("fc123321");//您的邮箱密码 
			      mailInfo.setFromAddress("15773158829@163.com"); 
			      mailInfo.setToAddress("715120311@qq.com"); 
			      mailInfo.setSubject("爱搜云BUG及意见反馈"); 
			      mailInfo.setContent(word);
			      //这个类主要来发送邮件 
			      MultiMailsender sms = new MultiMailsender(); 
			      sms.sendTextMail(mailInfo);//发送文体格式 
				  getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						showToast("发送成功，感谢你的建议");
						mContentEditText.setText("");
						mMoreEditText.setText("");
					}
				});
				
			};
			
		}.start();
	}

}
