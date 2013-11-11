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

	private String firstName;
	private String lastName;
	
	public Friend(User person) { //Construct friend object from User object
		firstName = person.getFirstName();
		lastName = person.getLastName();
	}
	
}
