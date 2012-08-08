<?PHP // Access protection
require_once("include/membersite_config.php");

if(!$fgmembersite->CheckLogin())
{
    $fgmembersite->RedirectToURL("login.php");
    exit;
}

include_once("include/db.php");
$db = new Database();

// Deletion of race
if(isset($_GET['delete'])){
    $id = mysql_real_escape_string(htmlentities($_GET['delete']));
    $check = "DELETE FROM tblRace WHERE id='$id'";
    $db->query($check);
}

include_once("include/header.php");
?>
		<form class="control-form" action="logout.php" method="post">
			<input type="submit" value="Logout">
		</form>
		<form class="control-form" action="admin-createRace.php" method="post">
			<input type="submit" value="Create race">
		</form>
        <div class="races">
<?php   // Listing of races
$db->query("SELECT * FROM tblRace");
while($db->nextRecord()):
?>
            <div class="race"><a href="scoreBoard/viewScore.php?raceId=<?PHP echo $db->Record['id']; ?>"><?PHP echo $db->Record['name']; ?></a>
				<div class="controls">
					<form class="good" action="admin-createChechpoint.php?race=<?PHP echo $db->Record['id']; ?>" method="post">
						<input type="submit" value="Create Checkpoint">
					</form>
					<form class="good" action="admin-createOperator.php?race=<?PHP echo $db->Record['id']; ?>" method="post">
						<input type="submit" value="Create operator">
					</form><form class="good" action="admin-createTeam.php?race=<?PHP echo $db->Record['id']; ?>" method="post">
						<input type="submit" value="Create team">
					</form><form class="dangerous" action="admin-index.php?delete=<?PHP echo $db->Record['id']; ?>" method="post">
						<input type="submit" value="Delete race" onclick="return confirm('Are you sure you want to delete the race')">
					</form>
				</div>
			</div>
<?endwhile;?>
        </div>
<?include_once("include/footer.php");?>