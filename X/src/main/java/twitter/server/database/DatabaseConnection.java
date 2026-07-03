package twitter.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/x_clone";
    private static final String USER = "ap_user";
    private static final String PASSWORD = "Zahra1386";

    private DatabaseConnection() {}

    public static Connection getConnection()throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
