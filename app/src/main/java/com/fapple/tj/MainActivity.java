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
import com.fapple.*;
import java.util.*;
import java.util.concurrent.*;
import android.widget.*;

import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;

public class MainActivity extends Activity 
{
	private Tools tool = new Tools();
	//private Tools.HttpService httpservice = tool.new HttpService();
	private Tools.TB tb = tool.new TB(this, "4592800021");
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
	RadioButton jumppagebutton;
	RadioButton jumpfloorbutton;

	//获取文本框
	EditText jumppageedit;
	TextView jumppagetext;
	EditText jumpflooredit;
	TextView jumpfloortext;

	//仨背景
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
				if (pbuttonback == none) {
					p1.setBackground(ojbk);
					did = 1;
				} else if (pbuttonback == ojbk) {
					p1.setBackground(well);
					did = 2;
				} else if (pbuttonback == well) {
					p1.setBackground(none);
					did = -1;
				}
				if (did != 0) {
					updatePerson(did, id.getText().toString(), nickname.getText().toString());
				}
			}
		};

		//设置ActionBar
		actionbar = getActionBar();
		if (actionbar != null) {
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
					try {
						ana(tb.getLastFloor());
					} catch (mException e) {

					}
				}
			});

		//设置next按钮事件
		nextbutton = (Button)actionbarview.findViewById(R.id.actionbarnext);
		nextbutton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					try {
						ana(tb.getNextFloor());
					} catch (mException e) {

					}
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
								if (p2) {
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
								if (p2) {
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
								if (jumppagebutton.isChecked() == true) {
									showWait("跳转中");
									double dpage = Integer.valueOf(jumppageedit.getText().toString());
									int page;
									if(dpage < pagemax + 200 && dpage > 0){
										page = Integer.valueOf(dpage);
									}
									tb.jumpFloor(page);
								} else if (jumpfloorbutton.isChecked() == true) {
									showWait("跳转中");
									double dfloor = Integer.valueOf(jumpflooredit.getText().toString());
									int floor;
									if(dfloor < floormax + 10 && dfloor > 0){
										floor = Integer.valueOf(dfloor);
									}
									tb.jumpPage(floor);
								} else {
									showtips("跳转失败", "你倒是选一个跳转方式啊", "oj8k");
								}
							}
						});

					//获取Alert
					mdialog = mdialogb.show();

					//tool.copyToClipBoard(cm, aa);
					//tool.loadHtmlInWebview(web, aa);
				}
			});

		//设置midtext长按事件
		midtext.setOnLongClickListener(new OnLongClickListener(){
				@Override
				public boolean onLongClick(View p1)
				{
					Toast.makeText(MainActivity.this, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
					hasCopy = 1;
					return true;
				}
			});

		//加载温馨提示
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
					freshmax();
				}
			});
		tips.show();

		//获取WebView
		web = (WebView)findViewById(R.id.mainWebView);

		// 设置可以支持缩放 
		//web.getSettings().setSupportZoom(true);

		web.getSettings().setUseWideViewPort(true);

		//自适应屏幕
		web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		web.getSettings().setLoadWithOverviewMode(true);//适应屏幕

		//默认UTF-8
		web.getSettings().setDefaultTextEncodingName("UTF-8");

		//获取ScrollView
		list = (LinearLayout)findViewById(R.id.mainLinearLayout);
		mlist = (ListView)findViewById(R.id.mainList);

	}

	//统计
	private Boolean updatePerson(int did, String id, String nickname)
	{
		hasCopy = 0;
		int old;
		switch (did) {
			case 1/*有效*/:
				if (tj.containsKey(id)) {
					if (tj.containsKey(id) == true) {
						old = Integer.valueOf(tj.get(id).get("yx"));
						old ++;
						if (old == Integer.valueOf(tj.get(id).put("yx", String.valueOf(old)))) {
							return true;
						}
					}
				} else {
					tj.put(id, new ArrayMap<String, String>(ntj));
					if (nickname == tj.get(id).put("nickname", nickname)) {
						return true;
					}
				}
				break;
			case 2/*高质量*/:
				if (tj.containsKey(id)) {
					return false;
				} else {
					old = Integer.valueOf(tj.get(id).get("gzl"));
					old ++;
					if (old == Integer.valueOf(tj.get(id).put("gzl", String.valueOf(old)))) {
						return true;
					}
				}
				break;
			case -1/*无效*/:
				if (tj.containsKey(id)) {
					return false;
				} else {
					old = Integer.valueOf(tj.get(id).get("yx"));
					int oldg = Integer.valueOf(tj.get(id).get("gzl"));
					if (old == 1) {
						tj.remove(id);
					} else {
						old --;
						oldg --;
						if (old == Integer.valueOf(tj.get(id).put("yx", String.valueOf(old))) && 
							oldg == Integer.valueOf(tj.get(id).put("gzl", String.valueOf(oldg)))) {
							return true;
						}
					}
				}
				break;
		}
		return false;
	}

	//更新名单
	public Boolean updatePersonList(ArrayList<String> personlist)
	{
		//清空内容
		if (list.getChildCount() != 0) {
			list.removeAllViews();
		}

		int ma = personlist.size();
		for (int i = 0; i < ma; i += 2) {
			person = View.inflate(MainActivity.this, R.layout.person, null);

			//设置id, 昵称
			((TextView)person.findViewById(R.id.personid)).setText(personlist.get(i));
			((TextView)person.findViewById(R.id.personnickname)).setText(personlist.get(i + 1));

			//设置按钮监听
			person.findViewById(R.id.personbutton).setBackground(none);
			person.findViewById(R.id.personbutton).setOnClickListener(personb);

			list.addView(person, personlp);
		}

		if (list.getChildCount() == personlist.size()) {
			return true;
		} else {
			return false;
		}
	}
	private void ana(ArrayList<String> list) throws mException
	{
		showWait("加载中...");
		if (list.size() < 5) {
			throw new mException("传值过少", "ana错误, ana.size=" + list.size());
		}
		pagenow = Integer.valueOf(list.get(0));
		pagemax = Integer.valueOf(list.get(1));
		floornow = Integer.valueOf(list.get(2));
		floormax = Integer.valueOf(list.get(3));

		midtext.setText(pagenow + "/" + pagemax + "页 " + floornow + "/" + floormax + "楼");

		tool.loadHtmlInWebview(web, list.get(4));

		int len = list.size();
		ArrayList<String> pr = new ArrayList<String>();
		String name = "";
		for (int i = 5; i < len; i += 2) {
			name = list.get(i);
			if (pr.contains(name) == false) {
				pr.add(name);
				name = list.get(i + 1);
				pr.add(name);
			}
		}
		updatePersonList(pr);
		killWait;
	}
	public void freshmax(int pagemax, int floormax){
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
		if (floormax == 1) {
			ArrayList<Integer> ma;
			try {
				ma = tb.getMax();
			} catch (mException e) {
				ma = null;
				showWarning("", e.getMessage(), e.getMore(), false);
			} 
			if (ma != null) {
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
					// TODO: Implement this method
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
		if (title == "" || title == null) {
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
					tool.copyToClipBoard(cm, more);
				}
			});
		warn.show();
	}
	public void showWait(String message)
	{
		ExecutorService executor = Executors.newCachedThreadPool();
		Task task = new Task(message);
		FutureTask<Boolean> futureTask = new FutureTask<Boolean>(task);
		executor.submit(futureTask);
		executor.shutdown();
		
		try {
			futureTask.get();
		} catch (InterruptedException e) {
			
		} catch (ExecutionException e) {
			
		}
	}
	public void killWait()
	{
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
	}
	class Task implements Callable<Boolean>
	{
		private String message;
		Task(String message)
		{
			this.message = message;
		}
		@Override
		public Boolean call() throws mException
		{
			if (pd != null) {
				pd.setMessage(message);
			}else{
				throw new mException("", "");
			}
			
			//等待框，下面是对应的关闭函数
			if (pd == null) {
				pd = new ProgressDialog(MainActivity.this);
				pd.setIndeterminate(false);//循环滚动
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pd.setMessage(message);
				pd.setCancelable(false);//false不能取消显示，true可以取消显示
				pd.show();
			}
			return true;
		}
	}
}

