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

//웹서버에서 현재위치 데이터를 가져와 DB에 저장하는 클래스(*최종 사용)

@WebServlet("/UpdateDistanceServlet")
public class UpdateDistanceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String latitudeStr = request.getParameter("latitude");
        String longitudeStr = request.getParameter("longitude");

        if (latitudeStr == null || longitudeStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Missing latitude or longitude parameter\"}");
            return;
        }

        try {
            double cLat = Double.parseDouble(latitudeStr);
            double cLnt = Double.parseDouble(longitudeStr);

            String url = "jdbc:mariadb://192.168.0.6:3306/apidb";
            String dbUserId = "apiuser1";
            String dbPassword = "zerobase";

            Class.forName("org.mariadb.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(url, dbUserId, dbPassword)) {
                if (connection != null) {
                    System.out.println("Database connected successfully");

                    String updateQuery = "UPDATE wifi_info "
                            + "SET DISTANCE = ( "
                            + "    6371 * 2 * ATAN2( "
                            + "        SQRT( "
                            + "            POW(SIN(RADIANS(LAT - ? ) / 2), 2) + "
                            + "            COS(RADIANS(?)) * COS(RADIANS(LAT)) * "
                            + "            POW(SIN(RADIANS(LNT - ? ) / 2), 2) "
                            + "        ), "
                            + "        SQRT(1 - ( "
                            + "            POW(SIN(RADIANS(LAT - ? ) / 2), 2) + "
                            + "            COS(RADIANS(?)) * COS(RADIANS(LAT)) * "
                            + "            POW(SIN(RADIANS(LNT - ? ) / 2), 2) "
                            + "        )) "
                            + "    ) "
                            + ")";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                        if (preparedStatement != null) {
                            preparedStatement.setDouble(1, cLat);
                            preparedStatement.setDouble(2, cLat);
                            preparedStatement.setDouble(3, cLnt);
                            preparedStatement.setDouble(4, cLat);
                            preparedStatement.setDouble(5, cLat);
                            preparedStatement.setDouble(6, cLnt);

                            int affected = preparedStatement.executeUpdate();

                            if (affected > 0) {
                                System.out.println("Update successful");
                                response.setContentType("application/json");
                                response.setCharacterEncoding("UTF-8");
                                response.getWriter().write("{\"status\":\"success\", \"message\":\"데이터베이스 업데이트 완료\"}");
                            } else {
                                System.out.println("Update failed");
                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                response.getWriter().write("{\"status\":\"error\", \"message\":\"No rows affected\"}");
                            }
                        } else {
                            System.out.println("Failed to prepare statement");
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            response.getWriter().write("{\"status\":\"error\", \"message\":\"Failed to prepare statement\"}");
                        }
                    }
                } else {
                    System.out.println("Failed to connect to database");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\":\"error\", \"message\":\"Failed to connect to database\"}");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Invalid latitude or longitude value\"}");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Database driver not found\"}");
        }
    }
}



