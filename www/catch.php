<?

	// Constants
		define("HIT_RADIUS", 6);
		define("CLOSE_RADIUS", HIT_RADIUS * 2);
		define("LAST_USER_POSITIONS_QUANTITY", 9);

	// MySQL
		require "mysql.php";

	// Input
		$user_input = array(
			"a" => $_GET["a"],
			"b" => $_GET["b"],
			"c" => $_GET["c"]
		);

	// Function get_average
		function get_average ($position) {
			// Add current position into stats
				$nearest = get_nearest($position);

				if (is_hit($nearest))
					$is_hit = 1;
				else
					$is_hit = 0;

				mysql_query("INSERT INTO stats (a, b, c, is_hit) VALUES ($position[a], $position[b], $position[c], $is_hit)");

			// Calculate average position
				$query = mysql_query("SELECT * FROM stats ORDER BY id DESC LIMIT ".LAST_USER_POSITIONS_QUANTITY);
				$num_rows = mysql_num_rows($query);

				while ($data = mysql_fetch_array($query)) {
					$sum_a += $data["a"];
					$sum_b += $data["b"];
					$sum_c += $data["c"];
				}

				$average_position = array(
					"a" => $sum_a / $num_rows,
					"b" => $sum_b / $num_rows,
					"c" => $sum_c / $num_rows
				);

			// Return
				return $average_position;
		}

	// Function get_nearest
		function get_nearest ($position) {
			$query = mysql_query("SELECT * FROM ghost");
			$nearest = array(
				"aa" => $position["a"],
				"bb" => $position["b"],
				"cc" => $position["c"],
				"message" => ""
			); // initial assume data

			while ($data = mysql_fetch_array($query)) {
				$aa = abs($position["a"] - $data["a"]);
				$bb = abs($position["b"] - $data["b"]);
				$cc = abs($position["c"] - $data["c"]);

				if ($aa + $bb + $cc < $nearest["aa"] + $nearest["bb"] + $nearest["cc"]) {
					$nearest = array(
						"aa" => $aa,
						"bb" => $bb,
						"cc" => $cc,
						"message" => $data["message"]
					);
				}
			}

			return $nearest;
		}

	// Function is_hit
		function is_hit ($nearest) {
			if ($nearest["aa"] < HIT_RADIUS && $nearest["bb"] < HIT_RADIUS && $nearest["cc"] < HIT_RADIUS)
				return true;
			else
				return false;
		}

	// Function is_close
		function is_close ($nearest) {
			if ($nearest["aa"] < CLOSE_RADIUS && $nearest["bb"] < CLOSE_RADIUS && $nearest["cc"] < CLOSE_RADIUS)
				return true;
			else
				return false;
		}

	// Main
		function main ($position) {
			$average_position = get_average($position);
			$nearest = get_nearest($average_position);

			if (is_hit($nearest))
				echo "0"."&".$nearest["message"];
			elseif (is_close($nearest))
				echo "1"."&"."_";
			else
				echo "-1"."&"."_";
		}

	// Run
		main($user_input);

	// MySQL
		mysql_close($mysql);

?>
