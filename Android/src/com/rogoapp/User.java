/*
 * The User class is initialized with every launch of the app.
 * It contains important user information such as name, birthday, email, friends, etc
 * Users are created in the RogoMainActivity class with information pulled from our server (tenative)
 * 
 *  **Created by Joey Siracusa for Speaksoft**
 *  
 *  TODO:
 *  -Finish creating get/set methods
 *  -Discuss scoring functionality with group (possibly for later sprint)
 *  -Discuss creation of Friend class with group (possibly for later sprint)
 *  -Discuss how User will be implemented in RogoMainActivity (possibly for later sprint)
 *  
 */

package com.rogoapp;

import java.util.Date;
import java.util.ArrayList;




public class User {
	//--------------------------------------------------------------------------------------
	//PROPERTIES: All class properties are set to private
	
	private String username;
	private String firstName;
	private String lastName;
	private int uid;
	
	//for user in maps
	private double lat;
	private double lon;
	private String loc_label;
	private double distanceFromCurrUser;
	private String lastLocationUpdate;
	private String recentness;
	
	private Date birthday;
	private String email;
	private ArrayList<String> interests;
	private int score;
	private int level;
	
	// Friend class created
	private ArrayList<Friend> friends;
	
	// List for recently met users - not used anywhere yet
	private ArrayList<User> recentlyMet; 
	
	//--------------------------------------------------------------------------------------
	//INITIALIZATION: Initilization of User object
	
	public User(){
		username = "default";
		firstName = "default";
		lastName = "default";
		birthday = new Date(0, 0, 1); //default date is Jan 1 1900
		email = "default";
		score = 0;	//starting score is 0
		level = 1;	//starting level is 1
	}
	
	public User(int uid, double lat, double lon, String loc_label, double distanceFromCurrUser, String lastLocationUpdate, String recentness) {
		username = "default";
		firstName = "default";
		lastName = "default";
		birthday = new Date(0, 0, 1); //default date is Jan 1 1900
		email = "default";
		score = 0;	//starting score is 0
		level = 1;	//starting level is 1
		this.uid = uid;
		this.lat = lat;
		this.lon = lon;
		this.loc_label = loc_label;
		this.distanceFromCurrUser = distanceFromCurrUser;
		this.lastLocationUpdate = lastLocationUpdate;
		this.recentness = recentness;
	}
	public User(int uid, double lat, double lon, String loc_label, double distanceFromCurrUser, String lastLocationUpdate, String recentness, String username) {
		this.username = username;
		firstName = "default";
		lastName = "default";
		birthday = new Date(0, 0, 1); //default date is Jan 1 1900
		email = "default";
		score = 0;	//starting score is 0
		level = 1;	//starting level is 1
		this.uid = uid;
		this.lat = lat;
		this.lon = lon;
		this.loc_label = loc_label;
		this.distanceFromCurrUser = distanceFromCurrUser;
		this.lastLocationUpdate = lastLocationUpdate;
		this.recentness = recentness;
	}
	
	public User(String firstName, String lastName, int year, int month, int day, String email){
		this.firstName = firstName;
		this.lastName = lastName;
		birthday = new Date(year, month, day);
		this.email = email;
		score = 0;	//starting score is 0
		level = 1;	//starting level is 1
	}
	
	public User(String firstName, String lastName, Date birthday, String email){
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
		this.email = email;
		score = 0;	//starting score is 0
		level = 1;	//starting level is 1
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
	public String getUsername() {
		return username;
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
	
	public int getScore(){
		return score;
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public void incrementScore(int increment){
		score += increment;
	}
	
	public int getLevel(){
		return level;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public void incrementLevel(int increment){
		level += increment;
	}
	
	/***** Methods for putting users on the map*********/
	public String getName() {
		return username;
	}
	public double getLat() {
		return lat;
	}
	
	public double getLon() {
		return lon;
	}
	
	public String getLabel() {
		return loc_label;
	}
	
	public double getDistance() {
		return distanceFromCurrUser;
	}
	
	public String getLastLocUpdate() {
		return lastLocationUpdate;
	}
	
	public String getRecentness() {
		return recentness;
	}
	
	public int getUID() {
		return uid;
	}
	
	/*********************************************/
	
//	public static ArrayList<Friend> getFriends(){
	//	return friends;
//	}
	
	// We might want to add a comparison function - Dax
	
}
