package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;

import Database.DatabaseConnect;
import Database.DatabaseInsert;
import Database.DatabaseQuery;
import Game.Board;
import Game.Profile;
import javafx.util.converter.LocalDateStringConverter;

class DatabaseTests {

	@Test
	void addProfileTests() {
		String expectedUsername = "jsmith";
		String expectedFirstName = "John";
		String expectedLastName = "Smith";
		String expectedPassword = "password123";
		LocalDate expectedDOB = LocalDate.of(1960, 10, 3);
		String expectedEmail = "johnsmith@gmail.com";
		Profile expectedProfile = new Profile(expectedUsername, expectedFirstName,
				expectedLastName, expectedPassword, expectedDOB, expectedEmail);
		
		// Simple insertion test
		DatabaseInsert.addProfile(expectedProfile);
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
		removeProfile(expectedProfile);
		
		// Double insertion test
		DatabaseInsert.addProfile(expectedProfile);
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
		removeProfile(expectedProfile);
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
	void addPlayerTests() {
		Board game = DatabaseInsert.createGame();
		Profile player = new Profile("jsmith", "password123");
		DatabaseInsert.addProfile(player);
		game.addPlayer(player.getUsername());
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
		assertEquals(player.getUsername(), actualPlayer1);
		removeGame(game);
		removeProfile(player);
	}
	
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
