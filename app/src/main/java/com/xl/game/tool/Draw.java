package com.xl.game.tool;

import android.text.TextPaint;
import android.graphics.Paint;
import android.graphics.Canvas;

public class Draw
{
	public static final int
	LEFT_CENTER=1, //左中
	LEFT_TOP=2, //左上
	LEFT_BOTTOM=3, //左下
	CENTER=4, //居中
	RIGHT_CENTER=5 //右对齐居中
  	
	;
	
	float dx,dy;
	TextPaint paint;
	public Draw()
	{
		this.paint = new TextPaint();
	}
	//设置颜色
	
	//获取paint
	
	//设置Paint
	
	//设置字体大小
	
	//设置文字显示方式
	void setDrawType(int type)
	{
		Paint.FontMetrics fontmetrics = this.paint.getFontMetrics();
		switch(type)
		{
			case LEFT_CENTER:
				dx=0;
				//dy= - ( fontmetrics.bottom+fontmetrics.top+this.paint.getTextSize())/2  -fontmetrics.top;
				dy= - ( fontmetrics.bottom+fontmetrics.top+this.paint.getTextSize())/2  -fontmetrics.top   -paint.getTextSize()/2;
				
				break;
			case LEFT_TOP:
				//y+ top -((bottom-top )/2-sixe/2)
				//y+(-top)-(bottom-top-size)/2
				dx=0;
				dy= - ( fontmetrics.bottom+fontmetrics.top+this.paint.getTextSize())/2  -fontmetrics.top;
				
				break;
			case LEFT_BOTTOM:
				dy= - ( fontmetrics.bottom+fontmetrics.top+this.paint.getTextSize())/2  -fontmetrics.top + paint.getTextSize()/2;
				break;
			case RIGHT_CENTER:
				dy= - ( fontmetrics.bottom+fontmetrics.top+this.paint.getTextSize())/2  -fontmetrics.top   -paint.getTextSize()/2;
				paint.setTextAlign(Paint.Align.RIGHT);
				break;
		}
	}
	//显示文字
	void drawText(Canvas canvas,String text,int x,int y)
	{
		canvas.drawText(text,x+dx,y+dy,paint);
	}
	
	
	
	
	
	
	
	
}
