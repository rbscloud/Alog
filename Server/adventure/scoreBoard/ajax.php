<?php
if (!isset($_GET['raceId']) || !is_numeric($_GET['raceId'])) {
    echo "Set race ID ";
    exit;
}
$raceId = mysql_real_escape_string($_GET['raceId']);
include_once('../include/db.php');

$db = new Database();
$query = "SELECT name FROM tblRace WHERE id=" . $raceId;
$db->query($query);
$db->singleRecord();
$raceName = $db->Record['name'];

$query = "SELECT" .
        " tblCheckpoint.latitude," .
        " tblCheckpoint.longitude," .
        " tblCheckpoint.checkpointName," .
        " COUNT(tblLog.id) as logs" .
        " FROM tblCheckpoint INNER JOIN tblLog" .
        " ON tblCheckpoint.id=tblLog.checkpointId" .
        " WHERE tblLog.raceId='$raceId'" .
        " GROUP BY tblLog.checkpointId";

/*
$query = "SELECT" . 
		" GROUP BY tblLog.checkpointId , tblLog.teamId" . 
		" HAVING COUNT(tblLog.teamId) = 1"; */
$db->query($query);

$checkpoints = array();
while($db->nextRecord()) {
	$checkpoints[] = $db->Record;
}

$query = "SELECT tblTeam.id, tblTeam.teamName, SUM(tblLog.point) as points FROM tblTeam INNER JOIN tblLog" .
		" ON tblTeam.id=tblLog.teamId" .
		" WHERE tblLog.raceId='$raceId'" .
		" GROUP BY tblLog.teamId" .
		" ORDER BY SUM(tblLog.point) DESC";
$db->query($query);

$teams = array();
while($db->nextRecord()) {
	$teams[] = $db->Record;
}

echo json_encode(array("checkpoints" => $checkpoints, "teams" => $teams));
?>