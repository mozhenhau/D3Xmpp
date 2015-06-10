/**
 * 
 */
package com.d3.d3xmpp.activites;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.adapter.SearchAdapter;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.dao.NewFriendDbHelper;
import com.d3.d3xmpp.dao.NewMsgDbHelper;

/**
 * @author MZH
 *
 */
public class NewFriendActivity extends BaseActivity {
	@D3View(click="onClick") TextView rightBtn;
	@D3View(click="onClick") ImageView leftBtn;
	@D3View TextView titleView;
	@D3View ListView listView;
	private List<String> friends = new ArrayList<String>();
	private SearchAdapter adapter;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_new_friend);
		adapter = new SearchAdapter(this);
		adapter.isNewFriend = true;
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					Intent intent = new Intent(getApplicationContext(), FriendActivity.class);
					intent.putExtra("username", adapter.getItem(position));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
			}
		});
		initData();
	}
	
	public void initData() {
		friends = NewFriendDbHelper.getInstance(getApplicationContext()).getNewFriend();
		adapter.addAll(friends);
		if (adapter.getCount()==0) {
			listView.setVisibility(View.GONE);
		}
	}
	
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		
		case R.id.rightBtn:
			new AlertDialog.Builder(this) 
		 	.setTitle("提示")
		 	.setMessage("确认清空信息?清空后不可恢复？")
		 	.setPositiveButton("是", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					NewFriendDbHelper.getInstance(getApplicationContext()).clear();
					adapter.clear();
					adapter.notifyDataSetChanged();
				}
			})
		 	.setNegativeButton("否", null)
		 	.show();
			
			break;
		}
	}
	

	@Override
	protected void onDestroy() {
		NewMsgDbHelper.getInstance(MyApplication.getInstance()).delNewMsg(""+0);
		MyApplication.getInstance().sendBroadcast(new Intent("FriendNewMsg"));
		super.onDestroy();
	}
	
}
