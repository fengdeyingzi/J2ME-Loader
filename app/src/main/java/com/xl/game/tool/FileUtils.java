package com.xl.game.tool;
import android.os.*;
import android.util.Base64;
import java.io.*;

public class FileUtils
{
	
		public static String encodeBase64File(String path) throws Exception {
			File file = new File(path);
			FileInputStream inputFile = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			inputFile.read(buffer);
			inputFile.close();
			return Base64.encodeToString(buffer, Base64.DEFAULT);
		}

		public static void decoderBase64File(String base64Code, String savePath) throws Exception {
			//byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
			byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
			FileOutputStream out = new FileOutputStream(savePath);
			out.write(buffer);
			out.close();
		}
	
	/**
	 * 向文件中写入错误信息
	 * 
	 * @param info
	 */
	public static void writeText(String filename,String info) {
		File file = new File(filename);
		
		
		try
		{
			if (!file.isFile())file.createNewFile();
		}
		catch (Exception e)
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

	
	//获取sd卡
	public static String getSDPath()
	{
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
		if(sdCardExist)
		{
			sdDir=Environment.getExternalStorageDirectory();//获取sd卡目录
		}
		else 
		{
			return null;
		}
		return sdDir.getPath();
	}
	
	//拼接目录
	public static String getPath(String path,String name)
	{
		if(path.endsWith(File.separator))
		{
			return path+name;
		}
		return path+File.separator+name;
	}
		
	
}
