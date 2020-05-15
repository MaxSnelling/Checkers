package Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Game.Board;
import Game.Coordinate2D;

/**
 * Tests for Board class
 * @author Max Snelling
 * @version 5/5/20
 */
class BoardTests {
	private Board board;
	
	@BeforeEach
	void beforeEach() {
		board = new Board(1);
	}

	@Test
	void fillBoardTest() {
		int[][] expectedBoard = {{0,1,0,1,0,1,0,1},
								 {1,0,1,0,1,0,1,0},
								 {0,1,0,1,0,1,0,1},
								 {0,0,0,0,0,0,0,0},
								 {0,0,0,0,0,0,0,0},
								 {2,0,2,0,2,0,2,0},
								 {0,2,0,2,0,2,0,2},
								 {2,0,2,0,2,0,2,0}};
		int[][] actualBoard = board.getBoardState();
		assertArrayEquals(expectedBoard, actualBoard);
	}
	
	@Test
	void addPlayerTests() {
		String player1 = "player1";
		String player2 = "player2";
		String player3 = "player3";
		
		board.addPlayer(player1);
		assertEquals(player1, board.getPlayer1());
		
		board.addPlayer(player2);
		assertEquals(player1, board.getPlayer1());
		assertEquals(player2, board.getPlayer2());
		
		Exception gameFullException = assertThrows(IllegalArgumentException.class, () -> {
			board.addPlayer(player3);
	    });	 
	    String expectedGameFullMessage = "Game is full";
	    String actualGameFullMessage = gameFullException.getMessage();	 
	    assertTrue(actualGameFullMessage.contains(expectedGameFullMessage));
	    
	    board = new Board(1);
	    
	    board.addPlayer(player1);
	    Exception repeatedPlayerException = assertThrows(IllegalArgumentException.class, () -> {
			board.addPlayer(player1);
	    });	 
	    String expectedRepeatedPlayerMessage = "Player already in game";
	    String actualRepeatedPlayerMessage = repeatedPlayerException.getMessage();	 
	    assertTrue(actualRepeatedPlayerMessage.contains(expectedRepeatedPlayerMessage));
	}
	
	@Test
	void moveCounterTests() {
		board.moveCounter(1, new Coordinate2D(3,2), new Coordinate2D(4,3));
		int[][] expectedBoard = {{0,1,0,1,0,1,0,1},
								 {1,0,1,0,1,0,1,0},
								 {0,1,0,0,0,1,0,1},
								 {0,0,0,0,1,0,0,0},
								 {0,0,0,0,0,0,0,0},
								 {2,0,2,0,2,0,2,0},
								 {0,2,0,2,0,2,0,2},
								 {2,0,2,0,2,0,2,0}};
		assertArrayEquals(expectedBoard, board.getBoardState());
		
		String expectedWrongTurnMessage = "Invalid Move, it is not this players turn";
		String expectedInvalidMoveMessage = "Invalid move";
		
		Exception wrongTurnException = assertThrows(IllegalArgumentException.class, () -> {
			board.moveCounter(1, new Coordinate2D(5,2), new Coordinate2D(6,3));
	    });	 
	    String actualWrongMessage = wrongTurnException.getMessage();	 
	    assertTrue(actualWrongMessage.contains(expectedWrongTurnMessage));

	    Exception noCounterSelectedException = assertThrows(IllegalArgumentException.class, () -> {
	    	board.moveCounter(2, new Coordinate2D(3,6), new Coordinate2D(4,5));
	    });	 
	    String actualNoCounterMessage = noCounterSelectedException.getMessage();	 
	    assertTrue(actualNoCounterMessage.contains(expectedInvalidMoveMessage));
	    
	    Exception notDiagonalException = assertThrows(IllegalArgumentException.class, () -> {
	    	board.moveCounter(2, new Coordinate2D(2,5), new Coordinate2D(2,4));
	    });	 	    
	    String actualNotDiagonalMessage = notDiagonalException.getMessage();	 
	    assertTrue(actualNotDiagonalMessage.contains(expectedInvalidMoveMessage));	    
	}
	
