<?php
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
		/*
		 * 29.649118753795555, -82.34416704064655
		 * 29.643509,-82.360729
		 * 29.650111,-82.348691
		 * 29.650372,-82.342941
		 */
		
		$data = array(
			'1' => array('user_id' => '1', 'location_latitude' => 29.649118753795555, 'location_longitude' => -82.34416704064655, 'location_label' => 'The dungeon', 'updated' => '14 minutes'),
			'5' => array('user_id' => '5', 'location_latitude' => 29.643509, 'location_longitude' => -82.360729, 'updated' => '30 minutes'),
			'14' => array('user_id' => '14', 'location_latitude' => 29.650111, 'location_longitude' => -82.348691, 'updated' => '1 minute'),
			'69' => array('user_id' => '69', 'location_latitude' => 29.650372, 'location_longitude' => -82.342941, 'location_label'=> 'Starbucks @ Club West', 'updated' => '1 hour, 15 minutes')
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