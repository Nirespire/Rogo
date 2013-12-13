package com.rogoapp;

public class Friend { 
	// Properties are private 
	private String username; 
	private String firstName; 
	
	// Constructors
	public Friend() { 
		username = "default";
		firstName = "default"; 
	} 
	public Friend(String username, String firstName) { 
		this.username = username;
		this.firstName = firstName; 
	} 
	
	// Construct friend object from User object
	public Friend(User person) {  
		firstName = person.getFirstName(); 
		username = person.getUsername(); 
	} 
	
	// Get/Set methods
	public String getUsername() { 
		return username; 
	} 
	public String getFirstName() { 
		return firstName; 
	} 
	public String toString() {
		return username + "/n" + firstName;
	}
	public void setFirstName(String firstName) { 
		this.firstName = firstName; 
	} 
}