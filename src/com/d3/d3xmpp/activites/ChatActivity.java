/**
 * 
 */
package com.d3.d3xmpp.activites;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.adapter.ChatAdapter;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.d3View.D3Activity;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.d3View.MyListView;
import com.d3.d3xmpp.d3View.MyListView.OnRefreshListener;
import com.d3.d3xmpp.d3View.RecordButton;
import com.d3.d3xmpp.d3View.RecordButton.OnFinishedRecordListener;
import com.d3.d3xmpp.d3View.expression.ExpressionListener;
import com.d3.d3xmpp.d3View.expression.ExpressionView;
import com.d3.d3xmpp.dao.MsgDbHelper;
import com.d3.d3xmpp.dao.NewMsgDbHelper;
import com.d3.d3xmpp.model.ChatItem;
import com.d3.d3xmpp.util.FileUtil;
import com.d3.d3xmpp.util.ImageUtil;
import com.d3.d3xmpp.util.Tool;
import com.d3.d3xmpp.xmpp.XmppConnection;

/**
 * @author MZH
 *
 */
@SuppressLint("NewApi")
public class ChatActivity extends D3Activity {
	@D3View(click="onClick") ImageView leftBtn,rightBtn,sendBtn,picBtn,expBtn,moreBtn;
	@D3View(click="onClick") Button takePicBtn,chosePicBtn,fileBtn,adrBtn;
	@D3View LinearLayout moreLayout;
	@D3View RecordButton recordBtn;
	@D3View ExpressionView expView;
	@D3View EditText msgText;
	@D3View TextView titleView;
	@D3View MyListView listView;
	private ChatAdapter adapter;
	private List<ChatItem> chatItems = new ArrayList<ChatItem>();
	private MultiUserChat mulChat;
	private UpMessageReceiver mUpMessageReceiver;
	private String chatName;   //群的时候是群名称
	private int chatType = ChatItem.CHAT;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_chat);
		chatName = getIntent().getStringExtra("chatName");
		chatType = getIntent().getIntExtra("chatType",ChatItem.CHAT);
		initView();
		initData();
		titleView.setText(chatName);
	}
	
	private void initView() {
		adapter = new ChatAdapter(getApplicationContext(), chatName);
		listView.setAdapter(adapter);
		listView.setonRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						handler.sendEmptyMessage(0);
					}
				}).start();
			}
		});
		
		msgText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				expView.setVisibility(View.GONE);
			}
		});
		msgText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if("".equals(msgText.getText().toString())){
					sendBtn.setImageResource(R.drawable.icon_voice);
				}
				else
					sendBtn.setImageResource(R.drawable.icon_send_w);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		
		recordBtn.setOnFinishedRecordListener(new OnFinishedRecordListener() {
			@Override
			public void onFinishedRecord(String audioPath, int time) {
				if (audioPath != null) {
					try {
						XmppConnection.getInstance().sendMsgWithParms(FileUtil.getFileName(audioPath), 
								new String[]{"imgData"}, new Object[]{ImageUtil.getBase64StringFromFile(audioPath)});
						Tool.initToast(ChatActivity.this, "发送成功");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Tool.initToast(ChatActivity.this, "发送失败");
				}

			}
		});
		expView.setEditText(msgText);
		expView.setGifListener(new ExpressionListener() {
			@Override
			public void clickGif(String msg) {
				XmppConnection.getInstance().sendMsg(msg);
			}
		});
		// 会话内容改变，接受广播
		mUpMessageReceiver = new UpMessageReceiver();
		registerReceiver(mUpMessageReceiver, new IntentFilter("ChatNewMsg"));
		registerReceiver(mUpMessageReceiver, new IntentFilter("LeaveRoom"));
		
		if (chatType == ChatItem.CHAT) {
			// 创建回话
			XmppConnection.getInstance().setRecevier(chatName);
		}
		else if (chatType == ChatItem.GROUP_CHAT){
			mulChat = new MultiUserChat(XmppConnection.getInstance().getConnection(), XmppConnection.getInstance().getFullRoomname(chatName));
		}
	}
	
	private void initData(){
		chatItems = MsgDbHelper.getInstance(getApplicationContext()).getChatMsg(chatName);
		adapter.clear();
		adapter.addAll(chatItems);
		listView.setSelection(adapter.getCount() + 1);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				listView.onRefreshComplete();
				List<ChatItem> moreChatItems = MsgDbHelper.getInstance(getApplicationContext()).getChatMsgMore(
						listView.getCount()-1, chatName);
				for (int i = 0; i < moreChatItems.size(); i++) {
					chatItems.add(i, moreChatItems.get(i));
				}
				adapter.clear();
				adapter.addAll(chatItems);
				adapter.notifyDataSetChanged();
			}
		}
	};
	
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
			
		case R.id.rightBtn:
			Intent intent = new Intent();
			if (chatType == ChatItem.CHAT) {
				intent.setClass(getApplicationContext(), FriendActivity.class);
				intent.putExtra("username", chatName);
				startActivity(intent);
			}
			else if(chatType == ChatItem.GROUP_CHAT){
				intent.setClass(getApplicationContext(), RoomMemActivity.class);
				intent.putExtra("roomName", chatName);
				startActivity(intent);
			}
			break;
			
		case R.id.sendBtn:
			String msg = msgText.getText().toString(); // 获取text文本
			if(!msg.isEmpty()){
				if (mulChat!=null) {
					try {
						mulChat.sendMessage(msg);
					} catch (XMPPException e) {
						Tool.initToast(getApplicationContext(), "网络不稳定...请稍后重试");
						e.printStackTrace();
					}
					// 清空text
					msgText.setText("");
				}
				else {
					XmppConnection.getInstance().sendMsg(msg);
					msgText.setText("");
				}
			}
			else if(recordBtn.getVisibility() == View.GONE){
				msgText.setVisibility(View.GONE);
				recordBtn.setVisibility(View.VISIBLE);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(msgText.getWindowToken(), 0);
				sendBtn.setImageResource(R.drawable.icon_keyboard);
			}
			else if(msgText.getVisibility() == View.GONE){
				msgText.setVisibility(View.VISIBLE);
				recordBtn.setVisibility(View.GONE);
				sendBtn.setImageResource(R.drawable.icon_voice);
			}
			
			break;
			

		case R.id.msgText:
			expView.setVisibility(View.GONE);
			listView.setSelection(adapter.getCount()); // 去到最后一行
			break;	
			
			
		case R.id.moreBtn:
