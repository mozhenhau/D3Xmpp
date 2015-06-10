/**
 * 
 */
package com.d3.d3xmpp.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.activites.ChatActivity;
import com.d3.d3xmpp.activites.MyRoomActivity;
import com.d3.d3xmpp.activites.NewFriendActivity;
import com.d3.d3xmpp.adapter.ContactsAdapter;
import com.d3.d3xmpp.d3View.D3Fragment;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.d3View.MyListView;
import com.d3.d3xmpp.d3View.MyListView.OnRefreshListener;
import com.d3.d3xmpp.model.Friend;
import com.d3.d3xmpp.util.MySideBar;
import com.d3.d3xmpp.util.MySideBar.OnTouchingLetterChangedListener;
import com.d3.d3xmpp.util.PinyinUtils;
import com.d3.d3xmpp.util.XmppLoadThread;
import com.d3.d3xmpp.xmpp.XmppConnection;

/**
 * @author MZH
 *
 */
public class ContactFragment extends D3Fragment implements OnTouchingLetterChangedListener{
	@D3View public EditText searchText;
	@D3View TextView newCountView;
	@D3View MyListView listView;
	@D3View MySideBar sideBar;
	@D3View Button groupBtn,addBtn;
	private ContactsAdapter adapter;
	private FriendReceiver reciver;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = setContentView(inflater, R.layout.acti_contacts);
		initView();
		// 接收到新消息的事件监听
		reciver = new FriendReceiver();
		getActivity().registerReceiver(reciver,new IntentFilter("friendChange"));
		getActivity().registerReceiver(reciver,new IntentFilter("FriendNewMsg"));
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
	}
	
	@Override
	public void onResume() {
		searchText.clearFocus();
		super.onResume();
	}
	
	public void initData() {
		adapter.clear();
		adapter.add(new Friend("新的朋友"));
		adapter.add(new Friend("群聊"));
		adapter.addAll(XmppConnection.getInstance().getFriendBothList());
	}
	
	
	public void initView() {
		sideBar.setOnTouchingLetterChangedListener(this);
		listView.setTextFilterEnabled(true);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent = new Intent();
				if (position == 1) {
					intent.setClass(getActivity(), NewFriendActivity.class);
				}
				else if (position == 2) {
					intent.setClass(getActivity(), MyRoomActivity.class);
				}
				else {
					intent.setClass(getActivity(), ChatActivity.class);
					intent.putExtra("chatName", XmppConnection.getInstance().getFriendBothList().get(position-3).username);
				}
				getActivity().startActivity(intent);
			}
		});
		listView.setonRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				friendChange();
			}
		});
		
		
		
		adapter = new ContactsAdapter(getActivity());
		listView.setAdapter(adapter);
		adapter.add(new Friend("新的朋友"));
		adapter.add(new Friend("群聊"));
		
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
				adapter.clear();
				adapter.add(new Friend("新的朋友"));
				adapter.add(new Friend("群聊"));
				if (s.toString().equals("")) {
					adapter.addAll(XmppConnection.getInstance().getFriendList());
				}
				else {
					List<Friend>  friendTemps = new ArrayList<Friend>();
					for (Friend friend : XmppConnection.getInstance().getFriendList()) {
						if (friend.username.contains(s.toString())) {
							friendTemps.add(friend);
						}
					}
					adapter.addAll(friendTemps);
				}
			}
		});
	}
	
	public void friendChange() {
		new XmppLoadThread(getActivity()) {
			@Override
			protected void result(Object object) {
				initData();
				listView.onRefreshComplete();
			}
			
			@Override
			protected Object load() {
				return XmppConnection.getInstance().getFriendBothList();
			}
		};
	}
	
	
	public void onClick(View v) {
	}
	
	public int alphaIndexer(String s) {
		int position = 0;
		String alpha;
		for (int i = 0; i < XmppConnection.getInstance().getFriendBothList().size(); i++) {
			alpha = PinyinUtils.getPingYin(XmppConnection.getInstance().getFriendBothList().get(i).username).toUpperCase();
			if (alpha.startsWith(s)) {
				position = i;
				break;
			}
		}
		return position;
	}

	@Override
	public void onTouchingLetterChanged(String s) {
		if (alphaIndexer(s) > 0) {
			int position = alphaIndexer(s);
			listView.setSelection(position);
		}
	}
	
	private class FriendReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("FriendNewMsg")) {
				adapter.notifyDataSetChanged();
			}
			else {
				// 更新界面
				friendChange();
			}
		}
	}
}
