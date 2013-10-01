package com.rogoapp;

import static org.junit.Assert.*;

import org.junit.Test;

public class UserTest extends AbstractTest {

	@Test
	public void testConstructorWithDateObject() {
		User user = new User(FIRST_NAME_DEFAULT, LAST_NAME_DEFAULT, BIRTHDAY_DEFAULT, EMAIL_DEFAULT);
		
		assertEquals(FIRST_NAME_DEFAULT, user.getFirstName());
		assertEquals(LAST_NAME_DEFAULT, user.getLastName());
		assertEquals(BIRTHDAY_DEFAULT, user.getBirthday());
		assertEquals(EMAIL_DEFAULT, user.getEmail());
		assertEquals(SCORE_START, user.getScore());
		assertEquals(LEVEL_START, user.getLevel());
	}

	@Test
	public void testConstructorWithIntDates() {
		User user = new User(FIRST_NAME_DEFAULT, LAST_NAME_DEFAULT, BIRTHDAY_DEFAULT_YEAR, BIRTHDAY_DEFAULT_MONTH,
				BIRTHDAY_DEFAULT_DAY, EMAIL_DEFAULT);
		
		assertEquals(FIRST_NAME_DEFAULT, user.getFirstName());
		assertEquals(LAST_NAME_DEFAULT, user.getLastName());
		assertEquals(BIRTHDAY_DEFAULT, user.getBirthday());
		assertEquals(EMAIL_DEFAULT, user.getEmail());
		assertEquals(SCORE_START, user.getScore());
		assertEquals(LEVEL_START, user.getLevel());
	}
	
}
