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

//최종 사용

@WebServlet("/HistoryServlet2")
public class HistoryServlet extends HttpServlet {
		
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

            String sql =" select ID, LNT, LAT, S_DATE, REF "
            			+ " from history "
            			+ " order by ID desc ";

            stat = connection.createStatement();
            rs = stat.executeQuery(sql);
            
            // JSON 배열 생성
            JSONArray jsonArray = new JSONArray();
            
            
            
            while (rs.next()) {
            	JSONObject jsonObject = new JSONObject();
                jsonObject.put("ID", rs.getDouble("ID"));
                jsonObject.put("LNT", rs.getString("LNT")); // 올바른 열 이름 사용
                jsonObject.put("LAT", rs.getString("LAT")); // 올바른 열 이름 사용
                jsonObject.put("S_DATE", rs.getString("S_DATE")); // 올바른 열 이름 사용
                jsonObject.put("REF", rs.getString("REF")); // 올바른 열 이름 사용
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
