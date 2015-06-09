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

}
