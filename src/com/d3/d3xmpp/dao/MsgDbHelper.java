package com.d3.d3xmpp.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.model.ChatItem;

public class MsgDbHelper {
	private static MsgDbHelper instance = null;
	
	private SqlLiteHelper helper;
	private SQLiteDatabase db;  // 我的最新聊天信息
	private final int SHOW_MSG_COUNT = 15;
	private final int MORE_MSG_COUNT = 10 ;	
	
	public MsgDbHelper(Context context) {
		helper = new SqlLiteHelper(context);
		db = helper.getWritableDatabase();
	}

	public void closeDb(){
		db.close();
		helper.close();
	}
	public static MsgDbHelper getInstance(Context context) {
		if (instance == null) {
			instance = new MsgDbHelper(context);
		}
		return instance;
	}
	
	private class SqlLiteHelper extends SQLiteOpenHelper {

		private static final int DB_VERSION = 1;
		private static final String DB_NAME = "chat";

		public SqlLiteHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		/* (non-Javadoc)
		 *
	public String chatName;  //群聊的话跟username不一样
	private String username;  //对方的昵称
	private String head;
	private String msg;
	private String sendDate;
	private int inOrOut; //0代表in 1代表out
	private String whos;
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE  IF NOT EXISTS " + DB_NAME
						+ "( id INTEGER PRIMARY KEY AUTOINCREMENT,chatType INTEGER,chatName text,"+
						"username text , head text ,msg text,sendDate text,inOrOut INTEGER," +
						"whos text,i_filed INTEGER,t_field text)";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			dropTable(db);
			onCreate(db);
		}

		private void dropTable(SQLiteDatabase db) {
			String sql = "DROP TABLE IF EXISTS "+DB_NAME;
			db.execSQL(sql);
		}

	}

	public void saveChatMsg(ChatItem msg){
		ContentValues values = new ContentValues();
		values.put("chatType", msg.chatType);
		values.put("chatName", msg.chatName);
		values.put("username", msg.username);
		values.put("head", msg.head);
		values.put("msg", msg.msg);
		values.put("sendDate",msg.sendDate);
		values.put("inOrOut", msg.inOrOut);
		values.put("whos", Constants.USER_NAME);
		db.insert(helper.DB_NAME, "id", values);
	}

	/**
	 * 取当前会话窗口的聊天记录，限量count
	 * @param friendName
	 */
	public List<ChatItem> getChatMsg(String chatName){
		List<ChatItem> chatItems = new ArrayList<ChatItem>();
		ChatItem msg;
		String sql = "select a.chatType,a.chatName,a.username,a.head,a.msg,a.sendDate,a.inOrOut " +
				" from(select * from "+helper.DB_NAME +
				" where chatName = ? and whos = ? order by id desc LIMIT " +SHOW_MSG_COUNT+")a order by a.id";
		Cursor cursor = db.rawQuery(sql, new String[]{chatName,Constants.USER_NAME});
		while(cursor.moveToNext()){
			msg = new ChatItem(cursor.getInt(0),cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4)
					, cursor.getString(5), cursor.getInt(6));
			chatItems.add(msg);
			msg = null;
		}
		cursor.close();
		return chatItems;
	}
	
	/**
	 * 获取更多好友聊天记录,显示多5条
	 * @param count
	 * @param friendName
	 */
	public List<ChatItem> getChatMsgMore(int startIndex,String chatName){
		List<ChatItem> chatItems = new ArrayList<ChatItem>();
		ChatItem msg;
		String sql ="select a.chatType,a.chatName,a.username,a.head,a.msg,a.sendDate,a.inOrOut " +
				" from(select * from "+helper.DB_NAME +
				" where chatName = ? and whos = ? order by id desc LIMIT " +MORE_MSG_COUNT+" offset "+startIndex+")a order by a.id";
		Cursor cursor = db.rawQuery(sql, new String[]{chatName,Constants.USER_NAME});
		while(cursor.moveToNext()){
			msg = new ChatItem(cursor.getInt(0),cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4)
					, cursor.getString(5), cursor.getInt(6));
			chatItems.add(msg);
			msg = null;
		}
		cursor.close();
		return chatItems;
	}
	
	/**
	 * 取得我的的最新消息，显示在好友表
	 */
	@SuppressWarnings("unused")
	public List<ChatItem> getLastMsg(){  
		List<ChatItem> chatItems = new ArrayList<ChatItem>();
		ChatItem msg;
		String sql ="select chatType,chatName,username,head,msg,sendDate,inOrOut from  "+helper.DB_NAME +
				" where whos = ? "+
				 " GROUP BY chatName "+
					"order by id desc";
		final Cursor cursor = db.rawQuery(sql, new String[]{Constants.USER_NAME});
		while (cursor.moveToNext()) {
			msg = new ChatItem(cursor.getInt(0),cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4)
					, cursor.getString(5), cursor.getInt(6));
			chatItems.add(msg);
			msg = null;
		}
		cursor.close();
		return chatItems;
	}
	
	/**
	 * 取得我的的最新消息，模糊搜索,显示在好友表
	 */
	@SuppressWarnings("unused")
	public List<ChatItem> getLastMsg(String keywords){  
		List<ChatItem> chatItems = new ArrayList<ChatItem>();
		ChatItem msg;
		String sql ="select chatType,chatName,username,head,msg,sendDate,inOrOut from  "+helper.DB_NAME +
			 	" where username like ? and whos = ? "+
				 " GROUP BY chatName "+
					" order by id desc";
		final Cursor cursor = db.rawQuery(sql, new String[]{"%"+keywords+"%",Constants.USER_NAME});
		while (cursor.moveToNext()) {
			msg = new ChatItem(cursor.getInt(0),cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4)
					, cursor.getString(5), cursor.getInt(6));
			chatItems.add(msg);
			msg = null;
		}
		cursor.close();
		return chatItems;
	}
	
	public void delChatMsg(String msgId){
		db.delete(helper.DB_NAME, "chatName=? and whos=?", new String[]{msgId,Constants.USER_NAME}); 
	}

	public void clear(){
		db.delete(helper.DB_NAME, "id>?", new String[]{"0"}); 
	}
}
