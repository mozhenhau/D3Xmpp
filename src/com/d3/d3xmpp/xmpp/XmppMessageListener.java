package com.d3.d3xmpp.xmpp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.packet.DelayInformation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.activites.MainActivity;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.dao.MsgDbHelper;
import com.d3.d3xmpp.dao.NewMsgDbHelper;
import com.d3.d3xmpp.model.ChatItem;
import com.d3.d3xmpp.util.DateUtil;
import com.d3.d3xmpp.util.FileUtil;
import com.d3.d3xmpp.util.MyAndroidUtil;


public class XmppMessageListener implements PacketListener {

	@Override
	public void processPacket(Packet packet) {
		Message nowMessage = (Message) packet;
		if(Constants.IS_DEBUG)
		Log.e("xmppchat come", nowMessage.toXML());

		if (nowMessage.toXML().contains("<invite")) {
			MyAndroidUtil.showNoti("你被邀请加入群组"+XmppConnection.getRoomName(nowMessage.getFrom()));
			XmppConnection.getInstance().joinMultiUserChat(Constants.USER_NAME, XmppConnection.getRoomName(nowMessage.getFrom()), null);
		}
		
		
		Type type = nowMessage.getType();
		if ((type == Message.Type.groupchat || type == Message.Type.chat || type == Message.Type.normal)&&!nowMessage.getBody().equals("")) { 
			String chatName = "";
			String userName = "";
			int chatType = ChatItem.CHAT;
			
			//name
			if (type == Message.Type.groupchat) {
				chatName = XmppConnection.getRoomName(nowMessage.getFrom());
				userName = XmppConnection.getRoomUserName(nowMessage.getFrom());
				chatType = ChatItem.GROUP_CHAT;
			}
			else {
				chatName = userName = XmppConnection.getUsername(nowMessage.getFrom());
			}
			
			if (!userName.equals(Constants.USER_NAME)) {  //不是自己发出的,防群聊
				//time
				String dateString;
				DelayInformation inf = (DelayInformation) nowMessage.getExtension("x",
						"jabber:x:delay");
				if (inf == null)
					dateString = DateUtil.now_MM_dd_HH_mm_ss();
				else
					dateString = DateUtil.dateToStr_MM_dd_HH_mm_ss(inf.getStamp());
				
				//msg
				ChatItem msg = null;
				String msgBody;                     //判断是否图片
				if (nowMessage.getProperty("imgData") != null) {
					if(FileUtil.getType(nowMessage.getBody()) == FileUtil.SOUND)
						msgBody = Constants.SAVE_SOUND_PATH + "/"+ nowMessage.getBody();
					else
						msgBody = Constants.SAVE_IMG_PATH + "/"+ nowMessage.getBody();
					FileUtil.saveFileByBase64(nowMessage.getProperty("imgData").toString(),msgBody );
				} else
					msgBody = nowMessage.getBody();
				
				msg =  new ChatItem(chatType,chatName,userName, "", msgBody, dateString, 0);
				NewMsgDbHelper.getInstance(MyApplication.getInstance()).saveNewMsg(chatName);
				MsgDbHelper.getInstance(MyApplication.getInstance()).saveChatMsg(msg);
				MyApplication.getInstance().sendBroadcast(new Intent("ChatNewMsg"));
				MyAndroidUtil.showNoti(msgBody);
			}
		}
	}
}
