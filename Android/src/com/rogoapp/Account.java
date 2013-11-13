/*
 * The Account class contains information relative to the User's account, online status,
 * and other Account settings.
 * 
 * **Created by Joey Siracusa for Speaksoft**
 * 
 * TODO:
 * -Determine visibility integers
 * -Determine choices of contact radius
 * -Determine username constraints 
 * 
 * 
 */

//test comment Anurag
//station cryptic Dax
package com.rogoapp;

public class Account {
	private int visibility; //numbers correspond to visibility status
	private double contactRadius; // units?
	private User user;
	
	public Account(){
		visibility = 0;
		contactRadius = 0.0;
		user = new User();
	}
	
	// this is going to use the default User constructor, is that really what we want?
	public Account(int visibility, double contactRadius, User user){
		this.visibility = visibility;
		this.contactRadius = contactRadius;
		this.user = user;
	}
	
	public int getVisibility(){
		return visibility;
	}
	
	public void setVisibility(int visibility){
		this.visibility = visibility;
	}
	
	public double getContactRadius(){
		return contactRadius;
	}
	
	public void setContactRadius(double contactRadius){
		this.contactRadius = contactRadius;
	}
	
	public User getUser(){
		return user;
	}
	
	public void setUser(User user){
		this.user = user;;
	}
	
	
	
}