//			Intent intent1 = new Intent();
//			intent1.setClass(this, PicSrcPickerActivity.class);
//			startActivityForResult(intent1,PicSrcPickerActivity.CROP);
			if (moreLayout.getVisibility() == View.GONE) {
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				moreLayout.setVisibility(View.VISIBLE);
				expView.setVisibility(View.GONE);
			} else {
				moreLayout.setVisibility(View.GONE);
			}
			break;

		case R.id.takePicBtn:
			Intent intent1 = new Intent();
			CropImageActivity.isAutoSend = true;
			intent1.setClass(this, PicSrcPickerActivity.class);
			intent1.putExtra("type", PicSrcPickerActivity.TAKE_PIC);
			startActivityForResult(intent1,PicSrcPickerActivity.CROP);
			break;

		case R.id.chosePicBtn:
			Intent intent2 = new Intent();
			CropImageActivity.isAutoSend = true;
			intent2.setClass(this, PicSrcPickerActivity.class);
			intent2.putExtra("type", PicSrcPickerActivity.CHOSE_PIC);
			startActivityForResult(intent2,PicSrcPickerActivity.CROP);
			break;
			
		case R.id.adrBtn:
			Tool.initToast(getApplicationContext(), "敬请期待");
			break;
			
		case R.id.expBtn:
			if (expView.getVisibility() == View.GONE) {
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				expView.setVisibility(View.VISIBLE);
				moreLayout.setVisibility(View.GONE);
			} else {
				expView.setVisibility(View.GONE);
			}
			//Determine the voice
			if(msgText.getVisibility() == View.GONE){
				msgText.setVisibility(View.VISIBLE);
				recordBtn.setVisibility(View.GONE);
				if("".equals(msgText.getText().toString()))
					sendBtn.setImageResource(R.drawable.icon_voice);
				else
					sendBtn.setImageResource(R.drawable.icon_send_w);
			}
			break;
		
			
		default:
			break;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
			case PicSrcPickerActivity.CROP:
				String imgName = data.getStringExtra("imgName");
				String base64String = data.getStringExtra("base64String");
				if (imgName != null) {
					XmppConnection.getInstance().sendMsgWithParms(imgName, new String[]{"imgData"}, new Object[]{base64String});
				}
				break;
				
			default:
				break;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		try {
			if (MyApplication.getInstance() != null) {
				unregisterReceiver(mUpMessageReceiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		clearMsgCount();
		finish();
		super.onDestroy();
	}
	
	private void clearMsgCount() {
		NewMsgDbHelper.getInstance(getApplicationContext()).delNewMsg(chatName);
		MyApplication.getInstance().sendBroadcast(new Intent("ChatNewMsg"));
	}
	
	private class UpMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 收到廣播更新我们的界面
			if (intent.getAction().equals("LeaveRoom")) {
				finish();
			}
			else{
				initData();
			}
		}
	}
}
