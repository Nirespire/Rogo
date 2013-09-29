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
		elseif(isset($_GET['extended'])){
			$post1 = array('id'=>1,'title'=>'Hello world!','body'=>'An example post');
			$post2 = array('id'=>2,'title'=>'Super awesome second post','body'=>'This is, in fact, a rather boring post');
			
			$data = array('posts'=>array($post1,$post2));
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