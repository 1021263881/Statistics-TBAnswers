package com.fapple.tj;
import android.app.*;
import android.os.*;
import android.widget.*;
import java.util.*;
import android.util.*;
import org.json.*;
import android.view.View.*;
import android.view.*;
import com.fapple.Tools.*;

public class Showtj extends Activity
{
	private Button cleenbutton;
	private Button copybutton;
	private MainActivity main;
	private ListView list;
	private ArrayList<ArrayMap<String, String>> tj;
	
	private int did = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showtj);
		
		//获取mainactivity
		main = (MainActivity) getIntent().getExtras().get("main");
		
		//获取传送过来的JSON
		String content = getIntent().getExtras().getString("tj");
		
		//解析
		JSONArray tjlist = null;
		try {
			tjlist = new JSONArray(content);
		} catch (JSONException e) {
			
		}
		
		String id = "";
		String nickname = "";
		int yx = -1;
		int gzl = -1;
		ArrayMap<String, String> meps;
		JSONObject eps;
		int len = tjlist.length();
		for (int i = 0; i < len; i++) {
			try {
				eps = (JSONObject)tjlist.get(i);
				id = (String)eps.get("id");
				nickname = (String)eps.get("nickname");
				yx = eps.get("yx");
				gzl = eps.get("gzl");
			} catch (JSONException e) {
				
			}
			meps = new ArrayMap<String, String>();
			meps.put("id", id);
			meps.put("nickname", nickname);
			meps.put("yx", String.valueOf(yx));
			meps.put("gzl", String.valueOf(gzl));
			tj.add(meps);
		}
		
		cleenbutton = (Button) findViewById(R.id.showtjclearbutton);
		cleenbutton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Toast.makeText(Showtj.this, "已清空统计数据", 0).show();
					list.removeAllViews();
					did = -1;
				}
			});
			
		copybutton = (Button) findViewById(R.id.showtjcopybutton);
		copybutton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Toast.makeText(Showtj.this, "已复制统计数据，记得清空哦~", 0);
					Tool.copyToClipBoard();
					did = 1;
				}
			});
			
		list = (ListView) findViewById(R.id.showtjlst);
	}

	@Override
	protected void onPause()
	{
		setResult(did);
		super.onPause();
	}
	
	class tjAdapter extends BaseAdapter
	{
		
		@Override
		public int getCount()
		{
			return tj.size();
		}

		@Override
		public Object getItem(int p1)
		{
			return tj.get(p1);
		}

		@Override
		public long getItemId(int p1)
		{
			return p1;
		}

		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			// TODO: Implement this method
			return null;
		}
		
		
	}
}
