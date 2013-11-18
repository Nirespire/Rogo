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
		$this->_user->initialize();
	}
	
	/** This is where the request actually occurs an is processed.
	 ** Once request.php determines the correct file corresponding to a request, it will call performRequest.
	 ** Do your processing and whatnot in here, then use $this->setResult() to set the output data. **/
	public function performRequest(){
		if(!$this->_user->IsLoggedIn()){
			$this->setResult(STATUS_NLI,'You must be logged in!');
			return;
		}
		$uid = $this->_user->getUID();
		
		try{
			$query = 'SELECT u.uid,u.username,a.status,a.location_label,a.update_time FROM users AS u INNER JOIN availability AS a ON a.uid=u.uid WHERE u.uid=:uid'; //Just your SQL query. Notice the tokens prefixed with a colon, ":sometid" and ":count"
			$userStatement = $this->_sqlCon->prepare($query); 					//Now we prepare the query. Just go with it, otherwise look it up. I don't feel like explaining it
			$userStatement->execute(array(':uid'=>$this->_user->getUID()));			//Now, using the array, we assign values to those tokens in the query that were prefixed with a colon.
																				//This prevents against SQL injection and stuff.
			$user = $userStatement->fetchAll(PDO::FETCH_ASSOC);				//Get an associated array (key-value) of all of the resulting rows
			
			$requestQuery = 'SELECT count(r.originid) AS requests FROM meetup_requests AS r WHERE r.targetid=:uid AND r.status="waiting"'; //Just your SQL query. Notice the tokens prefixed with a colon, ":sometid" and ":count"
			$requestStatement = $this->_sqlCon->prepare($requestQuery); 					//Now we prepare the query. Just go with it, otherwise look it up. I don't feel like explaining it
			$requestStatement->execute(array(':uid'=>$this->_user->getUID()));			//Now, using the array, we assign values to those tokens in the query that were prefixed with a colon.
																				//This prevents against SQL injection and stuff.
			$requestCount = $requestStatement->fetch(PDO::FETCH_ASSOC);				//Get an associated array (key-value) of all of the resulting rows
																				
			$this->setResult(STATUS_SUCCESS,array('requests'=>$requestCount['requests'],'user'=>$user));							//Yeah, return the result! 	
		}
		catch(PDOException $e){
			logError($_SERVER['SCRIPT_NAME'],__LINE__,'Unable to fetch user status',$e->getMessage(),time()); 		//Let's log the exception
			$this->setResult(STATUS_FAILURE,'Unable to fetch your information!');	//Tell the user everything died
		}
		
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