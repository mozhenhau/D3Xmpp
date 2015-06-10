package com.d3.d3xmpp.xmpp;

import org.jivesoftware.smack.ConnectionListener;

import android.content.Intent;
import android.util.Log;

import com.d3.d3xmpp.activites.LoginActivity;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.util.MyAndroidUtil;


public class XmppConnecionListener implements ConnectionListener {

	@Override
	public void connectionClosed() {
		Log.e("smack xmpp", "close");
		XmppConnection.getInstance().setNull();
	}

	@Override
	public void connectionClosedOnError(Exception e) {
//		Log.e("smack xmpp", e.getMessage());
		if(e.getMessage().contains("conflict")){
			MyAndroidUtil.removeXml(Constants.LOGIN_PWD);
			if (!MyApplication.sharedPreferences.getBoolean(Constants.LOGIN_CHECK, false)) {
				MyAndroidUtil.removeXml(Constants.LOGIN_ACCOUNT);
			}
			Constants.USER_NAME = "";
			Constants.loginUser = null;
			XmppConnection.getInstance().closeConnection();
			MyApplication.getInstance().sendBroadcast(new Intent("conflict"));
			//Ìø×ª
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("isRelogin", true);
			intent.setClass(MyApplication.getInstance(), LoginActivity.class);
			MyApplication.getInstance().startActivity(intent);
		}
	}

	@Override
	public void reconnectingIn(int seconds) {
//		Log.e("smack xmpp", "reconing:"+seconds);
	}

	@Override
	public void reconnectionSuccessful() {
		XmppConnection.getInstance().loadFriendAndJoinRoom();
//		Log.e("smack xmpp", "suc");
	}

	@Override
	public void reconnectionFailed(Exception e) {
//		Log.e("smack xmpp", "re fail");
	}

	

}
