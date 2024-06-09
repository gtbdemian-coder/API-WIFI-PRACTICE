package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

// history테이블의 insert문을 수행하기 위한 클래스
@WebServlet("/HistoryServlet1")
public class HistoryFetcher extends HttpServlet {
	
	public double sLat;
	public double sLnt;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }
        
        String jsonString = jsonBuffer.toString();
        Gson gson = new Gson();
        LocationGetterSetter location = gson.fromJson(jsonString, LocationGetterSetter.class);
        
        sLat = location.getLatitude();
        sLnt = location.getLongitude();
        
        
        System.out.println("Latitude: " + sLat);
        System.out.println("Longitude: " + sLnt);
        
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
        ResultSet rs = null;

        try {

        	Timestamp ts = Timestamp.valueOf(LocalDateTime.now());
        	
            connection = DriverManager.getConnection(url, dbUserId, dbPassword);

            String sql = " insert into history(LNT, LAT, S_DATE) "
            			 +" values(?, ?, ?) ";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, sLat);
            preparedStatement.setDouble(2, sLnt);
            preparedStatement.setTimestamp(3, ts);

            int affected = preparedStatement.executeUpdate();

            if (affected > 0) {
                System.out.println(" 저장 성공 ");
            } else {
                System.out.println(" 저장 실패 ");
            }
        }
        catch (SQLException e) {
                e.printStackTrace();

        } finally {

                try {
                    if(rs != null && !rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

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
	}
