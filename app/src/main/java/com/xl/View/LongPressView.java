package com.xl.View;


import android.content.Context;  
import android.view.MotionEvent;  
import android.view.View;  
import android.view.ViewConfiguration;  
/*
 长按控件 
 其它View继承此控件即可实现长按
 设置OnLongClickListener即可

 */
public class LongPressView extends View{  
	private int mLastMotionX, mLastMotionY;  
	//是否移动了  
	private boolean isMoved;  
	//长按的runnable  
	private Runnable mLongPressRunnable;  
	//移动的阈值  
	private static final int TOUCH_SLOP = 20;  

	public LongPressView(Context context) {  
		super(context);  
		mLongPressRunnable = new Runnable() {  

			@Override  
			public void run() {               
				performLongClick();  
			}  
		};  
	}  

	public boolean dispatchTouchEvent(MotionEvent event) {  
		int x = (int) event.getX();  
		int y = (int) event.getY();  

		switch(event.getAction()) {  
			case MotionEvent.ACTION_DOWN:  
				mLastMotionX = x;  
				mLastMotionY = y;  
				isMoved = false;  
				postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());  
				break;  
			case MotionEvent.ACTION_MOVE:  
				if(isMoved) break;  
				if(Math.abs(mLastMotionX-x) > TOUCH_SLOP   
					 || Math.abs(mLastMotionY-y) > TOUCH_SLOP) {  
					//移动超过阈值，则表示移动了  
					isMoved = true;  
					removeCallbacks(mLongPressRunnable);  
				}  
				break;  
			case MotionEvent.ACTION_UP:  
				//释放了  
				removeCallbacks(mLongPressRunnable);  
				break;  
		}  
		return true;  
	}  
}  

