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
import android.content.*;

public class Showtj extends Activity
{
	private Button cleenbutton;
	private Button copybutton;
	private MainActivity main;
	private ListView list;
	private ArrayList<ArrayMap<String, String>> tj = new ArrayList<ArrayMap<String, String>>();

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
		cleenbutton.setText("清空");
		cleenbutton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					did = -1;
					onPause();
					finish();
				}
			});

		copybutton = (Button) findViewById(R.id.showtjcopybutton);
		copybutton.setText("导出到剪贴板");
		copybutton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					did = 1;
					onPause();
					finish();
				}
			});

		list = (ListView) findViewById(R.id.showtjlst);

		tjAdapter tjadapter = new tjAdapter(this);
		list.setAdapter(tjadapter);
	}

	@Override
	protected void onPause()
	{
		setResult(did);
		super.onPause();
	}

	class tjAdapter extends BaseAdapter
	{
		private Context context;

		tjAdapter(Context context)
		{
			this.context = context;
		}

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
			ViewHolder viewholder;

			//判断view是否为空
			if (p2 == null) {
				viewholder = new ViewHolder();
				p2 = LayoutInflater.from(context).inflate(R.layout.showtjmessage, null);

				viewholder.id = (TextView)p2.findViewById(R.id.showtjmessageid);

				viewholder.num = (TextView)p2.findViewById(R.id.showtjmessagenum);

				p2.setTag(viewholder);
			} else {
				viewholder = (ViewHolder)p2.getTag();
			}

			ArrayMap ps = tj.get(p1);

			viewholder.id.setText(ps.get("id").toString() + "[" + ps.get("nickname").toString() + "]");
			viewholder.num.setText(ps.get("yx") + "---" + ps.get("gzl"));

			return p2;
		}

		class ViewHolder
		{
			TextView id;
			TextView num;
		}


	}
}
