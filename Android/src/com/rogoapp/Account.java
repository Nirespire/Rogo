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


package com.rogoapp;



public class Account extends User {
	private int visibility; //numbers correspond to visibility status
	private double contactRadius;
	private String username;
	
	public Account(){
		visibility = 0;
		contactRadius = 0.0;
		username = "default";
	}
	
	public Account(int visibility, double contactRadius, String username){
		this.visibility = visibility;
		this.contactRadius = contactRadius;
		this.username = username;
	}
	
	public int getVisiblity(){
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
