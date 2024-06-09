package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

// wifi_info 테이블의 select문을 작동하기 위한 클래스
@WebServlet("/NearWifiServlet")
public class NearWifiServlet extends HttpServlet {
		
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String url = "jdbc:mariadb://192.168.0.6:3306/apidb";
        String dbUserId = "apiuser1";
        String dbPassword = "zerobase";

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        PrintWriter out = response.getWriter();
        Connection connection = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(url, dbUserId, dbPassword);

            String sql =" select DISTANCE, X_SWIFI_MGR_NO, X_SWIFI_WRDOFC, X_SWIFI_MAIN_NM, X_SWIFI_ADRES1, X_SWIFI_ADRES2, X_SWIFI_INSTL_FLOOR, X_SWIFI_INSTL_TY, X_SWIFI_INSTL_MBY, X_SWIFI_SVC_SE, X_SWIFI_CMCWR,X_SWIFI_CNSTC_YEAR, X_SWIFI_INOUT_DOOR, X_SWIFI_REMARS3, LNT, LAT, WORK_DTTM "
            			+ " from wifi_info "
            			+ " order by DISTANCE asc "
            			+ " limit 20 ";

            stat = connection.createStatement();
            rs = stat.executeQuery(sql);
            
            // JSON 배열 생성
            JSONArray jsonArray = new JSONArray();
            
            
            while (rs.next()) {
            	JSONObject jsonObject = new JSONObject();
                jsonObject.put("DISTANCE", rs.getDouble("DISTANCE"));
                jsonObject.put("X_SWIFI_MGR_NO", rs.getString("X_SWIFI_MGR_NO")); 
                jsonObject.put("X_SWIFI_WRDOFC", rs.getString("X_SWIFI_WRDOFC")); 
                jsonObject.put("X_SWIFI_MAIN_NM", rs.getString("X_SWIFI_MAIN_NM")); 
                jsonObject.put("X_SWIFI_ADRES1", rs.getString("X_SWIFI_ADRES1")); 
                jsonObject.put("X_SWIFI_ADRES2", rs.getString("X_SWIFI_ADRES2")); 
                jsonObject.put("X_SWIFI_INSTL_FLOOR", rs.getString("X_SWIFI_INSTL_FLOOR")); 
                jsonObject.put("X_SWIFI_INSTL_TY", rs.getString("X_SWIFI_INSTL_TY")); 
                jsonObject.put("X_SWIFI_INSTL_MBY", rs.getString("X_SWIFI_INSTL_MBY")); 
                jsonObject.put("X_SWIFI_SVC_SE", rs.getString("X_SWIFI_SVC_SE")); 
                jsonObject.put("X_SWIFI_CMCWR", rs.getString("X_SWIFI_CMCWR")); 
                jsonObject.put("X_SWIFI_CNSTC_YEAR", rs.getInt("X_SWIFI_CNSTC_YEAR")); 
                jsonObject.put("X_SWIFI_INOUT_DOOR", rs.getString("X_SWIFI_INOUT_DOOR")); 
                jsonObject.put("X_SWIFI_REMARS3", rs.getString("X_SWIFI_REMARS3")); 
                jsonObject.put("LNT", rs.getDouble("LNT")); 
                jsonObject.put("LAT", rs.getDouble("LAT")); 
                jsonObject.put("WORK_DTTM", rs.getString("WORK_DTTM")); 
                jsonArray.put(jsonObject);
            }
            
            
            out.print(jsonArray.toString());
            out.flush();

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } finally {

            try {
                if(rs != null && !rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


            try {
                if(connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
	
	}
}
