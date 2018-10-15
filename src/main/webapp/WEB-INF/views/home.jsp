<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%> 
<html>
<head>
<link href="resources/css/bootstrap.min.css" rel="stylesheet">
<link href="resources/css/main.css" rel="stylesheet">
<script
   src="http://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.js"></script>
   
	<title>Home</title>
	
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
	}
	.float-left li a{
		border:1px solid orange; 
		margin-top:20px; 
		border-radius: 4px; 
		color:orange;				
		width: 100px;
	 	height: 45px; 
	 	font: 14px bold; 
	 	font-weight: 700; 
	 	padding:12px 30px;
	}
	.float-left li a:hover {
		background: orange;
		color: #fff;
	}
	
	</style>
	
</head>
<body>
<div class="container">
	<input type="hidden" id="color" name="color" value="Black">
	<input type="hidden" id="mode" name="mode" value="-1">
	<div class="col-md-3">
	<button id="whiteBucket" style="margin-top: 400px; background: transparent;" onclick="bucket('White')"><img id="whiteBucketImg" src="/LifeAndDeth/resources/img/WhiteBucket.png"></button>
	</div>
	
	
	<div class="col-md-7" style="text-align: center;">
	<h1 style="font-weight: bold; font-size: 30ox;">
		死活 BOT
	</h1>
	
	<table background="/LifeAndDeth/resources/img/Board1.jpg" style="background-repeat: no-repeat; 
			background-position: center; margin-left: auto; margin-right: auto;">
	<% for(int i=0; i<19; i++){
		%>
		<tr>
		<td class="space"></td>
		<% 
		for(int j=0; j<19; j++){
			%>
			<td><button name="b<%=j+i*19%>"onclick="putStone(<%=j+i*19%>)" ><img id="<%=j+i*19%>"  src=""></button> </td>
			<%}
		%>
		<td class="space"></td>
		</tr>
		<%
	}
	%>
	</table>
	</div>
		
	<div class="col-md-2">
		<div class="form-group" style="margin-top: 100px;">
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
						<li><a id="confirm" onclick="confirm()"> 확인</a>  <a id="reset" onclick="reset()"> 초기화</a></li>
					</ul>
			</div>
		</div>
		<button id="blackBucket" onclick="bucket('Black')" style="margin-top: 220px;  background: transparent;"><img id="blackBucketImg" src="/LifeAndDeth/resources/img/BlackBucket.png"></button>
		</div>
	</div>
	<script type="text/javascript">
	function putStone(idNum){
		var c=document.getElementById("color").value;
		var mode=document.getElementById("mode").value;
		var color
		if(c=="Black")
			color=1;
		else
			color=2;
		var dat={
				Num:idNum,
				color:color,
				mode:mode
		};
		 $.ajax({
		        type:"GET",
		        url:"/LifeAndDeth/putStone",
		        data : dat,
		        success: function(result){
		        	for(var i=0;i<result.length;i++){
		        		var img=document.getElementById(result[i][0]);
		        		if(result[i][1]=="1")
		        			img.src="/LifeAndDeth/resources/img/Black.png";
		        		else if(result[i][1]=="2")
		        			img.src="/LifeAndDeth/resources/img/White.png";
		        	}
		        	if(result.length>0&&result[result.length-1][1]!="0"&&mode!=-1){
		        		if(c=="Black")
		        			document.getElementById("color").value="White";
		        		else
		        			document.getElementById("color").value="Black";
		        	}
		        }
		        
		    });		
		
	}
	function bucket(color) {
		var c=document.getElementById("color");
		c.value=color;
	}
	function confirm() {
		var mode=document.getElementById("modeList").selectedIndex;
		document.getElementById("mode").value=mode;
		if(mode%2==0)
			document.getElementById("color").value="Black";
		else
			document.getElementById("color").value="White";
		document.getElementById("blackBucket").type="hidden";
		document.getElementById("whiteBucket").type="hidden";
		document.getElementById("blackBucketImg").src="";
		document.getElementById("whiteBucketImg").src="";
	}

	
	</script>
</body>
</html>
