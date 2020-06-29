package Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;

import org.postgresql.util.PSQLException;

import Game.Board;
import Game.Profile;
import Server.Command;

/**
 * Provides all data amend and acquire methods
 * @author Max Snelling
 * @version 5/5/20
 */
public class DatabaseQuery {
	
	/**
	 * Sets the status of a user as logged in on the database.
	 * Gets all the information related to that profile from the
	 * database and sends to back to the client.
	 * @param profile	profile object containing username/password input
	 * @return	the full database details of the user
	 */
	public static Profile logIn(Profile profile) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement logInStatement = connection.prepareStatement(
					"SELECT username, first_name, last_name, password, date_of_birth, email_address FROM users WHERE username = ?");
			PreparedStatement loggedInUpdateStatement = connection.prepareStatement(
					"UPDATE users SET logged_in = true WHERE username = ?");
			
			logInStatement.setString(1, profile.getUsername());
			loggedInUpdateStatement.setString(1, profile.getUsername());
			
			ResultSet logInResult = logInStatement.executeQuery();
			loggedInUpdateStatement.execute();
			logInResult.next();
			
			String profileUsername = logInResult.getString("username");
			String profileFirstName = logInResult.getString("first_name");
			String profileLastName = logInResult.getString("last_name");
			String profilePassword = logInResult.getString("password");
			LocalDate profileDateofBirth = logInResult.getDate("date_of_birth").toLocalDate();
			String profileEmailAddress = logInResult.getString("email_address");
			
			Profile serverProfile = new Profile(profileUsername, profileFirstName, profileLastName,
					profilePassword, profileDateofBirth, profileEmailAddress);
			
			return serverProfile;		
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return null;	
		}		
	}
	
	/**
	 * Checks the users table to check that the user is not already
	 * logged in on another device.
	 * @param username	username of profile to check log in status for
	 * @return	status on if the user is logged out
	 */
	public static boolean loggedOutCheck(String username) {
		Boolean loggedOutStatus = null;
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement loggedInStatement = connection.prepareStatement(
					"SELECT logged_in FROM users WHERE username = ?");
			loggedInStatement.setString(1, username);
			
			ResultSet loggedInResult = loggedInStatement.executeQuery();
			loggedInResult.next();
			loggedOutStatus = !loggedInResult.getBoolean("logged_in");	
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		return loggedOutStatus;
	}
	
	/**
	 * Checks whether the input password matches the database password
	 * for a given input username. Returns false if the username is not
	 * in the table.
	 * @param profile	profile object containing username/password input
	 * @return	status on if the password matches username
	 */
	public static boolean passwordCheck(Profile profile) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement passwordCheckStatement = connection.prepareStatement(
					"SELECT password FROM users WHERE username = ?");
			
			passwordCheckStatement.setString(1, profile.getUsername());
			ResultSet passwordCheckResult = passwordCheckStatement.executeQuery();
			passwordCheckResult.next();
			try {
				String serverProfilePassword = passwordCheckResult.getString("password");
				return profile.getPassword().equals(serverProfilePassword);			
			} catch (PSQLException e) {
				return false;
			}			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	/**
	 * Checks if the username entered during sign up is not used
	 * by any other user on the platform
	 * @param username	username entered at sign up
	 * @return	status on if username is available
	 */
	public static boolean usernameAvailableCheck(String username) {
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
	
	/**
	 * Adds a new player to a current game. Checks first if a player is already
	 * in the game to see if the new player should be player 1 or 2. Adds log
	 * of user to game connection in user_games. 
	 * @param board		board object containing players in game
	 */
	public static void addPlayer(Board board) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			
			PreparedStatement getPlayer1Statement = connection.prepareStatement(
					"SELECT player1 FROM games WHERE game_ID = ?");
			getPlayer1Statement.setInt(1, board.getGameID());
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
	
	/**
	 * Gets the database user ID for a given username. Returns -1 if
	 * the username is not found.
	 * @param username	username of user to check to database
	 * @return	user ID of user
	 */
	public static int getUserID(String username) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();			
			PreparedStatement getUserIDStatement = connection.prepareStatement(
					"SELECT user_ID FROM users WHERE username = ?");
			getUserIDStatement.setString(1, username);
			ResultSet getUserIDResult = getUserIDStatement.executeQuery();
			getUserIDResult.next();
			return getUserIDResult.getInt("user_ID");
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Retrieves the 5 most recent games which the user has played in.
	 * @param username	user to get recent games of
	 * @return	List of up to 5 most recent games as well as 1 identifier game 
	 */
	public static ArrayList<Board> getLast5Games(String username) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();			
			PreparedStatement getLast5GamesStatement = connection.prepareStatement(
					"SELECT game_id, player1, player2, winner, start_time, end_time FROM users JOIN "
					+ "(SELECT * FROM user_games JOIN games using(game_id)) AS A USING(user_id)"
					+ " WHERE username = ? ORDER BY \"start_time\" DESC LIMIT 5");
			
			getLast5GamesStatement.setString(1, username);
			ResultSet getLast5GamesResult = getLast5GamesStatement.executeQuery();
			
			ArrayList<Board> last5Games = new ArrayList<>();
			
			Board recentGamesSignalBoard = new Board();
			recentGamesSignalBoard.setCommand(Command.RECENT_GAMES);
			last5Games.add(recentGamesSignalBoard);
			
			while(getLast5GamesResult.next()) {
				int gameID = getLast5GamesResult.getInt("game_id");
				String player1 = getLast5GamesResult.getString("player1");
				String player2 = getLast5GamesResult.getString("player2");
				String winner = getLast5GamesResult.getString("winner");
				Timestamp timeStart = getLast5GamesResult.getTimestamp("start_time");
				Timestamp timeEnd = getLast5GamesResult.getTimestamp("end_time");
				
				Board databaseGame = new Board(gameID, player1, player2, winner, timeStart, timeEnd);
				last5Games.add(databaseGame);				
			}
			return last5Games;
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	/**
	 * Updates the game in the database with a game end time.
	 * @param board		the game which has finished
	 */
	public static void updateGameEnd(Board board) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();			
			PreparedStatement updateGameEndStatement = connection.prepareStatement(
					"UPDATE games SET end_time = ?, winner = ? where game_id = ?");
			
			Timestamp gameEndTime = new Timestamp(System.currentTimeMillis());
			String winner = board.getWinner();
			int gameID = board.getGameID();
			
			updateGameEndStatement.setTimestamp(1, gameEndTime);
			updateGameEndStatement.setString(2, winner);
			updateGameEndStatement.setInt(3, gameID);			
			updateGameEndStatement.execute();			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets a users status on the database as logged out when
	 * they exit the application.
	 * @param username	the user who needs to be logged out
	 */
	public static void logOut(String username) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement loggedOutUpdateStatement = connection.prepareStatement(
					"UPDATE users SET logged_in = false WHERE username = ?");
			
			loggedOutUpdateStatement.setString(1, username);			
			loggedOutUpdateStatement.execute();			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	
}
