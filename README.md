Rogo
====

Get out and meet new people!


API
----
The API will work by performing HTTP requests to the API server, which will output JSON data which follows the [JSend](http://labs.omniti.com/labs/jsend) specifications. Currently, the server will accept both GET and POST requests, however code implementations should use POST; GET is enabled primarily for testing purposes.

All API requests will follow the same basic URL construction: `https://api.rogoapp.com/request/(request)[.ext]`. The `(request)` is simply the name of the request operation, and the (optional) extension specifies the output content-type. At this time, you may use `.txt` or `.json`. Both return the same plain-text output, just with a different content type specified in the headers; `text/plain` for .txt, and `application/json` for .json. Use .json in code implementations. If the extension is omitted, it defaults to json. 

Example request strings:
* `https://api.rogoapp.com/request/test`
* `https://api.rogoapp.com/request/test.json?extended` (test request with a GET flag, 'extended')
* `https://api.rogoapp.com/request/test.txt?password=mypassword` (test with a GET parameter 'password' with the value 'mypassword')

### API Operations
#### Testing
*Request location:* `test.json`

*Functionality:* This serves for testing functionality. At this point it serves two primary purposes. First, when the `extented` flag is provided, it outputs a simple dataset containing two "posts", each with an "id", a "title", and a "body". Use this to practice parsing the JSON within code. Second, when given a password value (`password=mypassword`), it will provide the SHA512 hash for that password, and the bcrypt hash of the SHA512 hash. The former is necessary for testing, as the SHA512 hash is what the client (the phone) must send as the password when performing authentication; this page may be used to verify SHA512 values match expected values, or for manually entering password values during authentication testing. The bcrypt is mostly only necessary for manually entering new users into the database, and for testing the amount of time the bcrypt hash takes to compute.

*Examples:*
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
*Request location:* `register.json`

*Functionality:* Registers a new user and provides session authentication details. The output contains the user id, `uid`, the user's `username`, and two authentication variables, `session` and `secret`. The two auth variables will be discussed later. 

*Required parameters:*
* `username` The user's desired username. Currently set to only allow usernames from 4 to 30 characters, containing only alphanumeric characters and hypens—multiple hypens are not permitted to be next to each other (`kickin-rad-guy` is valid, but `captain--underpants` is not).
* `email` The user's email address. Minimum of 6 characters (`a@b.ca`)
* `password` The SHA512 hash of the user's (salted!) password. Currently restricted to exactly 128 characters, the length of a SHA512 hash. 

*Examples:*
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
*Request location:* `login.json`

*Functionality:* Provided the correct authentication information, it will log the user in and return new session information. Returns the same dataset as registration; `uid`, `username`, `session`, and `secret`. 

*Required parameters:*
* `email` The user's email address. Minimum of 6 characters (`a@b.ca`)
* `password` The SHA512 hash of the user's (salted!) password. Currently restricted to exactly 128 characters, the length of a SHA512 hash. 

*Examples:*
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