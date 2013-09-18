<?php
/** This is the page that will handle all incoming api requests **/

require 'require.php';

if(!isset($_GET['request_data'])){
	echo json_encode(array('status'=>'error','message'=>'No request was specified!'));
}
else{
	$REQUEST = strtolower($_GET['request_data']);
	if($REQUEST == 'test'){
		require OP_PATH . 'test.php';
	}
}

?>