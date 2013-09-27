<?php
/** Functions and definitions that will be included for every page **/
require 'serverconfig.php';
require 'config.php';

$path = 'include';
set_include_path(get_include_path() . PATH_SEPARATOR . $path); 

define('OP_PATH','operations/');
?>