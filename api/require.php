<?php
/** Functions and definitions that will be included for every page **/
require 'serverconfig.php';
require 'config.php';

$path = 'include'; 
set_include_path(get_include_path() . PATH_SEPARATOR . $path); //Adds the './include' folder to the include path
// That doesn't explain much, but basically, if I say "include 'file.php';", 
// it now searches './include' for file.php, as well as the default include locations.

require_once 'user.php';


function logError($script,$line,$description, $error, $time,$die = false){
	$data = "File:        $script (Line: $line)\nDescription: ".$description."\nError:       ".$error."\nTime:        ".date('l, j F Y, \a\t g:i:s:u A',$time)."\n--------------------------------\n";
	file_put_contents(LOG_PATH_ERRORS, $data, FILE_APPEND);
	if($die){
		die();
	}
}

function cleanInput($input,$minLength = 0,$maxLength = 0){
	if(!is_null($input)){
		$value = $input;
		$value = @strip_tags($value);	
		$value = @stripslashes($value);
		$value = trim($value);	
		if($maxLength > 0) $value = @substr($value,$maxLenth);
		$value = trim($value);	/*redundant so as to remove spaces
		that may have been placed in the middle of a string */
		if(strlen($value) < $minLength){
			return false;
		}
		else if(!empty($value)){
			return $value;
		}
		else{
			return false;
		}
	}
	else{
		return false;
	}
}

function SQLConnect(){
	try {
		$SQLCON = new PDO('mysql:host='.DB_HOST.';dbname='.DB_NAME, DB_USER, DB_PASSWORD);
		$SQLCON->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		return $SQLCON;
	}
	catch(PDOException $e){
		logError($_SERVER['SCRIPT_NAME'],__LINE__,"Could not select database (".DB_NAME.").",$e->getMessage(),time());
	}
	return null;
}

/** Create an SQL connection **/
$SQLCON = SQLConnect();
		
?>