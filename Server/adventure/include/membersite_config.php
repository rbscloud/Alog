<?PHP
//require_once("./include/fg_membersite.php");
require_once("fg_membersite.php");

$fgmembersite = new FGMembersite();

//Provide your site name here
$fgmembersite->SetWebsiteName('adventure.flab.com');

//Provide the email address where you want to get notifications
$fgmembersite->SetAdminEmail('rasmusbo_sorensen@hotmail.com');

//Provide your database login details here:
//hostname, user name, password, database name and table name
//note that the script will create the table (for example, fgusers in this case)
//by itself on submitting register.php for the first time
$fgmembersite->InitDB(/*hostname*/'localhost',
                      /*username*/'adventureflabdk',
                      /*password*/'debradanquah',
                      /*database name*/'dk_flab_adventure',
                      /*table name*/'fgusers');

//For better security. Get a random string from this link: http://tinyurl.com/randstr
// and put it here
$fgmembersite->SetRandomKey('6kWTgfpQuCE76D2');

?>