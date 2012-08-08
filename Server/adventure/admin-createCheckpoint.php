<?PHP // Access protection
require_once("include/membersite_config.php");

if(!$fgmembersite->CheckLogin())
{
    $fgmembersite->RedirectToURL("login.php");
    exit;
}

include_once("include/header.php");
?>

        <?php
        // put your code here
        ?>
<?include_once("include/footer.php");?>
