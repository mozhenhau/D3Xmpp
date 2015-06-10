/**
 * 
 */
package com.d3.d3xmpp.activites;

import org.jivesoftware.smackx.packet.VCard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.dao.MsgDbHelper;
import com.d3.d3xmpp.dao.NewMsgDbHelper;
import com.d3.d3xmpp.model.Friend;
import com.d3.d3xmpp.model.User;
import com.d3.d3xmpp.util.CircularImage;
import com.d3.d3xmpp.util.Tool;
import com.d3.d3xmpp.util.XmppLoadThread;
import com.d3.d3xmpp.xmpp.XmppConnection;

/**
 * @author MZH
 *
 */
public class FriendActivity extends BaseActivity {
	@D3View(click="onClick") Button operBtn;
	@D3View TextView nameView,sexView,signView,nickNameView,phoneView,emailView;
	@D3View CircularImage headView;
	private String username;
	private User friend;
	private FriendReceiver reciver;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_friend);
		initTitle();
		username = getIntent().getStringExtra("username");
		nameView.setText(username);
		// 接收到新消息的事件监听
		reciver = new FriendReceiver();
		registerReceiver(reciver,new IntentFilter("friendChange"));
		
		if (username.equals(Constants.USER_NAME)) {
			operBtn.setVisibility(View.GONE);
		}
		isFriend();
		initData();
	}
	
	public void isFriend(){
		if (XmppConnection.getInstance().getFriendBothList().contains(new Friend(username))) {
			operBtn.setText("移出通讯录");
		}
		else {
			operBtn.setText("添加到通讯录");
		}
	}
	
	
	public void	initData() {
		new XmppLoadThread(this) {
			
			@Override
			protected void result(Object object) {
				VCard vCard = (VCard)object;
				friend = new User(vCard);
				if (friend!=null) {
					if (friend.sex != null) {
						sexView.setText(friend.sex);
					}
					if (friend.intro != null) {
						signView.setText(friend.intro);
					}
					if (friend.nickname != null) {
						nickNameView.setText(friend.nickname);
					}
					if (friend.email != null) {
						emailView.setText(friend.email);
					}
					if (friend.mobile != null) {
						phoneView.setText(friend.mobile);
					}
					friend.showHead(headView);
				}
			}
			
			@Override
			protected Object load() {
				return XmppConnection.getInstance().getUserInfo(username);
			}
		};
	}
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.operBtn:
			if (operBtn.getText().equals("添加到通讯录")) {
				XmppConnection.getInstance().addUser(username);
				Tool.initToast(getApplicationContext(), "添加成功，等待通过验证");
				MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
				isFriend();
			}
			else if (operBtn.getText().equals("移出通讯录")){
				XmppConnection.getInstance().removeUser(username);
				Tool.initToast(getApplicationContext(), "移除成功");
				MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
				operBtn.setText("添加到通讯录");
				NewMsgDbHelper.getInstance(getApplicationContext()).delNewMsg(username);
				MyApplication.getInstance().sendBroadcast(new Intent("ChatNewMsg"));
				MsgDbHelper.getInstance(getApplicationContext()).delChatMsg(username);
			}
			break;

		default:
			break;
		}
		
	}
	
	private class FriendReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			isFriend();
		}
	}
}
