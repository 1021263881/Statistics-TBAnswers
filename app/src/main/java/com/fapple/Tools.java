package com.fapple;

import android.text.*;
import android.webkit.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import com.fapple.tj.*;

public class Tools
{
	public static void loadHtmlInWebview(WebView webview, String Html)
	{
		webview.loadData(Html, "text/html", "UTF-8");
	}

	public static void copyToClipBoard(ClipboardManager cm, String text)
	{
		// 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        //ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(text);

		/*A. 创建普通字符型ClipData：
		 ClipData mClipData =ClipData.newPlainText("Label", "Content");         //‘Label’这是任意文字标签
		 B. 创建URL型ClipData：
		 //ClipData.newRawUri("Label",Uri.parse("http://www.baidu.com"));
		 C. 创建Intent型ClipData：

		 ClipData.newIntent("Label", intent);
		 注意：上面三种方法只在ClipData对象中创建了一个ClipData.Item对象，如果想向ClipData对象中添加多个Item应该通过ClipData对象的addItem()方法添加。

		 （3）将ClipData数据复制到剪贴板：
		 ClipboardManager.setPrimaryClip(ClipData对象);
		 （4）从剪贴板中获取ClipData数据：
		 ClipboardManager.getPrimaryClip();
		 */
	}
	public static String UrlEncodeUtf_8(String str) throws mException
	{
		try {
			return URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new mException("Url编码出错了喵~", "在UrlEncode遇到错误，编码前文本:“" + str + "”错误信息:" + e.toString() + "\n" + e.getMessage());
		}
	}
	public static String UrlDecodeUtf_8(String str)throws mException
	{
		try {
			return URLDecoder.decode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new mException("Url解码出错了喵~", "在UrlDecode遇到错误，编码前文本:“" + str + "”错误信息:" + e.toString() + "\n" + e.getMessage());
		}
	}
	//index re0
	public static String ZZ(String str, String biaodashi, Boolean daxiaoxie, Integer index)
	{
		if (str == "" || str == null || biaodashi == "" || biaodashi == null || index < 0) {
			return null;
		}
		Pattern pa;
		if (daxiaoxie == true) {
			pa = Pattern.compile(biaodashi);
		} else {
			pa = Pattern.compile(biaodashi, Pattern.CASE_INSENSITIVE);
		}
		Matcher ma = pa.matcher(str);
		//ma.matches();
		//String b;
		for (int i = 0; i < index; i++) {
			if(ma.find()){
				ma.group();
			}else{
				break;
			}
		}
		if (ma.find()) {
			return ma.group();
		} else {
			return null;
		}
	}
	public ArrayList<String> ZZall(String str, String biaodashi, Boolean daxiaoxie, Integer index)
	{
		ArrayList<String> list = new ArrayList<String>();
		if (str == "" || str == null || biaodashi == "" || biaodashi == null || index < 0) {
			return list;
		}
		Pattern pa;
		if (daxiaoxie == true) {
			pa = Pattern.compile(biaodashi);
		} else {
			pa = Pattern.compile(biaodashi, Pattern.CASE_INSENSITIVE);
		}
		Matcher ma = pa.matcher(str);
		//ma.matches();
		while(ma.find())
		{
			list.add(ma.group());
		}
		return list;
	}
	public String newNumber(int num)
	{
		num --;
		Random rnd = new Random();
		String re = "";
		Integer r = 9 * (int)Math.pow(10, num) - 1;
		r = rnd.nextInt(r) + (int)Math.pow(10, num);
		re += r;
		return re;
	}
	public static String getMD5(String content) throws mException
	{
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new mException("取MD5出错了喵~", "取MD5错误，编码前文本:“" + content + "”\n错误信息:" + e.toString() + "\n" + e.getMessage());
		}
		digest.update(content.getBytes());
		return getHashString(digest);
	}
	private static String getHashString(MessageDigest digest)
	{
        StringBuilder builder = new StringBuilder();
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString().toLowerCase();
    }
	public class TB
	{
		HttpService httpService = new HttpService();
		private String getStamp()
		{
			return "wappc_" + newNumber(5) + newNumber(4) + newNumber(4) + "_" + newNumber(3);
		}
		
		private String sign(String str)throws mException
		{
			return getMD5(UrlDecodeUtf_8(str.replace("&", "")) + "tiebaclient!!!").toUpperCase();
		}
		
		public ArrayList<Integer> getMax() throws mException{
			String url = "https://tieba.baidu.com/p/4592800021?last=1";
			String thing = httpService.get(url, "");
			ArrayList<Integer> all = new ArrayList<Integer>();
			if(thing != "" && thing != null)
			{
				String maxpage = ZZ(thing, "第\\d+/\\d+页", false, 0);
				maxpage = ZZ(maxpage, "\\d+", false, 1);
				String maxfloor = "";
				maxfloor = ZZ(thing, "<div class=\"i\">\\d+楼\\.", false, 0);
				maxfloor = ZZ(maxfloor, "\\d+", false, 0);
				all.add(Integer.valueOf(maxpage));
				all.add(Integer.valueOf(maxfloor));
			}
			return all;
		}
		private ArrayList<String> 获取主题列表(String cookie, String tiebaname, Integer pn) throws mException
        {
			ArrayList<String> list = new ArrayList<String>();
            String 贴吧名 = tiebaname;
            String 贴吧名_URL_UTF8 = UrlEncodeUtf_8(tiebaname);

            String url = "http://c.tieba.baidu.com/c/f/frs/page";
            String data = cookie;
			data += "&_client_id=" + getStamp();
            data += "&_client_type=2&_client_version=7.8.1&kw=";
            data += 贴吧名_URL_UTF8;
            data += "&pn=" + pn;
            data += "&rn=50";

			data += "&sign=" + sign(data);
			return list;
		}
		public ArrayList<String> get帖子(String cookie, String tid, Integer pn, Boolean daoxu) throws mException
		{
			ArrayList<String> list = new ArrayList<String>();
			String url = "http://c.tieba.baidu.com/c/f/pb/page";
			String data = cookie;
			data += "&_client_id=" + getStamp();
			data += "&_client_type=2&_client_version=8.8.8.3";
			data += "&kz=" + tid;
			data += "&pn=" + pn;
			if (daoxu) {
				data += "&r=1";
			}
			data += "&sign=" + sign(data);
			String thing = httpService.post(url, data);
			list.add(thing);
			return list;
		}
		public ArrayList<String> getlzl(String cookie, String tid, String pid, Integer pn) throws mException
        {
			ArrayList<String> list = new ArrayList<String>();
            String url = "http://c.tieba.baidu.com/c/f/pb/floor";
            String data = cookie;
            data += "&_client_id=" + getStamp();
        	data += "&_client_type=2&_client_version=8.8.8.3";
        	data += "&kz=" + tid;
            data += "&pid=" + pid;
            data += "&pn=" + pn;
			data += "&sign=" + sign(data);

			String thing = httpService.post(url, data);
			
			return list;
		}
	}
	public class HttpService
	{
		public String get(String Url, String data) throws mException 
		{
			//第一种方式
			ExecutorService executor = Executors.newCachedThreadPool();
			Task task = new Task("GET", Url, data);
			FutureTask<String> futureTask = new FutureTask<String>(task);
			executor.submit(futureTask);
			executor.shutdown();
			String re = "";
			try {
				re = futureTask.get();
			} catch (InterruptedException e) {
				throw new mException("好像遇到了些奇怪的错误汪~", "Get出错，URL“" + Url + "”，Data:“" + data + "”\n错误信息:" + e.toString() + "\n" + e.getMessage());
			} catch (ExecutionException e) {
				throw new mException("好像遇到了些奇怪的错误汪~", "Get出错，URL“" + Url + "”，Data:“" + data + "”\n错误信息:" + e.toString() + "\n" + e.getMessage());
			}
			if(re == "" || re == null){
				throw new mException("网络好像出了点问题喵~", "Get无接收，URL“" + Url + "”，Data:“" + data + "”");
			}
			return re;
		}
		public String post(String Url, String data) throws mException
		{
			//第一种方式
			ExecutorService executor = Executors.newCachedThreadPool();
			Task task = new Task("POST", Url, data);
			FutureTask<String> futureTask = new FutureTask<String>(task);
			executor.submit(futureTask);
			executor.shutdown();
			String re = "";
			try {
				re = futureTask.get();
			} catch (InterruptedException e) {
				throw new mException("好像遇到了些奇怪的错误汪~", "Post出错，URL“" + Url + "”，Data:“" + data + "”\n错误信息:" + e.toString() + "\n" + e.getMessage());
			} catch (ExecutionException e) {
				throw new mException("好像遇到了些奇怪的错误汪~", "Post出错，URL“" + Url + "”，Data:“" + data + "”\n错误信息:" + e.toString() + "\n" + e.getMessage());
			}
			if(re == "" || re == null){
				throw new mException("网络好像出了点问题喵~", "Post无接收，URL“" + Url + "”，Data:“" + data + "”");
			}
			return re;
		}
		class Task implements Callable<String>
		{
			private String method;
			private String Url;
			private String data;
			Task(String method, String Url, String data)
			{
				this.method = method;
				this.Url = Url;
				this.data = data;
			}
			@Override
			public String call() throws IOException
			{
				switch (method) {
					case "GET":
						return Get(Url, data);
					case "POST":
						return Post(Url, data);
					default:
						return "Error Method!";
				}
			}
		}
		private String Get(String Url, String data)throws  UnsupportedEncodingException, IOException
		{
			//string转URL(utf-8)编码
			//产生UnsupportedEncodingException
			data = URLEncoder.encode(data, "utf-8");

			// 新建一个URL对象
			if (data != "" && data != null) {
				Url += "?" + data;
			}
			URL url = new URL(Url);

			// 打开一个HttpURLConnection连接
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

			// 设置连接主机超时时间
			urlConn.setConnectTimeout(5 * 1000);

			//设置从主机读取数据超时
			urlConn.setReadTimeout(5 * 1000);

			// 设置是否使用缓存  默认是true
			urlConn.setUseCaches(true);

			// 设置为Get请求
			urlConn.setRequestMethod("GET");

			//urlConn设置请求头信息
			//设置请求中的媒体类型信息。
			//urlConn.setRequestProperty("Content-Type", "application/json");

			//设置客户端与服务连接类型
			urlConn.addRequestProperty("Connection", "Keep-Alive");

			// 开始连接
			urlConn.connect();

			// 判断请求是否成功
			String result;
			if (urlConn.getResponseCode() == 200) {
				// 获取返回的数据
				result = streamToString(urlConn.getInputStream());
				//Log.e(TAG, "Get方式请求成功，result--->" + result);
			} else {
				result = null;
				//Log.e(TAG, "Get方式请求失败");
			}

			// 关闭连接
			urlConn.disconnect();
			return result;
		}
		private String Post(String Url, String data)throws  UnsupportedEncodingException, IOException
		{
			//string转URL(utf-8)编码
			//产生UnsupportedEncodingException
			data = URLEncoder.encode(data, "utf-8");

			// 请求的参数转换为byte数组
			byte[] postData = data.getBytes();

			// 新建一个URL对象
			URL url = new URL(Url);

			// 打开一个HttpURLConnection连接
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

			// 设置连接超时时间
			urlConn.setConnectTimeout(5 * 1000);

			//设置从主机读取数据超时
			urlConn.setReadTimeout(5 * 1000);

			// Post请求必须设置允许输出 默认false
			urlConn.setDoOutput(true);

			//设置请求允许输入 默认是true
			urlConn.setDoInput(true);

			// Post请求不能使用缓存
			urlConn.setUseCaches(false);

			// 设置为Post请求
			urlConn.setRequestMethod("POST");

			//设置本次连接是否自动处理重定向
			urlConn.setInstanceFollowRedirects(true);

			// 配置请求Content-Type
			urlConn.setRequestProperty("Content-Type", "application/json");
			
			//设置请求体的类型是文本类型
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			//设置请求体的长度
			urlConn.setRequestProperty("Content-Length", String.valueOf(postData.length));
			
			// 开始连接
			urlConn.connect();

			// 发送请求参数
			DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
			dos.write(postData);
			dos.flush();
			dos.close();

			// 判断请求是否成功
			String result;
			if (urlConn.getResponseCode() == 200) {
				// 获取返回的数据
				result = streamToString(urlConn.getInputStream());
				//Log.e(TAG, "Post方式请求成功，result--->" + result);
			} else {
				result = null;
				//Log.e(TAG, "Post方式请求失败");
			}
			// 关闭连接
			urlConn.disconnect();

			return result;
			//Log.e(TAG, e.toString());
		}

		/**
		 * 将输入流转换成字符串
		 *
		 * @param is 从网络获取的输入流
		 * @return
		 */
		private String streamToString(InputStream is)throws IOException
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			baos.close();
			is.close();
			byte[] byteArray = baos.toByteArray();
			return new String(byteArray);
		}
	}
}

