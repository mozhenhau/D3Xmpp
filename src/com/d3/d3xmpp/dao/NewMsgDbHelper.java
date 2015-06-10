package com.d3.d3xmpp.dao;

import com.d3.d3xmpp.constant.Constants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NewMsgDbHelper {             //新消息条数
	private static NewMsgDbHelper instance = null;
	
	private SqlLiteHelper helper;
	private SQLiteDatabase db;

	public NewMsgDbHelper(Context context) {
		helper = new SqlLiteHelper(context);
		db = helper.getWritableDatabase();
	}

	public void closeDb(){
		db.close();
		helper.close();
	}
	public static NewMsgDbHelper getInstance(Context context) {
		if (instance == null) {
			instance = new NewMsgDbHelper(context);
		}
		return instance;
	}
	
	private class SqlLiteHelper extends SQLiteOpenHelper {
		private static final int DB_VERSION = 1;
		private static final String DB_NAME = "newMsg";

		public SqlLiteHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {   //MsgId对聊天来说即对方id
			String sql = "CREATE TABLE  IF NOT EXISTS newMsg"
						+ "( id INTEGER PRIMARY KEY AUTOINCREMENT,msgId text,msgCount INTEGER, whosMsg text," +
						"i_field1 INTEGER, t_field1 text)";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			dropTable(db);
			onCreate(db);
		}

		private void dropTable(SQLiteDatabase db) {
			String sql = "DROP TABLE IF EXISTS newMsg";
			db.execSQL(sql);
		}

	}
	
	/**
	 * @param msgId  聊天的话代表userid
	 * 0代表好友添加
	 */
	public void saveNewMsg(String msgId){    //如果存在，则+1，不存在则插入
		int nowCount = getMsgCount(msgId);
		ContentValues values = new ContentValues();
		if(nowCount==0){
			values.put("msgId", msgId);
			values.put("msgCount", 1);
			values.put("whosMsg", Constants.USER_NAME);
			db.insert(helper.DB_NAME, null, values);   //第二，第三个参数其中一个不能为null
		}else{
			values.put("msgCount",nowCount+1 );
			db.update(helper.DB_NAME, values, " msgId=? and whosMsg=?", 
					new String[]{msgId,Constants.USER_NAME});
		}
	}
	
	/**
	 * @param msgId
	 */
	public void delNewMsg(String msgId){
		db.delete(helper.DB_NAME, " msgId=? and whosMsg=?", new String[]{msgId,Constants.USER_NAME}); 
	}

	//某个人
	public int getMsgCount(String msgId){   
		int count = 0 ;
		String sql ="select msgCount from newMsg where msgId=? and whosMsg=?";
		Cursor cursor = db.rawQuery(sql, new String[]{msgId,Constants.USER_NAME});
		while(cursor.moveToNext()){
			count = cursor.getInt(0);
		}
		cursor.close();
		return count;
	}
	
	//所有
	public int getMsgCount(){   
		int count = 0 ;
		String sql ="select sum(msgCount) from newMsg where whosMsg=? and msgId != 0";
		Cursor cursor = db.rawQuery(sql, new String[]{Constants.USER_NAME});
		while(cursor.moveToNext()){
			count = cursor.getInt(0);
		}
		cursor.close();
		return count;
	}

	public void clear(){
		db.delete(helper.DB_NAME, "id>?", new String[]{"0"}); 
	}
}