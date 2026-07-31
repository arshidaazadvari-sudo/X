package server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/x_clone";
    private static final String USER = "postgres";
    private static final String PASSWORD = "arshida";

    private static Connection connection = null;

    private DatabaseConnection() {}

    public static Connection getConnection()throws SQLException {
        if (connection == null || connection.isClosed()){
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to database");
        }
        return connection;
    }

    public static void closeConnection (){
        try {
            if (connection != null && !connection.isClosed()){
                connection.close();
                System.out.println("Connection closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean testConnection(){
        try {
            getConnection();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
