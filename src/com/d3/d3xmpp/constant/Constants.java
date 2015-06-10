package com.d3.d3xmpp.constant;

import com.d3.d3xmpp.model.User;
import com.d3.d3xmpp.util.Util;

public class Constants {
	//常量
	public final static boolean IS_DEBUG = true;
	public final static String SERVER_HOST = "112.74.82.228";
	public final static String SERVER_URL = "http://"+SERVER_HOST+":9090/plugins/xmppservice/";
	public static String SERVER_NAME = "d3";
	public final static int SERVER_PORT = 5222;
	public final static String PATH =  Util.getInstance().getExtPath()+"/xmpp";
	public final static String SAVE_IMG_PATH = PATH + "/images";//设置保存图片文件的路径
	public final static String SAVE_SOUND_PATH = PATH + "/sounds";//设置声音文件的路径
	public final static int UPDATE_TIME =  60*1000;   //好友位置刷新时间，同时也是自己的位置上传时间间隔
	public final static int ADR_UPDATE_TIME =  30*1000;   //刷新自己的位置
	public final static String SHARED_PREFERENCES = "openfile";
	public final static String LOGIN_CHECK = "check";
	public final static String LOGIN_ACCOUNT = "account";
	public final static String LOGIN_PWD = "pwd";	
	//URL
	public final static String URL_EXIT_ROOM = SERVER_URL+"exitroom";
	public final static String URL_EXIST_ROOM = SERVER_URL+"existroom";
	public final static String URL_GET_ADR = SERVER_URL+"getadr";
	
	//变量
	public static String USER_NAME = "";
	public static String PWD = "";
	public static User loginUser;
}
