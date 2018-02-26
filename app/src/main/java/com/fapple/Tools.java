package com.fapple;

import android.text.*;
import android.util.*;
import android.webkit.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import org.json.*;
import android.app.*;
import com.fapple.tj.*;

public class Tools
{
	public static void loadHtmlInWebview(WebView webview, String Html)
	{
		String thing = "<html><header>" + getCSS() + "</header><body>" + Html + "</body></html>";
		webview.loadData(thing, "text/html", "UTF-8");
	}

	//自适应CSS
	private static String getCSS()
	{
		return "<style type=\"text/css\"> img {" +
			"width:100%;" +//限定图片宽度填充屏幕
			"height:auto;" +//限定图片高度自动
			"}" +
			"body {" +
			"margin-right:15px;" +//限定网页中的文字右边距为15px(可根据实际需要进行行管屏幕适配操作)
			"margin-left:15px;" +//限定网页中的文字左边距为15px(可根据实际需要进行行管屏幕适配操作)
			"margin-top:15px;" +//限定网页中的文字上边距为15px(可根据实际需要进行行管屏幕适配操作)
			"font-size:42px;" +//限定网页中文字的大小为40px,请务必根据各种屏幕分辨率进行适配更改
			"word-wrap:break-word;" +//允许自动换行(汉字网页应该不需要这一属性,这个用来强制英文单词换行,类似于word/wps中的西文换行)
			"}" +
			"</style>";
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

	//正则匹配 index re0
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

	//产生一个新的随机数，传参随意位数
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

	//获取md5
	public static String getMD5(String content) throws mException
	{
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new mException("取MD5出错了喵~", "取MD5错误，编码前文本:“" + content + "”\n错误信息:" + e.toString() + "\n" + e.getMessage());
		}
		digest.update(content.getBytes());

		StringBuilder builder = new StringBuilder();
		for (byte b : digest.digest()) {
			builder.append(Integer.toHexString((b >> 4) & 0xf));
			builder.append(Integer.toHexString(b & 0xf));
		}
		return builder.toString().toLowerCase();
	}

	//字符串转时间戳
	/*public static String strTimeToUnix(String time)
	 {
	 String timeStamp = null;
	 //日期格式，yyyy-MM-dd HH:mm:ss
	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 Date d;
	 try {
	 d = sdf.parse(time);
	 long l = d.getTime();
	 timeStamp = String.valueOf(l);
	 } catch (ParseException e) {
	 e.printStackTrace();
	 }
	 return timeStamp;
	 }*/

	// 将时间戳转为字符串
	public static String unixToStrTime(String cc_time)
	{
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		return re_StrTime;
	}

	public class TB
	{
		private int pagemax = 1;
		private int pagenow = 1;
		private int floormax = 1;
		private int floornow = 1;
		private int indexInList = 0;

		private String tid = "";
		private MainActivity main;

		//对楼层，楼中楼，user建档
		private ArrayMap<Integer, ArrayList<ArrayMap<String, String>>> flr = new ArrayMap<Integer, ArrayList<ArrayMap<String, String>>>();
		private ArrayMap<String, ArrayList<ArrayMap<String, String>>> lzl = new ArrayMap<String, ArrayList<ArrayMap<String, String>>>();
		private ArrayMap<String, ArrayMap<String, String>> ps = new ArrayMap<String, ArrayMap<String, String>>();

		//private ArrayMap<String, String> mflr = new ArrayMap<String, String>();
		//private ArrayMap<String, String> mlzl = new ArrayMap<String, String>();
		//private ArrayMap<String, String> mps = new ArrayMap<String, String>();

		HttpService httpService = new HttpService();

