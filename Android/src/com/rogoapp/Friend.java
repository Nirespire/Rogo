package com.rogoapp;

public class Friend {
	
	// Properties are private
	private String username;
	private String firstName;
	
	public Friend() {
		username = "default";
		firstName = "default";
	}
	
	public Friend(String username, String firstName) {
		this.username = username;
		this.firstName = firstName;
	}

	public Friend(User person) { //Construct friend object from User object
		firstName = person.getFirstName();
		username = person.getUsername();
	}
	
	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

}
