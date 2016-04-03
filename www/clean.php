<?

	// MySQL
		require "mysql.php";

	// Output
		if (isset($_GET["ghost"]) && mysql_query("TRUNCATE TABLE ghost"))
			echo "Cleaned ghost"."<br>";
		if (isset($_GET["stats"]) && mysql_query("TRUNCATE TABLE stats"))
			echo "Cleaned stats"."<br>";

	// MySQL
		mysql_close($mysql);

?>
