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

public class DatabaseQuery {
	
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
	
	public static void logOutUser(String username) {
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
