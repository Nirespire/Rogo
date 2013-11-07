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
		$this->_user->initialize(); // We need to see if the user is logged in, so we must initialize the User object.
	}
	
	/** This is where the request actually occurs an is processed.
	 ** Once request.php determines the correct file corresponding to a request, it will call performRequest.
	 ** Do your processing and whatnot in here, then use $this->setResult() to set the output data. **/
	public function performRequest(){
		//Check if logged in
		if(!$this->_user->IsLoggedIn()){
			$this->setResult(STATUS_NLI,'You must be logged in!');
			return;
		}
		
		//Check to make sure all of the required parameters are provided
		$required = array('location_lat','location_lon','availability');
		$missing = array();
		if(!$this->checkRequiredArgs($required,$missing)){
			$this->setResult(STATUS_ERROR,'The request is missing the following required parameters: ' . implode(', ',$missing));
			return;
		}
		
		if(($data = $this->validateAllOfTheInput()) === false){
			return;
		}
		
	
		//Perform the insert
		try{
			$insertUpdateQuery = '	INSERT INTO availability VALUES (:uid, :status, :label, :lat, :lon, :now) 
									ON DUPLICATE KEY UPDATE status=:status, location_label=:label, location_lat=:lat, location_lon=:lon, update_time=:now';
			$iUStatement = $this->_sqlCon->prepare($insertUpdateQuery); 		//Now we prepare the query. Just go with it, otherwise look it up. I don't feel like explaining it
			$iUStatement->execute(array(
				':uid'=>$this->_user->getUID(),
				':status'=>$data['availability'],
				':label'=>$data['location'],
				':lat'=>$data['location_lat'],
				':lon'=>$data['location_lon'],
				':now'=>date('Y-m-d H:i:s')
			));	
			if($iUStatement->rowCount() == 1){
				$this->setResult(STATUS_SUCCESS,'Inserted!');															
			}
			elseif($iUStatement->rowCount() == 2){
				$this->setResult(STATUS_SUCCESS,'Updated!');
			}
			else{
				logError($_SERVER['SCRIPT_NAME'],__LINE__,'Something went wrong while inserting/updating availability!','No exception! ('.$iUStatement->rowCount().' rows altered)',time()); 
				$this->setResult(STATUS_FAILURE,'Something went wrong while trying to update your availability and location!');	//Tell the user everything died
			}
		}
		catch(PDOException $e){
			logError($_SERVER['SCRIPT_NAME'],__LINE__,'Something went wrong while inserting/updating availability!',$e->getMessage(),time()); 		//Let's log the exception
			$this->setResult(STATUS_FAILURE,'Something went wrong while trying to update your availability and location!');	//Tell the user everything died
		}
	}
	private function validateAllOfTheInput(){
		/** Time to start validating the input. Always a fun task. **/
		/** First validate the coordinates that were given. **/
		$latitude = $this->_req['location_lat'];
		$longitude = $this->_req['location_lon'];
		if(!$this->validateCoordinates($latitude,$longitude)){
			$this->setResult(STATUS_ERROR,'The latitude or longitude provided appear to be invalid!');
			return false;
		}
		
		/** Test if the optional location label is valid **/
		$location = "";
		if(isset($this->_req['location'])){
			$tempLoc = $this->_req['location'];
			if(strlen($tempLoc) > MEET_LOCATION_MAX_LENGTH){
				$this->setResult(STATUS_ERROR,'The location name appears to be too long!');
				return false;
			}
			$tempLoc = cleanInput($tempLoc,0,MEET_LOCATION_MAX_LENGTH);
			if($tempLoc === false){
				$this->setResult(STATUS_ERROR,'The location name appears to be invalid!');
				return false;
			}
			$location = $tempLoc;
		}
		
		/** Make sure the availability is either 0 or 1 **/
		if(!$this->_req['availability'] === 'available' && !$this->_req['available'] === 'busy'){
			$this->setResult(STATUS_ERROR,'The value for available must be either "available" or "busy"!');
			return false;
		}
		$available = ($this->_req['availability'] === 'available')?'available':'busy';
		
		

		return array('location_lat'=>$latitude, 'location_lon'=>$longitude, 'location' => $location, 'availability'=>$available);
	}
	
	private function validateCoordinates($lat,$lon){
		$latPat = '/^\-?\d{1,2}\.\d{5,20}$/';
		$lonPat = '/^\-?\d{1,3}\.\d{5,20}$/';
		
		if(preg_match($latPat,$lat) === 1 && preg_match($lonPat,$lon) === 1){
			return true;
		}
		return false;
	}
	
	private function checkRequiredArgs($required,&$missing = null){
		$status = true;
		foreach($required as $arg){
			if(!isset($this->_req[$arg])){
				$status = false;
				if($missing === null){
					return $status;
				}
				else{
					if(is_array($missing)){
						array_push($missing,$arg);
					}
				}
			}
		}
		return $status;
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