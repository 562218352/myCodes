package com.zh.swipelistviewtest;

import com.nineoldandroids.view.ViewHelper;

import android.R.integer;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.ListView;

public class SwipeDismissListView extends ListView{
	
	/**
	 * android认为用户滑动的最小距离
	 */
	private int mSlop;
	
	/**
	 * 滑动的最小速度
	 */
	private int mMinFlingVelocity;
	
	/**
	 * 滑动的最大速度
	 */
	private int mMaxFlingVelocity;
	
	/**
	 * 执行动画的时间
	 */
	private int mAnimationTime;
	
	/**
	 * 用户点击的索引
	 */
	private int mDownPosition;
	
	/**
	 * 滑动跟踪类
	 */
	private VelocityTracker velocityTracker;
	
	/**
	 * 用户是否滑动
	 */
	private boolean isSwiping;
	
	/**
	 * 用户点击的视图条目
	 */
	private View mDownView;
	
	private float mDownX;
	
	private float mDownY;
	
	private int mDownWidth;
	
	private OnDismissCallback onDismissCallback;
	
	public void setOnDismissCallback(OnDismissCallback onDismissCallback) {
		this.onDismissCallback = onDismissCallback;
	}

	public void setmAnimationTime(int mAnimationTime) {
		this.mAnimationTime = mAnimationTime;
	}


	public SwipeDismissListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
		mSlop = viewConfiguration.getScaledTouchSlop();
		mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
		mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
	}
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			handleActionDown(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			handleActionMove(ev);
			break;
		case MotionEvent.ACTION_UP:
			handleActionUp(ev);
			break;
		}
		return super.onTouchEvent(ev);
	}


	private void handleActionUp(MotionEvent ev) {
		if(velocityTracker ==null || mDownView ==null){
			return ;
		}
		
		float dx = ev.getX() - mDownX;
		velocityTracker.computeCurrentVelocity(2000);
		float velocityX = Math.abs(velocityTracker.getXVelocity());
		float velocityY = Math.abs(velocityTracker.getYVelocity());
		
		boolean dismiss = false;
		boolean dismissRight = true;
		
		if(Math.abs(dx)>(mDownWidth/2.0f)){
			dismiss = true;
			dismissRight = dx > 0;
		}else if((velocityX>mMinFlingVelocity)&&(velocityX<mMaxFlingVelocity) && velocityY<velocityX){
			dismiss = true;
			dismissRight = velocityTracker.getXVelocity() > 0;
		}
		
		ViewPropertyAnimator viewPropertyAnimator = mDownView.animate();
		if(dismiss){
			//移除
			viewPropertyAnimator.translationX(dismissRight?mDownWidth:-mDownWidth)
			.alpha(0).setDuration(mAnimationTime).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					performDismiss(mDownView,mDownPosition);
				}
			});
		}else{
			//回归
			viewPropertyAnimator.translationX(0).alpha(1).setDuration(mAnimationTime).setListener(null);
		}
		
		//移除速度监听器
		if(velocityTracker != null){
			velocityTracker.recycle();
			velocityTracker = null;
		}
		
		isSwiping = false;
	}

	/**
	 * 执行删除操作
	 * @param mDownView2
	 * @param mDownPosition2
	 */
	protected void performDismiss(final View mView, final int mPosition) {
		final ViewGroup.LayoutParams lp = mView.getLayoutParams();
		final float height = mView.getHeight();
		
		final ValueAnimator valueAnimator = ValueAnimator.ofInt((int)height,0).setDuration(mAnimationTime);
		valueAnimator.start();
		
		valueAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				if(onDismissCallback != null){
					onDismissCallback.onDismiss(mPosition);
				}
				
				//将数据删除以后，还得把item回复到原来高度和原来的透明度
				ViewHelper.setTranslationX(mView, 0);
				ViewHelper.setAlpha(mView, 1);
				mView.setLayoutParams(lp);
			}
		});
		
		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				//其他item向上滚
				lp.height = (Integer)valueAnimator.getAnimatedValue();
				mView.setLayoutParams(lp);
			}
		});
		
	}

	private boolean handleActionMove(MotionEvent ev) {
		if(velocityTracker==null || mDownView == null){
			return super.onTouchEvent(ev);
		}
		
		float dx = ev.getX() - mDownX;
		float dy = ev.getY() - mDownY;
		
		if(dx > mSlop && dy < mSlop){
			isSwiping = true;
			
			//取消item的点击事件
			MotionEvent cancleEvent = MotionEvent.obtain(ev);
			cancleEvent.setAction(MotionEvent.ACTION_CANCEL | 
								(ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
			
			onTouchEvent(cancleEvent);
		}
		
		if(isSwiping){
			ViewHelper.setTranslationX(mDownView, dx);
			ViewHelper.setAlpha(mDownView, Math.max(0f, Math.min(1f, 1f - 2f * Math.abs(dx)/mDownWidth)));
			return true;
		}
		
		return super.onTouchEvent(ev);
	}

	/**
	 * 处理点下事件
	 * @param ev
	 */
	private void handleActionDown(MotionEvent ev) {
		mDownX = ev.getX();
		mDownY = ev.getY();
		mDownPosition = pointToPosition((int)mDownX, (int)mDownY);
		
		
		if(mDownPosition == AdapterView.INVALID_POSITION){
			return;
		}
		
		mDownView = getChildAt(mDownPosition - getFirstVisiblePosition());
		if(mDownView!=null){
			mDownWidth = mDownView.getWidth();
		}
		
		velocityTracker = VelocityTracker.obtain();
		velocityTracker.addMovement(ev);
	}

	public interface OnDismissCallback{
		public void onDismiss(int mDownPosition);
	}

}
