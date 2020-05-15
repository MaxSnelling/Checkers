package Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.postgresql.util.PSQLException;

import Game.Board;
import Game.Profile;

/**
 * Provides all database queries which insert data
 * into tables
 * @author Max Snelling
 * @version 5/5/20
 */
public class DatabaseInsert {
	
	/**
	 * Adds profile data from sign up into users table.
	 * @param profile	new profile data to be added to database
	 */
	public static void addProfile(Profile profile) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement addProfileStatement = connection.prepareStatement(
					"INSERT INTO users(username, first_name, last_name, password, date_of_birth, email_address) VALUES (?,?,?,?,?,?)");
			
			addProfileStatement.setString(1, profile.getUsername());
			addProfileStatement.setString(2, profile.getFirstName());
			addProfileStatement.setString(3, profile.getLastName());
			addProfileStatement.setString(4, profile.getPassword());
			addProfileStatement.setDate(5, Date.valueOf(profile.getDateOfBirth()));
			addProfileStatement.setString(6, profile.getEmailAddress());			
			addProfileStatement.execute();			
		} catch (IOException | SQLException e) {
		}
	}
	
	/**
	 * Creates a new game in games database and sets start time
	 * as current time of creation. Gets the game ID as an increment
	 * of the previous game ID and returns it in the board object.
	 * @return	new game with database game ID
	 */
	public static Board createGame() {
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement createGameStatement = connection.prepareStatement(
					"INSERT INTO games(start_time) VALUES (?)");				
			
			Timestamp gameStartTime = new Timestamp(System.currentTimeMillis());
			createGameStatement.setTimestamp(1, gameStartTime);
			createGameStatement.execute();				
			
			PreparedStatement getGameIDStatement = connection.prepareStatement("SELECT MAX(game_ID) FROM games");				
			ResultSet getGameIDResult = getGameIDStatement.executeQuery();
			getGameIDResult.next();
			int newGameID = getGameIDResult.getInt("max");			
			return new Board(newGameID);				
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * When a user joins a game then the connection is shown 
	 * on the user_games table. Adds new row with just the ID
	 * of the user and the game.
	 * @param userID	user ID of user joining game
	 * @param gameID	game ID of game the user is joining
	 */
	public static void addUserGame(int userID, int gameID) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();			
			PreparedStatement insertUserGameStatement = connection.prepareStatement(
					"INSERT INTO user_games(user_ID, game_ID) VALUES (?,?)");
			insertUserGameStatement.setInt(1, userID);
			insertUserGameStatement.setInt(2, gameID);			
			insertUserGameStatement.execute();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

}
