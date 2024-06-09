package API;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//거리(DISTANCE)행의 UPDATE문을 위한 클래스
public class DatabaseUpdater {
	
	public void distanceUpdater() {
        //    
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

            String update = "UPDATE wifi_info "
            				+ "SET DISTANCE = ( "
            				+ "    6371 * 2 * ATAN2( "
            				+ "        SQRT( "
            				+ "            POW(SIN(RADIANS(LAT - C_LAT) / 2), 2) + "
            				+ "            COS(RADIANS(C_LAT)) * COS(RADIANS(37.561924)) * "
            				+ "            POW(SIN(RADIANS(LNT - C_LNT) / 2), 2) "
            				+ "        ), "
            				+ "        SQRT(1 - ( "
		            		+ "            POW(SIN(RADIANS(LAT - C_LAT) / 2), 2) + "
		            		+ "            COS(RADIANS(C_LAT)) * COS(RADIANS(37.561924)) * "
		            		+ "            POW(SIN(RADIANS(LNT - C_LNT) / 2), 2) "
		            		+ "        )) "
		            		+ "    ) "
		            		+ ")";
            
            preparedStatement = connection.prepareStatement(update);
            int affected = preparedStatement.executeUpdate();

            if (affected > 0) {
                System.out.println(" 수정 성공 ");
            } else {
                System.out.println(" 수정 실패 ");
            }
           

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

}
