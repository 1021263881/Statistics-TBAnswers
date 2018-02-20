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

import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;

public class MainActivity extends Activity 
{
	private Tools tool = new Tools();
	private Tools.HttpService httpservice = tool.new HttpService();
	private Tools.TB tb = tool.new TB();
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
		ntj.put("nicheng", "");
		ntj.put("yx", "1");
		ntj.put("gzl", "0");

		//设置personButton监听器
		personb = new OnClickListener(){
			@Override
			public void onClick(View p1)
			{
				Drawable pbuttonback = p1.getBackground();
				TextView id = (TextView)((View)p1.getParent()).findViewById(R.id.personid);
				TextView nicheng = (TextView)((View)p1.getParent()).findViewById(R.id.personNiCheng);
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
					updatePerson(did, id.getText().toString(), nicheng.getText().toString());
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

				}
			});

		//设置next按钮事件
		nextbutton = (Button)actionbarview.findViewById(R.id.actionbarnext);
		nextbutton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
				}
			});

		//设置midtext单击事件
		midtext = (TextView)actionbarview.findViewById(R.id.actionbarmidtext);
		midtext.setTextColor(getResources().getColor(R.color.mdblack));
		midtext.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					AlertDialog.Builder mdialogb = new AlertDialog.Builder(MainActivity.this);
					mdialogb.setIcon(R.drawable.jump);
					mdialogb.setTitle("你倒是选一个跳转方式啊");
					mdialogb.setView(R.layout.jumpdialog);
					mdialogb.setNegativeButton("ojbk", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								// TODO: Implement this method
								Toast.makeText(MainActivity.this, "oojbk", 0).show();
							}
						});

					//获取Alert
					mdialog = mdialogb.show();

					//获取按钮
					jumppagebutton = (RadioButton)mdialog.findViewById(R.id.jumppagebutton);
					jumpfloorbutton = (RadioButton)mdialog.findViewById(R.id.jumpfloorbutton);

					//获取文本框
					jumppageedit = (EditText)mdialog.findViewById(R.id.jumppage);
					jumppagetext = (TextView)mdialog.findViewById(R.id.jumppagetext);
					jumpflooredit = (EditText)mdialog.findViewById(R.id.jumpfloor);
					jumpfloortext = (TextView)mdialog.findViewById(R.id.jumpfloortext);

					//设置文字
					jumppagetext.setText("/" + String.valueOf(pagemax) + "页");
					jumpfloortext.setText("/" + String.valueOf(floormax) + "楼");

					//设置初始色
					setJumpDialogEdit_TextColor(jumppageedit, jumppagetext, R.color.mdblack_f);
					setJumpDialogEdit_TextColor(jumpflooredit, jumpfloortext, R.color.mdblack_f);

					String aa="中石化";
					try {
						//aa = tb.get帖子("", "4592800021", pagemax, pagemax, true).get(0);
						aa = tb.getFloor("", "4592800021", "91112608095", 1).toString();
						//aa = tb.获取主题列表("", "minecraftpe", 1).toString();
					} catch (mException e) {
						showWarning("", e.getMessage(), e.getMore());
					}
					//aa = tj.get("102").get("yx");
					ArrayMap<String, String> aaa = new ArrayMap<String, String>();
					aaa.put("仙山", "仙山快女装");
					aa = aaa.put("仙山", "仙山援交吧");

					//aa = aaa.get("仙山");

					Toast.makeText(MainActivity.this, "get√", 0).show();
					tool.copyToClipBoard(cm, aa);
					tool.loadHtmlInWebview(web, aa);

					//检测焦点改变
					jumppageedit.setOnFocusChangeListener(new OnFocusChangeListener(){
							@Override
							public void onFocusChange(View p1, boolean p2)
							{
								if (p2) {
									//展开软键盘
									InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);//得到系统的输入方法服务
									imm.showSoftInput(p1, 0);

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

		//获取ScrollView
		list = (LinearLayout)findViewById(R.id.mainLinearLayout);
		/*try
		 {
		 tool.loadHtmlInWebview(web, get("http://tieba.baidu.com", ""));
		 }
		 catch (InterruptedException e)
		 {
		 Toast.makeText(MainActivity.this, e.toString(), 0).show();
		 }
		 catch (ExecutionException e)
		 {
		 Toast.makeText(MainActivity.this, e.toString(), 0).show();
		 }*/
	}
	//统计
	private Boolean updatePerson(int did, String id, String nicheng)
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
					tj.put(id, ntj);
					if (nicheng == tj.get(id).put("nicheng", nicheng)) {
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
							oldg == Integer.valueOf(tj.get(id).put("gzl", String.valueOf(oldg)))){
								return true;
						}
					}
				}
				break;
		}
		return false;
	}
	//更新名单
	public Boolean updatePersonList(ArrayList<ArrayMap<String, String>> personlist)
	{
		//清空内容
		if (list.getChildCount() != 0) {
			list.removeAllViews();
		}

		int ma = personlist.size();
		for (int i = 0; i < ma; i++) {
			person = View.inflate(MainActivity.this, R.layout.person, null);

			//设置id, 昵称
			((TextView)person.findViewById(R.id.personid)).setText(personlist.get(i).get("id"));
			((TextView)person.findViewById(R.id.personNiCheng)).setText(personlist.get(i).get("nicheng"));

			//设置按钮监听
			person.findViewById(R.id.personbutton).setBackground(none);
			person.findViewById(R.id.personbutton).setOnClickListener(personb);
			list.addView(person, personlp);
		}

		person = View.inflate(MainActivity.this, R.layout.person, null);
		person.findViewById(R.id.personbutton).setOnClickListener(personb);
		list.addView(person, personlp);
		if (list.getChildCount() == personlist.size()) {
			return true;
		} else {
			return false;
		}
	}
	public void freshmax()
	{
		waitDialog("正在获取信息...");
		if (pagemax == 1 && floormax == 1) {
			ArrayList<Integer> ma;
			try {
				ma = tb.getMax();
			} catch (mException e) {
				ma = null;
				showWarning("", e.getMessage(), e.getMore());
			} 
			if (ma != null) {
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
		te += "层";
		midtext.setText(te);
		continueForWait();
	}
	public void setPageMax(int num)
	{
		pagemax = num;
	}
	public void setFloorMax(int num)
	{
		floormax = num;
	}
	private void setJumpDialogEdit_TextColor(EditText edit, TextView text, int colorid)
	{
		edit.setTextColor(getResources().getColor(colorid));
		text.setTextColor(getResources().getColor(colorid));
	}
	private void showtips(String title, String message)
	{
		AlertDialog.Builder tips = new AlertDialog.Builder(MainActivity.this);
		tips.setTitle(title);
		tips.setMessage(message);
		tips.setOnDismissListener(new DialogInterface.OnDismissListener(){
				@Override
				public void onDismiss(DialogInterface p1)
				{
				}
			});
		tips.show();
	}
	private void showWarning(String title, String message, final String more)
	{
		AlertDialog.Builder warn = new AlertDialog.Builder(MainActivity.this);
		if (title == "" || title == null) {
			title = "哎呦，好像遇到错误了";
		}
		warn.setTitle(title);
		warn.setMessage(message);
		warn.setCancelable(false);
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
	public void waitDialog(String message)
	{
		if (pd != null) {
			pd.dismiss();
			pd = null;
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
	}
	private void continueForWait()
	{
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
	}
}

