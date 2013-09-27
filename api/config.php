<?php

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