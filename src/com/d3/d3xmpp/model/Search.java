package com.d3.d3xmpp.model;


public class Search {
	Search items;
	String email;
	String username;
	String message;
	String state;


	public Search getItems() {
		return items;
	}

	public void setItems(Search items) {
		this.items = items;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}



	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
