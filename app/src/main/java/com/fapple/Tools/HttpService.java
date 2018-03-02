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
			try
			{
				re = futureTask.get();
			}
			catch (InterruptedException e)
			{
				throw new mException("好像遇到了些奇怪的错误汪~", "Get出错，URL“" + Url + "”，Data:“" + data + "”\n错误信息:" + e.toString() + "\n" + e.getMessage());
			}
			catch (ExecutionException e)
			{
				throw new mException("好像遇到了些奇怪的错误汪~", "Get出错，URL“" + Url + "”，Data:“" + data + "”\n错误信息:" + e.toString() + "\n" + e.getMessage());
			}
			if (re == "" || re == null)
			{
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
			try
			{
				re = futureTask.get();
			}
			catch (InterruptedException e)
			{
				throw new mException("好像遇到了些奇怪的错误汪~", "Post出错，URL“" + Url + "”，Data:“" + data + "”\n错误信息:" + e.toString() + "\n" + e.getMessage());
			}
			catch (ExecutionException e)
			{
				throw new mException("好像遇到了些奇怪的错误汪~", "Post出错，URL“" + Url + "”，Data:“" + data + "”\n错误信息:" + e.toString() + "\n" + e.getMessage());
			}
			if (re == "" || re == null)
			{
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
				switch (method)
				{
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
			if (data != "" && data != null)
			{
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
			if (urlConn.getResponseCode() == 200)
			{
				// 获取返回的数据
				result = streamToString(urlConn.getInputStream());
				//Log.e(TAG, "Get方式请求成功，result--->" + result);
			}
			else
			{
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
			while ((len = is.read(buffer)) != -1)
			{
				baos.write(buffer, 0, len);
			}
			baos.close();
			is.close();
			byte[] byteArray = baos.toByteArray();
			return new String(byteArray);
		}
	}