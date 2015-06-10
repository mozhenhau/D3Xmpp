/**
 * 
 */
package com.d3.d3xmpp.activites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.dao.MsgDbHelper;
import com.d3.d3xmpp.dao.NewMsgDbHelper;
import com.d3.d3xmpp.fragments.AdrFragment;
import com.d3.d3xmpp.fragments.ContactFragment;
import com.d3.d3xmpp.fragments.MeFragment;
import com.d3.d3xmpp.fragments.MsgFragment;
import com.d3.d3xmpp.model.Room;
import com.d3.d3xmpp.model.User;
import com.d3.d3xmpp.util.LoadThread;
import com.d3.d3xmpp.util.MyAndroidUtil;
import com.d3.d3xmpp.util.Tool;
import com.d3.d3xmpp.xmpp.XmppConnection;

/**
 * @author MZH
 *
 */
@SuppressLint("ResourceAsColor")
public class MainActivity extends BaseActivity {
	@D3View(click="onClick") Button chatBtn,contactBtn,adrBtn,meBtn;
	@D3View(click="onClick") ImageView leftBtn,rightBtn;
	@D3View FrameLayout page1,page2,page3,page4;
	@D3View TextView countView,countView1,titleView;
	private MsgFragment msgFragment;
	private ContactFragment contactFragment;
	private AdrFragment adrFragment;
	private MeFragment meFragment;
	private List<FrameLayout> pages = new ArrayList<FrameLayout>();
	private long exitTime = 0; // 两次按返回键退出用
	private NewMsgReceiver newMsgReceiver;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_main);
		pages.add(page1);
		pages.add(page2);
		pages.add(page3);
		pages.add(page4);
		switchFragment(1, chatBtn);
		// 接收到新消息的事件监听
		updateCount();
		updateCount1();
		newMsgReceiver = new NewMsgReceiver();
		registerReceiver(newMsgReceiver,new IntentFilter("ChatNewMsg"));
		registerReceiver(newMsgReceiver,new IntentFilter("FriendNewMsg"));
		registerReceiver(newMsgReceiver,new IntentFilter("conflict"));
		//用户详情
		Constants.loginUser = new User(XmppConnection.getInstance().getUserInfo(null));
