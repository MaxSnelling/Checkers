package Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnect {

	public static Connection connectDatabase() throws IOException {
        String url = "jdbc:postgresql://localhost/checkers";
        String username = "checkers";
        String password = "password";
        
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.println("Database Connection Failed");
        }

        return null;
    }

    public static void main(String[] args) throws IOException {
        connectDatabase();
        System.out.println("Database Connection Created");
    }
}
