package com.xl.game.tool;

import java.util.regex.*;
import java.util.*;
import android.app.AlertDialog.Builder;
import android.content.*;
import android.app.*;
import android.net.*;
public class xlDialog
{
	Context context;
	boolean[] arrays_selected;
	
	void showItems(String title,String[] items,final boolean[] arrays_selected ,DialogInterface.OnClickListener ok,DialogInterface.OnClickListener back)
	{
	  Builder builder=null;
	  builder = new Builder(context);
	  builder.setTitle(title);
	  for(int i=0;i<arrays_selected.length;i++)
	  {
			arrays_selected[i]=false;
	  }
	  builder.setMultiChoiceItems(items, arrays_selected,new DialogInterface.OnMultiChoiceClickListener()
			{

				@Override
				public void onClick(DialogInterface dialoginterface, int whith, boolean isChecked)
				{
					arrays_selected[whith]=isChecked;


				}


			});


	  builder.setNegativeButton("确定", ok);
	  builder.setPositiveButton("取消", back);
	  builder.create().show();

	}
	
	//单项选择
	public static void showItem(Context context,String title,String[] items,DialogInterface.OnClickListener listener)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
			.setTitle(title)
		.setItems(items,
			listener);
		
		builder.show();
	}
	
	
	//普通对话框
	public static void show(final Context context,String title,String info)
	{
		new AlertDialog.Builder(context)
			.setTitle(title)
			.setMessage(info)
			.setPositiveButton("确定", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialogInterface, int n) {
					
					
				}
			})

			.setNegativeButton("取消", new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					// TODO: Implement this method
					if(context instanceof Activity)
						((Activity)context).finish();
				}


			})
			.setCancelable(false)
			.show();
	}
	
	//加载布局的对话框
	
	
}
