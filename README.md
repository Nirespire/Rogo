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
    		"password": "password",
    		"sha512": "b109f3bbbc244eb82441917ed06d618b9008dd09b3befd1b5e07394c706a8bb980b1d7785e5976ec049b46df5f1326af5a2ea6d103fd07c95385ffab0cacbc86",
            "hash": "$2y$14$m20eE17vaXRBw5D4EYLROu7Sn18ZmhahXJzTJehzEwLZ01rJEPsrq",
            "execution_time": "4.7724039554596 seconds"
        }
    }