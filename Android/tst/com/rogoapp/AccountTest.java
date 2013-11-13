package com.rogoapp;

import static org.junit.Assert.*;

import org.junit.Test;

public class AccountTest extends AbstractTest {

	static final int VISIBILITY_DEFAULT = 1;
	static final double CONTACT_RADIUS_DEFAULT = 42.0;
	static final String USERNAME_DEFAULT = "manuel";
	
	@Test
	public void testConstructor() {
		Account account = new Account(VISIBILITY_DEFAULT, CONTACT_RADIUS_DEFAULT, USER_DEFAULT);
		
		assertEquals(VISIBILITY_DEFAULT, account.getVisibility());
		assertEquals(CONTACT_RADIUS_DEFAULT, account.getContactRadius(), 0.0);
		assertEquals(USER_DEFAULT, account.getUser());		
	}

	// we could tests the set methods but I think that's not worth our time
	
}
