package com.xl.game.tool;
import java.util.Vector;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
//import org.apache.http.util.EncodingUtils;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import android.util.Log;
import java.io.UnsupportedEncodingException;

public class RcList
{
	public String filename;
	Vector <String> texts;
	public int gb;
	
	public boolean load;
	
		 int FirstItem;//链表首项，当前项
		int AddrSize; //偏移所用字节数，<64K时为2位，>64K为4位
		int RcStrCode; //RC字符串编码，0 = Unicode Big，1 = GB2312

		public static final int
		_UNICODE_BIG=0,
		_GB2312=1,
		_UTF8=2;
		
	
	
	//新建
	public RcList(String filename,int math)
	{
		this.filename=filename;
		texts= new Vector<String>();
		for(int i=0;i<math;i++)
		texts.add("");
		
		
		gb=0;
		load=true;
	}
	
	
		//读文件
	public  RcList(String filename)
	{
		this.filename=filename;
texts=new Vector<String>();
gb=0;
load= false;
		File file = new File(filename);  
		FileInputStream fis = null ;
		int length=10;
		String res=null;
		byte [] buffer = new byte[length];
		try
			{
				fis= new FileInputStream(file);

				try
					{
						 length = fis.available();
						 buffer = new byte[length];
						fis.read(buffer);
						fis.close();
						load=true;
						 Log.e("XLLOG","信息：rc文件length"+length);
					}
				catch(IOException e)
					{
						load=false;
					} 

			//String newfilename=new String(filename);
			//newfilename+=".bak";
			//File newfile=new File(newfilename);
		//file.renameTo(newfile);
    //save();
					readRc(buffer);
	//res = EncodingUtils.getString(buffer, "UTF-16BE"); 

	
			}
		catch(FileNotFoundException e)
			{
				load=false;
			}  
     
		
	
	}
	
	int readRc(byte rc[])
	{
		char rc_int[]=new char[rc.length/2];
		if(rc.length<10)return -1;
		
		byte r1,r2;
		for(int i=0;i<rc_int.length;i++)
		{
			r1=rc[i*2];
			r2=rc[i*2+1];
			
			rc_int[i]=(char)( ((char)r2<<8)|((char)r1 & 0xff) );
			if(i<9)
			Log.e("XLLOG","信息："+(r2*256+r1)+(int)rc_int[i]);
		}
		
		int endptr;
		int math=1;//从第一位开始数
		//获取最后一个rc的偏移
		//当最后一项没有字符时，直接返回，否则跳转到0后的一位
		if(rc_int[rc_int.length-2]==0)
		{
			endptr=rc_int.length-2;
		}
		else
		{
		for(endptr=rc_int.length-2;endptr>=0;endptr--)
		{
			if(rc_int[endptr]==0)
			{
				endptr++;
				break;
			}
		}
		}
		Log.e("XLLOG","信息：endptr "+endptr);
		//分析rc数目
		int rOffset=0;
		int temp_offset=0;
		while(true)
		{
			//检测数组
			if(math>=rc_int.length)
				break;
			temp_offset=rOffset;
			rOffset=rc_int[math]/2;
			//rc前面偏移绝对比后面小
			if(temp_offset>=rOffset)
			{
				break;
			}
			//检测偏移是否超过文件
			if (rOffset >= (rc_int.length - math ))
				break;
			
			if(rc_int[math]/2+math+1==endptr)
			{
				math++;
				break;
			}
				math++;
		}
		//解析rc
		for(int i=0;i<math;i++)
		{
			byte temp[]=new byte[rc.length];
		//	System.arraycopy(temp,0, rc,rc_int[i],rc.length-rc_int[i]);
			wstrcpy(temp,rc,rc_int[i]+math*2);
			//System.arraycopy(temp,0,rc,rc_int[i]+math*2,
			
				texts.add(i, new String(unToChars( temp)));
			
			
		}
		
		return math;
	}
	
	int read()
	{
		
		return 0;
	}
	
