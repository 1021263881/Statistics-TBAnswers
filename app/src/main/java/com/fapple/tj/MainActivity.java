package com.fapple.tj;

import android.app.*;
import android.content.*;
import android.graphics.drawable.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.webkit.*;
import android.widget.*;
import android.widget.RelativeLayout.*;
import com.fapple.Tools.*;
import java.util.*;

import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import java.io.*;
import org.json.*;

public class MainActivity extends Activity 
{
	private TB tb = new TB(this, "4592800021");
	private ClipboardManager cm = null;
	private ProgressDialog pd = null;
	private LinearLayout.LayoutParams personlp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	private WebView web;
	private LinearLayout list;
	private ActionBar actionbar;
	private View actionbarview;
	private Button lastbutton;
	private Button nextbutton;
	private TextView midtext;

	private ListView mlist;

	//获取Alert
	private AlertDialog mdialog;

	//获取按钮
	private RadioButton jumppagebutton;
	private RadioButton jumpfloorbutton;

	//获取文本框
	private EditText jumppageedit;
	private TextView jumppagetext;
	private EditText jumpflooredit;
	private TextView jumpfloortext;

	//仨图片
	private Drawable ojbk;
	private Drawable well;
	private Drawable none;

	//personButton监听
	private OnClickListener personb;
	private View person;

	private int pagemax = 1;
	private int pagenow = 1;
	private int floormax = 1;
	private int floornow = 1;

	private int hasCopy = 0;
	private boolean opened = true;

	private JSONObject setting = new JSONObject();
	private int version = 0;

	private ArrayMap<String, ArrayMap<String, String>> tj = new ArrayMap<String, ArrayMap<String, String>>();
	private ArrayMap<String, String> ntj = new ArrayMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		//获取剪贴板
		cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

		//获取图片
		ojbk = getResources().getDrawable(R.drawable.ojbk);
		well = getResources().getDrawable(R.drawable.well);
		none = getResources().getDrawable(R.drawable.none);

		//设置ntj模板
		ntj.put("nickname", "");
		ntj.put("yx", "1");
		ntj.put("gzl", "0");

		//设置personButton监听器
		personb = new OnClickListener(){
			@Override
			public void onClick(View p1)
			{
				Drawable pbuttonback = p1.getBackground();
				TextView id = (TextView)((View)p1.getParent()).findViewById(R.id.personid);
				TextView nickname = (TextView)((View)p1.getParent()).findViewById(R.id.personnickname);
				int did = 0;
				if (pbuttonback == none)
				{
					p1.setBackground(ojbk);
					did = 1;
				}
				else if (pbuttonback == ojbk)
				{
					p1.setBackground(well);
					did = 2;
				}
				else if (pbuttonback == well)
				{
					p1.setBackground(none);
					did = -1;
				}
				if (did != 0)
				{
					updatePerson(did, id.getText().toString(), nickname.getText().toString());
				}
			}
		};

		//设置ActionBar
		actionbar = getActionBar();
		if (actionbar != null)
		{
			actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			actionbar.setCustomView(R.layout.actionbar);
		}

		//获取ActionBar的View
		actionbarview = actionbar.getCustomView();

