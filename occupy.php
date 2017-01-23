<?php
require "connect.php";

//{roomid:(), generatorid:(), team:()}
$roomid = mysqli_real_escape_string($conn, $_POST['roomid']);
$generatorid = mysqli_real_escape_string($conn, $_POST['generatorid']);
$team = mysqli_real_escape_string($conn, $_POST['team']);

$sql = "UPDATE (database name)
	SET team='$team'
	WHERE roomid='$roomid'
	AND generatorid='$generatorid'";

if ($conn->query($sql) === TRUE) {
    echo json_encode(array("status" => "success"));
} else {
    echo json_encode(array("error" => $conn->error));
}

$conn->close();
?>