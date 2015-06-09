package com.d3.d3xmpp.model;

import org.jivesoftware.smack.packet.RosterPacket.ItemStatus;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;

import android.graphics.Bitmap;

public class Friend {
	public String id;
	public String username;
	public String userHead;
	public ItemStatus status; // 为null则不在线
	public ItemType type;
	public String mood;
	public boolean isChose;
	
	
	public Friend(String username) {
		super();
		this.username = username;
	}


	public Friend(String username,ItemType type) {
		super();
		this.username = username;
		this.type = type;
	}

	public Friend(String id, String username, ItemStatus status, String mood) {
		super();
		this.id = id;
		this.username = username;
		this.status = status;
		this.mood = mood;
	}
	
	@Override
	 public boolean equals(Object obj) {
	  boolean isEqual = false;
	  if (obj instanceof Friend) {
		  Friend t = (Friend) obj;
		  isEqual = this.username.equals(t.username);
		  return isEqual;
	  }
	  return super.equals(obj);
	 }
}
