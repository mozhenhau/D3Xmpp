package com.d3.d3xmpp.constant;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Timer;
import java.util.TimerTask;

import com.d3.d3xmpp.xmpp.XmppConnection;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApplication extends Application implements UncaughtExceptionHandler{
	private static MyApplication instance;
	public static SharedPreferences sharedPreferences;
	public static double lat = 23.117055306224895;
	public static double lon = 113.2759952545166;

	public static MyApplication getInstance() {
		return instance;
	}
	

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		//全局未知异常捕获
//		Thread.setDefaultUncaughtExceptionHandler(this);

		sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		ImgConfig.initImageLoader();
		
		new Timer().schedule(new TimerTask() {  //1秒后开始，5分钟上传一次自己的位置
			@Override
			public void run() {
				if (MyApplication.sharedPreferences.getBoolean("isShare", true)) {
					uploadAdr();
				}
			}
		}, 1000,Constants.UPDATE_TIME);
	}
	
	public void uploadAdr(){
		if (Constants.loginUser != null && (lat != 23.117055306224895 || lon != 113.2759952545166)) { // 
			Constants.loginUser.vCard.setField("latAndlon", lat+","+lon);
			XmppConnection.getInstance().changeVcard(Constants.loginUser.vCard);
		}
	}
	

	public void clearAdr(){
		if (Constants.loginUser != null) { 
			Constants.loginUser.vCard.setField("latAndlon", 4.9E-324+","+4.9E-324);
			XmppConnection.getInstance().changeVcard(Constants.loginUser.vCard);
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}
}
