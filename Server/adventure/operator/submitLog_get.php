<?PHP // Verification of operator
if(isset($_GET['username'],$_GET['password'])){
    include_once('../include/db.php');
    $db = new Database();
    $username = mysql_real_escape_string($_GET['username']);
    $password = mysql_real_escape_string($_GET['password']);
    $query = "SELECT * FROM tblOperator WHERE operatorName='$username' and password='$password'";
    $db->query($query);
    //$db->singleRecord();
    if($db->numRows() <= 0){
        //Print error message or redirect;
        echo "Username or Password is incorrect.";
        exit;
    }
    $i = 0;
    while($db->nextRecord()){
        $raceIDs[$i] = $db->Record['raceId'];
        $i++;
    }
} else {
    //Print error message or redirect;
    echo "Specify both username and password.";
    exit;
}
?>
<?PHP // Insertion of log into database
if(isset($_GET['raceId'],$_GET['checkpointId'],$_GET['teamId'],$_GET['time'],$_GET['point'])){
    $raceId = mysql_real_escape_string(htmlentities($_GET['raceId']));
    if (in_array($raceId, $raceIDs)){
        $checkpointId = mysql_real_escape_string(htmlentities($_GET['checkpointId']));
        $teamId = mysql_real_escape_string(htmlentities($_GET['teamId']));
        $time = mysql_real_escape_string($_GET['time']);
        $point = mysql_real_escape_string(htmlentities($_GET['point']));    
        $date = new DateTime();
        $date->setTimestamp($time);

        $calChecksum = (((int)substr($teamId,0,1))+((int)substr($teamId,1,1))+((int)substr($teamId,2,1))) % 10;
        $checksum = (int)substr($teamId,3,1);
        $teamId = substr($teamId, 0, 3);
        if($checksum == $calChecksum){
            $query = "INSERT INTO tblLog (raceId,checkpointId,teamId,time,point) VALUES " .
                    "($raceId,$checkpointId,$teamId,'". $date->format('Y-m-d H:i:s') . "',$point)";
            $db->query($query);
?>
<Message>Log submitted.</Message>
<?PHP 
        } else{
?>
<Message>Checksum failed. Resubmit log.<?PHP echo $calChecksum; ?></Message>
<?PHP
        }
    } else{
        ?>
<Message>Your are not allowed to log in this race, log not submitted</Message>
<?PHP
    }
}else{
?>
<Message>Information missing, log not submitted</Message>
<?PHP
}
?>

