<?php
class RequestObject{
	private $_DATA = null;
	private $_STATUS = 0;
	public function performRequest(){
		$data = 'data';
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