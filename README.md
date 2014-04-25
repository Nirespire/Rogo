Rogo
====

University of Florida: Fall 2013
CEN3031 Intro to Software Engineering
Project Group: ROGO


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
**Request location:** `tips.json` and `mrtips.json`

**Functionality:** Returns a list of randomly selected tips (`tip`), along with their unique tip ids (`tip_id`). The tips however will not be randomly ordered, so it is suggested that the client select the tips at random when displaying them. Requesting `tips.json` will return tips for conversations and socializing, `mrtips.json` will return suggestions for random people to talk to.

**Login session required**: No.

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

**Login session required**: Yes. 

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

**Optional parameters:**
* `location`: A user-input string identifying the location the meeting took place. Example: `the dark alley behind Starbucks`.

**Examples:**  
Request: `meetsubmit.txt?location_lat=29.649118753795555&location_lon=-82.34416704064655&question=1&answer=My%20toilet%2C%20and%20a%20lazer%20pointer&is_user=0&person_id=Bill%20Nye&session=48e9aacb18ddd6b3df6991d9082ccd882d2462a2c2ccdbda31f297a621868549&location=Starbucks`

    I have no idea what the output will be at the moment.
	Please hold your breath until I have made a decision.
	
#### Updating Availability

**Request location:** `availability.json`

**Functionality:** Updates a user's current location, the user's desired radius of contact, and whether or not the user is available for meetup requests. 

**Login session required**: Yes. 

**Required parameters:**
* `location_lat`: The GPS latitude of the location the meet up took place. Example: `29.643509`.
* `location_lon`: The GPS longitude of the location the meet up took place. Example: `-82.360729`.
* `availability`: Either `available` or `busy`. This will set whether the user shows up in nearby users. 
* `radius`: A value representing the radius, in miles, that the user wishes to be made available to nearby users. Examples: `1.5` for 1.5 miles, or `0.0946969697` for 500 ft. 

**Optional parameters:**
* `location_label`: A string representing the name of the location, or a description o the location the user is at. Ex: `The tree in Edmonton`.

**Examples:**  
Request:  
`availability.txt?location_lat=29.649118753795555&location_lon=-82.34416704064655&availability=available&location=In The Sky With Diamonds&radius=0.5&session=[session key]`

    {
    	"status": "success",
    	"data": "Updated!",
    	"session": "changed"
    }

#### Getting Nearby Users

**Request location:** `nearby.json`

**Functionality:** Gets a list of nearby, available users who both are within the requesting user's radius, and who have a radius value that also includes the requesting user. That is, both the requesting user, and all users who are returned, must have contact radius values that are large enough to include both parties. *This used the location, radius, and availability that has been set by the above availability update. Make sure to update availability with current location to ensure that the user's most current location is used*

**Login session required**: Yes. 

**Required Parameters:**  
None

**Optional Parameters:**  
* `count`: An integer of the maximum number of nearby users to be returned. Default is `10`.

**Example:**  
Request: `nearby.txt?count=2&session=[session]`

    {
    	"status": "success",
    	"data": [
    		{
    			"uid": "5",
				"username": "GetOutFrog",
    			"location_label": "The dungeon",
    			"location_latitude": "29.6504",
    			"location_longitude": "-82.3429",
    			"distance": "0.11374666933902636",
    			"updated": "2013-11-13 18:38:44",
    			"recentness": "2 days"
    		},
    		{
    			"uid": "4",
				"username": "tits-palmer",
    			"location_label": "Table in back corner of Starbucks",
    			"location_latitude": "29.6501",
    			"location_longitude": "-82.3487",
    			"distance": "0.28019242840206504",
    			"updated": "2013-11-12 18:38:44",
    			"recentness": "3 days"
    		}
    	],
    	"session": "changed"
    }
	
#### Sending a meetup request

**Request location:** `meetrequest.json`  

**Functionality:** This will send a request to meet up with another user at a specified location. 

**Login session required**: Yes. 

