package com.rogoapp;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

// for constants used among multiple tests
public class AbstractTest {

	static final String USERNAME_DEFAULT = "examplemanuel";
	
	static final String FIRST_NAME_DEFAULT = "Manuel";
	static final String LAST_NAME_DEFAULT = "Bermudez";
	
	static final int BIRTHDAY_DEFAULT_DATE_YEAR = 43; // 1943
	static final int BIRTHDAY_DEFAULT_DATE_MONTH = 2; // March
	static final int BIRTHDAY_DEFAULT_DAY = 1; // 1
	static final Date BIRTHDAY_DEFAULT_DATE = 
			new Date(BIRTHDAY_DEFAULT_DATE_YEAR, BIRTHDAY_DEFAULT_DATE_MONTH, BIRTHDAY_DEFAULT_DAY);
	static final int BIRTHDAY_DEFAULT_ACTUAL_YEAR = 1943;
	static final int BIRTHDAY_DEFAULT_ACTUAL_MONTH = 3; // March
	
	static final String EMAIL_DEFAULT = "examplemanuel@example.com";

	static final int SCORE_START = 0;
	static final int SCORE_DEFAULT_INCREMENT = 3;
	static final int SCORE_INCREMENTED = SCORE_START + SCORE_DEFAULT_INCREMENT;

	static final int LEVEL_START = 1;
	static final int LEVEL_DEFAULT_INCREMENT = 5;
	static final int LEVEL_INCREMENTED = LEVEL_START + LEVEL_DEFAULT_INCREMENT;
	
	static final User USER_DEFAULT = new User(FIRST_NAME_DEFAULT, LAST_NAME_DEFAULT, BIRTHDAY_DEFAULT_DATE, EMAIL_DEFAULT);
	
	// helper methods
	
	protected void assertUserHasValues(User user, String username, String firstName, String lastName, Date birthday, String email,
			int score, int level) {
		assertEquals(username, user.getUsername());
		assertEquals(firstName, user.getFirstName());
		assertEquals(lastName, user.getLastName());
		assertEquals(birthday, user.getBirthday());
		assertEquals(email, user.getEmail());
		assertEquals(score, user.getScore());
		assertEquals(level, user.getLevel());
	}

}
