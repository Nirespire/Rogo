<?php
/** Functions and definitions that will be included for every page **/
require 'serverconfig.php';
require 'config.php';

function logError($script,$line,$description, $error, $time,$die = true){
	$data = "File:        $script (Line: $line)\nDescription: ".$description."\nError:       ".$error."\nTime:        ".date('l, j F Y, \a\t g:i:s:u A',$time)."\n--------------------------------\n";
	file_put_contents(LOG_PATH_ERRORS, $data, FILE_APPEND);
	if($die){
		die();
	}
}

$path = 'include';
set_include_path(get_include_path() . PATH_SEPARATOR . $path); 


$SQLCON = null;
try {
	$SQLCON = new PDO('mysql:host='.DB_HOST.';dbname='.DB_NAME, DB_USER, DB_PASSWORD);
	$SQLCON->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
}
catch(PDOException $e){
	logError($_SERVER['SCRIPT_NAME'],__LINE__,"Could not select database (".DB_NAME.").",$e->getMessage(),time());
}
		
?>