<?php
/** This is the page that will handle all incoming api requests **/

require 'require.php'; 

class RequestHandler{
	private static $REQUEST_SCRIPTS = array(
		'test' => 'test.php',
		'login' => 'login.php',
		'register' => 'register.php',
		'nearby' => 'nearby.php',
		'tips' => 'tips.php',
		'meetsubmit' => 'meetsubmit.php',
		'passwordch' => 'changepw.php',
		'availability' => 'availability.php',
		'meetrequest' => 'meetrequest.php'
	);
	private static $REQUEST_STATUS = array(
		STATUS_SUCCESS => 'success',
		STATUS_ERROR => 'error',
		STATUS_FAILURE => 'failure',
		STATUS_NLI => 'not logged in'
	);
	
	private $_request = null;
	private $_sqlCon = null;
	private $_user = null;
	
	/** Contructor
	 ** Optional arguments: string Request, PDO SQL connection
	 **/
	public function __construct(){
		for($i=0;$i<func_num_args();$i++){
			$arg = func_get_arg($i);
			if(is_string($arg)){
				$this->_request = $arg;
			}
			elseif(is_object($arg)){
				$class = get_class($arg);
				if($class == 'PDO'){
					$this->_sqlCon = $arg;
				}
			}
		}
		$this->_user = new User();
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
			
			$requestClass = REQUEST_CLASS;
			$requestHandler = new $requestClass($this->_sqlCon,$this->_user);
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
	
    /** Writes the requested content type. Now also returns true if the output should be pretty (txt is requested). **/
	private function writeContentType(){
        $pretty = false;
		/** Determine and set the output MIME-type **/
		/* This really isn't that necessary for this, but who cares */
		$type = 'application/json'; //Default to the JSON mime-type
		if(isset($_GET['_data_format'])){ //If the data format was specified in the request (***.json/.txt)
			$format = strtolower($_GET['_data_format']);
			if($format == 'txt'){
				$type = 'text/plain';
                $pretty = true;
			}
			if($format == 'json'){
				$type = 'application/json';
			}
		}
		header('Content-Type: ' . $type);
        return $pretty;
		/** Done with MIME-type stuff **/
	}

	
	private function handleOutput($status,$data){
		if(array_key_exists($status, RequestHandler::$REQUEST_STATUS) !== true){ //An unknown return status was provided
			$this->unexpectedError();
		}
		$pretty = $this->writeContentType();
		$outputArray = array('status'=>RequestHandler::$REQUEST_STATUS[$status],'data'=>$data,'session'=>$this->IsSessionUpdated());
		
        if($pretty){
            if(PHP_VERSION_ID < 50400){
                require_once 'prettyjson.php';
                echo prettyPrint(json_encode($outputArray));
            }
            else{
                echo json_encode($outputArray,JSON_PRETTY_PRINT);
            }
        }
        else{
            echo json_encode($outputArray);
        }
	}
	private function IsSessionUpdated(){
		if($this->_user->isLoggedIn()){
			return ($this->_user->didSessionUpdate())?'changed':'unchanged';
		}
		return 'nli'; //Not logged in
	}
	
	private function unexpectedError(){
		header('HTTP/1.0 500 Internal Server Error');
		$this->handleOutput(STATUS_ERROR,'An unexpected error has occurred!');
		die();
	}
}

global $SQLCON;
$Handler = new RequestHandler($SQLCON);
$Handler->setRequest($_GET['_request_data']);
$Handler->executeRequest();
?>