<?PHP // Verification of operator
if(isset($_POST['username'],$_POST['password'])){
    include_once('../include/db.php');
    $db = new Database();
    $username = mysql_real_escape_string($_POST['username']);
    $password = mysql_real_escape_string($_POST['password']);
    $query = "SELECT * FROM tblOperator WHERE operatorName='$username' and password='$password'";
    $db->query($query);
    //$db->singleRecord();
    if($db->numRows() <= 0){
        //Print error message or redirect;
?>
<Message>Username or Password is incorrect.</Message>
<?PHP
        exit;
    }
    $i = 0;
    while($db->nextRecord()){
        $raceIDs[$i] = $db->Record['raceId'];
        $i++;
    }
} else {
    //Print error message or redirect;
?>
<Message>Specify both username and password.</Message>
<?PHP
    exit;
}
?>
<?PHP // Insertion of log into database
if(isset($_POST['raceId'],$_POST['checkpointId'],$_POST['teamId'],$_POST['time'],$_POST['point'])){
    $raceId = mysql_real_escape_string(htmlentities($_POST['raceId']));
    if (in_array($raceId, $raceIDs)){
        $checkpointId = mysql_real_escape_string(htmlentities($_POST['checkpointId']));
        $teamId = mysql_real_escape_string(htmlentities($_POST['teamId']));
        $time = mysql_real_escape_string($_POST['time']);
        $point = mysql_real_escape_string(htmlentities($_POST['point']));    
        $date = new DateTime();
        $date->setTimestamp($time);
        
        $query = "SELECT id FROM tblCheckpoint WHERE raceId=$raceId AND id=$checkpointId";
        $db->query($query);
        if($db->numRows() <= 0){
?>
<Message>CheckpointId incorrect.</Message>
<?PHP
            exit;
        }
        
        $calChecksum = (((int)substr($teamId,0,1))+((int)substr($teamId,1,1))+((int)substr($teamId,2,1))) % 10;
        $checksum = (int)substr($teamId,3,1);
        $teamId = substr($teamId, 0, 3);
        
        $query = "SELECT id FROM tblTeam WHERE raceId=$raceId AND id=$teamId";
        $db->query($query);
        if(($db->numRows() <= 0) || ($checksum!=$calChecksum)){
?>
<Message>TeamId is invalid or incorrect. Checksum: <?PHP echo $calChecksum; ?>. Resubmit log.</Message>
<?PHP
            exit;
        }
        
        if(($point>=0) && ($point<=100)){
            $query = "INSERT INTO tblLog (raceId,checkpointId,teamId,time,point) VALUES " .
                "($raceId,$checkpointId,$teamId,'". $date->format('Y-m-d H:i:s') . "',$point)";
            $db->query($query);       
?>
<Message>Log submitted.</Message>
<?PHP   } else{ ?>
<Message>Points out of bound.</Message>
<?PHP }} else{ ?>
<Message>Your are not allowed to log in this race<?PHP echo $raceID; ?>, log not submitted</Message>
<?PHP }}else{ ?>
<Message>Information missing, log not submitted</Message>
<?PHP } ?>

