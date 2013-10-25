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
		$required = array('old_password','new_password','session');
		$missing = array();
		if(!$this->checkRequiredArgs($required,$missing)){
			$this->setResult(STATUS_ERROR,'The request is missing the following required parameters: ' . implode(', ',$missing));
			return;
		}
		
		$oldPassword = substr($this->_req['old_password'],0,INPUT_PASSWORD_LENGTH);
		$newPassword = substr($this->_req['new_password'],0,INPUT_PASSWORD_LENGTH);
	
		
		//Make sure the password, first name, and last name inputs are valid
		if(!$this->validLength($oldPassword,INPUT_PASSWORD_LENGTH,INPUT_PASSWORD_MIN_LENGTH)){
			$this->setResult(STATUS_ERROR,'The password old provided does not appear to be valid!');
			return;
		}
		if(!$this->validLength($newPassword,INPUT_PASSWORD_LENGTH,INPUT_PASSWORD_MIN_LENGTH)){
			$this->setResult(STATUS_ERROR,'The password new provided does not appear to be valid!');
			return;
		}
		
		
		$passwordQuery = 'SELECT uid, password, last_attempt, attempt_count, disabled FROM users WHERE uid=:uid LIMIT 1;';
		$uid = $this->getUID();
		if($uid === false){
			$this->setResult(STATUS_ERROR,'Invalid session credentials!');
			return;
		}
		try{
			$passwordStatement = $this->_sqlCon->prepare($passwordQuery);
			$passwordStatement->bindParam(':uid',$uid,PDO::PARAM_INT);
			$passwordStatement->execute();
			$passwordResult = $passwordStatement->fetch();
		}
		catch(PDOException $e){
			logError($_SERVER['SCRIPT_NAME'],__LINE__,"Could not query user data. Query: \"$passwordQuery\"",$e->getMessage(),time());
			$this->setResult(STATUS_FAILURE,'A server error occurred while trying to change your password. Please try again later.');
			return;
		}
		if($passwordStatement->rowCount() <= 0){
			$this->setResult(STATUS_ERROR,'Something went wrong!');
			return;
		}
		
		$currentTime = date("Y-m-d H:i:s");
		if($passwordResult['disabled'] == 1 && (strtotime($passwordResult['last_attempt']) + DISABLED_ACCOUNT_PERIOD) > time()){
			/** Someone has failed to provide authentication for this account too many times and it has been locked
			 ** We will provide a message saying how much time the user must wait before trying to log in again **/
			$seconds = (strtotime($passwordResult['last_attempt']) + DISABLED_ACCOUNT_PERIOD) - time();
			$minutes = floor($seconds / 60);
			$remainingSeconds = $seconds % 60;
			$minString = ($minutes > 1) ? "$minutes minutes, " : (($minutes == 0) ? "" : "$minutes minute, ");
			$secString = ($seconds > 1) ? "$remainingSeconds seconds" : (($seconds == 0) ? "" : "$remainingSeconds second ");
			$this->setResult(STATUS_ERROR, "Your account has been locked due to too many failed login attempts. You must wait $minString $secString before you may try to log in!");
			return;
		}
		else{
			echo $oldPassword;
			$uidValue = $passwordResult['uid'];
			$hash = $passwordResult['password'];
			if(!password_verify($oldPassword,$hash)){
				/** The password provided did not match the one in the database **/
				$attemptCount = (int)$passwordResult['attempt_count'] + 1;
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
				
				$this->setResult(STATUS_ERROR,'You did not enter a correct password!');
				return;
			}
			else{
				$hash = password_hash($newPassword, PASSWORD_BCRYPT, array("cost" => AUTH_HASH_COMPLEXITY));
				$updateSQL = "UPDATE users SET last_attempt=:currentTime,disabled='0',attempt_count='0',password=:newpass WHERE uid=:uid;";
				try{
					$updateStatement = $this->_sqlCon->prepare($updateSQL);
					$updateStatement->execute(array(':currentTime'=>$currentTime,':uid'=>$uidValue,':newpass'=>$hash));
					
					$this->setResult(STATUS_SUCCESS,'You password has been changed!');
				}
				catch(PDOException $e){
					$this->setResult(STATUS_ERROR,'Unable to change your password!');
					logError($_SERVER['SCRIPT_NAME'],__LINE__,"Could not change the user's password! Query: \"$updateSQL\"",$e->getMessage(),time(),false);
				}
				return;
			}
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
	private function validLength($input,$max,$min = 0){
        $len = strlen($input);
        if($len > $max) return false;
        if($len < $min) return false;
        return true;
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