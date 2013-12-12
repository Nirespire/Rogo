<?php
class RequestObject{
	private $_DATA = null;
	private $_STATUS = 0;
	
	private $_sqlCon;
	private $_user;
	/** Constructor
	 ** Currently takes optional PDO connection argument
	 ** There really isn't much of a reason to modify this unless you really need something initialized before performRequest()
	 **/
	public function __construct(){
		for($i=0;$i<func_num_args();$i++){	//Loop through all of the arguments provided to the instruction ("RequestObject($arg1,$arg2,...)").
			$arg = func_get_arg($i);
			if(is_object($arg)){ 			//If this argument is of class-type object (basically anything not a primative data type). 
				$class = get_class($arg); 	//Get the actual class of the argument
				if($class == 'PDO'){		//Hey look! It's our SQL object
					$this->_sqlCon = $arg; 	//We should save this. 
				}
				elseif($class == 'User'){	//If it's our User class
					$this->_user = $arg;
				}
			}
		} 
		if(REQUEST_DATA_ARRAY == 0){ 		//Determine whether we want request data from $_REQUEST or $_POST
			$this->_req = $_REQUEST;
		}
		else{
			$this->_req = $_POST;
		}
		
		if($this->_user == null){
			$this->_user = new User();
		}
		// Uncomment this initialize line if we need user information from session, otherwise, leave this commented out.
		// That is, if we need to make sure the user is logged in and/or need to get UID/email/username/whatnot for the requesting user.
		// $this->_user->initialize();
	}
	
	/** This is where the request actually occurs an is processed.
	 ** Once request.php determines the correct file corresponding to a request, it will call performRequest.
	 ** Do your processing and whatnot in here, then use $this->setResult() to set the output data. **/
	public function performRequest(){
		//Here's a quick way to check if the user is logged in. Make sure to uncomment the initialize line in the constructor if we wish to use this 
		if(!$this->_user->IsLoggedIn()){
			$this->setResult(STATUS_NLI,'You must be logged in!');
			return;
		}
		
		$sid = $this->_user->getSID();
		$regId = $this->_req['register_id'];
		
		try{
			$query = 'UPDATE sessions SET push_id=:pid WHERE sid=:sid';
			$pushIdStatement = $this->_sqlCon->prepare($query); 	
			$pushIdStatement->execute(array(':pid'=>$regId,':sid'=>$sid));
			//$result = $pushIdStatement->fetchAll(PDO::FETCH_ASSOC);
			if($pushIdStatement->rowCount() >= 1){ 												
				$this->setResult(STATUS_SUCCESS,'Updated');							//Yeah, return the result! 	
			}
			else{
				$this->setResult(STATUS_FAILURE,'Something went wrong while setting up push notifications!');
			}
		}
		catch(PDOException $e){
			logError($_SERVER['SCRIPT_NAME'],__LINE__,'Unable to update push notification registration ID',$e->getMessage(),time()); 		//Let's log the exception
			$this->setResult(STATUS_FAILURE,'Something went wrong while setting up push notifications!');	//Tell the user everything died
		}
		
		try{
			$cleaningQuery = 'DELETE FROM sessions WHERE push_id NOT NULL AND sid IN (SELECT s.sid FROM sessions AS s INNER JOIN (select uid,MAX(last_use) as most_recent FROM sessions GROUP BY uid) AS max ON s.uid=max.uid WHERE s.last_use <> max.most_recent AND s.uid=:uid)';
			$sessionCleaningStatement = $this->_sqlCon->prepare($cleaningQuery);
			$sessionCleaningStatement->execute(array(':uid'=>$this->_user->getUID()));
		}
		catch(PDOException $e){
			logError($_SERVER['SCRIPT_NAME'],__LINE__,'Unable to clean user session data',$e->getMessage(),time()); 		//Let's log the exception
		}
		
		$this->setResult(STATUS_SUCCESS,$data);
	}
	
	/** Sets the resultant status and data.
	 ** This is a little nicer than manually setting the variables
	 ** Status can be (currently) STATUS_SUCCESS, STATUS_ERROR, STATUS_FAILURE, STATUS_NLI
	 ** At the moment, I'm using failure for server issues like exceptions, and problems, and error for invalid input and the like 
	 ** NLI is for "not logged in"; use in cases where the user must be logged in **/
	private function setResult($status,$data){
		$this->_STATUS = $status;
		$this->_DATA = $data;
	}
	
	/** The function is used by request.php to get the output data once the request has been performed **/
	public function getData(){
		if($this->_DATA != null){
			return $this->_DATA;
		}
		else{
			return false;
		}
	}
	
	/** This function is used by request.php to get the result status once the request has been performed **/
	public function getStatus(){
		return $this->_STATUS;
	}
}
?>