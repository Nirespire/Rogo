<?php
/** This is the page that will handle all incoming api requests **/

require 'require.php'; 

class RequestHandler{
	private static $REQUEST_SCRIPTS = array(
		'test' => 'test.php'
	);
	private static $REQUEST_STATUS = array(
		STATUS_SUCCESS => 'success',
		STATUS_ERROR => 'error',
		STATUS_FAILURE => 'failure'
	);
	
	private $_request = null;
	
	/** Contructor without a request;
	  * request must be set later **/
	public function __construct(){
	}
	
	/** Set the request **/
	public function setRequest($request){
		$this->_request = $request;
	}
	
	/** Gets the include path for the script that handles the given request
	 ** Returns false if there is no script for the input request **/
	private function getRequestScript(){
		if($this->_request == null){
			return false;
		}
		if(array_key_exists($this->_request,RequestHandler::$REQUEST_SCRIPTS) !== true){
			return false;
		}
		return OP_PATH . RequestHandler::$REQUEST_SCRIPTS[$this->_request];
	}
	
	public function executeRequest(){
		$requestedScript = $this->getRequestScript();
		if($requestedScript == false){
			$this->handleOutput(STATUS_ERROR,'Invalid request object!');
		}
		else{
			require $requestedScript;
			if(!$this->validRequestClass()){
				$this->unexpectedError();
			}
			
			$requestHandler = new RequestObject();
			call_user_func(array($requestHandler,REQUEST_FUNC_EXEC));
			
			$outputData = call_user_func(array($requestHandler, REQUEST_FUNC_RET_DATA));
			$outputStatus = call_user_func(array($requestHandler, REQUEST_FUNC_RET_STATUS));
			
			$this->handleOutput($outputStatus,$outputData);
		}
	}
	private function validRequestClass(){
		if(!class_exists(REQUEST_CLASS)){
			return false;
		}
		if(!method_exists(REQUEST_CLASS,REQUEST_FUNC_EXEC) || 
		   !method_exists(REQUEST_CLASS, REQUEST_FUNC_RET_DATA) || 
		   !method_exists(REQUEST_CLASS, REQUEST_FUNC_RET_STATUS)){
			return false;
		}
		return true;
	}
	
	private function writeContentType(){
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
	}

	
	private function handleOutput($status,$data){
		if(array_key_exists($status, RequestHandler::$REQUEST_STATUS) !== true){ //An unknown return status was provided
			$this->unexpectedError();
		}
		$this->writeContentType();
		echo json_encode(array('status'=>RequestHandler::$REQUEST_STATUS[$status],'data'=>$data));
	}
	private function unexpectedError(){
		header('HTTP/1.0 500 Internal Server Error');
		$this->handleOutput(STATUS_ERROR,'An unexpected error has occurred!');
		die();
	}
}

$Handler = new RequestHandler();
$Handler->setRequest($_GET['_request_data']);
$Handler->executeRequest();
?>