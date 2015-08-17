/**
 * AnimationUtils.java [V 1..0.0]
 * classes : com.hb56.hps.android.utils.AnimationUtils
 * zhangyx Create at 2014-10-31 下午2:31:50
 */
package com.fanchen.aisou.utils;


import com.fanchen.aisou.R;

import android.app.Activity;
import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

/**
 * 自定义控件的动画效果
 *com.zhangyx.MyLauncherGuide.utils.AnimationUtil
 * @author Admin-zhangyx
 *
 * create at 2015-1-21 下午1:51:08
 */
public class AnimationUtil {

	/* 特效源码---------listview加载的效果 */
	public static LayoutAnimationController getListAnimTranslate() {
		AnimationSet set = new AnimationSet(true);
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(500);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(800);
		set.addAnimation(animation);
		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);

		controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
		return controller;
		/*-----------------------------------------*/
	}

	/**
	 * 退出Activity的动画 : zoom 动画
	 * 
	 * @param context
	 */
	public static void finishActivityAnimation(Context context) {
		((Activity) context).finish();
		((Activity) context).overridePendingTransition(R.anim.zoom_enter,
				R.anim.zoom_exit);
	}

	/***
	 * zoom 动画s
	 * 
	 * @param context
	 */
	public static void activityZoomAnimation(Context context) {
		((Activity) context).overridePendingTransition(R.anim.zoom_enter,
				R.anim.zoom_exit);
	}

}
