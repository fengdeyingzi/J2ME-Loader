package com.xl.game.tool;
import android.view.*;
import android.content.*;
import android.os.*;
//import android.net.*;
import java.io.*;
import java.util.*;
import android.net.*;
import android.graphics.*;
import android.provider.*;
import android.text.*;
import android.content.pm.*;

public class ShareTool
{
	//分享图片
	//分享单张图片 
	public static void shareSingleImage(Context context, String title, String path)
	{
		//String imagePath = Environment.getExternalStorageDirectory() + File.separator + "test.jpg";
		//由文件得到uri
		Uri imageUri = Uri.fromFile(new File(path));
		Log.d("share", "uri:" + imageUri);
		//输出：file:///storage/emulated/0/test.jpg 
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
		shareIntent.setType("image/*");
		context.startActivity(Intent.createChooser(shareIntent, title));
    }

	//分享多张图片
	public static void shareMultipleImage(Context context, ArrayList<String> filelist)
	{
		ArrayList uriList = new ArrayList<>();
		for (int i=0;i < filelist.size();i++)
		{
			uriList.add(Uri.fromFile(new File(filelist.get(i))));
		}
		//String path = Environment.getExternalStorageDirectory() + File.separator;

		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
		shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
		shareIntent.setType("image/*");
		context.startActivity(Intent.createChooser(shareIntent, "分享到"));
	}

	
	//分享app
	public static void shareApp(Context context,String title,String filename)
	{
		Intent intent = new Intent(Intent.ACTION_SEND);
		//intent.putExtra('subject', file.getName()); //
		//intent.putExtra('body', 'android123 - email sender'); //正文
		intent.putExtra(Intent.EXTRA_STREAM, filename); //添加附件，附件为file对象
		if (filename.endsWith(".gz")) {
			intent.setType("application/x-gzip"); //如果是gz使用gzip的mime
		} else if (filename.endsWith(".txt")) {
			intent.setType("text/plain"); //纯文本则用text/plain的mime
		} else {
			intent.setType("application/octet-stream"); //其他的均使用流当做二进制数据来发送
		}
		context.startActivity(Intent.createChooser(intent,title)); //调用系统的mail客户端进行发送
	}
	
	//分享文字
	//分享文字 
	public static void shareText(Context context, String title, String text)
	{
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT, text);
		shareIntent.setType("text/plain");
		//设置分享列表的标题，并且每次都显示分享列表 
		context.startActivity(Intent.createChooser(shareIntent, title));
	}
	//分享图片到QQ
	public static boolean shareImageToQQ(Context context, Bitmap bitmap)
	{
		try
		{
			Uri uriToImage = Uri.parse(MediaStore.Images.Media.insertImage(
										   context.getContentResolver(), bitmap, null, null));			
			Intent shareIntent = new Intent();	
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);	
			shareIntent.setType("image/*");	
			// 遍历所有支持发送图片的应用。找到需要的应用	
			PackageManager packageManager = context.getPackageManager();	
			List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(shareIntent, PackageManager.GET_INTENT_FILTERS);		
			ComponentName componentName = null;	
			for (int i = 0; i < resolveInfoList.size(); i++)
			{

				if (TextUtils.equals(resolveInfoList.get(i).activityInfo.packageName, "com.tencent.mobileqq"))
				{
					componentName = new ComponentName(
						resolveInfoList.get(i).activityInfo.packageName, resolveInfoList.get(i).activityInfo.name);					
					break;
				}
			}			// 已安装**			
			if (null != componentName) 
			{
				shareIntent.setComponent(componentName);				
				context.startActivity(shareIntent);	
				return true;
			}
			else
			{
				//ContextUtil.getInstance().showToastMsg("请先安装**");
			}
		}
		catch (Exception e) 
		{
			//ContextUtil.getInstance().showToastMsg("分享图片到**失败");
		}
		return false;
	}
	
	
	//分享图片到
	public static boolean shareImage(Context context,String title, Bitmap bitmap)
	{
		try
		{
			Uri uriToImage = Uri.parse(MediaStore.Images.Media.insertImage(
										   context.getContentResolver(), bitmap, null, null));			
			Intent shareIntent = new Intent();	
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);	
			shareIntent.setType("image/*");	
			context.startActivity(Intent.createChooser(shareIntent, title));
			
		}
		catch (Exception e) 
		{
			//ContextUtil.getInstance().showToastMsg("分享图片到**失败");
		}
		return false;
	}
	
	//分享文字到QQ
//分享图片到QQ
	public static boolean shareTextToQQ(Context context, String text)
	{
		try
		{
				
			Intent shareIntent = new Intent();	
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_TEXT, text);	
			shareIntent.setType("text/plain");	
			// 遍历所有支持发送图片的应用。找到需要的应用	
			PackageManager packageManager = context.getPackageManager();	
			List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(shareIntent, PackageManager.GET_INTENT_FILTERS);		
			ComponentName componentName = null;	
			for (int i = 0; i < resolveInfoList.size(); i++)
			{

				if (TextUtils.equals(resolveInfoList.get(i).activityInfo.packageName, "com.tencent.mobileqq"))
				{
					componentName = new ComponentName(
						resolveInfoList.get(i).activityInfo.packageName, resolveInfoList.get(i).activityInfo.name);					
					break;
				}
			}			// 已安装**			
			if (null != componentName) 
			{
				shareIntent.setComponent(componentName);				
				context.startActivity(shareIntent);	
				return true;
			}
			else
			{
				//ContextUtil.getInstance().showToastMsg("请先安装**");
			}
		}
		catch (Exception e) 
		{
			//ContextUtil.getInstance().showToastMsg("分享图片到**失败");
		}
		return false;
	}
	//分享图片到微信

	//分享文字到微信
	public static void shareFile(Context context, String str, String str2) {
        Context context2 = context;
        String str3 = str;
        String str4 = str2;
        Intent intent = new Intent("android.intent.action.SEND");
        Intent intent3 = intent;
        File file = new File(str4);
        intent = intent3.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
        intent = intent3.setType("*/*");
        context2.startActivity(Intent.createChooser(intent3, str3));
    }
	
}