	public int size()
	{
		return texts.size();
	}
	
	
	public boolean save()
	{
		boolean load=false;
		int rc_int[]=new int[size()];
		byte rc_intb[]=new byte[size()*2];
		byte temp_text[][]=new byte[size()][];
		//生成序列
		int i=0;
		int temp_int=0;
		while(i<size())
		{
			
			rc_int[i]=temp_int;
			//转换为byte
			rc_intb[i*2]=(byte)(temp_int &0xff);
			rc_intb[i*2+1]=(byte)((temp_int>>8) & 0xff);
			temp_int+=(getText(i).length()*2+2);
			//获取字符串
			temp_text[i]=getBytes(getText(i).toCharArray());
			
			i++;
		}
		File file = new File(filename);  
		File filebak= new File(filename+".bak");
		
		file.renameTo(filebak);
		
		FileOutputStream fos = null;
		try
			{
				fos = new FileOutputStream(file);
			}
		catch(FileNotFoundException e)
			{}  


		//byte [] bytes = str.getBytes(); 
byte rc0[]=new byte[2];
rc0[0]=0;rc0[1]=0;
		try
			{
				//写文件偏移
				fos.write(rc_intb);
				
				//写字符串
				for(i=0;i<texts.size();i++)
					{
						char [] c = texts.elementAt(i).toCharArray();

						fos.write(getBytes(c));
						fos.write(rc0);
					}

				fos.close(); 
				load=true;
			}
		catch(IOException e)
			{} 
		
		return load;
	}
	
	
	//保存为文本
	public boolean savetotext(String filename)
	{
		boolean load=false;
		File file = new File(filename);  
		FileOutputStream fos = null;
		try
			{
				 fos = new FileOutputStream(file);
			}
		catch(FileNotFoundException e)
			{}  

		
		//byte [] bytes = str.getBytes(); 

		try
			{
				for(int i=0;i<texts.size();i++)
					{
						char [] c = texts.get(i).toCharArray();

						fos.write(getBytes(c));
					}

				fos.close(); 
				load=true;
			}
		catch(IOException e)
			{} 

		return load;
	}
	
	//取得一项文本
	public String getText(int n)
	{
		if(texts.size()<n)
			return null;
		return texts.elementAt(n);
	}
	//设置一项文本
	public void setText(String text,int n)
	{
		texts.setElementAt(text,n);
	}
	
	//向后插入一项
	public void add_back(String text)
	{
		texts.add(gb+1, text);
	}
	
	//向前插入一项
	public void add_head(String text)
	{
		texts.add(gb, text);
	}
	
	//移除一项内容
	public void remove(int i)
	{
		texts.remove(i);
		
	}
	
	
	
	
	
		private byte[] getBytes (char[] chars)
		{
				Charset cs = Charset.forName ("UTF-16BE");
				CharBuffer cb = CharBuffer.allocate (chars.length);
				cb.put (chars);
				cb.flip ();
				ByteBuffer bb = cs.encode (cb);

				return bb.array();
			}
		private char[] getChars (byte[] bytes)
		{
				Charset cs = Charset.forName ("UTF-16BE");
				ByteBuffer bb = ByteBuffer.allocate (bytes.length);
				bb.put (bytes);
				bb.flip ();
				CharBuffer cb = cs.decode (bb);

				return cb.array();
			}
	
			//获取字符串的一部分
	private char[] getChars(char text[], int offset, int end)
	{
		int len=end-offset;
		char newtext[]=new char[len];
		for(int i=0;i<len;i++)
		{
			newtext[i]=text[offset+i];
			
			
		}
		return newtext;
	}
	
	//获取以0为结尾的字符串
	private char[] getChars(char text[],int offset)
	{
		int i=offset;
		int n=0;
		char newtext[] =new char[1];
		while(i<text.length)
		{
			if(text[i]==0)
				break;
				n++;
			i++;
		}
		if(n>0)
		newtext=new char[n];	
		
		for(i=0;i<n;i++)
		newtext[i]=text[i];
		
		return newtext;
		
		
	}
	
	void wstrcpy(byte temp[],byte bt[],int btstart)
	{
		int i=0;
		if(btstart+i+1>= bt.length || i+1>=temp.length)
		{
			Log.e("","wstrcpy溢出："+ "start="+btstart+"/"+bt.length);
			return;
		}
		while(bt[btstart+i]!=0 || bt[btstart+i+1]!=0)
		{
			
			temp[i]=bt[btstart+i];
			temp[i+1]=bt[btstart+i+1];
			i+=2;
			if(btstart+i+1>= bt.length || i+1>=temp.length)
			{
				Log.e("","wstrcpy 溢出："+ "start="+btstart);
				break;
			}
		}
		if(i+1<temp.length)
		{
		temp[i]=0;
		temp[i+1]=0;
		}
	}
	
	int wstrlen(byte temp[])
	{
		int i=0;
		while(temp[i]!=0 || temp[i+1]!=0)
		{
			i+=2;
			if(i+1 >= temp.length)
			{
				Log.e("","wstrlen 溢出");
				break;
			}
		}
		return i;
	}
	
	char [] unToChars(byte temp[])
	{
		//计算temp长度
		
		char c[]=new char[wstrlen(temp)/2];
		
		byte r1,r2;
		for(int i=0;i<c.length;i++)
		{
			r1=temp[i*2];
			r2=temp[i*2+1];
			
			c[i]=(char)(((char)r1<<8)|((char)r2 & 0xff));
		}
		
		return c;
	}
	
	//将int转换为byte(大端)
	public static byte[] intToByteArray1(int i) 
	{ 
byte[] result = new byte[4]; 
result[0] = (byte)((i >> 24) & 0xFF);
result[1] = (byte)((i >> 16) & 0xFF);
result[2] = (byte)((i >> 8) & 0xFF); 
result[3] = (byte)(i & 0xFF);
return result;
}
	
	
}
