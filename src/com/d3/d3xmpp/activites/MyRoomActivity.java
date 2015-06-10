/**
 * 
 */
package com.d3.d3xmpp.activites;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.adapter.MyRoomAdapter;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.model.ChatItem;
import com.d3.d3xmpp.model.Room;
import com.d3.d3xmpp.xmpp.XmppConnection;

/**
 * @author MZH
 *
 */
public class MyRoomActivity extends BaseActivity {
	@D3View ListView listView;
	private MyRoomAdapter adapter;
//	private List<Room> rooms;
	private UpMessageReceiver mUpMessageReceiver;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_my_room);
		initTitle();
		adapter = new MyRoomAdapter(getApplicationContext());	
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), ChatActivity.class);
				intent.putExtra("chatName", adapter.getItem(position).name);
				intent.putExtra("roomId", adapter.getItem(position).roomid);
				intent.putExtra("chatType", ChatItem.GROUP_CHAT);
				startActivity(intent);
			}
		});
		if (XmppConnection.getInstance().getMyRoom()!=null) {
			adapter.addAll(XmppConnection.getInstance().getMyRoom());
		}
		
		mUpMessageReceiver = new UpMessageReceiver();
		registerReceiver(mUpMessageReceiver, new IntentFilter("LeaveRoom"));
	}
	
	
	private class UpMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 收到廣播更新我们的界面
//			finish();
			if (XmppConnection.getInstance().getMyRoom()!=null) {
				adapter.clear();
				adapter.addAll(XmppConnection.getInstance().getMyRoom());
				adapter.notifyDataSetChanged();
			}
		}
	}
}
