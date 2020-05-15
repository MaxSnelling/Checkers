package Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connects to Checkers database on host system.
 * Database structure found in Checkers.sql
 * @author Max Snelling
 * @version 5/5/20
 */
public class DatabaseConnect {
	/**
	 * Connects to the system PostgreSQL Checkers database 
	 */
	public static Connection connectDatabase() throws IOException, SQLException {
		String url = "jdbc:postgresql://localhost/checkers";
		String username = "checkers";
		String password = "password";
		return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) throws IOException {
        try {
			connectDatabase();
		} catch (IOException | SQLException e) {
			System.out.println("Database Connection Failed");
		}
        System.out.println("Database Connection Created");
    }
}
