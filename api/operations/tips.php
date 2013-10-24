<?php
class RequestObject{
	private $_DEFAULT_TIP_COUNT = 10;


	private $_DATA = null;
	private $_STATUS = 0;
	private $_REQ;
	
	private $_sqlCon;
	/** Constructor
	 ** Currently takes optional PDO connection argument
	 ** Now allows n optional specification of the request array ($_GET, $_POST, $_REQUEST)
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
			$numListPattern = '/\d{1,3}(?:,\d{1,3}){0,19}/'; //Matches strings like "7", "100", "69,70", "1,2,3"; Up 20 exclusions allowed.
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
		$randomIDs = $this->generateRandomIDs($count * TIPS_RAND_ID_FACTOR,$maxID);
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