	@Test
	void takeCounterTests() {
		board.moveCounter(1, new Coordinate2D(3,2), new Coordinate2D(4,3));
		board.moveCounter(2, new Coordinate2D(6,5), new Coordinate2D(5,4));
		board.moveCounter(1, new Coordinate2D(4,3), new Coordinate2D(6,5));
		int[][] expectedBoard = {{0,1,0,1,0,1,0,1},
								 {1,0,1,0,1,0,1,0},
								 {0,1,0,0,0,1,0,1},
								 {0,0,0,0,0,0,0,0},
								 {0,0,0,0,0,0,0,0},
								 {2,0,2,0,2,0,1,0},
								 {0,2,0,2,0,2,0,2},
								 {2,0,2,0,2,0,2,0}};
		assertArrayEquals(expectedBoard, board.getBoardState());
		
		String expectedInvalidTakeMessage = "Invalid take";
		
		Exception noTakeCounterException = assertThrows(IllegalArgumentException.class, () -> {
			board.moveCounter(2, new Coordinate2D(0,5), new Coordinate2D(2,3));
	    });
	    String actualNoTakeCounterMessage = noTakeCounterException.getMessage();
	    assertTrue(actualNoTakeCounterMessage.contains(expectedInvalidTakeMessage));
	    
	    Exception takeOwnCounterException = assertThrows(IllegalArgumentException.class, () -> {
	    	board.moveCounter(2, new Coordinate2D(1,6), new Coordinate2D(3,4));
	    });	 
	    String actualTakeOwnCounterMessage = takeOwnCounterException.getMessage();
	    assertTrue(actualTakeOwnCounterMessage.contains(expectedInvalidTakeMessage));
	    
	    board.moveCounter(2, new Coordinate2D(0,5), new Coordinate2D(1,4));
	    board.moveCounter(1, new Coordinate2D(7,2), new Coordinate2D(6,3));
	    board.moveCounter(2, new Coordinate2D(1,4), new Coordinate2D(2,3));
	    board.moveCounter(1, new Coordinate2D(6,3), new Coordinate2D(5,4));
	    Exception landingSpaceFilledException = assertThrows(IllegalArgumentException.class, () -> {
	    	board.moveCounter(2, new Coordinate2D(7,6), new Coordinate2D(5,4));
	    });	 
	    String actualLandingSpaceFilledMessage = landingSpaceFilledException.getMessage();
	    assertTrue(actualLandingSpaceFilledMessage.contains(expectedInvalidTakeMessage));

	    
	    Exception opponentCounterSelectedException = assertThrows(IllegalArgumentException.class, () -> {
	    	board.moveCounter(2, new Coordinate2D(1,2), new Coordinate2D(3,4));
	    });	 
	    String actualOpponentCounterSelectedMessage = opponentCounterSelectedException.getMessage();
	    assertTrue(actualOpponentCounterSelectedMessage.contains(expectedInvalidTakeMessage));
	}
	
	@Test
	void upgradedTileTests() {
		board.moveCounter(1, new Coordinate2D(3,2), new Coordinate2D(4,3));
		board.moveCounter(2, new Coordinate2D(6,5), new Coordinate2D(5,4));
		board.moveCounter(1, new Coordinate2D(4,3), new Coordinate2D(6,5));
		board.moveCounter(2, new Coordinate2D(7,6), new Coordinate2D(5,4));
		board.moveCounter(1, new Coordinate2D(4,1), new Coordinate2D(3,2));
		board.moveCounter(2, new Coordinate2D(5,4), new Coordinate2D(4,3));
		board.moveCounter(1, new Coordinate2D(3,2), new Coordinate2D(2,3));
		board.moveCounter(2, new Coordinate2D(4,3), new Coordinate2D(3,2));
		board.moveCounter(1, new Coordinate2D(5,0), new Coordinate2D(4,1));
		board.moveCounter(2, new Coordinate2D(3,2), new Coordinate2D(5,0));
		int[][] expectedInitialBoard = {{0,1,0,1,0,-2,0,1},
										{1,0,1,0,0, 0,1,0},
										{0,1,0,0,0, 1,0,1},
										{0,0,1,0,0, 0,0,0},
										{0,0,0,0,0, 0,0,0},
										{2,0,2,0,2, 0,0,0},
										{0,2,0,2,0, 2,0,0},
										{2,0,2,0,2, 0,2,0}};
		assertArrayEquals(expectedInitialBoard, board.getBoardState());
		
		board.moveCounter(1, new Coordinate2D(1,2), new Coordinate2D(0,3));
		board.moveCounter(2, new Coordinate2D(5,0), new Coordinate2D(4,1));
		int[][] expectedMoveBoard = {{0,1,0,1, 0,0,0,1},
									 {1,0,1,0,-2,0,1,0},
									 {0,0,0,0, 0,1,0,1},
									 {1,0,1,0, 0,0,0,0},
								  	 {0,0,0,0, 0,0,0,0},
									 {2,0,2,0, 2,0,0,0},
									 {0,2,0,2, 0,2,0,0},
									 {2,0,2,0, 2,0,2,0}};
		assertArrayEquals(expectedMoveBoard, board.getBoardState());
		
		board.moveCounter(1, new Coordinate2D(2,1), new Coordinate2D(1,2));
		board.moveCounter(2, new Coordinate2D(4,1), new Coordinate2D(3,2));
		board.moveCounter(1, new Coordinate2D(3,0), new Coordinate2D(2,1));
		board.moveCounter(2, new Coordinate2D(3,2), new Coordinate2D(1,4));
		int[][] expectedTakeBoard = {{0, 1,0,0,0,0,0,1},
									 {1, 0,1,0,0,0,1,0},
									 {0, 1,0,0,0,1,0,1},
									 {1, 0,0,0,0,0,0,0},
								  	 {0,-2,0,0,0,0,0,0},
									 {2, 0,2,0,2,0,0,0},
									 {0, 2,0,2,0,2,0,0},
									 {2, 0,2,0,2,0,2,0}};
		assertArrayEquals(expectedTakeBoard, board.getBoardState());
	}

}
