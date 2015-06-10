/**
 * 
 */
package com.d3.d3xmpp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MZH
 *
 */
public class Room implements Serializable{
	public String name;
	public String roomid;
	public List<String> friendList = new ArrayList<String>();
	public Room() {
		super();
	}

	
	public Room(String name){
		this.name = name;
	}
	
	@Override
	 public boolean equals(Object obj) {
	  boolean isEqual = false;
	  if (obj instanceof Room) {
		  Room t = (Room) obj;
		  isEqual = this.name.equals(t.name);
		  return isEqual;
	  }
	  return super.equals(obj);
	 }
}
