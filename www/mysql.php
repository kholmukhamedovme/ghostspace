<?

	// MySQL
		$mysql = mysql_connect("localhost", "ghostspace_user", "") or die("MySQL connection error");
		mysql_select_db("ghostspace_db", $mysql) or die("MySQL database selection error");

?>
