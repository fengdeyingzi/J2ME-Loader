package com.xl.game.tool;
import android.media.*;
import android.content.*;
import android.net.*;

public class ImgTool
{
//添加到媒体库

	public static void FileToMedia(Context context, String filename)
	{
		MediaScannerConnection.scanFile(context, new String[] { filename }, null,                    
			new MediaScannerConnection.OnScanCompletedListener() 
			{
				public void onScanCompleted(String path, Uri uri) 
				{
					//Log.i("ExternalStorage","Scanned " + path + ":");
					//Log.i("ExternalStorage", "-> uri=" + uri);
				}
			});
	}
}
