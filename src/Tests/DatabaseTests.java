package Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;

import Database.DatabaseConnect;
import Database.DatabaseInsert;
import Database.DatabaseQuery;
import Game.Board;
import Game.Profile;
import Server.Command;

/**
 * Tests for all database classes
 * @author Max Snelling
 * @version 5/5/20
 */
class DatabaseTests {
	private String expectedUsername;
	private String expectedFirstName;
	private String expectedLastName;
	private String expectedPassword;
	private LocalDate expectedDOB;
	private String expectedEmail;
	private Profile expectedProfile;
	
	@BeforeEach
	void beforeEach() {
		expectedUsername = "jsmith";
		expectedFirstName = "John";
		expectedLastName = "Smith";
		expectedPassword = "password123";
		expectedDOB = LocalDate.of(1960, 10, 3);
		expectedEmail = "johnsmith@gmail.com";
		expectedProfile = new Profile(expectedUsername, expectedFirstName,
				expectedLastName, expectedPassword, expectedDOB, expectedEmail);
		DatabaseInsert.addProfile(expectedProfile);
	}
	
	@AfterEach
	void afterEach() {
		removeProfile(expectedProfile);
	}

	@Test
	void addProfileTests() {
		// Simple insertion test
		Profile actualProfile = null;		
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement getProfileStatement = connection.prepareStatement(
					"SELECT username, first_name, last_name, password, date_of_birth, "
					+ "email_address FROM users WHERE username = ?");
			getProfileStatement.setString(1, expectedUsername);
			
			ResultSet getProfileResult = getProfileStatement.executeQuery();
			getProfileResult.next();
			
			String actualUsername = getProfileResult.getString("username");
			String actualFirstName = getProfileResult.getString("first_name");
			String actualLastName = getProfileResult.getString("last_name");
			String actualPassword = getProfileResult.getString("password");
			LocalDate actualDOB = getProfileResult.getDate("date_of_birth").toLocalDate();
			String actualEmail = getProfileResult.getString("email_address");
			
			actualProfile = new Profile(actualUsername, actualFirstName, 
					actualLastName, actualPassword, actualDOB, actualEmail);					
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		
		assertTrue(expectedProfile.equalsTo(actualProfile));
		
		// Double insertion test
		DatabaseInsert.addProfile(expectedProfile);
		int profileCount = -1;
		
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement getProfileStatement = connection.prepareStatement(
					"SELECT COUNT(*) FROM users WHERE username = ?");
			getProfileStatement.setString(1, expectedUsername);
			
			ResultSet getProfileResult = getProfileStatement.executeQuery();
			getProfileResult.next();			
			profileCount = getProfileResult.getInt("count");				
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		assertTrue(profileCount == 1);
	}
	
	
	@Test
	void createGameTests() {
		Board expectedBoard = DatabaseInsert.createGame();
		int expectedGameID = expectedBoard.getGameID();
		int actualGameID = -1;
		
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement getGameStatement = connection.prepareStatement(
					"SELECT game_ID FROM games ORDER BY game_ID DESC LIMIT 1");
			
			ResultSet getGameResult = getGameStatement.executeQuery();
			getGameResult.next();
			
			actualGameID = getGameResult.getInt("game_ID");				
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}		
		assertEquals(expectedGameID, actualGameID);
		removeGame(expectedBoard);
	}
	
	
	@Test
	void logInTests() {
		boolean loggedInStatus = true;
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement getLoggedInStatement = connection.prepareStatement(
					"SELECT logged_in FROM users WHERE username = ?");
			getLoggedInStatement.setString(1, expectedUsername);
			
			ResultSet getLoggedInResult = getLoggedInStatement.executeQuery();
			getLoggedInResult.next();
			loggedInStatus = getLoggedInResult.getBoolean("logged_in");	
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}	
		assertFalse(loggedInStatus);	
		
		Profile correctLogInProfile = new Profile(expectedUsername, expectedPassword);
		Profile actualProfile = DatabaseQuery.logIn(correctLogInProfile);
		assertTrue(expectedProfile.equalsTo(actualProfile));
		
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement getLoggedInStatement = connection.prepareStatement(
					"SELECT logged_in FROM users WHERE username = ?");
			getLoggedInStatement.setString(1, expectedUsername);
			
			ResultSet getLoggedInResult = getLoggedInStatement.executeQuery();
			getLoggedInResult.next();
			loggedInStatus = getLoggedInResult.getBoolean("logged_in");	
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}	
		assertTrue(loggedInStatus);		
	}

	
	@Test
	void loggedOutCheckTests() {
		Boolean actualLoggedOutStatus = DatabaseQuery.loggedOutCheck(expectedUsername);
		assertTrue(actualLoggedOutStatus);
		
		DatabaseQuery.logIn(expectedProfile);
		actualLoggedOutStatus = DatabaseQuery.loggedOutCheck(expectedUsername);
		assertFalse(actualLoggedOutStatus);
		
		// Double log in
		DatabaseQuery.logIn(expectedProfile);
		actualLoggedOutStatus = DatabaseQuery.loggedOutCheck(expectedUsername);
		assertFalse(actualLoggedOutStatus);
		
		DatabaseQuery.logOut(expectedUsername);
		actualLoggedOutStatus = DatabaseQuery.loggedOutCheck(expectedUsername);
		assertTrue(actualLoggedOutStatus);
		
		// Double log out
		DatabaseQuery.logOut(expectedUsername);
		actualLoggedOutStatus = DatabaseQuery.loggedOutCheck(expectedUsername);
		assertTrue(actualLoggedOutStatus);
	}
	
	@Test
	void passwordCheckTests() {
		Profile correctPasswordProfile = new Profile(expectedUsername, expectedPassword);
		Boolean passwordCheckResult = DatabaseQuery.passwordCheck(correctPasswordProfile);
		assertTrue(passwordCheckResult);
		
		Profile incorrectPasswordProfile = new Profile(expectedUsername, "Password123");
		passwordCheckResult = DatabaseQuery.passwordCheck(incorrectPasswordProfile);
		assertFalse(passwordCheckResult);
	}

	@Test
	void usernameCheckTests() {
		Boolean usernameUsedResult = DatabaseQuery.usernameAvailableCheck(expectedUsername);
		assertFalse(usernameUsedResult);
		
		String newUsername = "jBrown";
		Boolean newUsernameResult = DatabaseQuery.usernameAvailableCheck(newUsername);
		assertTrue(newUsernameResult);
	}
	
	@Test
	void addPlayerTests() {
		// Single add player
		Board game = DatabaseInsert.createGame();
		game.addPlayer(expectedUsername);
		DatabaseQuery.addPlayer(game);
		String actualPlayer1 = null;
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement getPlayer1Statement = connection.prepareStatement(
					"SELECT player1 FROM games WHERE game_ID = ?");
			getPlayer1Statement.setInt(1, game.getGameID());
			
			ResultSet getPlayer1Result = getPlayer1Statement.executeQuery();
			getPlayer1Result.next();
			actualPlayer1 = getPlayer1Result.getString("player1");	
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}	
		assertEquals(expectedUsername, actualPlayer1);
		
		int gameCount = -1;
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement getUserGamesStatement = connection.prepareStatement(
					"SELECT COUNT(*) FROM user_games WHERE game_ID = ?");
			getUserGamesStatement.setInt(1, game.getGameID());			
			
			ResultSet getUserGamesResult = getUserGamesStatement.executeQuery();
			getUserGamesResult.next();
			gameCount = getUserGamesResult.getInt("count");	
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}	
		assertEquals(1, gameCount);
		
		// Add 2 players
		Profile player2 = new Profile("ebrown", "Eric", "Brown", "password123",
				LocalDate.of(1982, 3, 2), "ebrown@outlook.com");
		DatabaseInsert.addProfile(player2);
		game.addPlayer(player2.getUsername());
		DatabaseQuery.addPlayer(game);
		actualPlayer1 = null;
		String actualPlayer2 = null;
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement getPlayer2Statement = connection.prepareStatement(
					"SELECT player1, player2 FROM games WHERE game_ID = ?");
			getPlayer2Statement.setInt(1, game.getGameID());
			
			ResultSet getPlayer2Result = getPlayer2Statement.executeQuery();
			getPlayer2Result.next();
			actualPlayer1 = getPlayer2Result.getString("player1");	
			actualPlayer2 = getPlayer2Result.getString("player2");	
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}	
		assertEquals(expectedUsername, actualPlayer1);
		assertEquals(player2.getUsername(), actualPlayer2);
		
		gameCount = -1;
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement getUserGamesStatement = connection.prepareStatement(
					"SELECT COUNT(*) FROM user_games WHERE game_ID = ?");
			getUserGamesStatement.setInt(1, game.getGameID());			
			
			ResultSet getUserGamesResult = getUserGamesStatement.executeQuery();
			getUserGamesResult.next();
			gameCount = getUserGamesResult.getInt("count");	
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}	
		assertEquals(2, gameCount);	
		
		removeProfile(expectedProfile);
		removeProfile(player2);
		removeGame(game);
	}
	
	@Test
	void last5GamesTests() {
		// No games tests
		ArrayList<Board> actualNoGamesList = DatabaseQuery.getLast5Games(expectedUsername);
		int expectedListLength = 1;
		int actualListLength = actualNoGamesList.size();
		assertEquals(expectedListLength, actualListLength);
		
		Command expectedInitialCommand = Command.RECENT_GAMES;
		Command actualInitialCommand = actualNoGamesList.get(0).getCommand();
		assertEquals(expectedInitialCommand, actualInitialCommand);
		
		// 5 games tests
		Board game1 = DatabaseInsert.createGame();
		Board game2 = DatabaseInsert.createGame();
		Board game3 = DatabaseInsert.createGame();
		Board game4 = DatabaseInsert.createGame();
		Board game5 = DatabaseInsert.createGame();
		game1.addPlayer(expectedUsername);
		game2.addPlayer(expectedUsername);
		game3.addPlayer(expectedUsername);
		game4.addPlayer(expectedUsername);
		game5.addPlayer(expectedUsername);
		DatabaseQuery.addPlayer(game1);
		DatabaseQuery.addPlayer(game2);
		DatabaseQuery.addPlayer(game3);
		DatabaseQuery.addPlayer(game4);
		DatabaseQuery.addPlayer(game5);
		
		ArrayList<Board> actual5GamesList = DatabaseQuery.getLast5Games(expectedUsername);
		expectedListLength = 6;
		actualListLength = actual5GamesList.size();
		assertEquals(expectedListLength, actualListLength);
		
		actualInitialCommand = actual5GamesList.get(0).getCommand();
		assertEquals(expectedInitialCommand, actualInitialCommand);
		
		// 6 games tests
		Board game6 = DatabaseInsert.createGame();
		game6.addPlayer(expectedUsername);
		DatabaseQuery.addPlayer(game6);
		
		ArrayList<Board> actual6GamesList = DatabaseQuery.getLast5Games(expectedUsername);
		expectedListLength = 6;
		actualListLength = actual6GamesList.size();
		assertEquals(expectedListLength, actualListLength);
		
		actualInitialCommand = actual6GamesList.get(0).getCommand();
		assertEquals(expectedInitialCommand, actualInitialCommand);
		
		// most recent games checks
		Board oldestGame = actual5GamesList.get(5);
		Timestamp oldestGameTimeStart = oldestGame.getTimeStarted();		
		Board oldestGameOf5 = actual6GamesList.get(5);
		Timestamp oldestGameOf5TimeStart = oldestGameOf5.getTimeStarted();		
		assertTrue(oldestGameOf5TimeStart.after(oldestGameTimeStart));
		
		removeGame(game1);
		removeGame(game2);
		removeGame(game3);
		removeGame(game4);
		removeGame(game5);
		removeGame(game6);
	}
	
	@Test
	void updateGamesEndTests() {
		Board game = DatabaseInsert.createGame();
		Timestamp actualGameEnd = null;
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement getGameEndStatement = connection.prepareStatement(
					"SELECT end_time FROM games WHERE game_ID = ?");
			getGameEndStatement.setInt(1, game.getGameID());
			
			ResultSet getGameEndResult = getGameEndStatement.executeQuery();
			getGameEndResult.next();
			actualGameEnd = getGameEndResult.getTimestamp("end_time");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}	
		assertNull(actualGameEnd);
		
		DatabaseQuery.updateGameEnd(game);
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement getGameEndStatement = connection.prepareStatement(
					"SELECT end_time FROM games WHERE game_ID = ?");
			getGameEndStatement.setInt(1, game.getGameID());
			
			ResultSet getGameEndResult = getGameEndStatement.executeQuery();
			getGameEndResult.next();
			actualGameEnd = getGameEndResult.getTimestamp("end_time");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}	
		assertNotNull(actualGameEnd);		
		
		removeGame(game);
	}
	
	@Test
	void logOutTests() {
		DatabaseQuery.logIn(expectedProfile);
		Boolean actualLoggedInStatus = false;
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement getLoggedInStatement = connection.prepareStatement(
					"SELECT logged_in FROM users WHERE username = ?");
			getLoggedInStatement.setString(1, expectedUsername);
			
			ResultSet getLoggedInResult = getLoggedInStatement.executeQuery();
			getLoggedInResult.next();
			actualLoggedInStatus = getLoggedInResult.getBoolean("logged_in");	
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		assertTrue(actualLoggedInStatus);
		
		DatabaseQuery.logOut(expectedUsername);
		try {
			Connection connection = DatabaseConnect.connectDatabase();
			PreparedStatement getLoggedInStatement = connection.prepareStatement(
					"SELECT logged_in FROM users WHERE username = ?");
			getLoggedInStatement.setString(1, expectedUsername);
			
			ResultSet getLoggedInResult = getLoggedInStatement.executeQuery();
			getLoggedInResult.next();
			actualLoggedInStatus = getLoggedInResult.getBoolean("logged_in");	
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		assertFalse(actualLoggedInStatus);
	}
	
	// Clean up methods	
	void removeProfile(Profile profile) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();			
			PreparedStatement removeProfileStatement = connection.prepareStatement(
					"DELETE FROM users WHERE username = ?");
			removeProfileStatement.setString(1, profile.getUsername());
			removeProfileStatement.execute();			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	void removeGame(Board game) {
		try {
			Connection connection = DatabaseConnect.connectDatabase();			
			PreparedStatement removeProfileStatement = connection.prepareStatement(
					"DELETE FROM games WHERE game_ID = ?");
			removeProfileStatement.setInt(1, game.getGameID());
			removeProfileStatement.execute();			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
}
