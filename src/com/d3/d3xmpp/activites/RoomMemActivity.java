/**
 * 
 */
package com.d3.d3xmpp.activites;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.adapter.SearchAdapter;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.dao.MsgDbHelper;
import com.d3.d3xmpp.dao.NewMsgDbHelper;
import com.d3.d3xmpp.model.Room;
import com.d3.d3xmpp.util.LoadThread;
import com.d3.d3xmpp.util.Tool;
import com.d3.d3xmpp.xmpp.XmppConnection;

/**
 * @author MZH
 *
 */
public class RoomMemActivity extends BaseActivity {
	@D3View(click="onClick") ImageView leftBtn;
	@D3View(click="onClick") TextView rightBtn;
	@D3View(click="onClick") Button exitBtn;
	@D3View ListView listView;
	private SearchAdapter adapter;
//	private List<RoomMem> roomMems;
	private String roomName;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_room_mem);
		roomName = getIntent().getStringExtra("roomName");
		adapter = new SearchAdapter(getApplicationContext());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getApplicationContext(), FriendActivity.class);
				intent.putExtra("username", adapter.getItem(position));
				startActivity(intent);
			}
		});
		for (Room room : XmppConnection.getInstance().getMyRoom()) {
			if (room.name.equals(roomName)) {
				for (String mem : room.friendList) {
					adapter.add(mem);
				}
			}
		}
		
		
//		initData();
	}
	
//	public void initData() {
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("roomName", getIntent().getStringExtra("roomName"));
//		
//		new LoadThread(this,Constants.GET_ROOM_MEMBER_BY_NAME,map) {
//			
//			@Override
//			protected void refreshUI(String result) {
//				try {
//					JSONObject jsonObject = new JSONObject(result);
//					if (jsonObject.getString("state").equals("0")) {
//						roomMems = JsonUtil.jsonToObjectList(jsonObject.getString("items"), RoomMem.class);
//						for (RoomMem roomMem : roomMems) {
//							adapter.add(XmppConnection.getUsername(roomMem.jid));
//						}
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		
//	}
	
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		case R.id.rightBtn:
			Intent intent = new Intent(getApplicationContext(), ChoseActivity.class);
			intent.putExtra("roomName", roomName);
			startActivity(intent);
			break;
		case R.id.exitBtn:
			AlertDialog.Builder builder = new Builder(RoomMemActivity.this);
			builder
			.setMessage("确定要退出吗?")
			.setTitle("提示")
			.setPositiveButton("确认", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					XmppConnection.getInstance().leaveMuc(roomName);
//					Map<String, String> map = new HashMap<String, String>();
//					map.put("roomName", roomName);
//					map.put("jid", XmppConnection.getFullUsername(Constants.USER_NAME));
//					new LoadThread(RoomMemActivity.this,Constants.LEAVE_ROOM,map) {
//						@Override
//						protected void refreshUI(String result) {
//							XmppConnection.getInstance().leaveMuc(roomName);
//							Tool.initToast(RoomMemActivity.this, "退出成功");
//							NewMsgDbHelper.getInstance(MyApplication.getInstance()).delNewMsg(roomName);
//							MsgDbHelper.getInstance(MyApplication.getInstance()).delChatMsg(roomName);
//							MyApplication.getInstance().sendBroadcast(new Intent("ChatNewMsg"));
//							MyApplication.getInstance().sendBroadcast(new Intent("LeaveRoom"));
//							finish();
//						}
//					};
				}
			})
			.setNegativeButton("取消", null)
			.show();
			break;

		default:
			break;
		}
		
	}
}
