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
		
		//Here's a quick way to check if the user is logged in. Make sure to uncomment the initialize line in the constructor if we wish to use this 
		if(!$this->_user->IsLoggedIn()){
			$this->setResult(STATUS_NLI,'You must be logged in!');
			return;
		}
		$uid = $this->_user->getUID();
		
		$requestedCount = 10; 							//Just a nice default value
		if(isset($this->_req['count'])){ 				//Check if the "count" parameter was included in the request ("template.json?count=5").
			if(ctype_digit($this->_req['count']) && $this->_req['count'] > 0){
				$requestedCount = $this->_req['count']; 	//Gets the value of the count parameter
			}
			else{
				$this->setResult(STATUS_ERROR,'Invalid count requested!');
				return;
			}
		}
		
		
		try{
			$distanceQuery = '
				SELECT uid, location_label, location_latitude, location_longitude, distance, updated, 
				CASE WHEN recentness < 60 THEN CONCAT(recentness,\' seconds\')
				WHEN recentness < 3600 THEN CONCAT(FLOOR(recentness / 60),\' minutes\')
				WHEN recentness < 7200 THEN CONCAT(\'1 hour \',FLOOR((recentness - 3600) / 60),\' minutes\')
				WHEN recentness < 86400 THEN CONCAT(FLOOR(recentness / 3600),\' hours\')
				WHEN recentness < 90000 THEN CONCAT(\'1 day \',FLOOR((recentness - 86400)/60),\' minutes\')
				WHEN recentness < 172800 THEN CONCAT(\'1 day \',FLOOR((recentness - 86400)/3600),\' hours\')
				ELSE CONCAT(FLOOR(recentness / 86400),\' days\') END AS recentness
				 FROM (
				SELECT  a.uid,  a.location_label, a.location_lat AS location_latitude, a.location_lon AS location_longitude, a.update_time AS updated, usera.radius AS uradius, a.radius,
				(2 * (3959 * ATAN2(
						  SQRT(
							POWER(SIN((RADIANS(usera.location_lat - a.location_lat ) ) / 2 ), 2 ) +
							COS(RADIANS(a.location_lat)) *
							COS(RADIANS(usera.location_lat)) *
							POWER(SIN((RADIANS(usera.location_lon - a.location_lon ) ) / 2 ), 2 )
						  ),
						  SQRT(1-(
							POWER(SIN((RADIANS(usera.location_lat - a.location_lat ) ) / 2 ), 2 ) +
							COS(RADIANS(a.location_lat)) *
							COS(RADIANS(usera.location_lat)) *
							POWER(SIN((RADIANS(usera.location_lon - a.location_lon ) ) / 2 ), 2 )
						  ))
						)
					  ))
				AS distance,
				TIMESTAMPDIFF(SECOND,a.update_time,NOW()) AS recentness
				FROM  availability AS a, (SELECT * FROM availability WHERE uid=:uid) AS usera
				WHERE a.uid <> usera.uid AND a.status="available"
				HAVING distance < usera.radius AND distance < a.radius
				ORDER BY distance
				LIMIT 0 , :count) AS nearby
			';
			$nearbyStatement = $this->_sqlCon->prepare($distanceQuery); 					//Now we prepare the query. Just go with it, otherwise look it up. I don't feel like explaining it
			//$nearbyStatement->execute(array(':uid'=>$uid));			//Now, using the array, we assign values to those tokens in the query that were prefixed with a colon.
			$nearbyStatement->bindParam(':uid',$uid,PDO::PARAM_INT);
			$nearbyStatement->bindValue(':count',intval($requestedCount),PDO::PARAM_INT);
			$nearbyStatement->execute();
			
			$result = $nearbyStatement->fetchAll(PDO::FETCH_ASSOC);				//Get an associated array (key-value) of all of the resulting rows
			$this->setResult(STATUS_SUCCESS,$result);							//Yeah, return the result! 	
		}
		catch(PDOException $e){
			logError($_SERVER['SCRIPT_NAME'],__LINE__,'Unable to fetch nearby users from database',$e->getMessage(),time()); 		//Let's log the exception
			$this->setResult(STATUS_FAILURE,'Something went wrong while trying to fetch nearby users for you! Please try again!');	//Tell the user everything died
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