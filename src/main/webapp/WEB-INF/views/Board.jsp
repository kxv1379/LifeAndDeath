<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%> 
<html>
<head>
	<!-- 구글 api 관련 -->
	<meta name="google-signin-scope" content="profile email">
    <meta name="google-signin-client_id" content="3478733355-th21i3ju60dgkmt9fmpbq2p7ck1eemmh.apps.googleusercontent.com">
	<script src="https://apis.google.com/js/client:platform.js" async defer></script>
	 
	<!-- 스타일 시트 -->
	<link href="resources/css/bootstrap.min.css" rel="stylesheet">
	<link href="resources/css/main.css" rel="stylesheet">
	
	<!-- ajax -->
	<script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.js"></script>
	   
	<title>死活 BOT</title>
	
	<!-- 스타일 시트 나중에 따로 뺄 예정 -->
	<style type="text/css">
	.space{ 
		width: 220px;
		height: 31px;
	}
	button{ 
		width: 19px;
		height: 15px;
		padding: 0px;
		margin: 3 6;
		background:transparent;
		border: 0;
		outline: 0;
	}
	.float-left li a{
		border:1px solid orange; 
		margin:20px 10px; 
		border-radius: 4px; 
		color:orange;				
		width: 100px;
	 	height: 45px; 
	 	font: 14px bold; 
	 	font-weight: 700; 
	 	padding:12px 20px;
	 	text-align: center;
	}
	.float-left li a:hover {
		background: orange;
		color: #fff;
	}
	
	.selectButton{
		width:110px;
		height: 110px;
	}
	
	</style>
	
</head>
<body>
<div class="container">

	<!--변수 저장 color=돌 색상 mode= -1은 편집모드 0~5까지 플레이모드  -->
	<input type="hidden" id="color" name="color" value="Black">
	<input type="hidden" id="mode" name="mode" value="-1">
	<input type="hidden" id="stop" value="">
	
	<!-- 왼쪽 -->
	<div class="col-md-2">		
	</div>
	
	<!-- 가운데 --> 
	<div class="col-md-7" style="text-align: center;">		
	<h1 style="font-weight: bold; font-size: 30ox;">
		死活 BOT
	</h1>
	
	<!-- 보드판 만드는 테이블 -->
	<table background="resources/img/Board1.jpg" style="background-repeat: no-repeat; 
			background-position: center; margin-left: auto; margin-right: auto; margin-top:50px;">
	<% for(int i=0; i<19; i++){
		%>
		<tr>
		<td class="space"></td>
		<% 
		for(int j=0; j<19; j++){
			%>
			<td><button id="b<%=j+i*19%>" name="b<%=j+i*19%>"onclick="putStone(<%=j+i*19%>)" ><img id="<%=j+i*19%>"  src=""></button> </td>
			<%}
		%>
		<td class="space"></td>
		</tr>
		<%
	}
	%>
	</table>
	</div>
		
	 <!-- 오른쪽 -->	
	<div class="col-md-3">
		<div class="form-group">
		
		<!-- 구글 로그인 버튼 -->
			<div class="g-signin2" data-onsuccess="_onSignInCallback" data-theme="dark" id="signIn" style="margin-left:90px; margin-top:20px; margin-bottom:20px;"></div>
	 		
	 		<div id="signOut" hidden="" style="margin-top:20px; margin-bottom:32px;">
	 		<b id="userName"></b>님 환영합니다. <a  onclick="logOut()" href="#" style="color: blue"> 로그아웃</a></div>
	 		
	 		
			<div class="form-inline" style="text-align: left;">
				<label>사활</label>
				
				<select id="modeList" class="form-control" style="width: 200px; height: 50px;">
					<option value="1" selected>흑선 AI와 검토</option>
					<option value="2">백선 AI와 검토</option>
					<option value="3">흑선 AI에게 맞기기</option>
					<option value="4">백선 AI에게 맞기기</option>
					<option value="5">흑선으로 혼자 검토</option>
					<option value="6">백선으로 혼자 검토</option>
				</select>
	
	
				<ul class="nav navbar-nav navbar-left float-left">
					<li><a id="confirm" onclick="confirm()" > 확인</a></li>
					<li><a id="reset" onclick="reset()" href="reset"> 초기화</a></li>
				</ul>
				
				
				<button class="selectButton" onclick="bucket('Black')" id="Black" style="border: 2px solid orange"><img src="resources/img/bigBlack.png"></button>
				<button class="selectButton" onclick="pass()" id="pass"  hidden=""><img src="resources/img/passbutton.png" ></button>
				<button class="selectButton" onclick="bucket('White')" id="White"><img src="resources/img/bigWhite.png"></button>
				<button class="selectButton" onclick="undo()" id="undo" hidden=""><img src="resources/img/undo.png"></button>
				<button class="selectButton" onclick="bucket('Delete')" id="Delete"><img src="resources/img/xmark.png"></button>
				<button class="selectButton"  id="nowStone" hidden="" disabled="disabled"><img id="nowStoneImg" src="resources/img/bigBlack.png"></button>
				
			</div>
		</div>
	</div>
