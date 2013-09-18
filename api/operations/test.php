<?php

$data = 'tested!';
/** If you are good at noticing patterns,        **
 ** you will quickly see that I have a set of    **
 ** test cases that I very often repeatedly use. **/
if(isset($_GET['boobs'])){
	$data = '( . )( . )';
}
echo json_encode(array('status'=>'success','data'=>$data));

?>