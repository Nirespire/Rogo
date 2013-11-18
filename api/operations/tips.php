<?php
class RequestObject{
	private $_DATA = null;
	private $_STATUS = 0;
	private $_req;
	
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
		//$this->_user->initialize();
	}
	public function performRequest(){
		$count = TIPS_DEFAULT_COUNT;
		if(isset($this->_req['count'])){
			$requestCount = $this->_req['count'];
			if(ctype_digit($requestCount) && $requestCount <= TIPS_MAX_COUNT){ //Make sure requestCount is a number
				$count = $requestCount;
			}
			else{
				$this->setResult(STATUS_ERROR,'Invalid count requested!');
				return;
			}
		}
		
		$exclude = array();
		if(isset($this->_req['exclude'])){
			$numListPattern = '/^\d{1,3}(?:,\d{1,3}){0,19}$/'; //Matches strings like "7", "100", "69,70", "1,2,3"; Up 20 exclusions allowed.
			$requestedExclude = $this->_req['exclude'];
			if(($matchResult = preg_match($numListPattern,$requestedExclude)) === 1){
				$exclude = explode(',',$requestedExclude);
			}
			elseif($matchResult === 0){
				$this->setResult(STATUS_ERROR,'The requested exclusion list appears to be invalid!');
				return;
			}
			elseif($matchResult === FALSE){
				$requestedExclude = substr($requestedExclude,0,2000);
				logError($_SERVER['SCRIPT_NAME'],__LINE__,'Error matching exclusion list!',$requestedExclude,time());
				$this->setResult(STATUS_FAILURE,'Something went wrong while processing the request!');
				return;
			}
		}
		
		$maxID = $this->getMaxTipID();
		$randomIDs = $this->generateRandomIDs($count * TIPS_RAND_ID_FACTOR + count($exclude),$maxID);
		$this->removeExcludedIDs($randomIDs,$exclude);
		
		try{
			$int_count = intval($count);
			$query = 'SELECT tid AS tip_id, tip FROM tips WHERE tid IN ('.implode(',',$randomIDs).') LIMIT :count';
			$tipsStatement = $this->_sqlCon->prepare('SELECT tid AS tip_id, tip FROM tips WHERE tid IN ('.implode(',',$randomIDs).') LIMIT :count');
			$tipsStatement->bindParam(':count',$int_count,PDO::PARAM_INT);
			$tipsStatement->execute();
			
			$this->setResult(STATUS_SUCCESS,$tipsStatement->fetchAll(PDO::FETCH_ASSOC));
		}
		catch(PDOException $e){
			logError($_SERVER['SCRIPT_NAME'],__LINE__,'Unable to fetch tips from database',$e->getMessage(),time());
			$this->setResult(STATUS_FAILURE,'Something went wrong while trying to fetch tips for you! Please try again!');
		}
	}
	private function removeExcludedIDs(&$idList,$exclude){
		$idList = array_filter($idList,function($val) use($exclude){
			return !in_array($val,$exclude);
		});
	}
	/* Gets the largest auto-incremented unique ID from the tips table */
	private function getMaxTipID(){
		$maxStatement = $this->_sqlCon->query('SELECT MAX(tid) AS maxid FROM tips');
		$result = $maxStatement->fetch(PDO::FETCH_ASSOC);
		return $result['maxid'];
	}
	/* Generates an array of $number Random IDs with numeric values between $maxID and $minID. */
	private function generateRandomIDs($number, $maxID, $minID = 1){
		$idArray = array();
		if($maxID < $number){ //If there are fewer IDs available than numbers available
			for($x=$minID;$x<=$maxID;$x+=1){ //Just fill the array with all of the possible ID values
				$idArray[$minID - $x] = $x;
			}
		}
		else{
			for($x=0;$x<$number;$x+=1){
				$itCount = 0; //Count variable for the number of iterations
				while(in_array($randomVal = mt_rand($minID,$maxID),$idArray) && $itCount+=1 < 100); //Generate new randomVals until one doesn't exist in the array
				$idArray[$x] = $randomVal;
			}
		}
		return $idArray;
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