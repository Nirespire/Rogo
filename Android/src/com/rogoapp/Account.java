/*
 * The Account class is sublcass of User.  It contains information relative to the User's account, online status,
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

// Do we really want to extend User?  I think it would make more sense to link the two, like give User an
// Account property.   - Dax

public class Account extends User {
	private int visibility; //numbers correspond to visibility status
	private double contactRadius; // units?
	private String username;
	
	public Account(){
		visibility = 0;
		contactRadius = 0.0;
		username = "default";
	}
	
	// this is going to use the default User constructor, is that really what we want?
	public Account(int visibility, double contactRadius, String username){
		this.visibility = visibility;
		this.contactRadius = contactRadius;
		this.username = username;
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
	
	public String getUsername(){
		return username;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	
	
}
