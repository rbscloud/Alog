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
<?PHP
// Identification of request type
if (isset($_POST['type'])) {
    
    if ($_POST['type'] == "races") {
        $query = "SELECT * FROM tblRace";
        $db->query($query);
?>
<Races>
<?PHP
        while ($db->nextRecord()){
            foreach ($raceIDs as $allowedRace ){
                if ($db->Record['id'] == $allowedRace){   
?>
    <Race>
        <ID><?PHP echo $db->Record['id']; ?></ID>
        <name><?PHP echo $db->Record['name']; ?></name>
        <startTime><?PHP echo $db->Record['startTime']; ?></startTime>
        <endTime><?PHP echo $db->Record['endTime']; ?></endTime>
    </Race>
<?PHP
                }
            }
        }
?>
</Races>
<?PHP
                
    } else if ($_POST['type'] == "checkpoints") {
        if (isset($_POST['raceId'])) {
            $raceId = mysql_real_escape_string(htmlentities($_POST['raceId']));
            foreach($raceIDs as $allowedRace){
                if($allowedRace == $raceId){
                    $query = "SELECT * FROM tblCheckpoint WHERE raceId='$raceId'";
                    $db->query($query);
                    $allowed = true;
                }
            }
            if(!isset($allowed)){
?>
<Message>You are not alloed to log in the specified race.</Message>
<?PHP
                exit;
            }
?>
<CheckPoints>
<?PHP
            while ($db->nextRecord()) {
?>        
    <CheckPoint>
        <ID><?PHP echo $db->Record['id'];?></ID>
        <name><?PHP echo $db->Record['checkpointName']; ?></name>
    </CheckPoint>
<?PHP } ?>
</CheckPoints>
<?PHP } else { ?>
<Message>Your need to specify a race!</Message>
<?PHP }}} ?>

