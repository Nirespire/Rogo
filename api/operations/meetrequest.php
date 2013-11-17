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
	
		$required = array('characteristics','location_label','person_id');
		$missing = array();
		if(!$this->checkRequiredArgs($required,$missing)){
			$this->setResult(STATUS_ERROR,'The request is missing the following required parameters: ' . implode(', ',$missing));
			return;
		}
		
		if(($data = $this->validateAllOfTheInput()) === false){
			return;
		}
		
		$currentTime = date("Y-m-d H:i:s");
		try{
			$this->_sqlCon->beginTransaction();
			$meetDataStatement = $this->_sqlCon->prepare('INSERT INTO meetup_requests (originid,targetid,characteristic,location_label,location_lat,location_lon,request_time) VALUES (:uid,:personid,:char,:location,:lat,;lon,:now)');
			$meetDataStatement->execute(array(
				':location'=>$data['location_label'],
				':lat'=>$data['location_lat'],
				':lon'=>$data['location_lon'],
				':now'=>$currentTime,
				':uid'=>$this->_user->getUID(),
				':personid'=>$data['person_id'],
				':char'=>$data['characteristic']
			));
			if($meetDataStatement->rowCount() == 0){
				$this->setResult(STATUS_FAILURE,'Something went wrong while trying save your meeting data!');
				return;
			}
			$dataId = $this->_sqlCon->lastInsertId();
			
			if($data['is_user']){
				$userInsertStatement = $this->_sqlCon->prepare('INSERT INTO meetup_user (uid, other_uid) VALUES (:uid,:puid)');
				$userInsertStatement->execute(array(':uid'=>$this->getUID(), ':puid'=>$data['person_id']));
				if($userInsertStatement->rowCount() == 0){
					$this->_sqlCon->rollBack();
					$this->setResult(STATUS_FAILURE,'Something went wrong while trying save your meeting data!');
					return;
				}
			}
			else{
				$personInsertStatement = $this->_sqlCon->prepare('INSERT INTO meetup_nonuser (uid, name) VALUES (:uid,:name)');
				$personInsertStatement->execute(array(':uid'=>$this->getUID(), ':name'=>$data['person_id']));
				if($personInsertStatement->rowCount() == 0){
					$this->_sqlCon->rollBack();
					$this->setResult(STATUS_FAILURE,'Something went wrong while trying save your meeting data!');
					return;
				}
			}
			$this->_sqlCon->commit();
			
			$this->setResult(STATUS_SUCCESS,'Shit worked!');
		}
		catch(PDOException $e){
			$this->_sqlCon->rollBack();
			if($e->getCode() == 23000){ //a UNIQUE conflict
				if($data['is_user']){ //Right now there is only a UNIQUE key set for the user table. Non user can have duplicates.
					$this->setResult(STATUS_ERROR,'You can\'t meet the same user twice!');
					return;
				}
			}
			logError($_SERVER['SCRIPT_NAME'],__LINE__,'Error while performing meeting insertion!',$e->getMessage(),time());
			$this->setResult(STATUS_FAILURE,'Something went wrong while trying save your meeting data!');
			return;
		}
		
		$this->setResult(STATUS_SUCCESS,$data);
	}
	
	private function validateAllOfTheInput(){
		/** Time to start validating the input. Always a fun task. **/
		/** First validate the coordinates that were given. **/
		$latitude = 0; 
		$longitude = 0;
		if(isset($this->_req['location_lat']) && isset($this->_req['location_lon'])){
			$latitude = $this->_req['location_lat'];
			$longitude = $this->_req['location_lon'];
			if(!$this->validateCoordinates($latitude,$longitude)){
				$this->setResult(STATUS_ERROR,'The latitude or longitude provided appear to be invalid!');
				return false;
			}
		}
		
		/** Question answer cleansing **/
		$characteristic = $this->_req['characteristic'];
		if(strlen($characteristic) > MEET_REQUEST_CHARACTERISTIC_LENGTH){
			$this->setResult(STATUS_ERROR,'The submitted characteristic is too long!');
			return false;
		}
		$characteristic = cleanInput($answer,1,MEET_REQUEST_CHARACTERISTIC_LENGTH);
		if($characteristic === false){
			$this->setResult(STATUS_ERROR,'The submitted answer appears to be invalid!');
			return false;
		}
		
		$person_id = $this->_req['person_id'];
		if(strlen($person_id) > 11){
			$this->setResult(STATUS_ERROR,'The person ID entered appears to be too long!');
			return false;
		}
		if(!ctype_digit($person_id)){
			$this->setResult(STATUS_ERROR,'The person ID does not appear to be valid!');
			return false;
		}
		if(!$this->isUserId($person_id)){
			$this->setResult(STATUS_ERROR,'The person ID does not appear to be valid!');
			return false;
		}
		
		/** Test if the optional location label is valid **/
		$location = "";
		if(isset($this->_req['location_label'])){
			$tempLoc = $this->_req['location_label'];
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
		return array('location_lat'=>$latitude, 'location_lon'=>$longitude, 'question'=>$questionID, 'answer'=>$answer, 'is_user'=>$is_user, 'person_id'=>$person_id, 'location' => $location);
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