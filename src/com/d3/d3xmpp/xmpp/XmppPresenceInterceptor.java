package com.d3.d3xmpp.xmpp;

import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;

import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.model.Friend;

import android.content.Intent;
import android.util.Log;

public class XmppPresenceInterceptor implements PacketInterceptor {

	@Override
	public void interceptPacket(Packet packet) {
		Presence presence = (Presence) packet;
		if(Constants.IS_DEBUG)
		Log.e("xmppchat send", presence.toXML());

		String from = presence.getFrom();// 发送方
		String to = presence.getTo();// 接收方
		// Presence.Type有7中状态
		if (presence.getType().equals(Presence.Type.subscribe)) {// 发出好友申请
			for (Friend friend : XmppConnection.getInstance().getFriendList()) {
				System.out.println("我好友"+friend.username+"和我的关系"+friend.type);
				if ((friend.username.equals(XmppConnection.getUsername(to)) && friend.type == ItemType.from)) {
					XmppConnection.getInstance().changeFriend(friend, ItemType.both);
			        Log.e("friend", "我向"+to+"发出好友请求toBoth");
				}
				else if (friend.username.equals(XmppConnection.getUsername(to))) {
					XmppConnection.getInstance().changeFriend(friend, ItemType.to);
			        Log.e("friend", "我向"+to+"发出好友请求toTo");
				}
			}
			
			if (!XmppConnection.getInstance().getFriendList().contains(new Friend(XmppConnection.getUsername(to)))) {
				Friend friend  = new Friend(XmppConnection.getUsername(to));
				friend.type = ItemType.to;
				XmppConnection.getInstance().getFriendList().add(friend);
			}
			MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
		} 
		else if (presence.getType().equals(Presence.Type.subscribed)) {// 同意添加好友
			if(Constants.IS_DEBUG)
	        Log.e("friend", to+"我同意好友添加");
//			MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
		} 
		else if (presence.getType().equals(Presence.Type.unsubscribe)||presence.getType().equals(Presence.Type.unsubscribed)) {// 拒绝添加好友 删除好友
			if(Constants.IS_DEBUG)
		       Log.e("friend", "我删除好友"+to);
			for (Friend friend : XmppConnection.getInstance().getFriendList()) {
				if (friend.username.equals(XmppConnection.getUsername(to))) {
					XmppConnection.getInstance().changeFriend(friend, ItemType.remove);
				}
			}
			MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
		}
	}
}
