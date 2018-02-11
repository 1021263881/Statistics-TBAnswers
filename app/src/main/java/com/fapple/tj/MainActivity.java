package com.fapple.tj;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.webkit.*;
import android.widget.*;
import com.fapple.*;
import java.util.concurrent.*;
import java.util.*;

public class MainActivity extends Activity 
{
	private Tools tool = new Tools();
	private Tools.HttpService httpservice = tool.new HttpService();
	private Tools.TB tb = tool.new TB();
	private ClipboardManager cm = null;
	private ProgressDialog pd = null;

	WebView web;
	ScrollView list;
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

	private Integer pagemax = 1;
	private Integer pagenow = 1;
	private Integer floormax = 1;
	private Integer floornow = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		//获取剪贴板
		cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

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

					//设置初始色
					setJumpDialogEdit_TextColor(jumppageedit, jumppagetext, R.color.mdblack_f);
					setJumpDialogEdit_TextColor(jumpflooredit, jumpfloortext, R.color.mdblack_f);

					String aa="中石化";
					try {
						aa = tb.get帖子("", "4592800021", 1, true).get(0);
					} catch (mException e) {
						showWarning("", e.getMessage(), e.getMore());
					}
					Toast.makeText(MainActivity.this, "get√", 0).show();
					tool.copyToClipBoard(cm, aa);
					tool.loadHtmlInWebview(web, aa);

					//检测焦点改变
					jumppageedit.setOnFocusChangeListener(new OnFocusChangeListener(){
							@Override
							public void onFocusChange(View p1, boolean p2)
							{
								if (p2) {
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

		//获取ScrollView
		list = (ScrollView)findViewById(R.id.mainScrollView);
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
	private void freshmax()
	{
		waitDialog("正在获取信息...");
		try {
			ArrayList<Integer> ma = tb.getMax();
			pagemax = ma.get(0);
			floormax = ma.get(1);
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
		} catch (mException e) {
			showWarning("", e.getMessage(), e.getMore());
		} finally {
			continueForWait();
		}
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
		if(title == "" || title == null){
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
	private void waitDialog(String message)
	{
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

