<?php
/** This is the page that will handle all incoming api requests **/

require 'require.php'; 

/** Determine and set the output MIME-type **/
/* This really isn't that necessary for this, but who cares */
$type = 'application/json'; //Default to the JSON mime-type
if(isset($_GET['_data_format'])){ //If the data format was specified in the request (***.json/.txt)
	$format = strtolower($_GET['_data_format']);
	if($format == 'txt'){
		$type = 'text/plain';
	}
	if($format == 'json'){
		$type = 'application/json';
	}
}
header('Content-Type: ' . $type);
/** Done with MIME-type stuff **/

/** Time to actually figure out how to handle the request **/
if(!isset($_GET['_request_data'])){ //No request data was specified, so output an error
	echo json_encode(array('status'=>'error','message'=>'No request was specified!'));
}
else{ //Request was specified, so now we just need to find the correct script to pass the data to
	$REQUEST = strtolower($_GET['_request_data']);
	if($REQUEST == 'test'){
		require OP_PATH . 'test.php';
	}
	else{ //We would not find any handlers for the request
		echo json_encode(array('status'=>'error','message'=>'Invalid request object!'));
	}		
}

?>