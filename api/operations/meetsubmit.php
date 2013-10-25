<?php
class RequestObject{
	private $_DATA = null;
	private $_STATUS = 0;
	private $_req;
	
	private $_sqlCon;
	/** Constructor
	 ** Currently takes optional PDO connection argument
	 **/
	public function __construct(){
		for($i=0;$i<func_num_args();$i++){
			$arg = func_get_arg($i);
			if(is_object($arg)){
				$class = get_class($arg);
				if($class == 'PDO'){
					$this->_sqlCon = $arg;
				}
			}
		} 
		if(REQUEST_DATA_ARRAY == 0){
			$this->_req = $_REQUEST;
		}
		else{
			$this->_req = $_POST;
		}
	}
	public function performRequest(){
		$required = array('location_lat','location_lon','question','answer','is_user','person_id','session');
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
			$meetDataStatement = $this->_sqlCon->prepare('INSERT INTO meetup_data (location, location_lat, location_lon,question,question_answer,date) VALUES (:location,:lat,:lon,:question,:ans,:date)');
			$meetDataStatement->execute(array(':location'=>$data['location'], ':lat'=>$data['location_lat'], ':lon'=>$data['location_lon'], ':question'=>$data['question'], ':ans'=>$data['answer'],':date'=>$currentTime));
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
	}
	
	private function getUID(){
		try{
			$idStatement = $this->_sqlCon->prepare('SELECT uid FROM sessions WHERE session=:session');
			$idStatement->bindParam(':session',$this->_req['session'],PDO::PARAM_STR);
			$idStatement->execute();
			if($idStatement->rowCount() != 1){
				return false;
			}
			$sesData = $idStatement->fetch(PDO::FETCH_ASSOC);
			return $sesData['uid'];
		}
		catch(PDOException $e){
			logError($_SERVER['SCRIPT_NAME'],__LINE__,'Error while checking if User ID exists',$e->getMessage(),time());
			return false;
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
		
		/** Now let's just make sure that the question ID given is a number **/
		$questionID = $this->_req['question'];
		if(!ctype_digit($questionID)){
			$this->setResult(STATUS_ERROR,'The question ID does not appear to be valid!');
			return false;
		}
		
		/** Question answer cleansing **/
		$answer = $this->_req['answer'];
		if(strlen($answer) > MEET_FORM_ANSWER_MAX_LENGTH){
			$this->setResult(STATUS_ERROR,'The submitted answer is too long!');
			return false;
		}
		$answer = cleanInput($answer,1,MEET_FORM_ANSWER_MAX_LENGTH);
		if($answer === false){
			$this->setResult(STATUS_ERROR,'The submitted answer appears to be invalid!');
			return false;
		}
		
				
		/** Make sure the is_user is either 0 or 1 **/
		if(!$this->_req['is_user'] === '0' && !$this->_req['is_user'] === '1'){
			$this->setResult(STATUS_ERROR,'The value for is_user must be either zero or one!');
			return false;
		}
		$is_user = ($this->_req['is_user'] === '1');
		
		/** Make sure person_id is either a name (if is_user is 0) or an integer id (if is_user is 1) **/
		$person_id = $this->_req['person_id'];
		if(!$is_user){
			if(strlen($person_id) > MEET_FORM_NAME_MAX_LENGTH){
				$this->setResult(STATUS_ERROR,'The name entered appears to be too long!');
				return false;
			}
			if(preg_match('/[^\w\s\-\.]/',$person_id)){
				$this->setResult(STATUS_ERROR,'Names may only include alphanumeric characters, spaces, hyphens and periods!');
				return false;
			}
			$person_id = preg_replace(array('/\.{2,}/','/\s{2,}/','/\-{2,}/'),array('.',' ','-'),$person_id); //Replace multiple spaces, periods, hypens with single occurrences
		}
		else{
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
		return array('location_lat'=>$latitude, 'location_lon'=>$longitude, 'question'=>$questionID, 'answer'=>$answer, 'is_user'=>$is_user, 'person_id'=>$person_id, 'location' => $location);
	}
	
	private function isUserId($id){
		try{
			$idStatement = $this->_sqlCon->prepare('SELECT uid FROM users WHERE uid=:uid');
			$idStatement->bindParam(':uid',$id,PDO::PARAM_INT);
			$idStatement->execute();
			return $idStatement->rowCount() == 1;
		}
		catch(PDOException $e){
			logError($_SERVER['SCRIPT_NAME'],__LINE__,'Error while checking if User ID exists',$e->getMessage(),time());
			return false;
		}
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
	
	private function setResult($status,$data){
		$this->_STATUS = $status;
		$this->_DATA = $data;
	}
	public function getData(){
		if($this->_DATA != null){
			return $this->_DATA;
		}
		else{
			return false;
		}
	}
	public function getStatus(){
		return $this->_STATUS;
	}
}
?>