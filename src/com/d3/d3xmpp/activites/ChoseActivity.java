/**
 * 
 */
package com.d3.d3xmpp.activites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smackx.muc.MultiUserChat;
import org.json.JSONObject;

import android.R.integer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.d3.d3xmpp.adapter.ChoseAdapter;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.model.Friend;
import com.d3.d3xmpp.model.Room;
import com.d3.d3xmpp.util.LoadThread;
import com.d3.d3xmpp.util.Tool;
import com.d3.d3xmpp.xmpp.XmppConnection;
import com.d3.d3xmpp.R;

/**
 * @author MZH
 *
 */
public class ChoseActivity extends BaseActivity {
	@D3View(click="onClick") TextView leftBtn,rightBtn,subBtn,cancelBtn;
	@D3View(click="onClick") Button selectAllBtn;
	@D3View LinearLayout nameLayout;
	@D3View EditText nameText;
	@D3View ListView listView;
	@D3View EditText searchText;
	private ChoseAdapter adapter;
	private List<Friend> friends = new ArrayList<Friend>();
	private String roomName; 
	private int inviteCount = 0; //邀请人数
	private boolean isChoseAll = false;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_chose);
		roomName = getIntent().getStringExtra("roomName");
		if (roomName!=null) {
			nameText.setText(roomName);
		}
		adapter = new ChoseAdapter(getApplicationContext());
		listView.setAdapter(adapter);
		friends = XmppConnection.getInstance().getFriends();
		adapter.addAll(friends);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Friend item = (Friend) adapter.getItem(position);
				item.isChose = !item.isChose;
				adapter.notifyDataSetChanged();
			}
		});
		
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
				if (s.toString().equals("")) {
					adapter.addAll(friends);
				}
				else {
					List<Friend>  friendTemps = new ArrayList<Friend>();
					for (Friend friend : friends) {
						if (friend.username.contains(s.toString())) {
							friendTemps.add(friend);
						}
					}
					adapter.addAll(friendTemps);
				}
			}
		});
	}
	
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
			
		case R.id.rightBtn:
			if (roomName == null) {
				nameLayout.setVisibility(View.VISIBLE);
			}
			else {
				invite();
			}
			
			break;
			
		case R.id.subBtn:
			if (roomName == null) {
				final String name = nameText.getText().toString();
				for (Friend friend : friends) {
					if (friend.isChose) {
						inviteCount++;
					}
				}
				if (inviteCount > 0) {
					MultiUserChat muc = XmppConnection.getInstance().createRoom(name);
//					muc.invite(XmppConnection.getFullUsername(Constants.USER_NAME), name);
//					XmppConnection.getInstance().joinMultiUserChat(Constants.USER_NAME,name, null);
					//创建者，需要自己修改
					Room room = new Room();
					room.name = name;
					room.roomid = XmppConnection.getFullRoomname(name);
					for (Friend friend : friends) {
						if (friend.isChose) {
							room.friendList.add(friend.username);
						}
					}
					XmppConnection.getInstance().getMyRoom().add(room);
				}
				else {
					Tool.initToast(getApplicationContext(), "一个人怎么聊啊亲!");
				}
//				XmppConnection.getInstance().joinMultiUserChat(Constants.USER_NAME,name, null);
			}

			if (inviteCount > 0) {
				invite();
			}
			break;
			
		case R.id.cancelBtn:
			nameLayout.setVisibility(View.GONE);
			break;

		case R.id.selectAllBtn:
			for (int i = 0; i < adapter.getCount(); i++) {
				adapter.getItem(i).isChose = !isChoseAll;
			}
			isChoseAll = !isChoseAll;
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
		
	}
	
	
	public void invite(){
		String name = nameText.getText().toString();
		MultiUserChat muc = new MultiUserChat(XmppConnection.getInstance().getConnection(), XmppConnection.getFullRoomname(name));
//		final StringBuilder userString = new StringBuilder(Constants.USER_NAME);
		for (Friend friend : friends) {
			if (friend.isChose) {
				muc.invite(XmppConnection.getFullUsername(friend.username), name);
//				userString.append(","+friend.username);
			}
		}
		nameLayout.setVisibility(View.GONE);
		finish();
	}
	
	public boolean isRoomName(String text){
		Pattern p = Pattern.compile("^[a-zA-Z0-9\u4E00-\u9FA5]+$");    
        Matcher m = p.matcher(text);  
        if(m.find()){  
        	return true;
        } 
		return false;
	}
}
