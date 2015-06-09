package com.d3.d3xmpp.constant;

import com.d3.d3xmpp.model.User;
import com.d3.d3xmpp.util.Util;

public class Constants {
	public static boolean IS_DEBUG = true;
//	public final static String SERVER_HOST = "112.74.76.178";
	public final static String SERVER_HOST = "112.74.82.228";
//	public final static String SERVER_HOST = "192.168.2.117";
	public final static int SERVER_PORT = 5222;
	public static final String PATH =  Util.getInstance().getExtPath()+"/.d3";
	public static final String SAVE_IMG_PATH = PATH + "/images";//设置保存图片文件的路径
	public static final String SAVE_SOUND_PATH = PATH + "/sounds";//设置声音文件的路径
	public static final String IMG_PATH =  Util.getInstance().getExtPath()+"/d3/images";

	
	public final static String SHARED_PREFERENCES = "openfile";
	public static String USER_NAME = "";
	public static String PWD = "";
	public static User loginUser;
	
	public static String GROUP_NAME = "name";
	public static String GROUP_JJD = "jjd";

	public final static String LOGIN_CHECK = "check";
	public final static String LOGIN_ACCOUNT = "account";
	public final static String LOGIN_PWD = "pwd";
	public final static String LOGIN_IMAGE="image";
	public static String POST = "post";
	public static String RESULT_STATE = "0";
}
