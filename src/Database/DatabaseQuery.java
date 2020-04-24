package Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Game.Profile;

public class DatabaseQuery {
	
	public static Profile logIn(Profile profile) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement addProfileStatement = connection.prepareStatement(
					"SELECT username, first_name, last_name, password, date_of_birth, email_address FROM users WHERE username = ?");
			
			addProfileStatement.setString(1, profile.getUsername());
			
			ResultSet queryResult = addProfileStatement.executeQuery();
			queryResult.next();
			
			String profileUsername = queryResult.getString("username");
			String profileFirstName = queryResult.getString("first_name");
			String profileLastName = queryResult.getString("last_name");
			String profilePassword = queryResult.getString("password");
			Date profileDateofBirth = queryResult.getDate("date_of_birth");
			String profileEmailAddress = queryResult.getString("email_address");
			
			Profile serverProfile = new Profile(profileUsername, profileFirstName, profileLastName,
					profilePassword, profileDateofBirth, profileEmailAddress);
			
			return serverProfile;		
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return profile;	
		}
		
	}
	
	public static boolean passwordCheck(Profile profile) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement addProfileStatement = connection.prepareStatement(
					"SELECT password FROM users WHERE username = ?");
			
			addProfileStatement.setString(1, profile.getUsername());
			ResultSet queryResult = addProfileStatement.executeQuery();
			queryResult.next();
			String serverProfilePassword = queryResult.getString("password");
			
			return profile.getPassword().equals(serverProfilePassword);			
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	public static boolean userNameCheck(String username) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement addProfileStatement = connection.prepareStatement(
					"SELECT count(*) FROM users WHERE username = '" + username + "'");
			
			ResultSet queryResult = addProfileStatement.executeQuery();
			queryResult.next();
			int queryCount = queryResult.getInt("count");
			
			return queryCount == 0;			
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return false;
		}		
	}
}
