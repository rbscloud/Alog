<?PHP
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
?>
<!DOCTYPE html>
<html dir="ltr" xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <title><?PHP echo "Score Board of: $raceName" ?></title>
        <meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
        <link href="../style/scoreboard.css" rel="stylesheet" type="text/css" />

        <script type="text/javascript" src="../scripts/jquery-1.7.2.min.js"></script>
        <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?sensor=false">
        </script>
        <script type="text/javascript">
<?/*PHP
$query = "SELECT" .
        " tblCheckpoint.latitude," .
        " tblCheckpoint.longitude," .
        " tblCheckpoint.checkpointName," .
        " COUNT(tblLog.id)" .
        " FROM tblCheckpoint INNER JOIN tblLog" .
        " ON tblCheckpoint.id=tblLog.checkpointId" .
        " WHERE tblLog.raceId='$raceId'" .
        " GROUP BY tblLog.checkpointId";
$db->query($query);
$checkpoints = $db->numRows();
$latitude = 0;
$longitude = 0;
?>
            // Create an object containing LatLng, population.
            var citymap = {};
<?PHP while ($db->nextRecord()) { 
            if(isset($db->Record['latitude'])){
    ?>
            citymap['<?=$db->Record['checkpointName']; ?>'] = {
                center: new google.maps.LatLng(<?= $db->Record['latitude'] . "," . $db->Record['longitude']; ?>),
                population: <?=(($db->Record['COUNT(tblLog.id)'])*5); ?>
            };
        
<?PHP
		$latitude += $db->Record['latitude'];
		$longitude += $db->Record['longitude'];
    } else {
        $checkpoints = $checkpoints - 1;
    }
}
$c_lat = $latitude / $checkpoints;
$c_lng = $longitude / $checkpoints;*/
?>
            var cityCircles = [];
			var map;
			var raceId = <?=$raceId?>;

            function initialize() {
                var myOptions = {
                    zoom: 12,
                    disableDefaultUI: true,
                    mapTypeId: google.maps.MapTypeId.HYBRID
                };
                map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
            }
			
			var fps = 24;			
			var speed = -40;
			var offset = 0;
			function updateTeams() {
				//console.log();
				var totalHeight = $("#teams").outerHeight();
				var viewHeight = $("#high_score").innerHeight();
				
				if(offset < viewHeight-totalHeight) {
					speed *= -1;
				} else if(offset > 0) {
					speed *= -1;
				}
				
				offset += (speed * 1/fps);
				
				//console.log(totalHeight, viewHeight);
				$("#teams").css("top", offset);
				
			}
			
			function load() {
				$.ajax({
					url: "ajax.php?raceId="+raceId,
					dataType: "json",
					success: function(data) {
						var lats = 0;
						var lngs = 0;
						var count = 0;
						
						for(var circle in cityCircles) {
							cityCircles[circle].setMap(null);
						}
						cityCircles = []; // Reset!
						var checkpoints = data.checkpoints;
						for (var checkpoint in checkpoints) {
							// Construct the circle for each value in citymap.
							if(checkpoints[checkpoint].latitude != null && checkpoints[checkpoint].longitude != null) {
								lats += parseFloat(checkpoints[checkpoint].latitude);
								lngs += parseFloat(checkpoints[checkpoint].longitude);
								count++;
								var populationOptions = {
									strokeColor: "#FF0000",
									strokeOpacity: 0.8,
									strokeWeight: 2,
									fillColor: "#FF0000",
									fillOpacity: 0.35,
									map: map,
									center: new google.maps.LatLng(checkpoints[checkpoint].latitude, checkpoints[checkpoint].longitude),
									radius: checkpoints[checkpoint].logs*50
								};
								var cityCircle = new google.maps.Circle(populationOptions);
								cityCircles.push(cityCircle);
							}
						}
						var newCenter = new google.maps.LatLng(lats/count, lngs/count);
						map.setCenter(newCenter);
						
						var teams = data.teams;
						if($("#teams").children().length == 0) {
							for (var team in teams) {
								var teamElement = $("<div class='team' id='team-"+teams[team].id+"'>").appendTo("#teams");
								$("<span></span>").appendTo(teamElement).addClass("name").text(teams[team].teamName);
								var pointsLabel = $("<span></span>").appendTo(teamElement).addClass("points-label");
								$("<span></span>").appendTo(pointsLabel).addClass("points").text(teams[team].points);
								pointsLabel.append("point");
							}
						} else {
							
							for (var team in teams) {
								var teamElement = $("#team-"+teams[team].id);
								teamElement.empty();
								$("<span></span>").appendTo(teamElement).addClass("name").text(teams[team].teamName);
								var pointsLabel = $("<span></span>").appendTo(teamElement).addClass("points-label");
								$("<span></span>").appendTo(pointsLabel).addClass("points").text(teams[team].points);
								pointsLabel.append("point");
							}
						}
					}
				});
			}
			
			$(function(){
				initialize();
				load();
				setInterval(load, 5000);
				setInterval(updateTeams, 1000/fps);
			});
			
        </script>

    </head>

    <body>

        <div id="masthead">
            <div id="logo" style="width: 249px">
                <img alt="" height="100" src="../images/dds_logo.png" width="243" /></div>
            <div id="header"><?=$raceName?></div>
        </div>
        <div id="container">
            <div id="high_score">
                <div id="teams">
                    <!--<marquee  behavior="slide" direction="up" scrollamount="10" height="700" loop="1">-->
<?/*PHP
                $query = "SELECT tblTeam.teamName,SUM(tblLog.point) FROM tblTeam INNER JOIN tblLog" .
                        " ON tblTeam.id=tblLog.teamId" .
                        " WHERE tblLog.raceId='$raceId'" .
                        " GROUP BY tblLog.teamId" .
                        " ORDER BY SUM(tblLog.point) DESC";
                $db->query($query);
                while($db->nextRecord()){           
?>
                        <div class="team">
                            Holdnavn: <?PHP echo $db->Record['teamName']; ?></br>
                            Point : <?PHP echo $db->Record['SUM(tblLog.point)']; ?>
                        </div>
<?PHP
                }*/
?>
                    <!--</marquee>-->
                </div>
            </div>
            <div id="map_canvas">
            </div>
        </div>
    </body>
</html>
