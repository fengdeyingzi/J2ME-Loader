/*
 * Decompiled with CFR 0_58.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  java.lang.Object
 *  java.lang.String
 */
package com.xl.game.tool;

import android.content.*;
import android.telephony.*;
import android.util.*;
import java.io.*;

import android.util.Log;
import android.os.Build;

public class Tool
{
	/*
	 * Exception decompiling
	 */
	public static int[][] ReadMap(Context context,String mapname)
	{
		DataInputStream input;
		int x,y;

		int[][] r2_int_A_A =null;

		try
		{
			//DataInputStream r12_DataInputStream = r9_DataInputStream;
			DataInputStream r10_DataInputStream = new DataInputStream(context.getResources().getAssets().open(mapname));
			input=r10_DataInputStream;
			
			r2_int_A_A= new int[input.readInt()][input.readInt()];
			/*
			r2_int_A_A=(int[][] )
			Array.newInstance
			(
			Integer.TYPE,
			new int[]
			{input.readInt(), input.readInt()}
			);
			*/
			Log.e("XL", ""+r2_int_A_A[0].length );
			
			for(y=0;y<r2_int_A_A.length;y++)
			{
				for(x=0;x<r2_int_A_A[y].length;x++)
				{
					r2_int_A_A[y][x]=input.readInt();
				}
			}
			
			/*
			y=0;
			while(y<r2_int_A_A.length)
			{
				int x = 0;
				
				while(true)
				{
					
					if(x>=r2_int_A_A[y].length)
					{
						y+=1;
						break;
					}
					else
					{
						r2_int_A_A[y][x]=input.readInt();
						x+=1;
					}
				}
			}
			
			*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return r2_int_A_A;
	}
	
	//获取imei码
	public static String getImei(Context context)
	{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
		// activity.getSystemService(Context.TELEPHONY_SERVICE).getDeviceId();
//	String myIMSI=android.os.SystemProperties.get(android.telephony.TelephonyProperties.PROPERTY_IMSI);
	}

	//获取imsi码
	public static String getImsi(Context context)
	{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();

	}
	
	//设置剪切板内容
	public static void clipSet(Context context,CharSequence text)
	{
	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
		{
			android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboardManager.setText(text);
		}
		else
		{
			android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboardManager.setText(text);
		}
	}
	
	//获取剪切板内容
	public static CharSequence clipGet(Context context)
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
		{
			android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			return clipboardManager.getText();
			//StringBuffer text = new StringBuffer(getText());
		}
		else
		{
			android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		/*
			android.content.ClipData clipData = clipboardManager.getPrimaryClip();
			if (clipData != null && clipData.getItemCount() > 0)
			{
				return clipData.getItemAt(0).coerceToText(context);
				//StringBuffer text = new StringBuffer(getText());
			}
			*/
			return clipboardManager.getText();
		}
	}
	
}

