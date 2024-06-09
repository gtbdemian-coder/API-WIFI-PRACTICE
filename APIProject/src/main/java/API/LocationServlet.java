package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

//서버에서 현재위치 데이터를 가져와 DB에 저장하는 클래스
@WebServlet("/LocationServlet")
public class LocationServlet extends HttpServlet {
	
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
        // 서버에 요청하여 JSON데이터를 읽어오는 코드
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }

        // JSON 데이터 파싱애 관한 코드
        String jsonString = jsonBuffer.toString();
        Gson gson = new Gson();
        LocationGetterSetter location = gson.fromJson(jsonString, LocationGetterSetter.class);
        
        double cLat = location.getLatitude();
        double cLnt = location.getLongitude();
        
        // 데이터가 올바로 입력되었는지 확인하기 위한 코드
        System.out.println("Latitude: " + cLat);
        System.out.println("Longitude: " + cLnt);


        
    	String url = "jdbc:mariadb://192.168.0.6:3306/apidb";
        String dbUserId = "apiuser1";
        String dbPassword = "zerobase";

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(url, dbUserId, dbPassword);

            String update = "UPDATE wifi_info SET C_LNT = ?, C_LAT = ?";
            
            preparedStatement = connection.prepareStatement(update);
            preparedStatement.setDouble(1, cLnt);
            preparedStatement.setDouble(2, cLat);

            int affected = preparedStatement.executeUpdate();

            if (affected > 0) {
                System.out.println(" 수정 성공 ");
            } else {
                System.out.println(" 수정 실패 ");
            }
            
            // 서버의 응답을 확인하기 위한 코드
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"status\":\"success\"}");
            
            

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {


            try {
                if(preparedStatement != null && !preparedStatement.isClosed()) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if(connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }   
    }
    
    // 서버의 응답을 확인하기 위한 코드
    private void sendSuccessResponse(HttpServletResponse response, double cLat, double cLnt) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":\"success\", \"latitude\":" + cLat + ", \"longitude\":" + cLnt + "}");
    }
    
    // 서버의 응답을 확인하기 위한 코드
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":\"error\", \"message\":\"" + message + "\"}");
    }
 
}

