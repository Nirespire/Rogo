<?php
require 'password.php';

class $this->_requestObject{
	private $_DATA = null;
	private $_STATUS = 0;
	
	private $_sqlCon;
	private $_user;
	/** Constructor
	 ** Currently takes optional PDO connection argument
	 ** There really isn't much of a reason to modify this unless you really need something initialized before perform$this->_request()
	 **/
	public function __construct(){
		for($i=0;$i<func_num_args();$i++){	//Loop through all of the arguments provided to the instruction ("$this->_requestObject($arg1,$arg2,...)").
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
		if($this->_reqUEST_DATA_ARRAY == 0){ 		//Determine whether we want $this->_request data from $_$this->_reqUEST or $_POST
			$this->_$this->_req = $_$this->_reqUEST;
		}
		else{
			$this->_$this->_req = $_POST;
		}
		
		if($this->_user == null){
			$this->_user = new User();
		}
		// Uncomment this initialize line if we need user information from session, otherwise, leave this commented out.
		// That is, if we need to make sure the user is logged in and/or need to get UID/email/username/whatnot for the $this->_requesting user.
		//$this->_user->initialize();
	}
	public function perform$this->_request(){
		$data = '';
		
		/** BEGIN: Test to ensure that $this->_required $this->_request args are present **/
		$missingEmail = !isset($$this->_req['email']);
		$missingPass = !isset($$this->_req['password']);
		
		$margs = array();
		if($missingEmail){ array_push($margs,'email'); }
		if($missingPass){ array_push($margs,'password'); }
		
		if(count($margs) > 0){
			$this->setResult(STATUS_ERROR,'$this->_request is missing the following ' . ((count($margs)==1)?'field':'fields') . ': ' . implode(', ',$margs));
			return;
		}
		/** END: $this->_required args test **/
		
		$email = substr($$this->_req['email'],0,INPUT_EMAIL_LENGTH);
		$password = substr($$this->_req['password'],0,INPUT_PASSWORD_LENGTH);
		
		/** BEGIN: Query database for login authentication **/
		$loginQuery = 'SELECT uid, email, password, username, last_attempt, attempt_count, disabled FROM users WHERE email=:email LIMIT 1;';
		try{
			$loginStatement = $this->_sqlCon->prepare($loginQuery);
			$loginStatement->bindParam(':email',$email,PDO::PARAM_STR);
			$loginStatement->execute();
			$loginResult = $loginStatement->fetch();
		}
		catch(PDOException $e){
			logError($_SERVER['SCRIPT_NAME'],__LINE__,"Could not log in user. Query: \"$loginQuery\"",$e->getMessage(),time());
			$this->setResult(STATUS_FAILURE,'A server error occurred while trying to log you in. Please try again later.');
			return;
		}
		/** We've gotten the result of the query, now we need to validate **/
		if($loginStatement->rowCount() <= 0){
			$this->setResult(STATUS_ERROR,'Email or password is incorrect!');
			return;
			// Username was wrong, but we don't tell the
			// user as this information could be exploited 
		}
		
		/** At this point we know the email matches a record in the DB.
		 ** Now we just need to make sure the password is correct.
		 ** If the password is correct we'll give session info, if not, we need to update the failed attempts **/
		$currentTime = date("Y-m-d H:i:s");
		if($loginResult['disabled'] == 1 && (strtotime($loginResult['last_attempt']) + DISABLED_ACCOUNT_PERIOD) > time()){
			/** Someone has failed to provide authentication for this account too many times and it has been locked
			 ** We will provide a message saying how much time the user must wait before trying to log in again **/
			$seconds = (strtotime($loginResult['last_attempt']) + DISABLED_ACCOUNT_PERIOD) - time();
			$minutes = floor($seconds / 60);
			$remainingSeconds = $seconds % 60;
			$minString = ($minutes > 1) ? "$minutes minutes, " : (($minutes == 0) ? "" : "$minutes minute, ");
			$secString = ($seconds > 1) ? "$remainingSeconds seconds" : (($seconds == 0) ? "" : "$remainingSeconds second ");
			$this->setResult(STATUS_ERROR, "Your account has been locked due to too many failed login attempts. You must wait $minString $secString before you may try to log in!");
			return;
		}
		else{
			$uidValue = $loginResult['uid'];
			$hash = $loginResult['password'];
			if(!password_verify($password,$hash)){
				/** The password provided did not match the one in the database **/
				$attemptCount = (int)$loginResult['attempt_count'] + 1;
				$accountDisabled = 0;
				if($attemptCount >= DISABLED_ACCOUNT_TRIES){
					$accountDisabled = 1;
					$attemptCount = 0; //Reset attempt count now that the account has been locked 
				}
				
				$updateSQL = "UPDATE users SET last_attempt=:currentTime, disabled=:accountDisabled,attempt_count=:attemptCount WHERE uid=:uid;";
				try{
					$updateStatement = $this->_sqlCon->prepare($updateSQL);
					$updateStatement->execute(array(':currentTime'=>$currentTime,':accountDisabled'=>$accountDisabled,':attemptCount'=>$attemptCount,':uid'=>$uidValue));
				}
				catch(PDOException $e){
					logError($_SERVER['SCRIPT_NAME'],__LINE__,"Could not log user's log-in attempt into users table! Query: \"$updateSQL\"",$e->getMessage(),time(),false);
				}

				if($attemptCount >= DISABLED_ACCOUNT_TRIES){
					$seconds = DISABLED_ACCOUNT_PERIOD;
					$minutes = floor($seconds / 60);
					$remainingSeconds = $seconds % 60;
					$minString = ($minutes > 1) ? "$minutes minutes " : (($minutes == 0) ? "" : "$minutes minute ");
					$secString = ($seconds > 1) ? "$remainingSeconds seconds" : (($seconds == 0) ? "" : "$remainingSeconds second ");

					$this->setResult(STATUS_ERROR,"You have exceeded the maximum number of permitted log in attempts! You must wait $minString $secString before you may try to log in again!");
					return;
				}
				
				$this->setResult(STATUS_ERROR,'Email or password is incorrect!');
				// Password was wrong, but we don't tell the
				// user as this information could be exploited
				return;
			}
			else{
				if (password_needs_rehash($hash, PASSWORD_BCRYPT, array("cost" => AUTH_HASH_COMPLEXITY))) {
					/** If we change the hash algorithm, or the complexity, then old passwords need to be rehashed and updated **/
					$hash = password_hash($password, PASSWORD_BCRYPT, array("cost" => AUTH_HASH_COMPLEXITY));
					
					$hashUpdate = "UPDATE users SET password=:hash WHERE uid=:uid;";
					try{
						$hashUpdateStatement = $this->_sqlCon->prepare($hashUpdate);
						$hashUpdateStatement->execute(array(':hash'=>$hash,':uid'=>$uidValue));
					}
					catch(PDOException $e){
						logError($_SERVER['SCRIPT_NAME'],__LINE__,"Could not update a user's rehashed password! Query: \"$hashUpdate\"",$e->getMessage(),time(),false);
					}
				}
				$updateSQL = "UPDATE users SET last_login=:currentTime,last_attempt=:currentTime,disabled='0',attempt_count='0' WHERE uid=:uid;";
				try{
					$updateStatement = $this->_sqlCon->prepare($updateSQL);
					$updateStatement->execute(array(':currentTime'=>$currentTime,':uid'=>$uidValue));
				}
				catch(PDOException $e){
					logError($_SERVER['SCRIPT_NAME'],__LINE__,"Could not log user's log-in into users table! Query: \"$updateSQL\"",$e->getMessage(),time(),false);
				}
				
				$data = $this->_user->giveCredentials($uidValue,$username,$nowDatetime); 
				if($data === false){
					$this->setResult(STATUS_FAILURE,'Your log in information appears valid, but we were unable to complete your $this->_request due to a server error!');
				}
				else{
					$this->setResult(STATUS_SUCCESS,$data);
				}
				return;
			}
		}
		//This code should be unreachable
		$this->setResult(STATUS_FAIL,'Something has gone horribly wrong! Panic!');
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