package com.d3.d3xmpp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.ImgConfig;
import com.d3.d3xmpp.d3View.expression.ExpressionUtil;
import com.d3.d3xmpp.dao.NewMsgDbHelper;
import com.d3.d3xmpp.model.ChatItem;
import com.d3.d3xmpp.util.CircularImage;
import com.d3.d3xmpp.util.StringUtil;

public class MsgAdapter extends ArrayAdapter<ChatItem> {
	private Context cxt;
	private NewMsgDbHelper newMsgDbHelper ;
	
	public MsgAdapter(Context context) {
		super(context, 0);
		this.cxt = context;
		newMsgDbHelper = NewMsgDbHelper.getInstance(cxt);
	}


	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(cxt).inflate(R.layout.row_msg, null);
			holder = new ViewHolder();
			holder.headImg = (CircularImage) convertView.findViewById(R.id.headImg);
			holder.nickView = (TextView) convertView.findViewById(R.id.nickView);
			holder.msgView = (TextView) convertView.findViewById(R.id.msgView);
			holder.countView = (TextView) convertView.findViewById(R.id.countView);
			holder.dateView = (TextView) convertView.findViewById(R.id.dateView);
			convertView.setTag(holder); 
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ChatItem item = getItem(position);

		
//	//是图片或者表情则先替换
		if (item.msg!=null) {
			if(item.msg.contains(Constants.SAVE_IMG_PATH))
				holder.msgView.setText("[图片]");
			else if(item.msg.contains(Constants.SAVE_SOUND_PATH))
					holder.msgView.setText("[语音]");
			else if(item.msg.contains("[/g0"))
					holder.msgView.setText("[动画表情]");
			else if(item.msg.contains("[/f0"))  //适配表情
				holder.msgView.setText(ExpressionUtil.getText(cxt, StringUtil.Unicode2GBK(item.msg)));
			else if(item.msg.contains("[/a0"))
				holder.msgView.setText("[位置]");
			else{
				holder.msgView.setText(item.msg);
			}
		}
		holder.nickView.setText(item.chatName);
		holder.countView.setVisibility(View.GONE);
		holder.dateView.setText(item.sendDate);
		if (item.chatType == ChatItem.CHAT) {
			ImgConfig.showHeadImg(item.username, holder.headImg);
		}
		else{
			holder.headImg.setImageResource(R.drawable.group_chat_icon);
		}
			
		//是否显示有消息
		int newCount = newMsgDbHelper.getMsgCount(item.chatName);
		if(newCount!=0){
			holder.countView.setVisibility(View.VISIBLE);
			holder.countView.setText(""+newCount);
		}
		return convertView;
	}
	
	static class ViewHolder {
		CircularImage headImg;
		TextView nickView;
		TextView msgView;
		TextView countView;
		TextView dateView;
	} 
}