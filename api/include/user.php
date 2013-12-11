<?php
require_once 'require.php';
class User{
	private $_sqlCon;
	private $_req;
	
	private $_isLoggedIn = false;
	private $_userData = null;
	
	private $_updatedSession = false; //This will be changed if the session is used and rehashed. 
	private $_initialized = false;
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
		
		if(!isset($this->_sqlCon)){ 			//If we weren't given an SQL connection
			$this->_sqlCon = SQLConnect();		//From require.php
		}
		$this->_updatedSession = false; 
		$this->_initialized = false;
	}
	
	/** So as to not take up processing time validating the session if it's not needed, 
	 ** we're just going to require this Initialize function to be called.				 **/
	public function initialize(){
		if(!$this->_initialized){
			$this->ReadSession();
		}
	}
	
	private function ReadSession(){
		if(isset($this->_req['session'])){
			if(SERVER_DEV && $this->_req['session'] == 'dev'){
				$this->_userData = array('uid' => 2, 'username' => 'Developer', 'email' => 'dev@rogoapp.com');
				$this->_isLoggedIn = true;
				return;
			}
			$session = $this->_req['session'];
			if(strlen($session) != 64){ 		//64 characters for a SHA256 hash
				return;
			}
			
			$this->_isLoggedIn = $this->GetUserDataFromSession($session);
		}
	}
	
	private function GetUserDataFromSession($session){
		try{
			$query = 'SELECT s.sid, s.session, s.secret, u.uid, u.email, u.username FROM sessions AS s INNER JOIN users AS u ON s.uid=u.uid WHERE s.session=:session LIMIT 1';
			$sessionStatement = $this->_sqlCon->prepare($query);
			$sessionStatement->execute(array(':session'=>$session));
			if($sessionStatement->rowCount() > 0){
				$this->_userData = $sessionStatement->fetch(PDO::FETCH_ASSOC);
				
				$this->RehashUserSession();
				return true;
			}
			else{
				return false; //The session data was not valid
			}
		}
		catch(PDOException $e){
			logError($_SERVER['SCRIPT_NAME'],__LINE__,'Unable to fetch user data from database for session.',$e->getMessage(),time()); 		//Let's log the exception
		}
	}
	
	private function RehashUserSession(){
		if($this->_userData == null){
			return false;
		}
		$session = $this->_userData['session'];
		$secret = $this->_userData['secret'];
		$uid = $this->_userData['uid'];
		
		$now = date('Y-m-d H:i:s');
		
		$newSession = hash('sha256',$session . $secret);
		
		try{
			$updateQuery = 'UPDATE sessions SET session=:newsession, last_use=:now WHERE uid=:uid AND session=:oldsession'; //The oldsession thing is there because I think it's a good idea. I have no decent rational explanation for it, though.
			$updateStatement = $this->_sqlCon->prepare($updateQuery);
			$updateStatement->bindParam(':newsession',$newSession,PDO::PARAM_STR);
			$updateStatement->bindParam(':uid',$uid,PDO::PARAM_INT);
			$updateStatement->bindParam(':oldsession',$session,PDO::PARAM_STR);
			$updateStatement->bindParam(':now',$now,PDO::PARAM_STR);
			$updateStatement->execute();
			if($updateStatement->rowCount() == 1){
				$this->_updatedSession = true;
			}
			elseif($updateStatement->rowCount() < 1){ //Zero
				$this->_updatedSession = false;
				logError($_SERVER['SCRIPT_NAME'],__LINE__,"Unable to update user session data! UID: \"$uid\", Session: \"$session\", New session: \"$newSession\".",'No exception occurred',time()); 
			}
			else{ //Greater than 1
				$this->_updatedSession = true;
				logError($_SERVER['SCRIPT_NAME'],__LINE__,"UPDATED MORE THAN ONE SESSION. SOMETHING HAS GONE HORRIBLY WRONG!!! UID: \"$uid\", Session: \"$session\", New session: \"$newSession\".",'No exception occurred. Rows affected: ' . $updateStatement->rowCount(),time()); 
			}
		}
		catch(PDOException $e){
			logError($_SERVER['SCRIPT_NAME'],__LINE__,'Unable to update user session data!',$e->getMessage(),time()); 		//Let's log the exception
		}
	}
	
	public function giveCredentials($uid,$username,$currentTime){
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
			return false;
		}
		
		$data = array(
			'uid'=>$uid,
			'username' => $username,
			'session' => $session,
			'secret' => $secret
		);
		$this->_isLoggedIn = true;
		return $data;
		//$this->setResult(STATUS_SUCCESS,$data);
	}
	
	/** Used by request.php to tell client if the session was updated or not **/
	public function didSessionUpdate(){
		return $this->_updatedSession;
	}
	
	public function isLoggedIn(){
		return $this->_isLoggedIn;
	}
	public function getUID(){
		if(!$this->isLoggedIn()){ return false; }
		return $this->_userData['uid'];
	}
	public function getUsername(){
		if(!$this->isLoggedIn()){ return false; }
		return $this->_userData['username'];
	}
	public function getEmail(){
		if(!$this->isLoggedIn()){ return false; }
		return $this->_userData['email'];
	}
	public function getSID(){
		if(!$this->isLoggedIn()){ return false; }
		return $this->_userData['sid'];
	}
}

?>