		//设置last按钮事件
		lastbutton = (Button)actionbarview.findViewById(R.id.actionbarlast);
		lastbutton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					lastFloor();
				}
			});

		//设置next按钮事件
		nextbutton = (Button)actionbarview.findViewById(R.id.actionbarnext);
		nextbutton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					nextFloor();
				}
			});

		//设置midtext单击事件
		midtext = (TextView)actionbarview.findViewById(R.id.actionbarmidtext);
		midtext.setTextColor(getResources().getColor(R.color.white));
		midtext.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					AlertDialog.Builder mdialogb = new AlertDialog.Builder(MainActivity.this);
					mdialogb.setIcon(R.drawable.jump);
					mdialogb.setTitle("你倒是选一个跳转方式啊");

					//得到自定义View的对象
					View jump = View.inflate(MainActivity.this, R.layout.jumpdialog, null);

					//获取按钮
					jumppagebutton = (RadioButton)jump.findViewById(R.id.jumppagebutton);
					jumpfloorbutton = (RadioButton)jump.findViewById(R.id.jumpfloorbutton);

					//获取文本框
					jumppageedit = (EditText)jump.findViewById(R.id.jumppage);
					jumppagetext = (TextView)jump.findViewById(R.id.jumppagetext);
					jumpflooredit = (EditText)jump.findViewById(R.id.jumpfloor);
					jumpfloortext = (TextView)jump.findViewById(R.id.jumpfloortext);

					//设置文字
					jumppagetext.setText("/" + String.valueOf(pagemax) + "页");
					jumpfloortext.setText("/" + String.valueOf(floormax) + "楼");

					//设置初始色
					setJumpDialogEdit_TextColor(jumppageedit, jumppagetext, R.color.mdblack_f);
					setJumpDialogEdit_TextColor(jumpflooredit, jumpfloortext, R.color.mdblack_f);

					//检测焦点改变
					jumppageedit.setOnFocusChangeListener(new OnFocusChangeListener(){
							@Override
							public void onFocusChange(View p1, boolean p2)
							{
								if (p2)
								{
									//展开软键盘
									InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);//得到系统的输入方法服务
									imm.showSoftInput(p1, 0);

									jumppagebutton.setChecked(true);
									setJumpDialogEdit_TextColor(jumppageedit, jumppagetext, R.color.mdblack);
									jumpfloorbutton.setChecked(false);
									setJumpDialogEdit_TextColor(jumpflooredit, jumpfloortext, R.color.mdblack_f);
								}
							}
						});
					jumpflooredit.setOnFocusChangeListener(new OnFocusChangeListener(){
							@Override
							public void onFocusChange(View p1, boolean p2)
							{
								if (p2)
								{
									//展开软键盘
									InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);//得到系统的输入方法服务
									imm.showSoftInput(p1, 0);

									jumpfloorbutton.setChecked(true);
									setJumpDialogEdit_TextColor(jumpflooredit, jumpfloortext, R.color.mdblack);
									jumppagebutton.setChecked(false);
									setJumpDialogEdit_TextColor(jumppageedit, jumppagetext, R.color.mdblack_f);
								}
							}
						});

					//绑定RadioButton事件
					jumppagebutton.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View p1)
							{
								jumppageedit.requestFocus();
							}
						});
					jumpfloorbutton.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View p1)
							{
								jumpflooredit.requestFocus();
							}
						});

					//设置view
					mdialogb.setView(jump);

					mdialogb.setNegativeButton("ojbk", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								if (jumppagebutton.isChecked() == true)
								{
									int page = Integer.valueOf(jumppageedit.getText().toString());
									jumpPage(page);
								}
								else if (jumpfloorbutton.isChecked() == true)
								{
									int floor = Integer.valueOf(jumpflooredit.getText().toString());
									if (Math.abs(floor - floornow) > 300)
									{
										Toast.makeText(MainActivity.this, "跳转楼层数过长，请耐心等候", 1).show();
									}
									jumpFloor(floor);
								}
								else
								{
									showtips("跳转失败", "你倒是选一个跳转方式啊", "oj8k");
								}
							}
						});

					//获取Alert
					mdialog = mdialogb.show();
				}
			});

		//设置midtext长按事件
		midtext.setOnLongClickListener(new OnLongClickListener(){
				@Override
				public boolean onLongClick(View p1)
				{
					Intent intent = new Intent(MainActivity.this, Showtj.class);
					try
					{
						intent.putExtra("tj", getJsontjFromList());
						startActivityForResult(intent, 0);
					}
					catch (mException e)
					{
						showWarning("", e.getMessage(), e.getMore(), true);
					}
					return true;
				}
			});

		freshmax();
		try
		{
			loadsetting();
		}
		catch (mException e)
		{
			showWarning("", e.getMessage(), e.getMore(), true);
		}

		//获取版本号
		int version = 0;
		try
		{
			version = APKVersion.getVersionCode(this);
		}
		catch (mException e)
		{
			showWarning("", e.getMessage(), e.getMore(), true);
			version = 0;
		}

		if (version != this.version)
		{
			this.version = version;
			showUpdateMess();
		}

		//加载温馨提示
		if (opened == false)
		{
			opened = true;
			String tipstitle = getResources().getString(R.string.tips_title);
			String tipsmess = getResources().getString(R.string.tips_message);
			AlertDialog.Builder tips = new AlertDialog.Builder(MainActivity.this);
			tips.setTitle(tipstitle);
			tips.setMessage(tipsmess);
			tips.setNegativeButton("ojbk", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						// TODO: Implement this method
					}
				});
			tips.setOnDismissListener(new DialogInterface.OnDismissListener(){
					@Override
					public void onDismiss(DialogInterface p1)
					{
						jumpFloor(1);
					}
				});
			tips.show();
		}
		else
		{
			if (floornow != 1)
			{
				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setMessage("是否恢复到" + pagenow + "页" + floornow + "层");
				dialog.setCancelable(false);
				dialog.setNegativeButton("确定", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							jumpPage(pagenow);
							jumpFloor(floornow);
						}
					});
				dialog.setNeutralButton("取消", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							jumpPage(1);
						}
					});
				dialog.show();
			}
			else
			{
				jumpPage(1);
			}
		}

		//获取WebView
		web = (WebView)findViewById(R.id.mainWebView);

		web.getSettings().setUseWideViewPort(true);

		//自适应屏幕
		web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		web.getSettings().setLoadWithOverviewMode(true);//适应屏幕

		//默认UTF-8
		web.getSettings().setDefaultTextEncodingName("UTF-8");

		//获取ScrollView
		list = (LinearLayout)findViewById(R.id.mainLinearLayout);
		mlist = (ListView)findViewById(R.id.mainList);

		//从/data/data载入统计缓存
		try
		{
			gettjFromFile();
			if (tj.size() != 0)
			{
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setMessage("检测到本地有统计数据，是否恢复？");
				dialog.setCancelable(false);
				dialog.setNegativeButton("确定", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							showToast("已恢复", 0);
						}
					});
				dialog.setNeutralButton("取消", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							cleentj();
						}
					});
				dialog.show();
			}
		}
		catch (mException e)
		{
			showWarning("", e.getMessage(), e.getClass().getName(), true);
		}
	}

	@Override
	protected void onPause()
	{
		//销毁程序前保存未导出的数据
		//if (opened == true)
		{
			try
			{
				autoSave();
			}
			catch (mException e)
			{

			}
		}
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		/*
		 try
		 {
		 loadsetting();
		 }
		 catch (mException e)
		 {

		 }
		 if (floornow != 1)
		 {
		 AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
		 dialog.setMessage("是否恢复到" + pagenow + "页" + floornow + "层");
		 dialog.setCancelable(false);
		 dialog.setNegativeButton("确定", new DialogInterface.OnClickListener(){

		 @Override
		 public void onClick(DialogInterface p1, int p2)
		 {
		 jumpPage(pagenow);
		 jumpFloor(floornow);
		 }
		 });
		 dialog.setNeutralButton("取消", new DialogInterface.OnClickListener(){

		 @Override
		 public void onClick(DialogInterface p1, int p2)
		 {

		 }
		 });
		 }*/
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == 0)
		{
			switch (resultCode)
			{
				case 0:/*无操作*/
					Toast.makeText(this, "记得导出数据哦", 0).show();
					break;
				case 1:/*导出*/
					copytj();
					Toast.makeText(this, "已复制统计数据，记得清空哦~", 0).show();
					break;
				case -1:/*清空*/
					cleentj();
					Toast.makeText(this, "已清空统计数据", 0).show();
					break;
			}
		}
		//super.onActivityResult(requestCode, resultCode, data);
	}

	//复制统计数据至剪贴板
	private void copytj()
	{
		String content = "";

		int len = tj.size();
		String id;
		String nickname;
		int yx;
		int gzl;
		ArrayMap<String, String> meps;
		content += "ID" + "\t" + "昵称" + "\t" + "有效回答" + "\t" + "高质量回答";
		content += "\n";
		for (int i = 0; i < len; i++)
		{
			id = tj.keyAt(i);
			meps = tj.get(id);
			nickname = meps.get("nickname");
			yx = Integer.valueOf(meps.get("yx"));
			gzl = Integer.valueOf(meps.get("gzl"));

			content += id + "\t" + nickname + "\t" + yx + "\t" + gzl;
			content += "\n";
		}

		Tool.copyToClipBoard(cm, content);
	}

	//自动检查保存统计数据
	private void autoSave() throws mException
	{
		save("tj.json", getJsontjFromList());
		savesetting();
	}

	private void gettjFromFile() throws mException
	{
		String content = "";

		content = load("tj.json");
		if (content == "" || content == null)
		{
			return ;
		}
		JSONArray tjlist;
		try
		{
			tjlist = new JSONArray(content);
		}
		catch (JSONException e)
		{
			throw new mException("", e.toString());
		}
		String id = "";
		String nickname = "";
		int yx = -1;
		int gzl = -1;
		ArrayMap<String, String> meps;
		JSONObject eps;
		int len = tjlist.length();
		for (int i = 0; i < len; i++)
		{
			try
			{
				eps = (JSONObject)tjlist.get(i);
				id = (String)eps.get("id");
				nickname = (String)eps.get("nickname");
				yx = eps.get("yx");
				gzl = eps.get("gzl");
			}
			catch (JSONException e)
			{
				throw new mException("", e.toString());
			}
			meps = new ArrayMap<String, String>();
			meps.put("nickname", nickname);
			meps.put("yx", String.valueOf(yx));
			meps.put("gzl", String.valueOf(gzl));
			tj.put(id, meps);
		}
	}

	//获取字符串格式的统计表
	private String getJsontjFromList() throws mException
	{
		int len = tj.size();
		String id = "";
		String nickname = "";
		int yx = -1;
		int gzl = -1;
		ArrayMap meps;
		JSONArray ps = new JSONArray();
		JSONObject jeps;
		for (int i = 0; i < len; i++)
		{
			jeps = new JSONObject();
			id = tj.keyAt(i);
			meps = tj.get(id);
			nickname = (String)meps.get("nickname");
			yx = Integer.valueOf(meps.get("yx").toString());
			gzl = Integer.valueOf(meps.get("gzl").toString());
			try
			{
				jeps.put("id", id);
				jeps.put("nickname", nickname);
				jeps.put("yx", yx);
				jeps.put("gzl", gzl);
			}
			catch (JSONException e)
			{
				throw new mException("", e.toString());
			}
			ps.put(jeps);
		}
		return ps.toString();
	}

	//统计
	private Boolean updatePerson(int did, String id, String nickname)
	{
		hasCopy = 0;
		int old;
		switch (did)
		{
			case 1/*有效*/:
				if (tj.containsKey(id))
				{
					if (tj.containsKey(id) == true)
					{
						old = Integer.valueOf(tj.get(id).get("yx"));
						old ++;
						if (old == Integer.valueOf(tj.get(id).put("yx", String.valueOf(old))))
						{
							return true;
						}
					}
				}
				else
				{
					tj.put(id, new ArrayMap<String, String>(ntj));
					if (nickname == tj.get(id).put("nickname", nickname))
					{
						return true;
					}
				}
				break;
			case 2/*高质量*/:
				if (tj.containsKey(id))
				{
					old = Integer.valueOf(tj.get(id).get("gzl"));
					old ++;
					if (old == Integer.valueOf(tj.get(id).put("gzl", String.valueOf(old))))
					{
						return true;
					}
				}
				else
				{
					return false;
				}
				break;
			case -1/*无效*/:
				if (tj.containsKey(id))
				{
					old = Integer.valueOf(tj.get(id).get("yx"));
					int oldg = Integer.valueOf(tj.get(id).get("gzl"));
					if (old == 1)
					{
						tj.remove(id);
					}
					else
					{
						old --;
						oldg --;
						if (old == Integer.valueOf(tj.get(id).put("yx", String.valueOf(old))) && 
							oldg == Integer.valueOf(tj.get(id).put("gzl", String.valueOf(oldg))))
						{
							return true;
						}
					}
				}
				else
				{
					return false;
				}
				break;
		}
		return false;
	}

	//清空统计结果
	public void cleentj()
	{
		tj.clear();
	}

	//更新名单
	public Boolean updatePersonList(ArrayList<String> personlist)
	{
		//清空内容
		if (list.getChildCount() != 0)
		{
			list.removeAllViews();
		}

		int ma = personlist.size();
		for (int i = 0; i < ma; i += 2)
		{
			person = View.inflate(MainActivity.this, R.layout.person, null);

			//设置id, 昵称
			((TextView)person.findViewById(R.id.personid)).setText(personlist.get(i));
			((TextView)person.findViewById(R.id.personnickname)).setText(personlist.get(i + 1));

			//设置按钮监听
			person.findViewById(R.id.personbutton).setBackground(none);
			person.findViewById(R.id.personbutton).setOnClickListener(personb);

			list.addView(person, personlp);
		}

		if (list.getChildCount() == personlist.size())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	//分析TBreturn
	private void ana(ArrayList<String> list) throws mException
	{
		showWait("加载中...");
		if (list == null || list.size() < 5)
		{
			throw new mException("传值过少", "ana错误, ana.size=" + list.size());
		}
		pagenow = Integer.valueOf(list.get(0));
		pagemax = Integer.valueOf(list.get(1));
		floornow = Integer.valueOf(list.get(2));
		floormax = Integer.valueOf(list.get(3));

		midtext.setText(pagenow + "/" + pagemax + "页 " + floornow + "/" + floormax + "楼");

		Tool.loadHtmlInWebview(web, list.get(4));

		int len = list.size();
		ArrayList<String> pr = new ArrayList<String>();
		String name = "";
		for (int i = 5; i < len; i += 2)
		{
			name = list.get(i);
			if (pr.contains(name) == false)
			{
				pr.add(name);
				name = list.get(i + 1);
				pr.add(name);
			}
		}
		updatePersonList(pr);
		killWait();
	}

	private void save(String path, String content) throws mException
	{
		try
		{
			FileOutputStream output = openFileOutput(path, Context.MODE_PRIVATE);
			/**
			 * Context.MODE_PRIVATE = 0
			 * 为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容。
			 * Context.MODE_APPEND = 32768
			 * 该模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。　
			 * Context.MODE_WORLD_READABLE = 1
			 * 表示当前文件可以被其他应用读取。
			 * MODE_WORLD_WRITEABLE
			 * 表示当前文件可以被其他应用写入。
			 */

			output.write(content.getBytes());
			output.close();
		}
		catch (IOException e)
		{
			throw new mException("", e.toString());
		}
	}

	private void savesetting() throws mException
	{
		try
		{
			setting.put("version", version);
			setting.put("pagenow", pagenow);
			setting.put("floornow", floornow);
			save("setting.json", setting.toString());
		}
		catch (JSONException e)
		{
			throw new mException("", e.toString());
		}
	}

	private String load(String path) throws mException
	{
		String content = "";
		FileInputStream input = null;
		try
		{
			input = openFileInput(path);
		}
		catch (FileNotFoundException e)
		{
			if (e.getClass().getName().equals("java.io.FileNotFoundException"))
			{
				try
				{

					openFileInput("setting.json").close();

				}
				catch (FileNotFoundException e2)
				{
					if (e2.getClass().getName().equals("java.io.FileNotFoundException"))
					{
						opened = false;
						return content;
					}
					else
					{
						throw new mException("未找到文件(" + path + ")", e.toString());
					}
				}
				catch (IOException e3)
				{
					throw new mException("", e.toString());
				}
			}
			else
			{
				throw new mException("未找到文件(" + path + ")", e.toString());
			}
		}
		try
		{
			content = Tool.streamToString(input);
			input.close();
		}
		catch (IOException e)
		{
			throw new mException("", e.toString());
		}
		return content;
	}

	private void loadsetting() throws mException
	{
		String content;
		content = load("setting.json");
		JSONObject msetting = null;
		try
		{
			msetting = new JSONObject(content);
			version = msetting.optInt("version", version);
			pagenow = msetting.optInt("pagenow", pagenow);
			floornow = msetting.optInt("floornow", floornow);
		}
		catch (JSONException e)
		{
			throw new mException("", e.toString());
		}
	}

	/*--------------------------------跳转--------------------------*/

	private void nextFloor()
	{
		fAsyncTask asyncTask = new fAsyncTask();
		asyncTask.execute("nf");
	}

	private void lastFloor()
	{
		fAsyncTask asyncTask = new fAsyncTask();
		asyncTask.execute("lf");
	}

	private void jumpFloor(int floor)
	{
		fAsyncTask asyncTask = new fAsyncTask();
		asyncTask.execute("jf", String.valueOf(floor));
	}

	private void jumpPage(int page)
	{
		fAsyncTask asyncTask = new fAsyncTask();
		asyncTask.execute("jp", String.valueOf(page));
	}

	class fAsyncTask extends AsyncTask<String, Void, ArrayList<String>>
	{
		int error = 0;
		mException e;
		/**
		 * 即将要执行耗时任务时回调，这里可以做一些初始化操作
		 * 在主线程执行
		 */
		@Override
		protected void onPreExecute()
		{
			showWait("跳转中...");
			super.onPreExecute();
		}

		/**
		 * 在后台执行耗时操作，其返回值将作为onPostExecute方法的参数
		 * 在单独线程执行
		 */
		@Override
		protected ArrayList<String> doInBackground(String... params)
		{
			String met = params[0];
			if (met.equals("nf"))
			{
				try
				{
					return tb.getNextFloor();
				}
				catch (mException e)
				{
					error = 1;
					this.e = e;
					return null;
				}
			}
			else if (met.equals("lf"))
			{
				try
				{
					return tb.getLastFloor();
				}
				catch (mException e)
				{
					error = 1;
					this.e = e;
					return null;
				}
			}
			else if (met.equals("jf"))
			{
				try
				{
					return tb.jumpFloor(Integer.valueOf(params[1]));
				}
				catch (NumberFormatException e)
				{

				}
				catch (mException e)
				{
					error = 1;
					this.e = e;
					return null;
				}
			}
			else if (met.equals("jp"))
			{
				try
				{
					return tb.jumpPage(Integer.valueOf(params[1]));
				}
				catch (NumberFormatException e)
				{

				}
				catch (mException e)
				{
					error = 1;
					this.e = e;
					return null;
				}
			}
			//输出阶段性结果
			//publishProgress("");
			return null;
		}

		/**
		 *阶段性成果
		 */
        protected void onProgressUpdate(Object... values)
		{

        }

		/**
		 * 当这个异步任务执行完成后，也就是doInBackground（）方法完成后，
		 * 其方法的返回结果就是这里的参数
		 * 在主线程执行
		 */
		@Override
		protected void onPostExecute(ArrayList<String> list)
		{
			if (error == 1)
			{
				showWarning("", e.getMessage(), e.getMore(), true);
			}
			else
			{
				try
				{
					ana(list);
				}
				catch (mException e)
				{
					showWarning("", e.getMessage(), e.getMore(), true);
				}
			}
		}
	}

	/*------------------------------自定义Adapter------------------------*/
	class lzlAdapter extends BaseAdapter
	{
		ArrayList<ArrayMap<String, String>> list = new ArrayList<ArrayMap<String, String>>();
		Context context;

		//返回项数
		@Override
		public int getCount()
		{
			return list.size();
		}

		@Override
		public Object getItem(int p1)
		{
			return list.get(p1);
		}

		@Override
		public long getItemId(int p1)
		{
			return p1;
		}

		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			ViewHolder viewholder;

			//判断view是否为空
			if (p2 == null)
			{
				viewholder = new ViewHolder();
				p2 = LayoutInflater.from(context).inflate(R.layout.lzl, null);

				LinearLayout l1 = (LinearLayout)p2.findViewById(R.id.lzlL1);
				viewholder.head = (ImageView)l1.findViewById(R.id.lzlIcon);

				LinearLayout l2 = (LinearLayout)l1.findViewById(R.id.lzlL2);
				viewholder.id = (TextView)l2.findViewById(R.id.lzlID);
				viewholder.date = (TextView)l2.findViewById(R.id.lzlDate);

				viewholder.content = (TextView)p2.findViewById(R.id.lzlContent);

				p2.setTag(viewholder);
			}
			else
			{
				viewholder = (ViewHolder)p2.getTag();
			}

			ArrayMap plzl = list.get(p1);

			viewholder.id.setText(plzl.get("id").toString() + "[" + plzl.get("nickname").toString() + "]");
			viewholder.date.setText(plzl.get("time"));
			viewholder.content.setText(plzl.get("content"));

			return p2;
		}

		class ViewHolder
		{
			ImageView head;
			TextView id;
			TextView date;
			TextView content;
		}
	}

	public void freshmax(int pagemax, int floormax)
	{
		this.pagemax = pagemax;
		this.floormax = floormax;

		String te = "";
		te += pagenow;
		te += "/";
		te += pagemax;
		te += "页 ";
		te += floornow;
		te += "/";
		te += floormax;
		te += "楼";
		midtext.setText(te);
	}
	public void freshmax()
	{
		showWait("正在获取信息...");
		if (floormax == 1)
		{
			ArrayList<Integer> ma;
			try
			{
				ma = tb.getMax();
			}
			catch (mException e)
			{
				ma = null;
				showWarning("", e.getMessage(), e.getMore(), false);
			} 
			if (ma != null)
			{
				pagemax = ma.get(0);
				floormax = ma.get(1);
			}
		}
		String te = "";
		te += pagenow;
		te += "/";
		te += pagemax;
		te += "页 ";
		te += floornow;
		te += "/";
		te += floormax;
		te += "楼";
		midtext.setText(te);
		killWait();
	}
	private void setJumpDialogEdit_TextColor(EditText edit, TextView text, int colorid)
	{
		edit.setTextColor(getResources().getColor(colorid));
		text.setTextColor(getResources().getColor(colorid));
	}
	private void showtips(String title, String message, String negatiebutton)
	{
		AlertDialog.Builder tips = new AlertDialog.Builder(MainActivity.this);
		tips.setTitle(title);
		tips.setMessage(message);
		tips.setNegativeButton(negatiebutton, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
				}
			});
		tips.setOnDismissListener(new DialogInterface.OnDismissListener(){
				@Override
				public void onDismiss(DialogInterface p1)
				{
				}
			});
		tips.show();
	}
	private void showWarning(String title, String message, final String more, boolean cancelable)
	{
		AlertDialog.Builder warn = new AlertDialog.Builder(MainActivity.this);
		if (title == "" || title == null)
		{
			title = "哎呦，好像遇到错误了";
		}
		warn.setTitle(title);
		warn.setMessage(message);
		warn.setCancelable(cancelable);
		warn.setIcon(R.drawable.warning);
		warn.setNegativeButton("复制错误信息到剪贴板", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					Tool.copyToClipBoard(cm, more);
				}
			});
		warn.setOnDismissListener(new DialogInterface.OnDismissListener(){

				@Override
				public void onDismiss(DialogInterface p1)
				{
					killWait();
				}
			});
		warn.show();
	}
	public void showWait(String message)
	{
		if (pd != null)
		{
			pd.setMessage(message);
		}

		//等待框，下面是对应的关闭函数
		if (pd == null)
		{
			pd = new ProgressDialog(MainActivity.this);
			pd.setIndeterminate(false);//循环滚动
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage(message);
			pd.setCancelable(false);//false不能取消显示，true可以取消显示
			pd.show();
		}
	}
	public void killWait()
	{
		if (pd != null)
		{
			pd.dismiss();
			pd = null;
		}
	}
	private void showUpdateMess()
	{
		String content = getResources().getString(R.string.update);
		showtips("更新日志", content, "确定");
	}
	private void showToast(String content, int len)
	{
		if (len > 1 || len < 0)
		{
			len = 0;
		}
		Toast.makeText(this, content, len).show();
	}
}

