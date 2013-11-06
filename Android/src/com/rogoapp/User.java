/*
 * The User class is initialized with every launch of the app.
 * It contains important user information such as name, birthday, email, friends, etc
 * Users are created in the RogoMainActivity class with information pulled from our server (tenative)
 * 
 *  **Created by Joey Siracusa for Speaksoft**
 *  
 *  TODO:
 *  
 *  -Discuss creation of Friend class with group 
 *  -Discuss how User will be implemented in RogoMainActivity 
 *  
 */

package com.rogoapp;

import java.util.Date;
import java.util.ArrayList;


public class User {
	//--------------------------------------------------------------------------------------
	//PROPERTIES: All class properties are set to private
	
	private String firstName;
	private String lastName;
	private Date birthday;
	private String email;
	private ArrayList<String> interests;
	private int points;
	private ArrayList<Friend> friends; //Friend class yet to be created.
	
	//--------------------------------------------------------------------------------------
	//INITIALIZATION: Initialization of User object
	
	public User(){
		firstName = "default";
		lastName = "default";
		birthday = new Date(0, 0, 1); //default date is Jan 1 1900
		email = "default";
		points = 0;	//starting points is 0
	}
	
	public User(String firstName, String lastName, int year, int month, int day, String email){
		this.firstName = firstName;
		this.lastName = lastName;
		birthday = new Date(year, month, day);
		this.email = email;
		points = 0;	//starting points is 0
	}
	
	public User(String firstName, String lastName, Date birthday, String email){
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
		this.email = email;
		points = 0;	//starting points is 0
	}
	
	public User(String firstName, String lastName, Date birthday, String email, int points){
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
		this.email = email;
		this.points = points;	
	}
	
	//--------------------------------------------------------------------------------------
	//GET AND SET METHODS: Methods for accessing and changing class properties
	
	public String getFirstName(){
		return firstName;
	}
	
	public void setFirstName(String firstName){
		this.firstName = firstName;
	}
	
	public String getLastName(){
		return lastName;
	}
	
	public void setLastName(String lastName){
		this.lastName = lastName;
	}
	
	public Date getBirthday(){
		return birthday;
	}
	
	public String getBirthdayAsString(){
		return birthday.toString();
	}
	
	public void setBirthday(Date birthday){
		this.birthday = birthday;
	}
	
	// year: actual year
	// month: 1 - 12
	public void setBirthday(int year, int month, int day){
		birthday = new Date(year+1900, month+1, day);
	}
	
	public String getEmail(){
		return email;
	}
	
	public void setEmail(String email){
		this.email = email;
	}
	
	public ArrayList<String> getInterests(){
		return interests;
	}
	
	public int getPoints(){
		return points;
	}
	
	public void setPoints(int points){
		this.points = points;
	}
	
	public void addPoints(int add){
		this.points = this.points + add;
	}
	
	public ArrayList<Friend> getFriends(){
		return friends;
	}
	
	// We might want to add a comparison function - Dax
	
}
