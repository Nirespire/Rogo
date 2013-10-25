<?php
define('INPUT_EMAIL_LENGTH',255);
define('INPUT_USERNAME_LENGTH',30);
//Length to trim the input "password" to. 128 is the length of a Base64 SHA512 hash.
define('INPUT_PASSWORD_LENGTH',128);

define('INPUT_EMAIL_MIN_LENGTH',6); // a@b.cd

// Aww, but I wanted to be "dog"
define('INPUT_USERNAME_MIN_LENGTH',4);

//Minimum length of the incoming password
//This SHOULD be a hash, so 32 is far smaller than it should be.
//But, really, it doesn't matter too much. Even if someone is fucking with the API, it will be hashed internally. 
//Fake edit: You know what, I'm going to just make this 128 and strictly enforce SHA512 (or something that looks like it).
define('INPUT_PASSWORD_MIN_LENGTH',128); 


// Disable account after too many unsuccessful logins
define('DISABLED_ACCOUNT_PERIOD',60 * 5); 
// How many unsuccessful logins before an account is disabled
define('DISABLED_ACCOUNT_TRIES',5);

// This is the path relative to the web root directory where all of the request handling files will be located
define('OP_PATH','operations/');

date_default_timezone_set('America/New_York');

define('AUTH_HASH_COMPLEXITY',14);

define('REQUEST_DATA_ARRAY',0); //Use 0 for $_REQUEST, 1 for $_POST


/****************************************************************/
/** Tips request configuration                                 **/
/****************************************************************/
define('TIPS_DEFAULT_COUNT',10); //Default number of tips returned
define('TIPS_MAX_COUNT',100);    //Maximum allowed number of tips to be requested
define('TIPS_RAND_ID_FACTOR',1); //Multiplication factor for the number of extra IDs that should be generated
//See: http://www.titov.net/2005/09/21/do-not-use-order-by-rand-or-how-to-get-random-rows-from-table/

/****************************************************************/
/** Meeting form submit configuration                          **/
/****************************************************************/
define('MEET_FORM_ANSWER_MAX_LENGTH',255);

define('MEET_FORM_NAME_MAX_LENGTH',255);

define('MEET_LOCATION_MAX_LENGTH',255);

/****************************************************************/
/** The config definitions below here should NOT be modified!  **/
/** Unless you really know what you're doing, and are prepared **/
/** to make the changes required in other files, changing      **/
/** anything here will just break everything. Don't do it.     **/
/****************************************************************/


//Define the name of the class that will enclose any script that handles a request
define('REQUEST_CLASS','RequestObject');
//Define the name of the function within the REQUEST_CLASS that will perform the action of handling the request
define('REQUEST_FUNC_EXEC','performRequest');
//Define the name of the function within REQUEST_CLASS that will return the output data after the request has been handled
define('REQUEST_FUNC_RET_DATA','getData');
//Define the name of the function within REQUEST_CLASS that will return the output status after the request has been handled
define('REQUEST_FUNC_RET_STATUS','getStatus');

define('STATUS_SUCCESS',1);
define('STATUS_ERROR',2);
define('STATUS_FAILURE',3);
?>