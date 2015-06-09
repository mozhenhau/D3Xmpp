package com.d3.d3xmpp.constant;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApplication extends Application implements UncaughtExceptionHandler{
	private static MyApplication instance;
	public static SharedPreferences sharedPreferences;

	public static MyApplication getInstance() {
		return instance;
	}
	

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
//		ProviderManager.getInstance().addIQProvider("muc", "MZH", new MucProvider()); 
		//全局未知异常捕获
//		Thread.setDefaultUncaughtExceptionHandler(this);

		sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		// 聊天记录
//		AllFriendsMessageMapData = new ConcurrentHashMap<String, List<ChatItem>>();
//		registerDateTransReceiver();1
//        startService(new Intent(TogetherService.ACTION));  

		ImgConfig.initImageLoader();
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
