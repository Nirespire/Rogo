<?php
require 'password.php';

class RequestObject{
	private $_DATA = null;
	private $_STATUS = 0;
	
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
	}
	public function performRequest(){
		$REQ = $_REQUEST; 
		$data = '';
		
		/** BEGIN: Test to ensure that required request args are present **/
		$missingEmail = !isset($REQ['email']);
		$missingPass = !isset($REQ['password']);
		
		$margs = array();
		if($missingEmail){ array_push($margs,'email'); }
		if($missingPass){ array_push($margs,'password'); }
		
		if(count($margs) > 0){
			$this->setResult(STATUS_ERROR,'Request is missing the following ' . ((count($margs)==1)?'field':'fields') . ': ' . implode(', ',$margs));
			return;
		}
		/** END: Required args test **/
		
		$email = substr($REQ['email'],0,INPUT_EMAIL_LENGTH);
		$password = substr($REQ['password'],0,INPUT_PASSWORD_LENGTH);
		
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
				
				$this->giveCredentials($uidValue, $loginResult['username'],$currentTime);
				return;
			}
		}
		//This code should be unreachable
		$this->setResult(STATUS_FAIL,'Something has gone horribly wrong! Panic!');
	}
	private function giveCredentials($uid,$username,$currentTime){
		$bytes = openssl_random_pseudo_bytes(AUTH_SECRET_BYTES);
		$secret   = bin2hex($bytes);
		
		$bytes = openssl_random_pseudo_bytes(AUTH_SESSION_BYTES);
		$session = hash('sha256',$bytes);
		
		try{
			$credInsert = 'INSERT INTO sessions (uid, secret, session, last_use) VALUES (:uid,:secret,:session,:time)';
			$credStatement = $this->_sqlCon->prepare($credInsert);
			$credStatement->execute(array(':uid'=>$uid,':secret'=>$secret,':session'=>$session,':time'=>$currentTime));
		}
		catch(PDOException $e){
			logError($_SERVER['SCRIPT_NAME'],__LINE__,"Count not save user session! Query: \"$credInsert\"",$e->getMessage(),time(),false);
			$this->setResult(STATUS_FAILURE,'Your log in information appears valid, but we were unable to complete your request due to a server error!');
			return;
		}
		
		$data = array(
			'uid'=>$uid,
			'username' => $username,
			'session' => $session,
			'secret' => $secret
		);
		$this->setResult(STATUS_SUCCESS,$data);
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