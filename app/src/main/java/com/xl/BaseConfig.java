package com.xl;
import android.app.Activity;

public class BaseConfig
{
	public static String appName="EA-孢子生命起源";
	public static Activity activity;
	//app模式
	public static boolean isApp=true;
	//启动类
	public static String startMIDlet="META-INF/MANIFEST.MF";
	//显示状态栏
	public static  boolean showStatus = true;
	//显示toolbar
	public static  boolean showToolbar=true;
	//使用摇杆键盘
	public static  boolean useKeyBoard1=true;
	//使用八方向摇杆键盘
	public static  boolean useKeyBoard2=true;
	//使用四方向游戏键盘
	public static  boolean useKeyBoard3=true;
	//使用全键盘
	public static  boolean useKeyBoard4=true;
	
	
	//默认启用键盘
	public static  boolean showKeyBoard=true;
	
	
public static  int fontSizeSmall=16,
fontSizeMedium=18,
fontSizeLarge=20,
screenWidth=240,
screenHeight=320,
screenScaleRatio=100,
screenBackgroundColor=0xffb0b0b0;

public static boolean 
fontApplyDimensions=false, //字体
screenScaleToFit=true, //屏幕自适应
screenKeepAspectRatio=true, //保持宽高比
screenFilter=false, //屏幕过滤器
immediateMode=false, //即时模式
clearBuffer=false; //清除缓存区




}
