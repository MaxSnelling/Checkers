package Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import Game.Profile;

public class DatabaseInsert {
	
	public static void addProfile(Profile profile) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement addProfileStatement = connection.prepareStatement(
					"INSERT INTO users(username, first_name, last_name, password, date_of_birth, email_address) VALUES (?,?,?,?,?,?)");
			
			addProfileStatement.setString(1, profile.getUsername());
			addProfileStatement.setString(2, profile.getFirstName());
			addProfileStatement.setString(3, profile.getLastName());
			addProfileStatement.setString(4, profile.getPassword());
			addProfileStatement.setDate(5, profile.getDateOfBirth());
			addProfileStatement.setString(6, profile.getEmailAddress());
			
			addProfileStatement.execute();
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		
		
		
		
		
	
	}

}