</div>





	<script type="text/javascript">
	function putStone(idNum){  //돌을 놓았을때
		var c=document.getElementById("color").value;
		var mode=document.getElementById("mode").value;
		var color
		if(c=="Black")
			color=1;
		else if(c=="White")
			color=2;
		else if(c=='Delete')
			color=0;
		
		var data={
				Num:idNum,
				color:color,
				mode:mode
		};
		 $.ajax({
		        type:"GET",
		        url:"putStone",
		        data : data,
		        success: function(result){	//result = updateList 컨트롤러 에서 받아온 리스트를 통해 보드판의 돌들을 업데이트 해줌
		        	var list=result.list;
		        	var map=result.hashMap;
		        	for(var i=0;i<list.length;i++){
		        		var img=document.getElementById(list[i][0]);
		        		
		        		if(list[i][1]=="1")
		        			img.src="resources/img/Black.png";
		        		else if(list[i][1]=="2")
		        			img.src="resources/img/White.png";
		        		else
		        			img.src="";
		        	}
		        
		        	if((list.length>0||idNum==-1)&&mode!=-1){//	편집모드가 아닐시에 돌색깔 자동 변경
		        		
		        		if(c=="Black"){
		        			document.getElementById("color").value="White";
		        			document.getElementById("nowStoneImg").src="resources/img/bigWhite.png";
		        		}
		        		else{
		        			document.getElementById("color").value="Black";
		        			document.getElementById("nowStoneImg").src="resources/img/bigBlack.png";
		        		}
		        		
		        		if(mode==0||mode==1){	//AI와 두기
		        			PlayWithComputerMode(map);
		        		}
		        	}
		        }		        
		    });
	}
	
	
	function bucket(color) {	//돌 색깔 변경(Xmark포함)
		var c=document.getElementById("color");
		c.value=color;
		document.getElementById("Black").style.border="";
		document.getElementById("White").style.border="";
		document.getElementById("Delete").style.border="";
		document.getElementById(color).style.border="2px solid orange";
	}
	
	function confirm() { 	//확인 버튼
		var mode=document.getElementById("modeList").selectedIndex;
		document.getElementById("mode").value=mode;
		if(mode%2==0){
			document.getElementById("color").value="Black";
			document.getElementById("nowStoneImg").src="resources/img/bigBlack.png";
		}
		else{
			document.getElementById("color").value="White";
			document.getElementById("nowStoneImg").src="resources/img/bigWhite.png";
		}
		
		document.getElementById("Black").setAttribute("hidden","");
		document.getElementById("White").setAttribute("hidden","");
		document.getElementById("Delete").setAttribute("hidden","");
		document.getElementById("modeList").setAttribute("disabled","disabled");
		
		
		document.getElementById("nowStone").removeAttribute("hidden","");
		if(mode==2||mode==3){
			document.getElementById("confirm").setAttribute("onclick","stop()");
			document.getElementById("confirm").innerHTML="정지";
			helpMeAI();
		}
		else{
			document.getElementById("pass").removeAttribute("hidden","");
			document.getElementById("undo").removeAttribute("hidden","");
		}

	}
	
	function undo() {	//되돌리기
		var c=document.getElementById("color").value;
		var mode=document.getElementById("mode").value;
		
		var data={
				mode:mode
		};
		 $.ajax({
		        type:"GET",
		        url:"undo",
		        data : data,
		        success: function(result){		//putStone과 마찬가지로 result=updateList 보드판의 돌들 업데이트 해줌
		        	for(var i=0;i<result.length;i++){
		        		var img=document.getElementById(result[i][0]);
		        		if(result[i][1]=="1")
		        			img.src="resources/img/Black.png";
		        		else if(result[i][1]=="2")
		        			img.src="resources/img/White.png";
		        		else
		        			img.src="";
		        	}
		        	if(result.length>0){		//플레이 모드에서 0~1(AI와 사활)은 돌이 두개가 되돌려지고 4~5(혼자 사활)은 돌이 한개가 되돌려짐.
			        	if (mode == 4||mode==5){
			        		if(c=="Black"){
			        			document.getElementById("color").value="White";
			        			document.getElementById("nowStoneImg").src="resources/img/bigWhite.png";
			        		}
			        		else if(c=="White"){
			        			document.getElementById("color").value="Black";
			        			document.getElementById("nowStoneImg").src="resources/img/bigBlack.png";
			        		}
			        	}
		        	}
		        }
		        
		    });	
	}
	
	function pass() {		//한수 넘기는 기능
		putStone(-1);
	}
	
	// 구글 api 연동
	
	//로그인
	function _onSignInCallback(googleUser){
		var profile = googleUser.getBasicProfile();
        var data={
				name:profile.getName(),
				email:profile.getEmail()
		};
        $.ajax({
	        type:"GET",
	        url:"login",
	        data : data,
	        success: function(){	
	        	   $('#signIn').hide();
	               $('#signOut').show();
	               document.getElementById("userName").innerHTML=profile.getName();
	        } 
	    });	
        
	}
	
	//로그아웃
	function logOut() {
        console.log("on disconnect");
        var auth2 = gapi.auth2.getAuthInstance();
        if (!auth2.isSignedIn.get()) {
            setMessage("Not signed in, cannot disconnect");
            return;
        }
        auth2.disconnect();       
        $.ajax({
	        type:"GET",
	        url:"logout",
	        success: function(){	
	        	   $('#signIn').show();
	               $('#signOut').hide();
	               document.getElementById("userName").innerHTML="";
	        } 
	    });	
        
    }
	
	//AI와 두기
	function PlayWithComputerMode(map){
		var c=document.getElementById("color").value;
		if(c=="Black"){
			map.stone="black";
		}
		else{
			map.stone="white";
		}
		console.log(map);
	
		 $.ajax({
		        type:"POST",
		        url:"http://ec2-18-224-181-233.us-east-2.compute.amazonaws.com:8080/LifeAndDeath/android/test",
		        dataType : "json",
		        data:map,
		        success: function(s){
		        	console.log(s.result);
		        	if(map[s.result]!=null){
		        		PlayWithComputerMode(map)
		        	}
		        	else{
		        		putStoneAI(s.result);
		        	}
		        } 
		    });	
	}
	
	function helpMeAI(){
		var mode=document.getElementById("mode").value;
		for(var i=0; i<360;i++){
			document.getElementById("b"+i).setAttribute("disabled","disabled");
		}
		 $.ajax({
		        type:"GET",
		        url:"putStone",
		        data :{
						Num:-1,
						color:-1,
						mode:mode
				},
		        success: function(result){//result = updateList 컨트롤러 에서 받아온 리스트를 통해 보드판의 돌들을 업데이트 해줌
		        
		        	PlayWithComputerMode(result.hashMap);
		        	
		        }
		        	
		    });
	}
	
	function putStoneAI(num){
		var c=document.getElementById("color").value;
		var mode=document.getElementById("mode").value;
		var color
		
		if(c=="Black")
			color=1;
		else if(c=="White")
			color=2;
		
		var data={
				Num:num,
				color:color,
				mode:mode
		};
		 $.ajax({
		        type:"GET",
		        url:"putStone",
		        data : data,
		        success: function(result){	//result = updateList 컨트롤러 에서 받아온 리스트를 통해 보드판의 돌들을 업데이트 해줌
		        	var list=result.list;
		        	var map=result.hashMap;
		        	if(list.length==0){
		        		PlayWithComputerMode(map)
		        	}
		        	else{
			        	for(var i=0;i<list.length;i++){
			        		var img=document.getElementById(list[i][0]);
			        		
			        		if(list[i][1]=="1")
			        			img.src="resources/img/Black.png";
			        		else if(list[i][1]=="2")
			        			img.src="resources/img/White.png";
			        		else
			        			img.src="";
			        	}
			        
			        	if(list.length>0&&list[list.length-1][1]!="0"&&mode!=-1){//	편집모드가 아닐시에 돌색깔 자동 변경
			        		
			        		if(c=="Black"){
			        			document.getElementById("color").value="White";
			        			document.getElementById("nowStoneImg").src="resources/img/bigWhite.png";
			        		}
			        		else{
			        			document.getElementById("color").value="Black";
			        			document.getElementById("nowStoneImg").src="resources/img/bigBlack.png";
			        		}
			        	}
		        	}
		        	if(mode==2||mode==3){
		        		pause(500);
		        		if(document.getElementById("stop").value==0)
		        			PlayWithComputerMode(result.hashMap);
		        	}
		        }		        
		    });
		
	}
	function pause(numberMillis) {
	     var now = new Date();
	     var exitTime = now.getTime() + numberMillis;


	     while (true) {
	          now = new Date();
	          if (now.getTime() > exitTime)
	              return;
	     }
	}
	
	function stop(){
		document.getElementById("stop").value=1;
		document.getElementById("confirm").setAttribute("onclick","start()");
		document.getElementById("confirm").innerHTML="다시 시작";
	}
	function start(){
		document.getElementById("stop").value=0;
		document.getElementById("confirm").setAttribute("onclick","stop()");
		document.getElementById("confirm").innerHTML="정지";
		helpMeAI();
	}
		

	</script>
</body>
</html>
