<?PHP
// Access protection
require_once("include/membersite_config.php");

if (!$fgmembersite->CheckLogin()) {
    $fgmembersite->RedirectToURL("login.php");
    exit;
}
?>
<?php
// Creation of a Team
if (isset($_POST['teamName'])) {
    include_once("include/db.php");

    $db = new Database();
    $teamName = mysql_real_escape_string(htmlentities($_POST['teamName']));

    $check = "SELECT teamName FROM tblTeam WHERE teamName='$teamName'";
    $db->query($check);
    $aValid = array('-', '_', ' ');
    if ($db->numRows() == 0 && $teamName != '' && ctype_alnum(str_replace($aValid, '', $teamName))) {
        $raceId = mysql_real_escape_string(htmlentities($_POST['raceId']));
        $memberCount = mysql_real_escape_string(htmlentities($_POST['memberCount']));
        $query = "INSERT INTO tblTeam (raceId,teamName) VALUES" .
                "('$raceId','$teamName')";
        $db->query($query);
        
        $query = "SELECT id FROM tblTeam WHERE teamName='$teamName'";
        $db->query($query);
        $db->singleRecord();
        $teamId = $db->Record['id'];

        $names = $_POST['Names'];

        for ($i = 0; $i < count($names); $i++) {
            $teamMemberName = mysql_real_escape_string(htmlentities($names[$i]));
            $query = "INSERT INTO tblTeamMember (raceId,teamId,teamMemberName) VALUES" .
                    "('$raceId','$teamId','$teamMemberName')";

            $db->query($query);
        }
        header("Location:http://adventure.flab.dk/admin/");
        exit;
    } else {
        $alert = true;
    }
}

include_once("include/header.php");
?>
			<script type="text/javascript">
            function BuildFormFields($amount)
            {
                var
                $container = document.getElementById('FormFields'),
                $item, $field, $i;

                $container.innerHTML = '';
                for ($i = 0; $i < $amount; $i++) {
                    $item = document.createElement('div');
                    $item.style.margin = '3px';

                    $field = document.createElement('span');
                    $field.innerHTML = 'Name of team member';
                    $field.style.marginRight = '10px';
                    $item.appendChild($field);

                    $field = document.createElement('input');
                    $field.name = 'Names[' + $i + ']';
                    $field.type = 'text';
                    $item.appendChild($field);

                    $container.appendChild($item);
                }
            }

        </script>
        <form action="createTeam.php" method="post">
            <input type="hidden" name="raceId" value="<?PHP echo $_GET['race'] ?>"/>
            Team name : <input type="text" name="teamName"/> <br/>
            Number of team members : <input type="text" name="memberCount" onkeyup="BuildFormFields(parseInt(this.value, 10));" />
            <div id="FormFields" style="margin: 20px 0px;"></div>
            <input type="submit" value="Create Team"/>
        </form>
		<form action="admin-index.php"><input type="submit" value="Back"></form>
<?include_once("include/footer.php");?>