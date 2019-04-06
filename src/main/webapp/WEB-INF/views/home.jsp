<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="google-signin-scope" content="profile email">
    <meta name="google-signin-client_id" content="3478733355-th21i3ju60dgkmt9fmpbq2p7ck1eemmh.apps.googleusercontent.com">
    <script src="https://apis.google.com/js/platform.js" async defer></script>
<title>Insert title here</title>
</head>
<body>
<form id="form" name="form" action="board">
<div class="g-signin2" data-onsuccess="onSignIn" data-theme="dark"></div>
<input type="hidden" id="Name" name="Name" value="">
<input type="hidden" id="Email" name="Email" value="">
</form>

	<script>
	  function onSignIn(googleUser) {

	    var profile = googleUser.getBasicProfile();

	    document.getElementById("Name").value=profile.getName();
	    document.getElementById("Email").value=profile.getEmail();
	    document.form.submit();
	  };
	</script>
</body>
</html>