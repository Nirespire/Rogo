<?php

class RequestObject{
	private $_DATA = null;
	private $_STATUS = 0;
	public function performRequest(){
		$data = 'tested!';
		/** If you are good at noticing patterns,        **
		 ** you will quickly see that I have a set of    **
		 ** test cases that I very often repeatedly use. **/
		if(isset($_GET['boobs'])){
			$data = '( . )( . )';
		}
		
		$this->_DATA = $data;
		$this->_STATUS = STATUS_SUCCESS;
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