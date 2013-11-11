package com.rogoapp;

import java.util.ArrayList;
import java.util.List;

/*
 * To use BuddyList, you must create a BuddyList object.
 * To create a BuddyList object, you must have a CacheClient object.
 * So if you don't have a CacheClient object, your code should look something like this:
 * 
 * CacheClient cache = new CacheClient(this);
 * BuddyList buddyList = new BuddyList(cache);
 * 
 * Note that constructing a CacheClient requires passing in the current Activity.
 * So the above code will only work inside an Activity.
 */
public class BuddyList {

	public String BUDDY_LIST_FILE = "buddies";
	public String BUDDY_DELIMITER = "\n";
	public String BUDDY_INFO_DELIMITER = "$";
	
	CacheClient cache;
	
	public BuddyList(CacheClient cache) {
		this.cache = cache;
	}
	
	public List<Friend> getList() {
		String listWholeString = cache.loadFile(BUDDY_LIST_FILE);
		String[] listStrings = listWholeString.split(BUDDY_DELIMITER);
		
		List<Friend> list = new ArrayList<Friend>(listStrings.length);
		for (String buddy: listStrings) {
			String[] buddyParts = buddy.split(BUDDY_INFO_DELIMITER);
			if (buddyParts.length >= 2) list.add(new Friend(buddyParts[0], buddyParts[1]));
		}
		
		return list;
	}
	
	public void addBuddy(Friend friend) {
		cache.addToFile(BUDDY_LIST_FILE, friend.getUsername() + BUDDY_INFO_DELIMITER + friend.getFirstName() + BUDDY_DELIMITER);
	}
	
	public boolean removeBuddy(Friend friend) {
		return cache.deleteLineFromFile(BUDDY_LIST_FILE, friend.getUsername() + BUDDY_INFO_DELIMITER + friend.getFirstName());
	}
	
}
