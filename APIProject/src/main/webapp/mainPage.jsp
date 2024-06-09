<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="API.DatabaseUpdater" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>MAIN PAGE</title>
	<style>
		a:link {
			
		}
		table {
			width: 100%;
			border-collapse: collapse;
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
		
        .container {
            display: flex;
            align-items: center;
            gap: 5px;
            padding: 10px 0px;
        }
        .input-box {
            display: flex;
            align-items: center;
        }
        .input-box input {
            margin-left: 5px;
        }
        .button-box button {
            margin-left: 5px;
        }
        .center-bold {
        	text-align: center;
        	font-weight: bold;
        }
	</style>
	
    <script>
 	// 현재 위치를 가져오는 메소드
    function getLocation() {
        
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function(position) {
                var latitude = position.coords.latitude;
                var longitude = position.coords.longitude;
                
                document.getElementById('lat-input').value = latitude;
                document.getElementById('lnt-input').value = longitude;
                
                var locationData = {
                        latitude: latitude,
                        longitude: longitude
                };
                
                var jsonData = JSON.stringify(locationData);
                
                console.log(jsonData);
                
                sendLocationData(jsonData, latitude, longitude);

            }, function(error) {
                alert('위치를 가져올 수 없습니다: ' + error.message);
            });
        } else {
            alert('Geolocation을 지원하지 않는 브라우저입니다.');
        }
        
    }
    
    // 현재위치 정보를 서버로 보내는 메소드(*wifi_info 테이블용)
	function sendLocationData(jsonData, latitude, longitude) {
	    var xhr = new XMLHttpRequest();
	    xhr.open("POST", "LocationServlet", true);
	    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	    xhr.onreadystatechange = function() {
	        if (xhr.readyState === XMLHttpRequest.DONE) {
	            if (xhr.status === 200) {
	                try {
	                    var response = JSON.parse(xhr.responseText);
	                    if (response.status === "success") {
	                        updateDistance(latitude, longitude);
	                    }
	                } catch (e) {
	                    console.error("Invalid JSON response: " + xhr.responseText);
	                }
	            } else {
	                console.error("Request failed with status: " + xhr.status);
	            }
	        }
	    };
	    xhr.send(jsonData);
	}
    
    // 거리(DISTANCE)를 계산하기 위해 현재위치 정보를 서버로 보내는 메소드(*history 테이블용)
    function updateDistance(latitude, longitude) {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "UpdateDistanceServlet", true);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.onreadystatechange = function() {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    try {
                        var response = JSON.parse(xhr.responseText);
                        if (response.status === "success") {
                            alert(response.message);
                        }
                    } catch (e) {
                        console.error("Invalid JSON response: " + xhr.responseText);
                    }
                } else {
                    console.error("Request failed with status: " + xhr.status);
                }
            }
        };
        xhr.send("latitude=" + latitude + "&longitude=" + longitude);
    }
	
    // SQL SELECT문을 웹페이지에 출력하기 위한 메소드
    function nearWifi() {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", "NearWifiServlet", true);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.onreadystatechange = function() {
            if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
                var wifiData = JSON.parse(xhr.responseText);
                var tableBody = document.getElementById("wifiTableBody");
                tableBody.innerHTML = "";
                if (wifiData.length > 0) {
                    wifiData.forEach(function(row) {
                        var tr = document.createElement("tr");
                        tr.appendChild(createCell(row.DISTANCE));
                        tr.appendChild(createCell(row.X_SWIFI_MGR_NO));
                        tr.appendChild(createCell(row.X_SWIFI_WRDOFC));
                        tr.appendChild(createCell(row.X_SWIFI_MAIN_NM));
                        tr.appendChild(createCell(row.X_SWIFI_ADRES1));
                        tr.appendChild(createCell(row.X_SWIFI_ADRES2));
                        tr.appendChild(createCell(row.X_SWIFI_INSTL_FLOOR));
                        tr.appendChild(createCell(row.X_SWIFI_INSTL_TY));
                        tr.appendChild(createCell(row.X_SWIFI_INSTL_MBY));
                        tr.appendChild(createCell(row.X_SWIFI_SVC_SE));
                        tr.appendChild(createCell(row.X_SWIFI_CMCWR));
                        tr.appendChild(createCell(row.X_SWIFI_CNSTC_YEAR));
                        tr.appendChild(createCell(row.X_SWIFI_INOUT_DOOR));
                        tr.appendChild(createCell(row.X_SWIFI_REMARS3));
                        tr.appendChild(createCell(row.LNT));
                        tr.appendChild(createCell(row.LAT));
                        tr.appendChild(createCell(row.WORK_DTTM));
                        tableBody.appendChild(tr);
                    });
                } else {
                    var tr = document.createElement("tr");
                    var td = document.createElement("td");
                    td.setAttribute("colspan", "17");
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
    
    // <td></td>태그를 생성하고 그 안에 텍스트를 넣는 메소드
    function createCell(text) {
        var td = document.createElement("td");
        td.textContent = text !== null ? text : "";
        return td;
    }
   </script>
</head>
<body>
	<h1>와이파이 정보 구하기</h1>
	
	<a href="mainPage.jsp" target="_blank">홈</a> |
	<a href="history.jsp" target="_blank">위치 히스토리 목록</a> |
	<a href="searchPage.jsp" target="_blank">Open API 와이파이 정보 가져오기</a> 
	
    <div class="container">
        <div class="input-box">
            LAT: <input id="lat-input" type="text">
        </div>
        <div class="input-box">
            LNT: <input id="lnt-input" type="text">
        </div>
        <div class="button-box">
            <button onclick="getLocation()">내 위치 가져오기</button>
        </div>
        <div class="button-box">
            <button onclick="nearWifi()">근처 WIFI 정보 보기</button>
        </div>
    </div>
    

	<div style="overflow-x: auto;">
	<table>
		<thead>
			<tr>
				<th>거리(km)</th>
				<th>관리번호</th>
				<th>자치구</th>
				<th>와이파이명</th>
				<th>도로명주소</th>
				<th>상세주소</th>
				<th>설치위치(층)</th>
				<th>설치유형</th>
				<th>설치기관</th>
				<th>서비스구분</th>
				<th>망종류</th>
				<th>설치년도</th>
				<th>실내외구분</th>
				<th>WIFI접속환경</th>
				<th>X좌표</th>
				<th>Y좌표</th>
				<th>작업일자</th>		
			</tr>
		</thead>
        <tbody id="wifiTableBody">
            <tr>
                <td colspan="17" class="center-bold">위치 정보를 입력한 후에 조회해 주세요.</td>
            </tr>
        </tbody>	
		</tbody>
	</table>
	</div>

</body>
</html>