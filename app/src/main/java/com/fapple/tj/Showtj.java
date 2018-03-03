package com.fapple.tj;
import android.app.*;
import android.os.*;
import android.widget.*;
import java.util.*;
import android.util.*;
import org.json.*;

public class Showtj extends Activity
{
	private Button clearbutton;
	private Button copybutton;
	private MainActivity main;
	private ListView list;
	private ArrayList<ArrayMap<String, String>> tj;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
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
		
		clearbutton = (Button) findViewById(R.id.showtjclearbutton);
		copybutton = (Button) findViewById(R.id.showtjcopybutton);
	}
	
}