**Required Parameters:**  
* `person_id`: The uid of the person whom the user is requesting to meet with. 
* `characteristic`: A plain-text string for the user to describe his/her appearance in a way that will help the other user identify himself/hersef. Example: `Fedora with MLP t-shirt and black flame shorts`. 
* `location_label`: A plain-text string for the location to meet up. Example: `The combination PizzaHut and Taco Bell`.

**Optional Parameters:**
* `location_lat`: The GPS latitude of the location the meet up took place. Example: `29.643509`.
* `location_lon`: The GPS longitude of the location the meet up took place. Example: `-82.360729`.

**Example:**  
Request: `meetrequest.txt?characteristic=Awkward guy in dungeon&location_label=The Dungeon&person_id=3`  

    {
        "status": "success",
        "data": {
            "location_lat": null,
            "location_lon": null,
            "person_id": "3",
            "location_label": "The Dungeon",
            "characteristic": "Awkward guy in dungeon"
        },
        "session": "changed"
    }
 
	
#### Getting basic user status information

**Request location:** `status.json`

**Functionality:** Fetches some simple status information about the current user, including `uid`, `username`, the time which the user's availability and location were last updated, and the number of new meetup requests the user has.

**Login session required**: Yes. 

**Required Parameters:**  
None

**Optional Parameters:** 
None

**Example:**  
Request: `status.txt?session=[session]`

    {
    	"status": "success",
    	"data": {
    		"requests": "3",
    		"user": [
    			{
    				"uid": "69",
    				"username": "GetOutFrog",
    				"status": "available",
    				"location_label": "Outer Space",
    				"update_time": "2013-11-06 23:29:27"
					"points": "420"
    			}
    		]
    	},
    	"session": "changed"
    }
	
#### Updating Push ID registration

**Request location:** `regidupdate.json`

**Functionality:** Updates the Google Cloud Messenger push ID for the current session. 

**Login session required**: Yes. 

**Required Parameters:**  
* `register_id`: The GCM ID for the phone in use. Example: `???`

**Optional Parameters:** 
None

#### Getting another user's information

**Request location:** `userdata.json`

**Functionality:** Gets some basic profile and availability information about other users.

**Login session required**: Yes. 

**Required Parameters:**  
* `person_id`: The UID of the user whose information you are trying to view. Example: `24`. 

**Optional Parameters:** 
None

**Example:**  
Request: `userdata.json?person_id=3`

    {
        "status": "success",
        "data": {
            "user": [
                {
                    "uid": "3",
                    "username": "Dongs REO Speedwagon",
                    "status": "available",
                    "location_label": "Hiding in ceiling tiles in local coffee establishment",
                    "update_time": "2013-11-13 12:38:44",
                    "recentness": "2 hours",
                    "points": "47"
                }
            ]
        },
        "session": "changed"
    }

#### Viewing Incoming and Outgoing Meetup Requests

**Request location:** `myrequests.json`

**Functionality:** Returns both the requests to meet up with the current user, and the requests the user has sent to other users.

**Login session required**: Yes. 

**Required Parameters:**  
None

**Optional Parameters:** 
None

**Example:**
Request: `myrequests.json`

    {
        "status": "success",
        "data": {
            "incoming": [
                {
                    "rid": "23",
                    "characteristic": "Red Hat",
                    "location_label": "Starbux Reitz Union",
                    "location_lat": "29.6483233333333",
                    "location_lon": "-82.3443366666667",
                    "request_time": "2013-12-11 22:15:27",
                    "status": "waiting",
                    "recentness": "1 day 3 hours",
                    "uid": "14",
                    "username": "xXxHEADSHOT9000xXx"
                }
            ],
            "outgoing": [
                {
                    "request_id": "89",
                    "characteristic": "Awkward guy in dungeon",
                    "location_label": "The Dungeon",
                    "location_lat": "29.6483233333333",
                    "location_lon": "-82.3443366666667",
                    "request_time": "2013-12-12 19:30:00",
                    "status": "waiting",
                    "recentness": "6 hours",
                    "uid": "3",
                    "username": "AndThenThereWere3"
                }
            ]
        },
        "session": "changed"
    }