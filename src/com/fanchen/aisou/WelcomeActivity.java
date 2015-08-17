package com.fanchen.aisou;

import com.fanchen.aisou.R;
import com.fanchen.aisou.callback.OnScrollChangedListener;
import com.fanchen.aisou.utils.AnimationUtil;
import com.fanchen.aisou.view.MyScrollView;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 程序引导界面
 * 
 * @author Administrator
 * 
 */
public class WelcomeActivity extends BaseActivity implements OnScrollChangedListener {
	private LinearLayout mLLAnim;// 用来排列图标的布局
	private MyScrollView mSVmain;// 自定义垂直滚动布局
	private int mScrollViewHeight;
	private int mStartAnimateTop; //
	private boolean hasStart = false;
	private TextView tvInNew;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mScrollViewHeight = mSVmain.getHeight();
		mStartAnimateTop = mScrollViewHeight / 5 * 4;
	}
	
	
	//整个界面发生滚动的时候调用这个方法
	@Override
	public void onScrollChanged(int top, int oldTop) {
		int animTop = mLLAnim.getTop() - top;
		if (top > oldTop) {
			// 如果拉到下面，加载图标
			if (animTop < mStartAnimateTop && !hasStart) {
				Animation anim = AnimationUtils.loadAnimation(this, R.anim.show);
				mLLAnim.setVisibility(View.VISIBLE);
				mLLAnim.startAnimation(anim);
				hasStart = true;
			}
		} else {
			// 如果拉到上面，影藏图标
			if (animTop > mStartAnimateTop && hasStart) {
				Animation anim = AnimationUtils.loadAnimation(this,R.anim.close);
				mLLAnim.setVisibility(View.INVISIBLE);
				mLLAnim.startAnimation(anim);
				hasStart = false;
			}
		}
	}

	@Override
	public int getResId() {
		return R.layout.activity_welcome;
	}

	@Override
	public void findView() {
		mSVmain = (MyScrollView) findViewById(R.id.sv_main);
		mLLAnim = (LinearLayout) findViewById(R.id.ll_anim);
		tvInNew = (TextView) findViewById(R.id.tvInNew);
	}

	@Override
	public void setListener() {
		mSVmain.setOnScrollChangedListener(this);
		tvInNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//点击立即体验的按钮的时候，跳转到主界面
				startActivity(MainActivity.class);
				//过度动画
				AnimationUtil.finishActivityAnimation(WelcomeActivity.this);
			}
		});
	}

	@Override
	public void initData(Bundle b) {
		mLLAnim.setVisibility(View.INVISIBLE);
	}

}
