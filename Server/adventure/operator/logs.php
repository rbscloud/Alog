<!--
To change this template, choose Tools | Templates
and open the template in the editor.
-->
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title></title>
    </head>
    <body>
        <?php
        
        include_once("../include/db.php");
        $db = new Database();
    
        $query = "SELECT * FROM tblLog ORDER BY time DESC";
        $db->query($query);

        while ($db->nextRecord()){
            echo "Log: \t" .
                 "raceId :" . $db->Record['raceId'] . " \t" .
                 "checkpointId :" . $db->Record['checkpointId'] . " \t" .
                 "teamId :" . $db->Record['teamId'] . " \t" .
                 "time :" . $db->Record['time'] . " \t" .
                 "point :" . $db->Record['point'] . "<br/>\n";
        }
        ?>
    </body>
</html>
