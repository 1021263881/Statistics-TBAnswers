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
import org.json.*;
import android.util.*;

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

	//Url编码
	public static String UrlEncodeUtf_8(String str) throws mException
	{
		try {
			return URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new mException("Url编码出错了喵~", "UrlEncode出错，编码前文本:“" + str + "”错误信息:" + e.toString() + "\n" + e.getMessage());
		}
	}

	//Url解码
	public static String UrlDecodeUtf_8(String str)throws mException
	{
		try {
			return URLDecoder.decode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new mException("Url解码出错了喵~", "UrlDecode出错，编码前文本:“" + str + "”错误信息:" + e.toString() + "\n" + e.getMessage());
		}
	}

	//index re0
	public static String ZZ(String str, String biaodashi, Boolean daxiaoxie, int index)
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
		for (int i = 0; i < index; i++) {
			if (ma.find()) {
				ma.group();
			} else {
				break;
			}
		}
		if (ma.find()) {
			return ma.group();
		} else {
			return null;
		}
	}
	public ArrayList<String> ZZall(String str, String biaodashi, Boolean daxiaoxie, int index)
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
		while (ma.find()) {
			list.add(ma.group());
		}
		return list;
	}
	public String newNumber(int num)
	{
		num --;
		Random rnd = new Random();
		String re = "";
		int r = 9 * (int)Math.pow(10, num) - 1;
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
		private int pagemax;
		private int pagenow = 1;
		private int floormax;
		private int floornow = 1;

		ArrayMap<String, String> floor = new ArrayMap<String, String>();
		ArrayMap<String, String> lzl = new ArrayMap<String, String>();

		HttpService httpService = new HttpService();

		public TB()
		{
			//初始化楼层
			floor.put("id", "");
			floor.put("nicheng", "");
			floor.put("time", "");
			floor.put("pid", "");
		}

		private String getStamp()
		{
			return "wappc_" + newNumber(5) + newNumber(4) + newNumber(4) + "_" + newNumber(3);
		}

		private String sign(String str)throws mException
		{
			return getMD5(UrlDecodeUtf_8(str.replace("&", "")) + "tiebaclient!!!").toUpperCase();
		}

		public ArrayList<Integer> getMax() throws mException
		{
			String url = "https://tieba.baidu.com/p/4592800021?last=1";
			String thing = httpService.get(url, "");
			ArrayList<Integer> all = new ArrayList<Integer>();
			if (thing != "" && thing != null) {
				String maxpage = ZZ(thing, "第\\d+/\\d+页", false, 0);
				maxpage = ZZ(maxpage, "\\d+", false, 1);
				String maxfloor = "";
				maxfloor = ZZ(thing, "<div class=\"i\">\\d+楼\\.", false, 0);
				maxfloor = ZZ(maxfloor, "\\d+", false, 0);
				all.add(Integer.valueOf(maxpage));
				all.add(Integer.valueOf(maxfloor));

				pagemax = Integer.valueOf(maxpage);
				floormax = Integer.valueOf(maxfloor);
			}
			return all;
		}
		public int getPageMax()
		{
			return pagemax;
		}
		public int getFloorMax()
		{
			return floormax;
		}
		private ArrayList<String> 获取主题列表(String cookie, String tiebaname, int pn) throws mException
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
		private JSONArray getpage(String cookie, String tid, int pn, int mpn, Boolean daoxu) throws mException
		{
			String url = "http://c.tieba.baidu.com/c/f/pb/page";
			String data = cookie;
			data += "&_client_id=" + getStamp();
			data += "&_client_type=2&_client_version=8.8.8.3";
			data += "&kz=" + tid;
			if (daoxu == true && pn == mpn) {
				data += "&last=1&r=1";
			} else if (daoxu == true) {
				data += "&pn=" + pn;
				data += "&r=1";
			} else {
				data += "&pn=" + pn;
			}
			data += "&sign=" + sign(data);

			String thing = httpService.post(url, data);

			JSONObject page;
			try {
				page = new JSONObject(thing);
			} catch (JSONException e) {
				throw new mException("JSON解析好像遇到问题了", "JSON解析错误, Str:“" + thing + "”, 错误信息:" + e.toString());
			}
			pagemax = page.optJSONObject("page").optInt("total_page");
			return page.optJSONArray("post_list");
		}
		private JSONObject getfloor(String cookie, String tid, String pid, int pn) throws mException
        {
            String url = "http://c.tieba.baidu.com/c/f/pb/floor";
            String data = cookie;
            data += "&_client_id=" + getStamp();
        	data += "&_client_type=2&_client_version=8.7.8.6";
        	data += "&kz=" + tid;
            data += "&pid=" + pid;
            data += "&pn=" + pn;
			data += "&sign=" + sign(data);

			String thing = httpService.post(url, data);

			JSONObject floor;
			try {
				floor = new JSONObject(thing);
			} catch (JSONException e) {
				throw new mException("JSON解析好像遇到问题了", "JSON解析错误, Str:“" + thing + "”, 错误信息:" + e.toString());
			}
			return floor.optJSONObject("a");
		}
		public HashMap<Integer, HashMap<String, String>> anaPage(JSONArray list)
		{
			int len = list.length();
			if (len < 1) {
				return null;
			}
			HashMap<Integer, HashMap<String, String>> tz = new HashMap<Integer, HashMap<String, String>>();
			for (int i = 0;i < len; i++) {

			}
			return tz;
		}
		private String anaContent(JSONArray content)
		{
			int len = content.length();
			if (len < 1) {
				return null;
			}
			String text = "";
			for(int i = 0; i < len; i++){
				text += classifyToHtml(content.optJSONObject(i));
			}
			return null;
		}
		private String classifyToHtml(JSONObject content){
			String type = content.optString("type", "0");
			String thing = "";
			if (type == "0") {
				//thing += (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"];
				thing += content.optString("text", "");
			} else if (type == "1") {
				//{"type":1,"link":"网址","text":"网址"}
				//thing += "#链接=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"] + "#";
				thing += "<a href=\"" + content.optString("link", "") + "\">" + content.optString("text", "") + "<\\a>";
				//控制台_输出(0, "#链接# " + (String)内容源码["post_list"][内容计数]["content"][文本计数]["link"]);
			} else if (type == "2") {
				thing += "#表情=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["c"] + "#";
			} else if (type == "3") {
				//{"type":3,"src":"链接","bsize":"189,199","size":"49634"}
				//Console.WriteLine((String)内容源码["post_list"][内容计数]["content"].ToString());
				//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["origin_src"] + "=MD5=" + tlib.取网络资源MD5((String)内容源码["post_list"][内容计数]["content"][文本计数]["origin_src"]) + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
				thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["origin_src"] + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
			} else if (type == "4") {
				thing += "#艾特=" + 内容源码["post_list"][内容计数]["content"][文本计数]["text"].ToString().Replace("@", "") + "#";
			} else if (type == "5") {
				//{"type":5,"e_type":15,"width":"480","height":"480","bsize":"480,480","during_time":"2","origin_size":"168046","text":"http:\/\/tieba.baidu.com\/mo\/q\/movideo\/page?thumbnail=d109b3de9c82d158d3fcee1d880a19d8bc3e421b&video=10363_ed294eae88371575b3dbcf9f1990f68d","link":"http:\/\/tb-video.bdstatic.com\/tieba-smallvideo\/10363_ed294eae88371575b3dbcf9f1990f68d.mp4","src":"http:\/\/imgsrc.baidu.com\/forum\/pic\/item\/d109b3de9c82d158d3fcee1d880a19d8bc3e421b.jpg","is_native_app":0,"native_app":[]}
				//thing += "#视频#";
			} else if (type == "7") {
				//{"type":"7","text":"\n"}
				thing += "<br>";
			} else if (type == "9") {
				//{"type":"9","text":"6666666","phonetype":"2"}
				thing += (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"];
			} else if (type == "10") {
				//{"type":"10","during_time":"15000","voice_md5":"e25ef2db5076f825e229c6cdb1613f38_1064475243"}
				//thing += "#语音=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["voice_md5"] + "," + (String)内容源码["post_list"][内容计数]["content"][文本计数]["during_time"] + "#";
			} else if (type == "11") {
				//{"type":"11","c":"白发魔女传之明月天国_女屌丝","static":"png静态图链接","dynamic":"gif动态图链接","height":"160","width":"160","icon":"http://tb2.bdstatic.com/tb/editor/images/faceshop/1058_baifa/panel.png","packet_name":"白发魔女传之明月天国"}
				//thing += "#表情=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["c"] + "#";
			} else if (type == "16") {
				//{"type":"16","bsize":"560,560","graffiti_info":{"url":"jpg网页端原图","gid":"123456"},"cdn_src":"客户端缩略图","big_cdn_src":"客户端大图"}
				//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["graffiti_info"]["url"] + "=MD5=" + tlib.取网络资源MD5((String)内容源码["post_list"][内容计数]["content"][文本计数]["graffiti_info"]["url"]) + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";

				thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["graffiti_info"]["url"] + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
			} else if (type == "17") {
				//{"type":"17","high_together":{"album_id":"478448408116821906","album_name":"关于众筹西游记歌曲演唱会活动","start_time":"0","end_time":"0","location":"","num_join":"0","pic_urls":[]}}
				//thing += "#活动=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["album_name"] + "#";
			} else if (type == "18") {
				//{"type":"18","text":"#白狐狸不改国庆礼包就滚出dnf#","link":"http://tieba.baidu.com/mo/q/hotMessage?topic_id=0&topic_name=白狐狸不改国庆礼包就滚出dnf"}
				//thing += "#热议=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"] + "#";
			} else if (type == "20") {
				//{"type":"20","src":"http:\/\/imgsrc.baidu.com\/forum\/pic\/item\/4c086e061d950a7bce3c370300d162d9f3d3c9e8.jpg","bsize":"375,348","meme_info":{"pck_id":"0","pic_id":"47098639564","width":"375","height":"348","pic_url":"http:\/\/imgsrc.baidu.com\/forum\/pic\/item\/4c086e061d950a7bce3c370300d162d9f3d3c9e8.jpg","thumbnail":"http:\/\/imgsrc.baidu.com\/forum\/abpic\/item\/4c086e061d950a7bce3c370300d162d9f3d3c9e8.jpg","detail_link":"http:\/\/tieba.baidu.com\/n\/interact\/emoticon\/0\/47098639564?frompb=1"}}

				//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["src"] + "=MD5=" + tlib.取网络资源MD5((String)内容源码["post_list"][内容计数]["content"][文本计数]["src"]) + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
				thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["src"] + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
			}
			return thing;
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
			if (re == "" || re == null) {
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
			if (re == "" || re == null) {
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
						return Post_(Url, data);
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
		/*
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
		 */
		private String Post_(String path, String Info) throws IOException
		{ 

			//1, 得到URL对象 
			URL url = new URL(path); 

			//2, 打开连接 
			HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 

			//3, 设置提交类型 
			conn.setRequestMethod("POST"); 

			//4, 设置允许写出数据,默认是不允许 false 
			conn.setDoOutput(true); 
			conn.setDoInput(true);//当前的连接可以从服务器读取内容, 默认是true 

			//5, 获取向服务器写出数据的流 
			OutputStream os = conn.getOutputStream(); 
			//参数是键值队  , 不以"?"开始 
			os.write(Info.getBytes()); 
			//os.write("googleTokenKey=&username=admin&password=5df5c29ae86331e1b5b526ad90d767e4".getBytes()); 
			os.flush();
			//6, 获取响应的数据 
			//得到服务器写回的响应数据 
			BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			String str = br.readLine();   
			//System.out.println("响应内容为:  " + str); 

			return  str;
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

