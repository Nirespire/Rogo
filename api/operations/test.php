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
		elseif(isset($_GET['password'])){
			require 'password.php';
			$password = $_GET['password'];
			
			$sha512 = hash('sha512',$password);
			
			$mtime = microtime();
			$mtime = explode(" ",$mtime);
			$mtime = $mtime[1] + $mtime[0];
			$starttime = $mtime;
			
			$hash = password_hash($sha512, PASSWORD_BCRYPT, array("cost" => AUTH_HASH_COMPLEXITY));

			$mtime = microtime();
			$mtime = explode(" ",$mtime);
			$mtime = $mtime[1] + $mtime[0];
			$endtime = $mtime;
			$totaltime = ($endtime - $starttime);
			
			$data = array(
				'password' => $_GET['password'],
				'sha512' => $sha512,
				'hash' => $hash,
				'execution_time' => $totaltime . ' seconds'
			);
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