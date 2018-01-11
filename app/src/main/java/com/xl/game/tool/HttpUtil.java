package com.xl.game.tool;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.util.*;
import javax.net.ssl.*;
/*

*/
public class HttpUtil
{
    private final static int CONNENT_TIMEOUT = 5000;
    private final static int READ_TIMEOUT = 15000;
	private static String ua= "Dalvik/1.6.0 (Linux; U; Android 4.4.4; MI 4LTE MIUI/V6.6.2.0.KXDCNCF)";
    static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session)
		{
            return true;
        }
    };
    /*
	 * 
	 * @function trustAllHosts
	 * @Description 信任所有主机-对于任何证书都不做检查
	 */
    private static void trustAllHosts()
    {
		TrustManager[] arrayOfTrustManager = new TrustManager[1];
		//实现自己的信任管理器类
		arrayOfTrustManager[0] = new X509TrustManager()
		{
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
            throws CertificateException
			{
				// TODO Auto-generated method stub

			}
			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
            throws CertificateException
			{
				// TODO Auto-generated method stub

			}
			@Override
			public X509Certificate[] getAcceptedIssuers()
			{
				// TODO Auto-generated method stub
				return new X509Certificate[0];
			}

		};
		try
		{
			SSLContext localSSLContext = SSLContext.getInstance("TLS");
			localSSLContext.init(null, arrayOfTrustManager, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(localSSLContext.getSocketFactory());
			return;
		}
		catch (Exception localException)
		{
			localException.printStackTrace();
		}
    }
	
	public static void setUA(String str) {
        ua = str;
    }
	
    /*
	 * 
	 * @function 
	 * @Description  https post方法，返回值是https请求
	 * @param httpsurl
	 * @param data 
	 * @return
	 orgin参数
	 */
    public static String HttpsPost(String httpsurl, String data, String cookie, String Referer)
	{
        String result = null;
        HttpURLConnection http = null;
        URL url;
        try
		{
            url = new URL(httpsurl);
            // 判断是http请求还是https请求
            if (url.getProtocol().toLowerCase().equals("https"))
			{
                trustAllHosts();
                http = (HttpsURLConnection) url.openConnection();
                ((HttpsURLConnection) http).setHostnameVerifier(DO_NOT_VERIFY);// 对所有主机都进行确认
            }
			else
			{
                http = (HttpURLConnection) url.openConnection();
            }
            http.setConnectTimeout(CONNENT_TIMEOUT);// 设置超时时间
            http.setReadTimeout(READ_TIMEOUT);
            if (data == null)
			{
                http.setRequestMethod("GET");// 设置请求类型
                http.setDoInput(true);
				//get请求不需要携带此参数
                //http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				http.setRequestProperty("Cookie", cookie);
				http.setRequestProperty("Referer", Referer);
            }
			else
			{
                http.setRequestMethod("POST");// 设置请求类型为post
                http.setDoInput(true);
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				http.setRequestProperty("Cookie", cookie);
				http.setRequestProperty("Referer", Referer);
                DataOutputStream out = new DataOutputStream(
					http.getOutputStream());
                out.writeBytes(data);
                out.flush();
                out.close();
            }
            // 设置http返回状态200（ok）还是403
            int responseCode = http.getResponseCode();
            BufferedReader in = null;
            if (responseCode == 200)
			{
                in = new BufferedReader(new InputStreamReader(
											http.getInputStream()));
            }
			else
                in = new BufferedReader(new InputStreamReader(
											http.getErrorStream()));
            String temp = in.readLine();
            while (temp != null)
			{
                if (result != null)
                    result += temp + "\n";
                else
                    result = temp;
                temp = in.readLine();
            }
            in.close();
            http.disconnect();
        }
		catch (Exception e)
		{
            e.printStackTrace();
        }
        return result;
    }

	public static String HttpsPost(String httpsurl, String data, String cookie)
	{
        String result = null;
        HttpURLConnection http = null;
        URL url;
        try
		{
            url = new URL(httpsurl);
            // 判断是http请求还是https请求
            if (url.getProtocol().toLowerCase().equals("https"))
			{
                trustAllHosts();
                http = (HttpsURLConnection) url.openConnection();
                ((HttpsURLConnection) http).setHostnameVerifier(DO_NOT_VERIFY);// 对所有主机都进行确认
            }
			else
			{
                http = (HttpURLConnection) url.openConnection();
            }

            http.setConnectTimeout(CONNENT_TIMEOUT);// 设置超时时间
            http.setReadTimeout(READ_TIMEOUT);
			/*
			 // 获取所有响应头字段
			 Map<String, List<String>> map = http.getHeaderFields();
			 // 遍历所有的响应头字段
			 for (String key : map.keySet()) {
			 System.out.println(key + "--->" + map.get(key));
			 }*/
            if (data == null)
			{
                http.setRequestMethod("GET");// 设置请求类型
                http.setDoInput(true);
                http.setRequestProperty("Content-Type", "text/xml");
				http.setRequestProperty("user-agent", "Dalvik/1.6.0 (Linux; U; Android 4.4.4; MI 4LTE MIUI/V6.6.2.0.KXDCNCF)");
				if (cookie != null)
					http.setRequestProperty("Cookie", cookie);
            }
			else
			{
                http.setRequestMethod("POST");// 设置请求类型为post
                http.setDoInput(true);
                http.setDoOutput(true);
				http.setRequestProperty("user-agent", "Dalvik/1.6.0 (Linux; U; Android 4.4.4; MI 4LTE MIUI/V6.6.2.0.KXDCNCF)");
                http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				if (cookie != null)
					http.setRequestProperty("Cookie", cookie);
                DataOutputStream out = new DataOutputStream(
					http.getOutputStream());
                out.writeBytes(data);
                out.flush();
                out.close();
            }
            // 设置http返回状态200（ok）还是403
            int responseCode = http.getResponseCode();
            BufferedReader in = null;
            if (responseCode == 200)
			{
                in = new BufferedReader(new InputStreamReader(
											http.getInputStream()));
            }
			else
                in = new BufferedReader(new InputStreamReader(
											http.getErrorStream()));
            String temp = in.readLine();
            while (temp != null)
			{
                if (result != null)
                    result += temp + "\n";
                else
                    result = temp;
                temp = in.readLine();
            }
            in.close();
            http.disconnect();
        }
		catch (Exception e)
		{
            e.printStackTrace();
        }
        return result;
    }
	public static String get(String url)
	{
		String result = "";
		BufferedReader in = null;
		try
		{
			String urlName = url ;
			URL realUrl = new URL(urlName);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			//conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("user-agent", "Dalvik/1.6.0 (Linux; U; Android 4.4.4; MI 4LTE MIUI/V6.6.2.0.KXDCNCF)");
			conn.setConnectTimeout(CONNENT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			
			//conn.setRequestProperty("user-agent","MQQBrowser\nQ-UA2: QV=3&PL=ADR&PR=QB&PP=com.tencent.mtt&PPVN=6.5.0.2170&TBSVC=26001&CO=BK&COVC=036504&PB=GE&VE=GA&DE=PHONE&CHID=0&LCID=9678&MO= MI4LTE &RL=1080*1920&OS=4.4.4&API=19");
			// 建立实际的连接
			conn.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = conn.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet())
			{
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
			{
				result += "\n" + line;
			}
		}
		catch (Exception e)
		{
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return result;
	}



	public static String get(String url, String cookie)
	{
		String result = "";
		BufferedReader in = null;
		try
		{
			String urlName = url ;
			URL realUrl = new URL(urlName);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			//conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("user-agent", "Dalvik/1.6.0 (Linux; U; Android 4.4.4; MI 4LTE MIUI/V6.6.2.0.KXDCNCF)");
			//conn.setRequestProperty("user-agent","MQQBrowser\nQ-UA2: QV=3&PL=ADR&PR=QB&PP=com.tencent.mtt&PPVN=6.5.0.2170&TBSVC=26001&CO=BK&COVC=036504&PB=GE&VE=GA&DE=PHONE&CHID=0&LCID=9678&MO= MI4LTE &RL=1080*1920&OS=4.4.4&API=19");
			conn.setConnectTimeout(CONNENT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			
			if (cookie != null)
				conn.setRequestProperty("Cookie", cookie);
			// 建立实际的连接
			conn.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = conn.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet())
			{
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
			{
				result += "\n" + line;
			}
		}
		catch (Exception e)
		{
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String _get(String str, String str2)
	{
        String[] split;
        String str3 = str;
        String str4 = str2;
        String str5 = (String) null;
        if (str3.indexOf(63) > 0)
		{
            split = str3.split("\\?");
            if (split.length >= 2)
			{
                str5 = split[1];
            }
        }
        if (str5 == null)
		{
            return (String) null;
        }
        String[] strArr = new String[1];
        String[] strArr2 = strArr;
        strArr[0] = str5;
        split = strArr2;
        if (str5.indexOf("&") > 0)
		{
            split = str5.split("&");
        }
        String str6 = (String) null;
        String[] strArr3 = split;
        for (String str7 : strArr3)
		{
            String str8 = str7;
            StringBuffer stringBuffer = new StringBuffer();
            if (str8.startsWith(stringBuffer.append(str4).append("=").toString()))
			{
                int i = 0;
                while (i < str7.length())
				{
                    if (str7.charAt(i) == '=')
					{
                        try
						{
                            return URLDecoder.decode(str7.substring(i + 1, str7.length()), "utf-8");
                        }
						catch (UnsupportedEncodingException e)
						{
                            e.printStackTrace();
                        }
                    }
					else
					{
                        i++;
                    }
                }
                continue;
            }
        }
        return (String) null;
    }


	/**
	 * 向指定URL发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是name1=value1&name2=value2的形式。
	 * @return URL所代表远程资源的响应
	 */
	public static String post(String url, String param)
	{
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try
		{
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性




			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			//conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("user-agent", "Dalvik/1.6.0 (Linux; U; Android 4.4.4; MI 4LTE MIUI/V6.6.2.0.KXDCNCF)");
			conn.setConnectTimeout(CONNENT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
			{
				result += "\n" + line;
			}
		}
		catch (Exception e)
		{
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String post(String url, String param, String cookie)
	{
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try
		{
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性




			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			//conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("user-agent", "Dalvik/1.6.0 (Linux; U; Android 4.4.4; MI 4LTE MIUI/V6.6.2.0.KXDCNCF)");
			conn.setConnectTimeout(CONNENT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

			if (cookie != null)
			{
                conn.setRequestProperty("Cookie", cookie);
            }
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
			{
				result += "\n" + line;
			}
		}
		catch (Exception e)
		{
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return result;
	}
		
   /*
	public static String post(String str, String str2, String str3) {
        StringBuffer stringBuffer;
        StringBuffer stringBuffer2;
        String str4 = str;
        String str5 = str2;
        String str6 = str3;
        PrintWriter printWriter = (PrintWriter) null;
        BufferedReader bufferedReader = (BufferedReader) null;
       // String str7 = ConfigConstant.WIRELESS_FILENAME;
        try {
            URL url = new URL(str4);
            URLConnection openConnection = url.openConnection();
            
            openConnection.setRequestProperty("connection", "Keep-Alive");
            openConnection.setRequestProperty("User-Agent", ua);
            openConnection.setConnectTimeout(CONNENT_TIMEOUT);
            openConnection.setReadTimeout(READ_TIMEOUT);
            if (str6 != null) {
                openConnection.setRequestProperty("Cookie", str6);
            }
            openConnection.setDoOutput(true);
            openConnection.setDoInput(true);
            PrintWriter printWriter2 = new PrintWriter(openConnection.getOutputStream());
            printWriter = printWriter2;
            printWriter.print(str5);
            printWriter.flush();
            BufferedReader bufferedReader2 = r23;
            Reader reader =  new InputStreamReader(openConnection.getInputStream(), coding);
            BufferedReader bufferedReader3 = new BufferedReader(reader);
            bufferedReader = bufferedReader2;
            while (true) {
                String readLine = bufferedReader.readLine();
                String str8 = readLine;
                if (readLine == null) {
                    break;
                }
                StringBuffer stringBuffer3 = new StringBuffer();
                stringBuffer3 = stringBuffer3.append(str7);
                stringBuffer = new StringBuffer();
                str7 = stringBuffer3.append(stringBuffer.append("\n").append(str8).toString()).toString();
            }
        } catch (Exception e) {
            Exception exception = e;
            PrintStream printStream = System.out;
            stringBuffer = r23;
            stringBuffer2 = new StringBuffer();
            printStream.println(stringBuffer.append("\u53d1\u9001POST\u8bf7\u6c42\u51fa\u73b0\u5f02\u5e38\uff01").append(exception).toString());
            exception.printStackTrace();
        } catch (Throwable th) {
            Throwable th2;
            Throwable th3 = th2;
            if (printWriter != null) {
                try {
                    printWriter.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                    th2 = th3;
                }
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            th2 = th3;
        }
        if (printWriter != null) {
            try {
                printWriter.close();
            } catch (IOException e22) {
                e22.printStackTrace();
            }
        }
        if (bufferedReader != null) {
            bufferedReader.close();
        }
        return str7;
    }*/
}
