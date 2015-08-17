package com.fanchen.aisou.view;

import com.fanchen.aisou.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * 进度条对话框
 * 
 * @author fanchen
 * 
 */
public class LoadingDialog extends Dialog {

	private static LoadingDialog myProgressDialog;

	private LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	/**
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param cancelable
	 * 
	 * @return
	 */
	public static LoadingDialog createDialog(Context context, String title,
			String message, boolean cancelable) {
		myProgressDialog = new LoadingDialog(context,
				R.style.CustomProgressDialog);
		myProgressDialog.setContentView(R.layout.dialog_loading);
		myProgressDialog.setCancelable(cancelable);
		myProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

		TextView tv_message = (TextView) myProgressDialog
				.findViewById(R.id.tv_message);
		if (null == message || "".equals(message))
			tv_message.setVisibility(View.GONE);
		else
			tv_message.setText(message);
		return myProgressDialog;
	}

	public static LoadingDialog createDialog(Context context, String title,
			String message) {
		return createDialog(context, title, message, false);
	}

	public static LoadingDialog createDialog(Context context, String message) {
		return createDialog(context, "", message, false);
	}

	public static LoadingDialog createDialog(Context context) {
		return createDialog(context, "");
	}

	@Override
	public void cancel() {
		super.cancel();
		myProgressDialog = null;
	}

	@Override
	public void dismiss() {
		if (myProgressDialog == null || !myProgressDialog.isShowing()) {

			return;
		}
		super.dismiss();
		myProgressDialog = null;
	}

}