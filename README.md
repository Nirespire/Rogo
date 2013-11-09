Rogo
====

Get out and meet new people!


API
----
The API will work by performing HTTP requests to the API server, which will output JSON data which follows the [JSend](http://labs.omniti.com/labs/jsend) specifications. Currently, the server will accept both GET and POST requests, however code implementations should use POST; GET is enabled primarily for testing purposes.

All API requests will follow the same basic URL construction: `https://api.rogoapp.com/request/(request)[.ext]`. The `(request)` is simply the name of the request operation, and the (optional) extension specifies the output content-type. At this time, you may use `.txt` or `.json`. Both return the same output data, but the two have a different content type specified in the headers; `text/plain` for .txt, and `application/json` for .json. **The plain-text output is also formatted to be easier to read.** Use .json in code implementations. If the extension is omitted, it defaults to json. 

Example request strings:
* `https://api.rogoapp.com/request/test`
* `https://api.rogoapp.com/request/test.json?extended` (test request with a GET flag, 'extended')
* `https://api.rogoapp.com/request/test.txt?password=mypassword` (test with a GET parameter 'password' with the value 'mypassword')

### API Operations
#### Testing
**Request location:** `test.json`

**Functionality:** This serves for testing functionality. At this point it serves two primary purposes. First, when the `extented` flag is provided, it outputs a simple dataset containing two "posts", each with an "id", a "title", and a "body". Use this to practice parsing the JSON within code. Second, when given a password value (`password=mypassword`), it will provide the SHA512 hash for that password, and the bcrypt hash of the SHA512 hash. The former is necessary for testing, as the SHA512 hash is what the client (the phone) must send as the password when performing authentication; this page may be used to verify SHA512 values match expected values, or for manually entering password values during authentication testing. The bcrypt is mostly only necessary for manually entering new users into the database, and for testing the amount of time the bcrypt hash takes to compute.

**Examples:**  
Request: `test.txt?password=mypassword`

    {
        "status": "success",
        "data": {
            "password": "mypassword",
            "sha512": "a336f671080fbf4f2a230f313560ddf0d0c12dfcf1741e49e8722a234673037dc493caa8d291d8025f71089d63cea809cc8ae53e5b17054806837dbe4099c4ca",
            "hash": "$2y$14$kDwvwzeiVN.tuASI9iMay.ToQrZwMSw0PJ3F.NSlyxGNoe9zWbq5W",
            "execution_time": "4.5447180271149 seconds"
        }
    }   

#### Registering
**Request location:** `register.json`

**Functionality:** Registers a new user and provides session authentication details. The output contains the user id, `uid`, the user's `username`, and two authentication variables, `session` and `secret`. The two auth variables will be discussed later. 

**Required parameters:**
* `username`: The user's desired username. Currently set to only allow usernames from 4 to 30 characters, containing only alphanumeric characters and hypens—multiple hypens are not permitted to be next to each other (`kickin-rad-guy` is valid, but `captain--underpants` is not).
* `email`: The user's email address. Minimum of 6 characters (`a@b.ca`)
* `password`: The SHA512 hash of the user's (salted!) password. Currently restricted to exactly 128 characters, the length of a SHA512 hash. 

**Examples:**  
Request: `register.txt?username=tits-palmer&email=xxheadshot420xx@aol.com&password=a3[...the above example hash...]ca`

    {
        "status": "success",
        "data": {
            "uid": "5",
            "username": "tits-palmer",
            "session": "829ae1fe97f263ca3e62a23e3a51a39487a5365c139ba32b2c4092a54faab8f3",
            "secret": "96045fd903c84fcde31e67371903809e"
        }
    }

Request: `register.txt?username=foxnews&email=lol@lol.com`

    {
        "status": "error",
        "data": "Request is missing the following field: password"
    }

#### Logging in
**Request location:** `login.json`

**Functionality:** Provided the correct authentication information, it will log the user in and return new session information. Returns the same dataset as registration; `uid`, `username`, `session`, and `secret`. 

**Required parameters:**
* `email`: The user's email address. Minimum of 6 characters (`a@b.ca`)
* `password`: The SHA512 hash of the user's (salted!) password. Currently restricted to exactly 128 characters, the length of a SHA512 hash. 

**Examples:**  
Request: `login.txt?email=xxheadshot420xx@aol.com&password=a3[...the above example hash...]ca`

    {
        "status": "success",
        "data": {
            "uid": "5",
            "username": "tits-palmer",
            "session": "3527af1f01b5be3094b718c40d08cfcbe71582bafd378a80506b515eec7a05b5",
            "secret": "ea784b691cfdb4480db9bf08edb067f0"
        }
    }

Request: `login.txt?email=idontknowhowtocomputer@gmail.com&password=google.com`

    {
        "status": "error",
        "data": "Email or password is incorrect!"
    }

#### Getting Tips
**Request location:** `tips.json`

**Functionality:** Returns a list of randomly selected tips (`tip`), along with their unique tip ids (`tip_id`). The tips however will not be randomly ordered, so it is suggested that the client select the tips at random when displaying them.

**Required parameters:** None.  

**Optional parameters:**  
* `count`: An integer of the number of tips to be returned. Default is `10`.
* `exclude`: A comma separated list of `tip_id` values, with no spaces. A maximum of 20 IDs may be listed. Example: `1,5,9`. Default is an empty set.

**Examples:**  
Request: `tips.txt?count=3`

    {
    	"status": "success",
    	"data": [
    		{
    			"tip_id": "3",
    			"tip": "Open the door, get on the floor, everybody walk the dinosaur"
    		},
    		{
    			"tip_id": "6",
    			"tip": "PARTY HARD"
    		},
    		{
    			"tip_id": "12",
    			"tip": "Take a class or join a club to meet people with common interests"
    		},
    	]
    }

#### Submitting a meeting

**Request location:** `meetsubmit.json`

**Functionality:** Handles the recording of a meeting occurrence between a user and another person. 

***
*MOST OF THIS IS VERY LIKELY TO CHANGE!*
***

**Required parameters:**
* `location_lat`: The GPS latitude of the location the meet up took place. Example: `29.643509`.
* `location_lon`: The GPS longitude of the location the meet up took place. Example: `-82.360729`.
* `question`: The question ID of the conversational question that was answered. Confused? You should be! Questions and their IDs have not been implemented yet! Woo!
* `answer`: The plain-text answer for the question that was asked. Maximum length: 255 characters. Example: `A grilled cheese sandwich`.
* `is_user`: Either `1` or `0`. If 1, the person the user met is a registered Rogo user. If 0, the person is not a Rogo user.
* `person_id`: If `is_user` is `1`, then this is the user id _number_; else, if `0`, this is simply the plain-text name of the person who was met. 
* `session`: The current user's 64-character session identifier.

**Optional parameters:**
* `location`: A user-input string identifying the location the meeting took place. Example: `the dark alley behind Starbucks`.

**Examples:**  
Request: `meetsubmit.txt?location_lat=29.649118753795555&location_lon=-82.34416704064655&question=1&answer=My%20toilet%2C%20and%20a%20lazer%20pointer&is_user=0&person_id=Bill%20Nye&session=48e9aacb18ddd6b3df6991d9082ccd882d2462a2c2ccdbda31f297a621868549&location=Starbucks`

    I have no idea what the output will be at the moment.
	Please hold your breath until I have made a decision.
	
