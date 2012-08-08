<?PHP
require_once("./include/membersite_config.php");

if(isset($_POST['submitted']))
{
   if($fgmembersite->Login())
   {
        $fgmembersite->RedirectToURL("admin-index.php");
   }
}
if($fgmembersite->CheckLogin())
{
	$fgmembersite->RedirectToURL("admin-index.php");
}
include_once("include/header.php");
?>
  <style type="text/css" media="screen"><!--
	/*#outline { position: relative; height: 800px; width: 800px; margin: 18px auto 0; border: solid 1px #999; }
	#caption { width: 260px; left: 48px; top: 318px; position: absolute; visibility: visible; }
	#text { left: 336px; top: 318px; position: absolute; width: 400px; visibility: visible; margin-top: 10px; }*/
	/*p { color: #666; font-size: 16px; font-weight: normal; margin-top: 0; }
	h1 { color: #778fbd; font-size: 20px; font-weight: 500; line-height: 32px; margin-top: 4px; }
	h2 { color: #778fbd; font-size: 18px; font-weight: normal; margin: 0.83em 0 0; }
	h3 { color: #666; font-size: 60px; font-weight: bold; text-align: center; letter-spacing: -1px; width: auto; }
	h4 { font-weight: bold; text-align: center; margin: 1.33em 0; }
	a { color: #666; text-decoration: underline; }*/
--></style>
<link rel="STYLESHEET" type="text/css" href="style/fg_membersite.css" />
<script type='text/javascript' src='scripts/gen_validatorv31.js'></script>
<!-- Form Code Start -->
<div id='fg_membersite'>
<form id='login' action='<?php echo $fgmembersite->GetSelfScript(); ?>' method='post' accept-charset='UTF-8'>
<fieldset >
<legend>Login</legend>

<input type='hidden' name='submitted' id='submitted' value='1'/>

<div class='short_explanation'>* required fields</div>

<div><span class='error'><?php echo $fgmembersite->GetErrorMessage(); ?></span></div>
<div class='container'>
    <label for='username' >UserName*:</label><br/>
    <input type='text' name='username' id='username' value='<?php echo $fgmembersite->SafeDisplay('username') ?>' maxlength="50" /><br/>
    <span id='login_username_errorloc' class='error'></span>
</div>
<div class='container'>
    <label for='password' >Password*:</label><br/>
    <input type='password' name='password' id='password' maxlength="50" /><br/>
    <span id='login_password_errorloc' class='error'></span>
</div>

<div class='container'>
    <input type='submit' name='Submit' value='Login' />
</div>

<ul>
	<li><a href='register.php'>Register</a></li>
	<li><a href='confirmreg.php'>Confirm registration</a></li>
</ul>

</fieldset>
</form>
<!-- client-side Form Validations:
Uses the excellent form validation script from JavaScript-coder.com-->

<script type='text/javascript'>
// <![CDATA[

    var frmvalidator  = new Validator("login");
    frmvalidator.EnableOnPageErrorDisplay();
    frmvalidator.EnableMsgsTogether();

    frmvalidator.addValidation("username","req","Please provide your username");
    
    frmvalidator.addValidation("password","req","Please provide the password");

// ]]>
</script>
<div id='fg_crdiv'><p><a href='http://www.html-form-guide.com/php-form/php-login-form.html'
>see: php login form</a>.</p></div>
</div>
<!--
Form Code End (see html-form-guide.com for more info.)
-->
<?include_once("include/footer.php");?>