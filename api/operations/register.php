<?php
require_once 'emailvalidate.php';
require_once 'password.php';
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
		$missingUser = !isset($REQ['username']);
		$missingEmail = !isset($REQ['email']);
		$missingPass = !isset($REQ['password']);
		
		$margs = array();
		if($missingUser){ array_push($margs,'username'); }
		if($missingEmail){ array_push($margs,'email'); }
		if($missingPass){ array_push($margs,'password'); }
		
		if(count($margs) > 0){
			$this->setResult(STATUS_ERROR,'Request is missing the following ' . ((count($margs)==1)?'field':'fields') . ': ' . implode(', ',$margs));
			return;
		}
		/** END: Required args test **/
		
		/** BEGIN: Filter, clean and validate input **/
		/*$email = substr($REQ['email'],0,INPUT_EMAIL_LENGTH);
		$username = substr($REQ['username'],0,INPUT_USERNAME_LENGTH);
		$password = substr($REQ['password'],0,INPUT_PASSWORD_LENGTH);*/
        $email = $REQ['email'];
        $username = $REQ['username'];
        $password = $REQ['password'];
        
		//Make sure the email is valid
		if(!$this->validLength($email, INPUT_EMAIL_LENGTH, INPUT_EMAIL_MIN_LENGTH) || ($email = cleanInput($email,INPUT_EMAIL_MIN_LENGTH,INPUT_EMAIL_LENGTH)) == false || !validEmail($email)){
			$this->setResult(STATUS_ERROR,'The email provided does not appear to be valid!');
			return;
		}
		//Make sure the password, first name, and last name inputs are valid
		if(!$this->validLength($password,INPUT_PASSWORD_LENGTH,INPUT_PASSWORD_MIN_LENGTH) || ($password = cleanInput($password,INPUT_PASSWORD_MIN_LENGTH,INPUT_PASSWORD_LENGTH)) == false){
			$this->setResult(STATUS_ERROR,'The password provided does not appear to be valid!');
			return;
		}
        
        if(!$this->validLength($username,INPUT_USERNAME_LENGTH,INPUT_USERNAME_MIN_LENGTH)){
            $this->setResult(STATUS_ERROR,'Your username is either too short or too long. Make sure it is longer than ' . INPUT_USERNAME_MIN_LENGTH . ' characters and shorter than ' . INPUT_USERNAME_LENGTH . ' characters.');
            return;
        }
        if(!preg_match("/^[\w-]*$/",$username)){
			$this->setResult(STATUS_ERROR,'Your username may only contain alphanumeric characters and hyphens!');
            return;
		}
        if(preg_match('/--/',$username)){
            $this->setResult(STATUS_ERROR,'Please don\'t place multiple hyphens next to each other in your username.');
            return;
        }
		
		//Make sure the first and last name contain only valid characters
		//Commented out because we'll probably use this later
		/*if(!preg_match("/^[\w-]*$/",$fname) || !preg_match("/^[\w-]*$/",$lname)){
			$return["success"] = 0;
			array_push($return["errors"],"Your first name may only consist of alphanumeric characters or hyphens!");
			return $return;
		}*/
		
		
		$email = strtolower($email);
		$hash = password_hash($password, PASSWORD_BCRYPT, array("cost" => AUTH_HASH_COMPLEXITY));
		
		$existingDetailCheck = $this->checkEmailAndUsername($email,$username);
		if($existingDetailCheck == false){
			//Already provided output within the function
			return;
		}
		else{
			$nowDatetime = date('Y-m-d H:i:s');
			$insertQuery="INSERT INTO users (email,username,password,date_created) VALUES (:email,:username,:password,:now);";
			try{
				$this->_sqlCon->beginTransaction();
				
				$regStatement = $this->_sqlCon->prepare($insertQuery);
				$regStatement->execute(array(':email'=>$email,':username'=>$username,':password'=>$hash,':now'=>$nowDatetime));
				$uidValue = $this->_sqlCon->lastInsertId(); //Make sure this is called BEFORE commit() in a transaction
				/*$confirmStatement = $this->_sqlCon->prepare('INSERT INTO confirmations (confirm_code,user_id,date) VALUES (:confirmCode,:uid,:now);');
				$confirmStatement->execute(array(':confirmCode'=>$activationKey,':uid'=>$uidValue,':now'=>$nowDatetime));*/
				
				$this->_sqlCon->commit();
			}
			catch(PDOException $e){
				logError($_SERVER['SCRIPT_NAME'],__LINE__,"An error occurred while registering a new user! Code: {$e->getCode()}.",$e->getMessage(),time(),false);

				//$this->opError = "Oh no! An error occurred while trying to create your account! Please try again.";
				$this->setResult(STATUS_FAILURE,'Oh no! An error occurred while trying to create your account! Please try to register again.');
				return;
			}
			
			//If the uidValue is valid
			if($uidValue > 0 && $uidValue != false){
				// $emailer = new Emailer();
				// $sent = $emailer->sendAccountConfirmationEmail($uidValue,$activationKey);
				$this->giveCredentials($uidValue,$username,$nowDatetime); 
				// if(!$sent){
					// array_push($return["errors"],'Your account was created successfully, but we couldn\'t send your confirmation email! Please send a message to support about it.');
					// logError($_SERVER['SCRIPT_NAME'],__LINE__,'Unable to send user\'s confirmation email!',null,time(),false);
				// }
				return;
			}
			else{
			}
		}
		$this->setResult(STATUS_FAILURE,'Oh no! Something unexpected has happened!');
		return;
	}
	private function checkEmailAndUsername($email,$username){
		$userCheckQuery = "SELECT email,username FROM users WHERE email=:email OR username=:username LIMIT 1;"; //Make sure this keeps LIMIT 1
		try{
			$statement = $this->_sqlCon->prepare($userCheckQuery);
			$statement->execute(array(':email' => $email, ':username'=>$username));
		}
		catch(PDOException $e){
			$this->setResult(STATUS_ERROR,'Something has gone wrong! Please try to register again.');
			logError($_SERVER['SCRIPT_NAME'],__LINE__,"Error executing user check query! Query: \"$userCheckQuery\", Email: \"$email\".",$e->getMessage(),time());
			return false;
		}
		
		//If the query returned rows, then someone IS registered using this email
		if($statement->rowCount() > 0){
			$conflict = $statement->fetch();

			if($conflict['email'] == $email){
				$this->setResult(STATUS_ERROR,'An account already exists associated with this email!');
				return false;
			}
			elseif($conflict['username'] == $username){
				$this->setResult(STATUS_ERROR,'An account already exists with that username!');
				return false;
			}
			else{
				//Something bad
			}
		}
		return true;
	}
	private function giveCredentials($uid,$username,$currentTime){
		$bytes = openssl_random_pseudo_bytes(16);
		$secret   = bin2hex($bytes);
		
		$bytes = openssl_random_pseudo_bytes(128);
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
    private function validLength($input,$max,$min = 0){
        $len = strlen($input);
        if($len > $max) return false;
        if($len < $min) return false;
        return true;
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