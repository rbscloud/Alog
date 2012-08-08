<?PHP // Access protection
require_once("include/membersite_config.php");

if(!$fgmembersite->CheckLogin())
{
    $fgmembersite->RedirectToURL("login.php");
    exit;
}
?>

<?php // Creation of the race
    if(isset($_POST['raceName'])){
        include_once("include/db.php");

        $db = new Database();
        $name = mysql_real_escape_string(htmlentities($_POST['raceName']));
        
        $check = "SELECT name FROM tblRace WHERE name='$name'";
        $db->query($check);
        $aValid = array('-', '_',' ');
        if($db->numRows() == 0 && $name != '' && ctype_alnum(str_replace($aValid, '', $name))){
            $startTime = mysql_real_escape_string(htmlentities($_POST['startTime']));
            $endTime = mysql_real_escape_string(htmlentities($_POST['endTime']));
            $numCheckpoint = mysql_real_escape_string(htmlentities($_POST['numCheckpoint']));
            $maxNumTeam = mysql_real_escape_string(htmlentities($_POST['maxNumTeam']));
            $query = "INSERT INTO tblRace (name,startTime,endTime,numCheckpoint,maxNumTeam) VALUES" .
                "('$name','$startTime','$endTime','$numCheckpoint','$maxNumTeam')";
            $db->query($query);
            header("Location:http://adventure.flab.dk/");
            exit;
        }else{
            $alert = true;
        }
    }
	
include_once("include/header.php");
    ?>
        <?php // Alert incase of erroneous input
            if(isset($alert)){
                echo "<script type=\"text/javascript\">\n" .
                    "window.alert(\"A race with that name already exist.\\n" .
                    "Please choose another name.\\n" .
                    "The race name must only contain alphanumeric characters.\")\n" .
                    "</script>";
            }
        ?>
        
        <table>
		<script>
		$(function(){
			$("input[name=startTime],input[name=endTime]").datetimepicker();
		});
		</script>
        <form action="<?php echo htmlentities($_SERVER['PHP_SELF']); ?>" method="post">
            <tr><td>Name</td><td> : <input type="text" name="raceName" /> </td></tr>
            <tr><td>StartTime</td><td> : <input type="text" name="startTime" /> </td></tr>
            <tr><td>EndTime</td><td> : <input type="text" name="endTime" /> </td></tr>
            <tr><td>Number of checkpoints</td><td> : <input type="text" name="numCheckpoint" /> </td></tr>
            <tr><td>Maximum number of teams</td><td> : <input type="text" name="maxNumTeam" /> </td></tr>
            <tr><td><input type="submit" value="Create Race"></td>
        </form>
        <td><form action="admin-index.php"><input type="submit" value="Back"></form></td></tr>
        </table>
<?include_once("include/footer.php");?>