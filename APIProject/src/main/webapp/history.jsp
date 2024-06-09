<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="API.HistoryFetcher" %>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>history</title>
	<style>
		a:link {
		
		}
		table {
			width: 100%;
			border-collapse: collapse;
			padding: 15px;
			margin-top: 10px;
		}
		th {
			border: 1px groove;
			background-color: green;
			color: white;
			height: 20px;
			padding: 15px;
			font-size: 12px;
		}
		td {
			border: 1px groove;
			height: 20px;
			padding: 15px;
			font-size: 12px;
		}
		
		tr:nth-child(even) {background-color: #f2f2f2;}
		

        .input-box {
            display: flex;
            align-items: center;
        }
        .center-bold {
        	text-align: center;
        	font-weight: bold;
        }
	</style>
	
    <script>
    function getLocation(callback) {
        // 사용자의 현재 위치를 가져오는 예제 (Geolocation API 사용)
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function(position) {
                var latitude = position.coords.latitude;
                var longitude = position.coords.longitude;
                
                
                var locationData = {
                        latitude: latitude,
                        longitude: longitude
                };
                
                var jsonData = JSON.stringify(locationData);
                
                console.log(jsonData);
                
                sendLocationData(jsonData, callback);
                

            }, function(error) {
                alert('위치를 가져올 수 없습니다: ' + error.message);
            });
        } else {
            alert('Geolocation을 지원하지 않는 브라우저입니다.');
        }
        
    }
    
    // 사용자의 현재위치 정보를 서버로 보내는 메소드(*서버에서 DB로 보내기 위한 장치)
    function sendLocationData(jsonData, callback) {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "http://localhost:8080/APIProject/HistoryServlet1", true);
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        xhr.onreadystatechange = function() {
            if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
                console.log(xhr.responseText);
                if (callback) callback();
            } else if (xhr.readyState === XMLHttpRequest.DONE) {
                console.error("Request failed with status: " + xhr.status);
                if (callback) callback();
            }
        };
        xhr.send(jsonData);
    }
    

    
    function history() {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", "HistoryServlet2", true);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.onreadystatechange = function() {
            if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            	console.log("Response Text:", xhr.responseText);
                var historyData = JSON.parse(xhr.responseText);
                var tableBody = document.getElementById("historyBody");
                if (historyData.length > 0) {
                    historyData.forEach(function(row) {
                        var tr = document.createElement("tr");
                        tr.appendChild(createCell(row.ID));
                        tr.appendChild(createCell(row.LNT));
                        tr.appendChild(createCell(row.LAT));
                        tr.appendChild(createCell(row.S_DATE));
                        tr.appendChild(createCell(row.REF));
                        tableBody.appendChild(tr);
                    });
                } else {
                    var tr = document.createElement("tr");
                    var td = document.createElement("td");
                    td.setAttribute("colspan", "5");
                    td.classList.add("center-bold");
                    td.textContent = "조회 결과가 없습니다.";
                    tr.appendChild(td);
                    tableBody.appendChild(tr);
                }
            } else if (xhr.readyState === XMLHttpRequest.DONE) {
                console.error("Request failed with status: " + xhr.status);
            }
        };
        xhr.send();
    }
    
    function createCell(text) {
        var td = document.createElement("td");
        td.textContent = text !== null ? text : "";
        return td;
    }
    
    window.onload = function() {
        getLocation(history);
    };
    
   </script>
    
</head>
<body>

	<h1>위치 히스토리 목록</h1>
	
	<a href="mainPage.jsp" target="_blank">홈</a> |
	<a href="history.jsp" target="_blank">위치 히스토리 목록</a> |
	<a href="searchPage.jsp" target="_blank">Open API 와이파이 정보 가져오기</a> 
	
	<div style="overflow-x: auto;">
	<table>
		<thead>
			<tr>
				<th>ID</th>
				<th>X좌표</th>
				<th>Y좌표</th>
				<th>조회일자</th>
				<th>비고</th>
			</tr>
		</thead>
		<tbody id="historyBody">	
		</tbody>
	</table>
	</div>

</body>
</html>