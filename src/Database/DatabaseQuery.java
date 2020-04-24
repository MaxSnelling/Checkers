package Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.sun.java.accessibility.util.GUIInitializedListener;

import Game.Board;
import Game.Profile;

public class DatabaseQuery {
	
	public static Profile logIn(Profile profile) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement logInStatement = connection.prepareStatement(
					"SELECT username, first_name, last_name, password, date_of_birth, email_address FROM users WHERE username = ?");
			
			logInStatement.setString(1, profile.getUsername());
			
			ResultSet logInResult = logInStatement.executeQuery();
			logInResult.next();
			
			String profileUsername = logInResult.getString("username");
			String profileFirstName = logInResult.getString("first_name");
			String profileLastName = logInResult.getString("last_name");
			String profilePassword = logInResult.getString("password");
			Date profileDateofBirth = logInResult.getDate("date_of_birth");
			String profileEmailAddress = logInResult.getString("email_address");
			
			Profile serverProfile = new Profile(profileUsername, profileFirstName, profileLastName,
					profilePassword, profileDateofBirth, profileEmailAddress);
			
			return serverProfile;		
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return null;	
		}
		
	}
	
	public static boolean passwordCheck(Profile profile) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement passwordCheckStatement = connection.prepareStatement(
					"SELECT password FROM users WHERE username = ?");
			
			passwordCheckStatement.setString(1, profile.getUsername());
			ResultSet passwordCheckResult = passwordCheckStatement.executeQuery();
			passwordCheckResult.next();
			String serverProfilePassword = passwordCheckResult.getString("password");
			
			return profile.getPassword().equals(serverProfilePassword);			
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	public static boolean usernameCheck(String username) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement usernameCheckStatement = connection.prepareStatement(
					"SELECT count(*) FROM users WHERE username = '" + username + "'");
			
			ResultSet usernameCheckResult = usernameCheckStatement.executeQuery();
			usernameCheckResult.next();
			int queryCount = usernameCheckResult.getInt("count");
			
			return queryCount == 0;			
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	public static void addPlayer(Board board) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			
			PreparedStatement getPlayer1Statement = connection.prepareStatement(
					"SELECT player1 FROM games WHERE game_ID = '" + board.getGameID() + "'");
			ResultSet getPlayer1Result = getPlayer1Statement.executeQuery();
			getPlayer1Result.next();
			String player1 = getPlayer1Result.getString("player1");	
			
			PreparedStatement updateGameStatement = connection.prepareStatement(
					"UPDATE games SET player1=?, player2=? WHERE game_id=?");			
			updateGameStatement.setString(1, board.getPlayer1());
			updateGameStatement.setString(2, board.getPlayer2());
			updateGameStatement.setInt(3, board.getGameID());			
			updateGameStatement.execute();		

			if(player1 == null) {
				int player1ID = getUserID(board.getPlayer1());
				DatabaseInsert.addUserGame(player1ID, board.getGameID());
			} else {
				int player2ID = getUserID(board.getPlayer2());
				DatabaseInsert.addUserGame(player2ID, board.getGameID());
			}			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}	
	}
	
	public static int getUserID(String username) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();			
			PreparedStatement getUserIDStatement = connection.prepareStatement(
					"SELECT user_ID FROM users WHERE username = '" + username + "'");
			ResultSet getUserIDResult = getUserIDStatement.executeQuery();
			getUserIDResult.next();
			return getUserIDResult.getInt("user_ID");
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
}
