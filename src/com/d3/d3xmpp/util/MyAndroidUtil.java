package com.d3.d3xmpp.util;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.activites.MainActivity;
import com.d3.d3xmpp.constant.MyApplication;

public class MyAndroidUtil {

	/**
	 * @param context
	 * @param title
	 * @param message
	 * @param icon
	 * @param okBtn
	 * 没有取消功能的了
	 */
	public static void showDialog(Context context ,String title,String message,int icon,DialogInterface.OnClickListener okBtn){
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setIcon(icon)
		.setMessage(message)
		.setPositiveButton("确定",okBtn)
		.setNegativeButton("返回", null).show();
	}
	
	/**
	 * 修改缓存
	 * @param name     一般都name+   actid、或者userId
	 * @param object        要保存的
	 */
	public static void editXml(String name,Object object) {
		Editor editor = MyApplication.sharedPreferences.edit();
		if (MyApplication.sharedPreferences.getString(name, null) != null) {
			editor.remove(name);
		}
		editor.putString(name, JsonUtil.objectToJson(object));
		editor.commit();
	}
	
	
	/**
	 * 修改缓存
	 * @param name     一般都name+   actid、或者userId
	 * @param result        要保存的
	 */
	public static void editXmlByString(String name,String result) {
		Editor editor = MyApplication.sharedPreferences.edit();
		if (MyApplication.sharedPreferences.getString(name, null) != null) {
			editor.remove(name);
		}
		editor.putString(name, result);
		editor.commit();
	}
	
	/**
	 * 修改缓存
	 * @param name     一般都name+   actid、或者userId
	 * @param true or fasle        要保存的
	 */
	public static void editXml(String name,boolean is) {
		Editor editor = MyApplication.sharedPreferences.edit();
		editor.putBoolean(name, is);
		editor.commit();
	}

	
	public static void removeXml(String name){
		Editor editor = MyApplication.sharedPreferences.edit();
		editor.remove(name);
		editor.commit();
	}
	
	public static void showNoti(String notiMsg){
		//android推送
		Notification myNoti = new Notification();
		myNoti.tickerText = notiMsg;
		Intent intent = new Intent();   //要跳去的界面
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(MyApplication.getInstance(), MainActivity.class);
		
		NotificationManager mNotificationManager = 
	    		(NotificationManager) MyApplication.getInstance().getSystemService(Service.NOTIFICATION_SERVICE);
		PendingIntent appIntent = PendingIntent.getActivity(MyApplication.getInstance(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		myNoti.icon = R.drawable.ic_launcher;
		myNoti.flags = Notification.FLAG_SHOW_LIGHTS|Notification.FLAG_AUTO_CANCEL;  //闪光灯
		myNoti.ledARGB= 0xff00ff00;           //绿色
		
//		myNoti.defaults = Notification.DEFAULT_SOUND; // 响铃
		myNoti.setLatestEventInfo(MyApplication.getInstance(), MyApplication.getInstance().getString(R.string.app_name), myNoti.tickerText, appIntent);
		mNotificationManager.notify(0, myNoti);
	}
}
