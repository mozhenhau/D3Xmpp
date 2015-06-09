package com.d3.d3xmpp.xmpp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;

import android.content.Intent;
import android.util.Log;

import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.dao.MsgDbHelper;
import com.d3.d3xmpp.dao.NewFriendDbHelper;
import com.d3.d3xmpp.dao.NewMsgDbHelper;
import com.d3.d3xmpp.model.ChatItem;
import com.d3.d3xmpp.model.Friend;
import com.d3.d3xmpp.util.DateUtil;
import com.d3.d3xmpp.util.MyAndroidUtil;


public class XmppPresenceListener implements PacketListener {

	@Override
	public void processPacket(Packet packet) {
		Presence presence = (Presence) packet;
		if(Constants.IS_DEBUG)
		Log.e("xmppchat come", presence.toXML());
		
		String jid = presence.getFrom();//发送方  
        String to = presence.getTo();//接收方  
        //Presence.Type有7中状态  
        if (presence.getType().equals(Presence.Type.subscribe)) {// 好友申请
//        	if (!XmppConnection.getInstance().getFriendList().contains(new Friend(XmppConnection.getUsername(jid)))) {
        	boolean isExist = false;
			for (Friend friend : XmppConnection.getInstance().getFriendListAll()) {
				if (friend.equals(new Friend(XmppConnection.getUsername(jid)))) {
					String userName = XmppConnection.getUsername(jid);
					MyAndroidUtil.showNoti(userName+"同意添加好友");
					ChatItem msg =  new ChatItem(ChatItem.CHAT,userName,userName, "", userName+"同意添加好友", DateUtil.now_MM_dd_HH_mm_ss(), 0);
					NewMsgDbHelper.getInstance(MyApplication.getInstance()).saveNewMsg(userName);
					MsgDbHelper.getInstance(MyApplication.getInstance()).saveChatMsg(msg);
					MyApplication.getInstance().sendBroadcast(new Intent("ChatNewMsg"));
					
					friend.type = ItemType.both;
					XmppConnection.friendListTemp.add(friend);
					isExist = true;
				}
			}
        	if (!isExist) {
               	NewFriendDbHelper.getInstance(MyApplication.getInstance()).saveNewFriend(XmppConnection.getUsername(jid));
               	NewMsgDbHelper.getInstance(MyApplication.getInstance()).saveNewMsg(""+0);
			}
			MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
//			}
		} 
		else if (presence.getType().equals(Presence.Type.subscribed)) {// 同意添加好友
			if(Constants.IS_DEBUG)
        	Log.e("friend", jid+"同意添加");
//			MyAndroidUtil.showNoti(jid+"同意添加好友");
			XmppConnection.getInstance().addUser(XmppConnection.getUsername(jid));
			MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
		} 
		else if (presence.getType().equals(Presence.Type.unsubscribe)) {// 拒绝添加好友 删除好友
			if(Constants.IS_DEBUG)
    		Log.e("friend", "好友删除");
			
			for (Friend friend : XmppConnection.getInstance().getFriendList()) {
				if (friend.equals(new Friend(XmppConnection.getUsername(jid)))) {
					friend.type = ItemType.remove;
					XmppConnection.friendListTemp.add(friend);
				}
			}
			MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
		} else if (presence.getType().equals(Presence.Type.unsubscribed)) {
			if(Constants.IS_DEBUG)
    		Log.e("friend", "好友删除1");
			for (Friend friend : XmppConnection.getInstance().getFriendList()) {
				if (friend.equals(new Friend(XmppConnection.getUsername(jid)))) {
					friend.type = ItemType.remove;
					XmppConnection.friendListTemp.add(friend);
				}
			}
			MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
		} 
	}
}
