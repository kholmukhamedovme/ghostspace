<?

	// MySQL
		require "mysql.php";

	// Input
		$user_input = array(
			"a" => $_GET["a"],
			"b" => $_GET["b"],
			"c" => $_GET["c"],
			"message" => $_GET["message"]
		);

	// Main
		function main ($input) {
			$query = mysql_query("INSERT INTO ghost (a, b, c, message) VALUES ($input[a], $input[b], $input[c], '$input[message]')");

			if ($query)
				echo 0;
			else
				echo -1;
		}

	// Run
		main($user_input);

	// MySQL
		mysql_close($mysql);

?>