		public TB(MainActivity main, String tid)
		{
			this.main = main;
			this.tid = tid;

			//初始化楼层
			/*mflr.put("tid", "");//4592800021
			 mflr.put("pid", "");
			 mflr.put("id", "");
			 mflr.put("nickname", "");
			 mflr.put("level", "");
			 mflr.put("content", "");
			 mflr.put("floor", "");
			 mflr.put("time", "");

			 //初始化楼中楼
			 mlzl.put("tid", "");//4592800021
			 mlzl.put("pid", "");
			 mlzl.put("spid", "");
			 mlzl.put("id", "");
			 mlzl.put("nickname", "");
			 mlzl.put("level", "");
			 mlzl.put("content", "");
			 mlzl.put("time", "");

			 mps.put("id", "");
			 mps.put("nickname", "");
			 mps.put("level", "");
			 mps.put("aid", "");*/

			try {
				jumpPage(1);
			} catch (mException e) {

			}
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
			String url = "http://c.tieba.baidu.com/c/f/pb/page";
			String data = "";
			data += "&_client_id=" + getStamp();
			data += "&_client_type=2&_client_version=8.8.8.3";
			data += "&kz=" + tid;
			data += "&last=1&r=1";
			data += "&sign=" + sign(data);

			String thing = httpService.post(url, data);

			JSONObject page;
			try {
				page = new JSONObject(thing);
				String maxpage = page.optJSONObject("page").optString("total_page");
				String maxfloor = page.optJSONArray("post_list").optJSONObject(0).optString("floor");
				all.add(Integer.valueOf(maxpage));
				all.add(Integer.valueOf(maxfloor));

				pagemax = Integer.valueOf(maxpage);
				floormax = Integer.valueOf(maxfloor);
			} catch (JSONException e) {
				throw new mException("JSON解析好像遇到问题了", "获取帖子JSON解析错误, Str:“" + thing + "”, 错误信息:" + e.toString());
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
		public int getPageNow()
		{
			return pagenow;
		}
		public int getFloorNow()
		{
			return floornow;
		}
		
		private ArrayList<String> getLastFloor() throws mException
		{
			if (indexInList == 0) {
				if (pagenow > 1) {
					pagenow --;
					jumpPage(pagenow);
					indexInList = flr.get(pagenow).size() - 1;
					return getReturn(flr.get(pagenow).get(indexInList));
				} else {
					throw new mException("已经是第一页了", "");
				}
			} else {
				indexInList--;
				return getReturn(flr.get(pagenow).get(indexInList));
			}
		}
		private ArrayList<String> getNextFloor() throws mException
		{
			if (indexInList == flr.get(pagenow).size() - 1) {
				if (pagenow < pagemax) {
					return getReturn(getNextPage());
				} else {
					getMax();
					if (pagenow < pagemax) {
						return getNextFloor();
					} else {
						throw new mException("获取下一页失败", "超出范围");
					}
				}
			} else {
				indexInList ++;
				return getReturn(flr.get(pagenow).get(indexInList));
			}
		}
		private ArrayList<String> getNextPage() throws mException
		{
			if (pagenow < pagemax) {
				pagenow ++;
				return getReturn(jumpPage(pagenow));
			} else {
				getMax();
				if (pagenow < pagemax) {
					return getNextPage();
				} else {
					throw new mException("获取下一页失败", "获取下一页失败, page=" + pagenow + ", pagemax=" + pagemax);
				}
			}
		}
		private ArrayList<String> jumpFloor(int floor) throws mException
		{
				if (floor > floornow) {
					ArrayList<ArrayMap<String, String>> page;
					ArrayMap<String, String> flmap;
					//遍历page
					for(int p = pagenow; p <= pagemax; p++){
						if(flr.containsKey(p) == false){
							getPage("", tid, p);
						}
						page = flr.get(p);
						//遍历楼层
						for(int i = page.size() - 1; i > -1; i--){
							flmap = page.get(i);
							if(Integer.valueOf(flmap.get("floor")) >= floor){
								return getReturn(flmap);
							}
						}
					}
					throw new mException("没有找到该楼层", "");
				} else if (floor < floornow) {
					ArrayList<ArrayMap<String, String>> page;
					ArrayMap<String, String> flmap;
					//遍历page
					for(int p = pagenow; p > 0; p--){
						if(flr.containsKey(p) == false){
							getPage("", tid, p);
						}
						page = flr.get(p);
						//遍历楼层
						for(int i = 0; i < page.size(); i++){
							flmap = page.get(i);
							if(Integer.valueOf(flmap.get("floor")) <= floor){
								return getReturn(flmap);
							}
						}
					}
					throw new mException("没有找到该楼层", "");
				} else {
					return getReturn(flr.get(pagenow).get(indexInList));
				}
		}
		private ArrayList<String> jumpPage(int page) throws mException 
		{
			indexInList = 0;
			getPage("", tid, page, false);
			if (flr.containsKey(pagenow) == true) {
				return getReturn(flr.get(pagenow).get(0));
			} else {
				throw new mException("楼层不存在", "jumppage时楼层不存在, page=" + page);
			}
		}

		/*-------------------------内部处理--------------------------*/
		//等待维护
		private JSONArray gettz(String cookie, String tiebaname, int pn, int sorttype) throws mException
		{
			String 贴吧名_URL_UTF8 = UrlEncodeUtf_8(tiebaname);

			String url = "http://c.tieba.baidu.com/c/f/frs/page";
			String data = cookie;
			data += "&_client_id=" + getStamp();
			data += "&_client_type=2&_client_version=7.8.1&kw=";
			data += 贴吧名_URL_UTF8;
			data += "&pn=" + pn;
			data += "&rn=50";
			data += "&sort_type=" + sorttype;//回复0发帖1智障3
			data += "&sign=" + sign(data);

			String thing = httpService.post(url, data);
			JSONObject list;
			try {
				list = new JSONObject(thing);
			} catch (JSONException e) {
				throw new mException("JSON解析好像遇到问题了", "主题列表JSON解析错误, Str:“" + thing + "”, 错误信息:" + e.toString());
			}
			//return list.optJSONArray("thread_list");
			return null;
		}
		private void getPage(String cookie, String tid, int pn) throws mException{
			getPage(cookie, tid, pn, false);
		}
		private void getPage(String cookie, String tid, int pn, boolean daoxu) throws mException
		{
			String url = "http://c.tieba.baidu.com/c/f/pb/page";
			String data = cookie;
			data += "&_client_id=" + getStamp();
			data += "&_client_type=2&_client_version=8.8.8.3";
			data += "&kz=" + tid;
			if (daoxu == true) {
				data += "&last=1&r=1";
			} else {
				data += "&pn=" + pn;
			}
			data += "&sign=" + sign(data);

			String thing = httpService.post(url, data);

			JSONObject page;
			try {
				page = new JSONObject(thing);
			} catch (JSONException e) {
				throw new mException("JSON解析好像遇到问题了", "获取帖子JSON解析错误, Str:“" + thing + "”, 错误信息:" + e.toString());
			}
			pagemax = Integer.valueOf(page.optJSONObject("page").optString("total_page", String.valueOf(pagemax)));
			if (daoxu == false) {
				pagenow = Integer.valueOf(page.optJSONObject("page").optString("current_page", String.valueOf(pagenow)));
			}
			if(pagenow > pagemax){
				pagenow = pagemax;
			}
			if (anaPerson(page.optJSONArray("user_list")) == false) {
				throw new mException("解析page的user_list错误");
			}
			anaPage(page.optJSONArray("post_list"), tid, pagenow);
		}

		//自带遍历
		private void getlzl(String cookie, String tid, String pid, int pn) throws mException
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

			JSONObject lzl;
			try {
				lzl = new JSONObject(thing);
			} catch (JSONException e) {
				throw new mException("JSON解析好像遇到问题了", "获取楼中楼JSON解析错误, Str:“" + thing + "”, 错误信息:" + e.toString());
			}
			analzl(lzl.optJSONArray("subpost_list"), tid, pid);

			//如果没有到底就继续
			int pmax = Integer.valueOf(lzl.optJSONObject("page").optString("total_page"));
			if (pn < pmax) {
				getlzl("", tid, pid, pn + 1);
			}
		}
		private void anaPage(JSONArray list, String tid, int page) throws mException
		{
			int len = list.length();
			if (len < 1) {
				return ;
			}
			ArrayList<ArrayMap<String, String>> floorlist = new ArrayList<ArrayMap<String, String>>();
			JSONObject fl;
			ArrayMap<String, String> epg;
			ArrayMap<String, String> nflr;
			for (int i = 0;i < len; i++) {
				fl = list.optJSONObject(i);
				nflr = new ArrayMap<String, String>();
				//nflr.putAll(mflr);
				nflr.put("tid", tid);
				nflr.put("pid", fl.optString("id", ""));
				epg = ps.get(fl.optString("author_id", ""));
				nflr.put("id", epg.get("id"));
				nflr.put("nickname", epg.get("nickname"));
				nflr.put("level", epg.get("level"));
				nflr.put("content", anaContent(fl.optJSONArray("content")));
				nflr.put("floor", fl.optString("floor", ""));
				nflr.put("time", unixToStrTime(fl.optString("time", "")));

				floorlist.add(nflr);
			}
			flr.put(page, floorlist);
		}
		private void analzl(JSONArray list, String tid, String pid) throws mException
		{
			int len = list.length();
			if (len < 1) {
				return ;
			}
			ArrayList<ArrayMap<String, String>> lzllist = new ArrayList<ArrayMap<String, String>>();
			JSONObject fl;
			ArrayMap<String, String> epg;
			ArrayMap<String, String> nlzl;
			for (int i = 0;i < len; i++) {
				fl = list.optJSONObject(i);
				nlzl = new ArrayMap<String, String>();
				nlzl.put("tid", tid);
				nlzl.put("pid", pid);
				nlzl.put("spid", fl.optString("id"));
				epg = anaPerson(fl.optJSONObject("author"));
				nlzl.put("id", epg.get("id"));
				nlzl.put("nickname", epg.get("nickname"));
				nlzl.put("level", epg.get("level"));
				nlzl.put("content", anaContent(fl.optJSONArray("content")));
				nlzl.put("time", unixToStrTime(fl.optString("time")));
				lzllist.add(nlzl);
			}
			lzl.put(pid, lzllist);
		}
		private boolean anaPerson(JSONArray list)
		{
			int len = list.length();
			if (len < 1) {
				return false;
			}
			ArrayMap<String, ArrayMap<String, String>> pslist = new ArrayMap<String, ArrayMap<String, String>>();
			JSONObject eps;
			ArrayMap<String, String> mps;
			for (int i = 0;i < len; i++) {
				eps = list.optJSONObject(i);
				mps = new ArrayMap<String, String>();
				mps.put("aid", eps.optString("id", ""));
				mps.put("id", eps.optString("name", "[未获取到用户ID]"));
				mps.put("nickname", eps.optString("name_show", "[未获取到用户昵称]"));
				mps.put("level", eps.optString("level_id", "[未获取到用户等级]"));
				pslist.put(mps.get("aid").toString(), new ArrayMap<String, String>(mps));
			}
			if (pslist.size() == list.length()) {
				ps.putAll(pslist);
				return true;
			} else {
				return false;
			}
		}
		private ArrayMap<String, String> anaPerson(JSONObject eps)
		{
			if (eps == null) {
				return null;
			}
			ArrayMap<String, String> mps = new ArrayMap<String, String>();
			mps.put("aid", eps.optString("id"));
			mps.put("id", eps.optString("name", "[未获取到用户ID]"));
			mps.put("nickname", eps.optString("name_show", "[未获取到用户昵称]"));
			mps.put("level", eps.optString("level_id", "[未获取到用户等级]"));
			ps.put(mps.get("aid"), mps);
			return mps;
		}
		private ArrayList<String> getReturn(ArrayMap<String, String> floor) throws mException
		{
			ArrayList<String> thing = new ArrayList<String>();
			floornow = Integer.valueOf(floor.get("floor"));
			thing.add(String.valueOf(pagenow));
			thing.add(String.valueOf(pagemax));
			thing.add(String.valueOf(floornow));
			thing.add(String.valueOf(floormax));

			getlzl("", floor.get("tid"), floor.get("pid"), 1);

			String content = "";

			ArrayList<ArrayMap<String, String>> lzl = this.lzl.get(floor.get("pid"));
			int len = 0;
			if (lzl != null) {
				len = lzl.size();
			}
			ArrayList<String> pr = new ArrayList<String>();
			ArrayMap<String, String> elzl = null;

			content += "<a href=\"http://tieba.baidu.com/home/main/?un=" + floor.get("id") + "\">" + floor.get("nickname") + "</a>   Level-" + floor.get("level") + "  :<br>";
			content += floor.get("content") + "<br><br><div style=\"float:right;\">------" + floor.get("floor") + "楼  " + floor.get("time") + "</div><br>";
			content += "<hr>";

			for (int i = 0; i < len; i++) {
				elzl = lzl.get(i);
				content += "<a href=\"http://tieba.baidu.com/home/main/?un=" + elzl.get("id") + "\">" + elzl.get("nickname") + "</a>: " + elzl.get("content") + "<br><div style=\"float:right;\">------" + elzl.get("time") + "</div><br>";
				pr.add(elzl.get("id"));
				pr.add(elzl.get("nickname"));
			}
			thing.add(content);
			thing.addAll(pr);
			return thing;
		}
		private String anaContent(JSONArray content) throws mException
		{
			int len = content.length();
			if (len < 1) {
				return null;
			}
			String text = "";
			for (int i = 0; i < len; i++) {
				text += anaContentToHtml(content.optJSONObject(i));
			}
			return text;
		}
		private String anaContentToHtml(JSONObject content) throws mException
		{
			String type = content.optString("type", "0");
			String thing = "";
			if (type.equals("0")) {
				//thing += (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"];
				thing = content.optString("text", "[文本获取失败]");
			} else if (type.equals("1")) {
				//{"type":1,"link":"网址","text":"网址"}
				//thing += "#链接=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"] + "#";
				thing = "<a href=\"" + content.optString("link", "") + "\">" + content.optString("text", "") + "</a>";
				//控制台_输出(0, "#链接# " + (String)内容源码["post_list"][内容计数]["content"][文本计数]["link"]);
			} else if (type.equals("2")) {
				//{"type": "2","text": "image_emoticon33","c": "喷"}
				//thing += "#表情=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["c"] + "#";
				thing = "[贴吧表情:" + content.optString("c") + "]";
			} else if (type.equals("3")) {
				//{"type":3,"src":"链接","bsize":"189,199","size":"49634"}
				//Console.WriteLine((String)内容源码["post_list"][内容计数]["content"].ToString());
				//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["origin_src"] + "=MD5=" + tlib.取网络资源MD5((String)内容源码["post_list"][内容计数]["content"][文本计数]["origin_src"]) + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
				//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["origin_src"] + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
				thing = "<img src=\"" + content.optString("origin_src") + "\" alt=\"图片加载失败，请反馈至1021263881@qq.com\" >";
			} else if (type.equals("4")) {
				//thing += "#艾特=" + 内容源码["post_list"][内容计数]["content"][文本计数]["text"].ToString().Replace("@", "") + "#";
				thing += "<a href=\"http://tieba.baidu.com/home/main/?un=" + UrlEncodeUtf_8(content.optString("text", "").replace("@", "")) + "\">" + content.optString("text", "") + "</a>";
			} else if (type.equals("5")) {
				//{"type":5,"e_type":15,"width":"480","height":"480","bsize":"480,480","during_time":"2","origin_size":"168046","text":"http:\/\/tieba.baidu.com\/mo\/q\/movideo\/page?thumbnail=d109b3de9c82d158d3fcee1d880a19d8bc3e421b&video=10363_ed294eae88371575b3dbcf9f1990f68d","link":"http:\/\/tb-video.bdstatic.com\/tieba-smallvideo\/10363_ed294eae88371575b3dbcf9f1990f68d.mp4","src":"http:\/\/imgsrc.baidu.com\/forum\/pic\/item\/d109b3de9c82d158d3fcee1d880a19d8bc3e421b.jpg","is_native_app":0,"native_app":[]}
				//thing += "#视频#";
			} else if (type.equals("7")) {
				//{"type":"7","text":"\n"}
				thing = "<br>";
			} else if (type.equals("9")) {
				//{"type":"9","text":"6666666","phonetype":"2"}
				//thing += (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"];
				thing = content.optString("text", "");
			} else if (type.equals("10")) {
				//{"type":"10","during_time":"15000","voice_md5":"e25ef2db5076f825e229c6cdb1613f38_1064475243"}
				//thing += "#语音=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["voice_md5"] + "," + (String)内容源码["post_list"][内容计数]["content"][文本计数]["during_time"] + "#";
			} else if (type.equals("11")) {
				//{"type":"11","c":"白发魔女传之明月天国_女屌丝","static":"png静态图链接","dynamic":"gif动态图链接","height":"160","width":"160","icon":"http://tb2.bdstatic.com/tb/editor/images/faceshop/1058_baifa/panel.png","packet_name":"白发魔女传之明月天国"}
				//thing += "#表情=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["c"] + "#";
			} else if (type.equals("16")) {
				//{"type":"16","bsize":"560,560","graffiti_info":{"url":"jpg网页端原图","gid":"123456"},"cdn_src":"客户端缩略图","big_cdn_src":"客户端大图"}
				//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["graffiti_info"]["url"] + "=MD5=" + tlib.取网络资源MD5((String)内容源码["post_list"][内容计数]["content"][文本计数]["graffiti_info"]["url"]) + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
				//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["graffiti_info"]["url"] + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
				thing = "<img src=\"" + content.optJSONObject("graffiti_info").optString("url") + "alt=\"图片加载失败，请反馈至1021263881@qq.com\" >";
			} else if (type.equals("17")) {
				//{"type":"17","high_together":{"album_id":"478448408116821906","album_name":"关于众筹西游记歌曲演唱会活动","start_time":"0","end_time":"0","location":"","num_join":"0","pic_urls":[]}}
				//thing += "#活动=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["album_name"] + "#";
			} else if (type.equals("18")) {
				//{"type":"18","text":"#白狐狸不改国庆礼包就滚出dnf#","link":"http://tieba.baidu.com/mo/q/hotMessage?topic_id=0&topic_name=白狐狸不改国庆礼包就滚出dnf"}
				//thing += "#热议=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"] + "#";
			} else if (type.equals("20")) {
				//{"type":"20","src":"http:\/\/imgsrc.baidu.com\/forum\/pic\/item\/4c086e061d950a7bce3c370300d162d9f3d3c9e8.jpg","bsize":"375,348","meme_info":{"pck_id":"0","pic_id":"47098639564","width":"375","height":"348","pic_url":"http:\/\/imgsrc.baidu.com\/forum\/pic\/item\/4c086e061d950a7bce3c370300d162d9f3d3c9e8.jpg","thumbnail":"http:\/\/imgsrc.baidu.com\/forum\/abpic\/item\/4c086e061d950a7bce3c370300d162d9f3d3c9e8.jpg","detail_link":"http:\/\/tieba.baidu.com\/n\/interact\/emoticon\/0\/47098639564?frompb=1"}}
				//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["src"] + "=MD5=" + tlib.取网络资源MD5((String)内容源码["post_list"][内容计数]["content"][文本计数]["src"]) + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
				//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["src"] + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
				thing = "<img src=\"" + content.optString("src") + "\" alt=\"图片加载失败，请反馈至1021263881@qq.com\" >";
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
	public class mAdapter extends BaseAdapter{
		private ArrayList<ArrayMap<String, String>> list;
		private Context context;

		mAdapter(Context context, ArrayList<ArrayMap<String, String>> list){
			this.context = context;
			this.list = list;
		}
	}
}

