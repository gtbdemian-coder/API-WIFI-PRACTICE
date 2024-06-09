package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;

//API서버로부터 데이터 총 개수를 가져와 변수에 저장하는 클래스(*최종 사용)
public class NumberFetcher {
    public static void askNumber(int a, int b) throws IOException, SQLException {
    		
    		StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088"); /*URL*/
            urlBuilder.append("/" + URLEncoder.encode("7a614f775867746234374346434a4c", "UTF-8")); /*인증키 (sample사용시에는 호출시 제한됩니다.)*/
            urlBuilder.append("/" + URLEncoder.encode("json", "UTF-8")); /*요청파일타입 (xml,xmlf,xls,json) */
            urlBuilder.append("/" + URLEncoder.encode("TbPublicWifiInfo", "UTF-8")); /*서비스명 (대소문자 구분 필수입니다.)*/
            urlBuilder.append("/" + a); /*요청시작위치 (sample인증키 사용시 5이내 숫자)*/
            urlBuilder.append("/" + b); /*요청종료위치(sample인증키 사용시 5이상 숫자 선택 안 됨)*/
            // 상위 5개는 필수적으로 순서바꾸지 않고 호출해야 합니다.
            
            // API데이터 요청을 위한 URL 생성
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode()); /* 연결 자체에 대한 확인이 필요하므로 추가합니다.*/
            BufferedReader rd;

            // Response Code 확인: 요청이 잘 들어갔는지 확인하기 위한 절차
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            String apiResponse = sb.toString();
            System.out.println(apiResponse);
            
            // JSON 파싱을 위한 코드
            JSONObject jsonObject = new JSONObject(apiResponse);
            JSONArray rows = jsonObject.getJSONObject("TbPublicWifiInfo").getJSONArray("row");
            
            // 로컬DB 접속
            String dbUrl = "jdbc:mariadb://192.168.0.6:3306/apidb";
            String dbUserId = "apiuser1";
            String dbPassword = "zerobase";
            Connection connection = DriverManager.getConnection(dbUrl, dbUserId, dbPassword);
            
            //파싱된 JSON을 로컬DB에 저장
            String insert = "INSERT INTO wifi_info (X_SWIFI_MGR_NO, X_SWIFI_WRDOFC, X_SWIFI_MAIN_NM, X_SWIFI_ADRES1, X_SWIFI_ADRES2, X_SWIFI_INSTL_FLOOR, X_SWIFI_INSTL_TY, X_SWIFI_INSTL_MBY, X_SWIFI_SVC_SE, X_SWIFI_CMCWR, X_SWIFI_CNSTC_YEAR, X_SWIFI_INOUT_DOOR, X_SWIFI_REMARS3, LNT, LAT, WORK_DTTM) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            
            for (int i = 0; i < rows.length(); i++) {
                JSONObject row = rows.getJSONObject(i);
                preparedStatement.setString(1, row.getString("X_SWIFI_MGR_NO"));
                preparedStatement.setString(2, row.getString("X_SWIFI_WRDOFC"));
                preparedStatement.setString(3, row.getString("X_SWIFI_MAIN_NM"));
                preparedStatement.setString(4, row.getString("X_SWIFI_ADRES1"));
                preparedStatement.setString(5, row.getString("X_SWIFI_ADRES2"));
                preparedStatement.setString(6, row.getString("X_SWIFI_INSTL_FLOOR"));
                preparedStatement.setString(7, row.getString("X_SWIFI_INSTL_TY"));
                preparedStatement.setString(8, row.getString("X_SWIFI_INSTL_MBY"));
                preparedStatement.setString(9, row.getString("X_SWIFI_SVC_SE"));
                preparedStatement.setString(10, row.getString("X_SWIFI_CMCWR"));
                preparedStatement.setInt(11, row.getInt("X_SWIFI_CNSTC_YEAR"));
                preparedStatement.setString(12, row.getString("X_SWIFI_INOUT_DOOR"));
                preparedStatement.setString(13, row.getString("X_SWIFI_REMARS3"));
                preparedStatement.setDouble(14, row.getDouble("LNT"));
                preparedStatement.setDouble(15, row.getDouble("LAT"));
                preparedStatement.setString(16, row.getString("WORK_DTTM"));

                preparedStatement.executeUpdate();
            }
            
  
    	
    			
    }
        
            
}

