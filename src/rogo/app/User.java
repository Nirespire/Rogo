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

package rogo.app;

import java.util.Date;
import java.util.ArrayList;



public class User {
	//--------------------------------------------------------------------------------------
	//PROPERTIES: All class properties are set to private
	
	private String firstname;
	private String lastname;
	private Date birthday;
	private String email;
	private ArrayList<String> interests;
	private int score;
	private int level;
	private ArrayList<Friend> friends; //Friend class yet to be created.
	
	//--------------------------------------------------------------------------------------
	//INITIALIZATION: Initilization of User object
	
	public User(){
		firstname = "default";
		lastname = "default";
		birthday = new Date(1900, 1, 1); //default date is Jan 1 1900
		email = "default";
		score = 0;	//starting score is 0
		level = 1;	//starting level is 1
	}
	
	public User(String firstname, String lastname, int year, int month, int day, String email){
		this.firstname = firstname;
		this.lastname = lastname;
		birthday = new Date(year, month, day);
		this.email = email;
		score = 0;	//starting score is 0
		level = 1;	//starting level is 1
	}
	
	public User(String firstname, String lastname, Date birthday, String email){
		this.firstname = firstname;
		this.lastname = lastname;
		this.birthday = birthday;
		this.email = email;
		score = 0;	//starting score is 0
		level = 1;	//starting level is 1
	}
	
	//--------------------------------------------------------------------------------------
	//GET AND SET METHODS: Methods for accessing and changing class properties
	
	public String getFirstname(){
		return firstname;
	}
	
	public void setFirstname(String firstname){
		this.firstname = firstname;
	}
	
	public String getLastname(){
		return lastname;
	}
	
	public void setLastname(String lastname){
		this.lastname = lastname;
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
	
	public void setBirthday(int year, int month, int day){
		birthday = new Date(year, month, day);
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
	
	public ArrayList<Friend> getFriends(){
		return friends;
	}
	

}
