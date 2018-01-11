package com.xl.game.tool;
import java.nio.charset.Charset;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;

/*
编码转换
*/

public class Coding
{
	
	//char转byte
		public byte[] getBytes (char[] chars,String coding)
			{
				Charset cs = Charset.forName (coding);
				CharBuffer cb = CharBuffer.allocate (chars.length);
				cb.put (chars);
				cb.flip ();
				ByteBuffer bb = cs.encode (cb);

				return bb.array();
			}
			
		//byte转char	
		public char[] getChars (byte[] bytes,String coding)
			{
				Charset cs = Charset.forName (coding);
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

		//获取以0为结尾的字符串 字符串 开始位置
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

				return newtext;


			}

			//将byte以0结尾的部分复制到temp
		void wstrcpy(byte temp[],byte bt[],int btstart)
			{
				int i=0;
				while(bt[btstart+i]+bt[btstart+i+1]!=0)
					{
						temp[i]=bt[btstart+i];
						temp[i+1]=bt[btstart+i+1];
						i+=2;
					}
				temp[i]=0;
				temp[i+1]=0;
			}

		int wstrlen(byte temp[])
			{
				int i=0;
				while(temp[i]+temp[i+1]!=0)
					{
						i+=2;
					}
				return i+=2;
			}

			//将char直接转换成byte
		char [] toChars(byte temp[])
			{
				//计算temp长度

				char c[]=new char[wstrlen(temp)/2];

				int r1,r2;
				for(int i=0;i<c.length;i++)
					{
						r1=temp[i*2+1];
						r2=temp[i*2];
						if(r1<0)r1+=256;
						if(r2<0)r2+=256;
						c[i]=(char)(r2*256+r1);
					}

				return c;
			}


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
