package com.xl.game.tool;

import java.io.*;

public class Log
{
	static final String DIR="mnt/sdcard";
	/**
	 * 向文件中写入错误信息
	 * 
	 * @param info
	 */
	public static void e(String text,String info) {
		File dir = new File(DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		//打印到log日志
		android.util.Log.e(text,info);
		//打印到文本文件
		File file = new File(dir, "log_e.txt");
		try
		{
			if (!file.isFile())
			{
				//file.createNewFile();
			}
			else
			{
				try
					{
						FileOutputStream fileOutputStream = new FileOutputStream(file, true);

						fileOutputStream.write((text+" "+info+"\n\n").getBytes());
						fileOutputStream.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		catch (Exception e)
		{}
		

	}
	
	public static void i(String text,String info) {
		File dir = new File(DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, "log_i.txt");
		try
		{
			if (!file.isFile())file.createNewFile();
		}
		catch (IOException e)
		{}
		try
		{
			FileOutputStream fileOutputStream = new FileOutputStream(file, true);
			fileOutputStream.write(info.getBytes());
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void w(String text,String info) {
		File dir = new File(DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, "log_w.txt");
		try
		{
			if (!file.isFile())file.createNewFile();
		}
		catch (IOException e)
		{}
		try
		{
			FileOutputStream fileOutputStream = new FileOutputStream(file, true);
			fileOutputStream.write(info.getBytes());
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		
	public static void d(String text,String info) {
		File dir = new File(DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, "log_d.txt");
		try
		{
			if (!file.isFile())file.createNewFile();
		}
		catch (IOException e)
		{}
		try
		{
			FileOutputStream fileOutputStream = new FileOutputStream(file, false);
			fileOutputStream.write(info.getBytes());
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	
}
