package com.xl.game.tool;

import android.content.*;
import android.content.pm.*;
import java.util.*;
import android.graphics.*;
import android.app.*;
import android.view.*;
import java.io.*;
import android.net.*;

public class AppTool
{
	private static final String[][] OPEN_Tab= new String[][]
{
        {".apk","application/vnd.android.package-archive"},
     
        {".avi", "video/x-msvideo"},
     
        {".mrp",
        
        "application/mrp"},
     
        {".png",
    
        "image/png"},
     
      {".gif",
    
        "image/gif"},
        
        {".jpg",
        
        "image/jpeg"},
    {".bmp",
        
      "image/bmp"},
    
      {".html",
        
        "text/html"},
     
     { ".mp3",
        
      "audio/x-mpeg"},
     
        {".wav",
        
    "audio/x-wav"},
     
      {".mid",
        
        "audio"},
      
     { ".m4a",
    "audio/mp4a-latm"},
        
        {".amr",
        
        "audio"},
        
      {".mp4",
        
      "video/mp4"},
        
      {".zip",
        
      "application/x-zip-compressed"}
        
    };
	
	/**
	 　　* 获取版本号
	 　　* @return 当前应用的版本号　
	 　*/
	public static int getVersionCode(Context context) 
	{
		try
		{
			PackageManager manager = context.getPackageManager();

			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

			String version = ""+info.versionCode;
			return  info.versionCode;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			return 0;
		}

	}


	public static String getVersionName(Context context) 
	{
		try
		{
			PackageManager manager = context.getPackageManager();

			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

			String version = info.versionName;
			return  version;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			return null;
		}

	}
	
	//跳转到指定应用
	public static boolean startApp(Context context, String packageName) {
//String packageName = "XXX";
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> listInfos = pm.queryIntentActivities(intent, 0);
		String className = null;
		for (ResolveInfo info : listInfos) {
			if (packageName.equals(info.activityInfo.packageName)) {
				className = info.activityInfo.name;
				break;
			}
		}
		if (className != null && className.length() > 0) {
			intent.setComponent(new ComponentName(packageName, className));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			context.startActivity(intent);
			return true;
		}
		return false;
	}
	
	//获取状态栏高度
	public static int getstatusBarHeight(Activity activity)
	{
		// 获取状态栏高度 /  
		Rect frame = new Rect();  
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
		int statusBarHeight = frame.top;  
		return statusBarHeight;
	}
	
	//获得View的截图
	public static Bitmap getBitmap(View mLayoutSource)
	{
		mLayoutSource.setDrawingCacheEnabled(true);  
		Bitmap tBitmap = mLayoutSource.getDrawingCache();  
		// 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉  
		tBitmap = tBitmap.createBitmap(tBitmap);  
		mLayoutSource.setDrawingCacheEnabled(false);  
		/*
		if (tBitmap != null) {  
			  
			Toast.makeText(getApplicationContext(), "获取成功", Toast.LENGTH_SHORT).show();  
		} else {  
			Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();  
		} 
		*/
		return tBitmap;
	}
	
	public static boolean packageApp(Context context, String str) {
        Context context2 = context;
        String str2 = str;
        for (int i = 0; i < OPEN_Tab.length; i++) {
            if (str2.endsWith(OPEN_Tab[i][0])) {
                File file =  new File(str2);
                Uri fromFile = Uri.fromFile(file);
                Intent intent = new Intent("android.intent.action.VIEW");
                Intent intent3 = intent;
                intent = intent3.setDataAndType(fromFile, OPEN_Tab[i][1]);
                try {
                    context2.startActivity(intent3);
                    return true;
                } catch (ActivityNotFoundException e) {
                    ActivityNotFoundException activityNotFoundException = e;
                    return false;
                }
            }
        }
        return false;
    }
	
	
}
