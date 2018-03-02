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

			try
			{
				jumpPage(1);
			}
			catch (mException e)
			{

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
			ArrayList<Integer> all = new ArrayList<Integer>();

			JSONObject page;
			try
			{
				page = new JSONObject(thing);
				String maxpage = page.optJSONObject("page").optString("total_page");
				String maxfloor = page.optJSONArray("post_list").optJSONObject(0).optString("floor");
				all.add(Integer.valueOf(maxpage));
				all.add(Integer.valueOf(maxfloor));

				pagemax = Integer.valueOf(maxpage);
				floormax = Integer.valueOf(maxfloor);
			}
			catch (JSONException e)
			{
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

		public ArrayList<String> getLastFloor() throws mException
		{
			if (indexInList == 0)
			{
				if (pagenow > 1)
				{
					pagenow --;
					jumpPage(pagenow);
					indexInList = flr.get(pagenow).size() - 1;
					return getReturn(flr.get(pagenow).get(indexInList));
				}
				else
				{
					throw new mException("已经是第一页了", "");
				}
			}
			else
			{
				indexInList--;
				return getReturn(flr.get(pagenow).get(indexInList));
			}
		}
		public ArrayList<String> getNextFloor() throws mException
		{
			if (indexInList == flr.get(pagenow).size() - 1)
			{
				if (pagenow < pagemax)
				{
					return getNextPage();
				}
				else
				{
					getMax();
					if (pagenow < pagemax)
					{
						return getNextFloor();
					}
					else
					{
						throw new mException("获取下一页失败", "超出范围");
					}
				}
			}
			else
			{
				indexInList ++;
				return getReturn(flr.get(pagenow).get(indexInList));
			}
		}
		private ArrayList<String> getNextPage() throws mException
		{
			if (pagenow < pagemax)
			{
				pagenow ++;
				return jumpPage(pagenow);
			}
			else
			{
				getMax();
				if (pagenow < pagemax)
				{
					return getNextPage();
				}
				else
				{
					throw new mException("获取下一页失败", "获取下一页失败, page=" + pagenow + ", pagemax=" + pagemax);
				}
			}
		}
		public ArrayList<String> jumpFloor(int floor) throws mException
		{
			if (floor > floornow)
			{
				ArrayList<ArrayMap<String, String>> page;
				ArrayMap<String, String> flmap;
				//遍历page
				for (int p = pagenow; p <= pagemax; p++)
				{
					if (flr.containsKey(p) == false)
					{
						getPage("", tid, p);
					}
					page = flr.get(p);
					//遍历楼层
					for (int i = page.size() - 1; i > -1; i--)
					{
						flmap = page.get(i);
						if (Integer.valueOf(flmap.get("floor")) >= floor)
						{
							return getReturn(flmap);
						}
					}
				}
				throw new mException("没有找到该楼层", "");
			}
			else if (floor < floornow)
			{
				ArrayList<ArrayMap<String, String>> page;
				ArrayMap<String, String> flmap;
				//遍历page
				for (int p = pagenow; p > 0; p--)
				{
					if (flr.containsKey(p) == false)
					{
						getPage("", tid, p);
					}
					page = flr.get(p);
					//遍历楼层
					for (int i = 0; i < page.size(); i++)
					{
						flmap = page.get(i);
						if (Integer.valueOf(flmap.get("floor")) <= floor)
						{
							return getReturn(flmap);
						}
					}
				}
				throw new mException("没有找到该楼层", "");
			}
			else
			{
				return getReturn(flr.get(pagenow).get(indexInList));
			}
		}
		public ArrayList<String> jumpPage(int page) throws mException 
		{
			indexInList = 0;
			getPage("", tid, page, false);
			if (flr.containsKey(pagenow) == true)
			{
				return getReturn(flr.get(pagenow).get(0));
			}
			else
			{
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
			try
			{
				list = new JSONObject(thing);
			}
			catch (JSONException e)
			{
				throw new mException("JSON解析好像遇到问题了", "主题列表JSON解析错误, Str:“" + thing + "”, 错误信息:" + e.toString());
			}
			//return list.optJSONArray("thread_list");
			return null;
		}
		private void getPage(String cookie, String tid, int pn) throws mException
		{
			getPage(cookie, tid, pn, false);
		}
		private void getPage(String cookie, String tid, int pn, boolean daoxu) throws mException
		{
			String url = "http://c.tieba.baidu.com/c/f/pb/page";
			String data = cookie;
			data += "&_client_id=" + getStamp();
			data += "&_client_type=2&_client_version=8.8.8.3";
			data += "&kz=" + tid;
			if (daoxu == true)
			{
				data += "&last=1&r=1";
			}
			else
			{
				data += "&pn=" + pn;
			}
			data += "&sign=" + sign(data);

			String thing = httpService.post(url, data);

			JSONObject page;
			try
			{
				page = new JSONObject(thing);
			}
			catch (JSONException e)
			{
				throw new mException("JSON解析好像遇到问题了", "获取帖子JSON解析错误, Str:“" + thing + "”, 错误信息:" + e.toString());
			}
			pagemax = Integer.valueOf(page.optJSONObject("page").optString("total_page", String.valueOf(pagemax)));
			if (daoxu == false)
			{
				pagenow = Integer.valueOf(page.optJSONObject("page").optString("current_page", String.valueOf(pagenow)));
			}
			if (pagenow > pagemax)
			{
				pagenow = pagemax;
			}
			if (anaPerson(page.optJSONArray("user_list")) == false)
			{
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
			try
			{
				lzl = new JSONObject(thing);
			}
			catch (JSONException e)
			{
				throw new mException("JSON解析好像遇到问题了", "获取楼中楼JSON解析错误, Str:“" + thing + "”, 错误信息:" + e.toString());
			}
			analzl(lzl.optJSONArray("subpost_list"), tid, pid);

			//如果没有到底就继续
			int pmax = Integer.valueOf(lzl.optJSONObject("page").optString("total_page"));
			if (pn < pmax)
			{
				getlzl("", tid, pid, pn + 1);
			}
		}
		private void anaPage(JSONArray list, String tid, int page) throws mException
		{
			int len = list.length();
			if (len < 1)
			{
				return ;
			}
			ArrayList<ArrayMap<String, String>> floorlist = new ArrayList<ArrayMap<String, String>>();
			JSONObject fl;
			ArrayMap<String, String> epg;
			ArrayMap<String, String> nflr;
			for (int i = 0;i < len; i++)
			{
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
			if (len < 1)
			{
				return ;
			}
			ArrayList<ArrayMap<String, String>> lzllist = new ArrayList<ArrayMap<String, String>>();
			JSONObject fl;
			ArrayMap<String, String> epg;
			ArrayMap<String, String> nlzl;
			for (int i = 0;i < len; i++)
			{
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
			if (len < 1)
			{
				return false;
			}
			ArrayMap<String, ArrayMap<String, String>> pslist = new ArrayMap<String, ArrayMap<String, String>>();
			JSONObject eps;
			ArrayMap<String, String> mps;
			for (int i = 0;i < len; i++)
			{
				eps = list.optJSONObject(i);
				mps = new ArrayMap<String, String>();
				mps.put("aid", eps.optString("id", ""));
				mps.put("id", eps.optString("name", "[未获取到用户ID]"));
				mps.put("nickname", eps.optString("name_show", "[未获取到用户昵称]"));
				mps.put("level", eps.optString("level_id", "[未获取到用户等级]"));
				pslist.put(mps.get("aid").toString(), new ArrayMap<String, String>(mps));
			}
			if (pslist.size() == list.length())
			{
				ps.putAll(pslist);
				return true;
			}
			else
			{
				return false;
			}
		}
		private ArrayMap<String, String> anaPerson(JSONObject eps)
		{
			if (eps == null)
			{
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
			if (lzl != null)
			{
				len = lzl.size();
			}
			ArrayList<String> pr = new ArrayList<String>();
			ArrayMap<String, String> elzl = null;

			content += "<a href=\"http://tieba.baidu.com/home/main/?un=" + floor.get("id") + "\">" + floor.get("nickname") + "</a>   Level-" + floor.get("level") + "  :<br>";
			content += floor.get("content") + "<br><br><div style=\"float:right;\">------" + floor.get("floor") + "楼  " + floor.get("time") + "</div><br>";
			content += "<hr>";

			for (int i = 0; i < len; i++)
			{
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
			if (len < 1)
			{
				return null;
			}
			String text = "";
			for (int i = 0; i < len; i++)
			{
				text += anaContentToHtml(content.optJSONObject(i));
			}
			return text;
		}
		private String anaContentToHtml(JSONObject content) throws mException
		{
			String type = content.optString("type", "0");
			String thing = "";
			if (type.equals("0"))
			{
				//thing += (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"];
				thing = content.optString("text", "[文本获取失败]");
			}
			else if (type.equals("1"))
			{
				//{"type":1,"link":"网址","text":"网址"}
				//thing += "#链接=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"] + "#";
				thing = "<a href=\"" + content.optString("link", "") + "\">" + content.optString("text", "") + "</a>";
				//控制台_输出(0, "#链接# " + (String)内容源码["post_list"][内容计数]["content"][文本计数]["link"]);
			}
			else if (type.equals("2"))
			{
				//{"type": "2","text": "image_emoticon33","c": "喷"}
				//thing += "#表情=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["c"] + "#";
				thing = "[贴吧表情:" + content.optString("c") + "]";
			}
			else if (type.equals("3"))
			{
				//{"type":3,"src":"链接","bsize":"189,199","size":"49634"}
				//Console.WriteLine((String)内容源码["post_list"][内容计数]["content"].ToString());
				//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["origin_src"] + "=MD5=" + tlib.取网络资源MD5((String)内容源码["post_list"][内容计数]["content"][文本计数]["origin_src"]) + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
				//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["origin_src"] + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
				thing = "<img src=\"" + content.optString("origin_src") + "\" alt=\"图片加载失败，请反馈至1021263881@qq.com\" >";
			}
			else if (type.equals("4"))
			{
				//thing += "#艾特=" + 内容源码["post_list"][内容计数]["content"][文本计数]["text"].ToString().Replace("@", "") + "#";
				thing += "<a href=\"http://tieba.baidu.com/home/main/?un=" + UrlEncodeUtf_8(content.optString("text", "").replace("@", "")) + "\">" + content.optString("text", "") + "</a>";
			}
			else if (type.equals("5"))
			{
				//{"type":5,"e_type":15,"width":"480","height":"480","bsize":"480,480","during_time":"2","origin_size":"168046","text":"http:\/\/tieba.baidu.com\/mo\/q\/movideo\/page?thumbnail=d109b3de9c82d158d3fcee1d880a19d8bc3e421b&video=10363_ed294eae88371575b3dbcf9f1990f68d","link":"http:\/\/tb-video.bdstatic.com\/tieba-smallvideo\/10363_ed294eae88371575b3dbcf9f1990f68d.mp4","src":"http:\/\/imgsrc.baidu.com\/forum\/pic\/item\/d109b3de9c82d158d3fcee1d880a19d8bc3e421b.jpg","is_native_app":0,"native_app":[]}
				//thing += "#视频#";
			}
			else if (type.equals("7"))
			{
				//{"type":"7","text":"\n"}
				thing = "<br>";
			}
			else if (type.equals("9"))
			{
				//{"type":"9","text":"6666666","phonetype":"2"}
				//thing += (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"];
				thing = content.optString("text", "");
			}
			else if (type.equals("10"))
			{
				//{"type":"10","during_time":"15000","voice_md5":"e25ef2db5076f825e229c6cdb1613f38_1064475243"}
				//thing += "#语音=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["voice_md5"] + "," + (String)内容源码["post_list"][内容计数]["content"][文本计数]["during_time"] + "#";
			}
			else if (type.equals("11"))
			{
				//{"type":"11","c":"白发魔女传之明月天国_女屌丝","static":"png静态图链接","dynamic":"gif动态图链接","height":"160","width":"160","icon":"http://tb2.bdstatic.com/tb/editor/images/faceshop/1058_baifa/panel.png","packet_name":"白发魔女传之明月天国"}
				//thing += "#表情=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["c"] + "#";
			}
			else if (type.equals("16"))
			{
				//{"type":"16","bsize":"560,560","graffiti_info":{"url":"jpg网页端原图","gid":"123456"},"cdn_src":"客户端缩略图","big_cdn_src":"客户端大图"}
				//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["graffiti_info"]["url"] + "=MD5=" + tlib.取网络资源MD5((String)内容源码["post_list"][内容计数]["content"][文本计数]["graffiti_info"]["url"]) + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
				//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["graffiti_info"]["url"] + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
				thing = "<img src=\"" + content.optJSONObject("graffiti_info").optString("url") + "alt=\"图片加载失败，请反馈至1021263881@qq.com\" >";
			}
			else if (type.equals("17"))
			{
				//{"type":"17","high_together":{"album_id":"478448408116821906","album_name":"关于众筹西游记歌曲演唱会活动","start_time":"0","end_time":"0","location":"","num_join":"0","pic_urls":[]}}
				//thing += "#活动=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["album_name"] + "#";
			}
			else if (type.equals("18"))
			{
				//{"type":"18","text":"#白狐狸不改国庆礼包就滚出dnf#","link":"http://tieba.baidu.com/mo/q/hotMessage?topic_id=0&topic_name=白狐狸不改国庆礼包就滚出dnf"}
				//thing += "#热议=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"] + "#";
			}
			else if (type.equals("20"))
			{
				//{"type":"20","src":"http:\/\/imgsrc.baidu.com\/forum\/pic\/item\/4c086e061d950a7bce3c370300d162d9f3d3c9e8.jpg","bsize":"375,348","meme_info":{"pck_id":"0","pic_id":"47098639564","width":"375","height":"348","pic_url":"http:\/\/imgsrc.baidu.com\/forum\/pic\/item\/4c086e061d950a7bce3c370300d162d9f3d3c9e8.jpg","thumbnail":"http:\/\/imgsrc.baidu.com\/forum\/abpic\/item\/4c086e061d950a7bce3c370300d162d9f3d3c9e8.jpg","detail_link":"http:\/\/tieba.baidu.com\/n\/interact\/emoticon\/0\/47098639564?frompb=1"}}
				//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["src"] + "=MD5=" + tlib.取网络资源MD5((String)内容源码["post_list"][内容计数]["content"][文本计数]["src"]) + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
				//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["src"] + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
				thing = "<img src=\"" + content.optString("src") + "\" alt=\"图片加载失败，请反馈至1021263881@qq.com\" >";
			}
			return thing;
		}
}