//		XmppConnection.getInstance().loadFriendAndJoinRoom();
	}
	
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.leftBtn:
			if (page3.getVisibility() == View.VISIBLE && adrFragment!=null) {
				Tool.initToast(getApplicationContext(), "刷新中...");
				adrFragment.initFriendAdr();
			}
			else if (page1.getVisibility() == View.VISIBLE && msgFragment != null) {
				Tool.initToast(getApplicationContext(), "刷新中...");
				XmppConnection.getInstance().reconnect();
			}
			break;
			
		case R.id.rightBtn:
			if (page1.getVisibility() == View.VISIBLE) {
				startActivity(new Intent(getApplicationContext(), ChoseActivity.class));
			}
			else if (page2.getVisibility() == View.VISIBLE) {
				startActivity(new Intent(getApplicationContext(), SearchActivity.class));
			}
			else if (page3.getVisibility() == View.VISIBLE && adrFragment != null) {
				if (adrFragment.searchView.getVisibility() == View.GONE) {
					adrFragment.searchView.setVisibility(View.VISIBLE);
				}
				else {
					adrFragment.searchView.setVisibility(View.GONE);
				}
			}
			break;
		
		case R.id.chatBtn:
			switchFragment(1, v);
			break;
			
		case R.id.contactBtn:
			switchFragment(2, v);
			break;

		case R.id.adrBtn:
			switchFragment(3, v);
			break;
		case R.id.meBtn:
			switchFragment(4, v);
			break;
			
		default:
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (System.currentTimeMillis() - exitTime > 2000) {
				Tool.initToast(this, "再按一次退出程序");
				exitTime = System.currentTimeMillis();
			} else {
				XmppConnection.getInstance().closeConnection();
				finish();
				System.exit(0);
			}
		}
		else if (KeyEvent.KEYCODE_VOLUME_DOWN == keyCode) {
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
		} else if (KeyEvent.KEYCODE_VOLUME_UP == keyCode) {
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
		}
		return false;
	}
	
	
	/*
	 * Tab的逻辑处理
	 * v是点击的
	 */
	public void switchFragment(int tabIndex,View v) { 
		page1.setVisibility(View.GONE);
		page2.setVisibility(View.GONE);
		page3.setVisibility(View.GONE);
		page4.setVisibility(View.GONE);
		chatBtn.setBackgroundColor(this.getResources().getColor(R.color.transparent_background));
		contactBtn.setBackgroundColor(this.getResources().getColor(R.color.transparent_background));
		adrBtn.setBackgroundColor(this.getResources().getColor(R.color.transparent_background));
		meBtn.setBackgroundColor(this.getResources().getColor(R.color.transparent_background));
		leftBtn.setVisibility(View.GONE);
		rightBtn.setVisibility(View.GONE);
		rightBtn.setImageResource(R.drawable.add);

		switch (tabIndex) {
		case 1:
			if (msgFragment == null) {
				msgFragment = new MsgFragment();
				getSupportFragmentManager().beginTransaction()
						.replace(pages.get(tabIndex-1).getId(), msgFragment).commit();
			}
			page1.setVisibility(View.VISIBLE);
			chatBtn.setBackgroundColor(this.getResources().getColor(R.color.green));
			titleView.setText("信息");
//			leftBtn.setVisibility(View.VISIBLE);
			rightBtn.setVisibility(View.VISIBLE);
			break;
		case 2:
			if (contactFragment == null) {
				contactFragment = new ContactFragment();
				getSupportFragmentManager().beginTransaction()
						.replace(pages.get(tabIndex-1).getId(), contactFragment).commit();
			}
			page2.setVisibility(View.VISIBLE);
			contactBtn.setBackgroundColor(this.getResources().getColor(R.color.green));
			titleView.setText("通讯录");
			rightBtn.setVisibility(View.VISIBLE);
			break;
		case 3:
			if (adrFragment == null) {
				adrFragment = new AdrFragment();
				getSupportFragmentManager().beginTransaction()
						.replace(pages.get(tabIndex-1).getId(), adrFragment).commit();
			}
			else {
				adrFragment.initFriendAdr();
				if (!MyApplication.sharedPreferences.getBoolean("isShare", true)) {
					adrFragment.mLocClient.stop();
				}
				else if (!adrFragment.mLocClient.isStarted()) {
					adrFragment.mLocClient.start();
				}
			}
			leftBtn.setVisibility(View.VISIBLE);
			rightBtn.setVisibility(View.VISIBLE);
			rightBtn.setImageResource(R.drawable.search_b);
			page3.setVisibility(View.VISIBLE);
			adrBtn.setBackgroundColor(this.getResources().getColor(R.color.green));
			titleView.setText("位置");
			break;
		case 4:
			if (meFragment == null) {
				meFragment = new MeFragment();
				getSupportFragmentManager().beginTransaction()
						.replace(pages.get(tabIndex-1).getId(), meFragment).commit();
			}
			page4.setVisibility(View.VISIBLE);
			meBtn.setBackgroundColor(this.getResources().getColor(R.color.green));
			titleView.setText("个人");
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
			case PicSrcPickerActivity.CROP:
				if (page4.getVisibility() == View.VISIBLE) {
//					String imgName = data.getStringExtra("imgName");
					meFragment.changeHead(data.getStringExtra("imgPath"));
				}
				break;
				
			default:
				break;
			}
		}
	}
	
	@Override
	protected void onResume() {
		MyAndroidUtil.clearNoti();
		super.onResume();
	}
	
	private class NewMsgReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("ChatNewMsg")) {
				updateCount();
			}
			else if (intent.getAction().equals("conflict")) {
				if (adrFragment!=null && adrFragment.mLocClient != null) {
					adrFragment.mLocClient.stop();
					adrFragment.timer1.cancel();
					adrFragment.timer2.cancel();
				}
			}
			else {
				updateCount1();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		if (adrFragment!=null && adrFragment.mLocClient != null) {
			adrFragment.mLocClient.stop();
		}
		super.onDestroy();
	}
	
	public void updateCount() {
		// 更新界面
		int count = NewMsgDbHelper.getInstance(getApplicationContext()).getMsgCount();
		if (count>0) {
			countView.setVisibility(View.VISIBLE);
			countView.setText(""+count);
		}
		else {
			countView.setVisibility(View.GONE);
		}
	}
	
	public void updateCount1() {
		// 更新界面
		int count = NewMsgDbHelper.getInstance(getApplicationContext()).getMsgCount(""+0);
		if (count>0) {
			countView1.setVisibility(View.VISIBLE);
			countView1.setText(""+count);
		}
		else {
			countView1.setVisibility(View.GONE);
		}
	}
	
}
