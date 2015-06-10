/**
 * 
 */
package com.d3.d3xmpp.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.activites.ChatActivity;
import com.d3.d3xmpp.adapter.MsgAdapter;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.d3View.D3Fragment;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.dao.MsgDbHelper;
import com.d3.d3xmpp.dao.NewMsgDbHelper;
import com.d3.d3xmpp.model.ChatItem;

/**
 * @author MZH
 *
 */
public class MsgFragment extends D3Fragment{
	@D3View TextView emptyView;
	@D3View EditText searchText;
	@D3View ImageView searchBtn,searchImg;
	@D3View ListView listView;
	public List<ChatItem> lastMsgs = new ArrayList<ChatItem>();
	private MsgDbHelper msgDbHelper;
	private MsgAdapter adapter;
	private NewMsgReceiver newMsgReceiver;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = setContentView(inflater, R.layout.acti_msg);
		initView();
		initData();
		return view;
	}
	
	public void initView() {
		msgDbHelper = MsgDbHelper.getInstance(getActivity());
		adapter = new MsgAdapter(getActivity());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ChatItem chatItem = lastMsgs.get(position);
				Intent intent = new Intent();
				intent.setClass(getActivity(), ChatActivity.class);
				intent.putExtra("chatName", chatItem.chatName);
				intent.putExtra("chatType", chatItem.chatType);
				getActivity().startActivity(intent);
			}
			
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				new AlertDialog.Builder(getActivity()) 
			 	.setTitle("提示")
			 	.setMessage("确认删除信息？删除后不可恢复？")
			 	.setPositiveButton("是", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String username = adapter.getItem(position).chatName;
						NewMsgDbHelper.getInstance(getActivity()).delNewMsg(username);
						MyApplication.getInstance().sendBroadcast(new Intent("ChatNewMsg"));
						MsgDbHelper.getInstance(getActivity()).delChatMsg(username);
						adapter.notifyDataSetChanged();
					}
				})
			 	.setNegativeButton("否", null)
			 	.show();
				return true;
			}
		});
		
		
		// 接收到新消息的事件监听
		newMsgReceiver = new NewMsgReceiver();
		getActivity().registerReceiver(newMsgReceiver,new IntentFilter("ChatNewMsg"));
		searchText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().equals("")) {
					initData();
				}
				else {
					lastMsgs = msgDbHelper.getLastMsg(s.toString());
					adapter.clear();
					adapter.addAll(lastMsgs);
				}
			}
		});
		
	}
	
	
	public void initData() {
		lastMsgs = msgDbHelper.getLastMsg();
		adapter.clear();
		adapter.addAll(lastMsgs);
		if (adapter.getCount()==0) {
			listView.setVisibility(View.GONE);
		}
		else {
			listView.setVisibility(View.VISIBLE);
		}
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchText:
			
			break;

		default:
			break;
		}
	}
	
	private class NewMsgReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 更新界面
			initData();
		}
	}
	
	@Override
	public void onDestroy() {
		try {
			if (newMsgReceiver != null)
				getActivity().unregisterReceiver(newMsgReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	@Override
	public void onResume() {
		searchText.clearFocus();
		super.onResume();
	}
}
