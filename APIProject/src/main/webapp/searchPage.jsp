<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="API.APIFetcher" %>   
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>SEARCH COMPLETE</title>
<style>
        body, html{
            text-align: center;
            font-size: 24px;
            font-weight: bold;
            padding: 5px;
        }
        .home{
        	font-size: 13px;
        	font-weight: normal;
        	padding: 10px;
        	
        }
</style>
</head>
<body>
	<%
	APIFetcher fe = new APIFetcher();
    for (int c = 0; c < 24600; c += 100) {
            fe.askData(c, c + 99);
    }
	int listTotalCount = fe.getListTotalCount();
	out.println(listTotalCount + "개의 WIFI 정보를 정상적으로 저장하였습니다.");
	%>
	<div class="home" id="home">
	<a href="mainPage.jsp" target="_blank">홈 으로 가기</a>
	</div> 
</body>
</html>