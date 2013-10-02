package com.rogoapp;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class UserTest extends AbstractTest {

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
	
	@Test
	public void testConstructorWithDateObject() {
		User user = new User(FIRST_NAME_DEFAULT, LAST_NAME_DEFAULT, BIRTHDAY_DEFAULT_DATE, EMAIL_DEFAULT);
		
		assertHasValues(user, FIRST_NAME_DEFAULT, LAST_NAME_DEFAULT, BIRTHDAY_DEFAULT_DATE, EMAIL_DEFAULT,
				SCORE_START, LEVEL_START);
	}

	@Test
	public void testConstructorWithIntDates() {
		User user = new User(FIRST_NAME_DEFAULT, LAST_NAME_DEFAULT, BIRTHDAY_DEFAULT_DATE_YEAR,
				BIRTHDAY_DEFAULT_DATE_MONTH, BIRTHDAY_DEFAULT_DAY, EMAIL_DEFAULT);
		
		assertHasValues(user, FIRST_NAME_DEFAULT, LAST_NAME_DEFAULT, BIRTHDAY_DEFAULT_DATE, EMAIL_DEFAULT,
				SCORE_START, LEVEL_START);
	}

	@Test
	public void testIncrementScore() {
		User user = new User(FIRST_NAME_DEFAULT, LAST_NAME_DEFAULT, BIRTHDAY_DEFAULT_DATE, EMAIL_DEFAULT);
		
		user.incrementScore(SCORE_DEFAULT_INCREMENT);
		
		assertHasValues(user, FIRST_NAME_DEFAULT, LAST_NAME_DEFAULT, BIRTHDAY_DEFAULT_DATE, EMAIL_DEFAULT,
				SCORE_INCREMENTED, LEVEL_START);
	}
	
	@Test
	public void testIncrementLevel() {
		User user = new User(FIRST_NAME_DEFAULT, LAST_NAME_DEFAULT, BIRTHDAY_DEFAULT_DATE, EMAIL_DEFAULT);
		
		user.incrementLevel(LEVEL_DEFAULT_INCREMENT);
		
		assertHasValues(user, FIRST_NAME_DEFAULT, LAST_NAME_DEFAULT, BIRTHDAY_DEFAULT_DATE, EMAIL_DEFAULT,
				SCORE_START, LEVEL_INCREMENTED);
	}
	
	// helper methods
	
	private void assertHasValues(User user, String firstName, String lastName, Date birthday, String email,
			int score, int level) {
		assertEquals(firstName, user.getFirstName());
		assertEquals(lastName, user.getLastName());
		assertEquals(birthday, user.getBirthday());
		assertEquals(email, user.getEmail());
		assertEquals(score, user.getScore());
		assertEquals(level, user.getLevel());
	}
}
