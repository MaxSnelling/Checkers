package Game;

import java.io.Serializable;
import java.sql.Timestamp;

import Server.Command;

/**
 * Stores game data and executes game logic. Stores
 * instructions for Server-Client communication.
 * @author Max Snelling
 * @version 15/5/20
 */
public class Board implements Serializable {
	private static final long serialVersionUID = 3L;
	private final int BOARD_SIZE = 8;
	private final int COUNTER_NUMBER = 12;
	private String player1;
	private String player2;
	private int gameID;
	private int[][] boardState;
	private int player1TileCount;
	private int player2TileCount;
	private int playersTurn;
	private Timestamp timeStarted;
	private Timestamp timeEnded;
	private String winner;
	private boolean playing;
	private Command command;

	// Constructor used for creating a game from the database
	public Board(int gameID) {		
		this.gameID = gameID;
		boardState = new int[BOARD_SIZE][BOARD_SIZE];
		player1TileCount = COUNTER_NUMBER;
		player2TileCount = COUNTER_NUMBER;
		fillBoard();
		playing = true;
		playersTurn = 1;
	}

	// Constructor use for retrieving game information from the database
	public Board(int gameID, String player1, String player2, 
			String winner, Timestamp timeStarted, Timestamp timeEnded) {
		this.gameID = gameID;
		this.player1 = player1;
		this.player2 = player2;
		this.winner = winner;	
		this.timeStarted = timeStarted;
		this.timeEnded = timeEnded;
	}

	// Empty constructor used for messaging commands only
	public Board() {}
	
	/**
	 * Adds a new player to a game in the position based
	 * on if there is already a player in the game
	 * @param newPlayer	username of player being added
	 * @return	number of new player
	 */
	public int addPlayer(String newPlayer) {
		if(player1 == null) {
			player1 = newPlayer;
			return 1;
		} else if(player2 == null) {
			if(!player1.equals(newPlayer)) {
				player2 = newPlayer;
				return 2;
			} else {
				throw new IllegalArgumentException("Player already in game");
			}			
		} else {
			throw new IllegalArgumentException("Game is full");
		}
	}
	
	private void fillBoard() {
		fillPlayerTiles(1);
		fillPlayerTiles(2);		
	}

	/**
	 * Adds the users tiles to the board in their correct
	 * positions
	 * @param playerNumber	player number of tiles being added
	 */
	private void fillPlayerTiles(int playerNumber) {
		int row;
		int column;
		if(playerNumber == 1) {
			row = 0;
			column = 1;
		} else {
			row = BOARD_SIZE-(COUNTER_NUMBER/4);
			column = 0;
		}

		// TODO make board size work for odd numbers
		for(int i=0; i<COUNTER_NUMBER; i++) {
			boardState[row][column] = playerNumber;
			if(column == BOARD_SIZE-1) {
				row++;
				column = 0;
			} else if(column == BOARD_SIZE-2) {
				row++;
				column = 1;
			} else 
				column = column+2;
		}
	}
	
	/**
	 * Method called by client when user clicks to move a counter.
	 * Makes checks to see if move is a take and sees whether the 
	 * move is valid.
	 * @param playerNumber	number of player making turn
	 * @param currentPos	coordinate of where counter is
	 * @param newPos		coordinate of where counter is going
	 */
	public void moveCounter(int playerNumber, Coordinate2D currentPos, Coordinate2D newPos) {
		if(playerNumber == playersTurn) {
			if(Math.abs(currentPos.x-newPos.x)==2 && Math.abs(currentPos.y-newPos.y)==2) {
				takeCounter(playerNumber, currentPos, newPos);
			} else if(validMove(playerNumber, currentPos, newPos)) {
				boardState[newPos.y][newPos.x] = boardState[currentPos.y][currentPos.x];
				boardState[currentPos.y][currentPos.x] = 0;			
				if(upgradeCheck(newPos.y)) {
					upgradeTile(newPos.x, newPos.y);
				}
			} else {
				throw new IllegalArgumentException("Invalid move");
			}	
			changePlayersTurn();
		} else {
			throw new IllegalArgumentException("Invalid Move, it is not this players turn");
		}
	}
	
	/**
	 * If move is determined to be a take move then take is checked to
	 * be valid and executed.
	 * @param playerNumber	number of player making move
	 * @param currentPos	coordinate of where counter is
	 * @param newPos		coordinate of where counter is going
	 */
	private void takeCounter(int playerNumber, Coordinate2D currentPos, Coordinate2D newPos) {
		if(validTake(playerNumber, currentPos, newPos)) {
			boardState[newPos.y][newPos.x] = boardState[currentPos.y][currentPos.x];
			boardState[currentPos.y][currentPos.x] = 0;
			boardState[(currentPos.y+newPos.y)/2][(currentPos.x+newPos.x)/2] = 0;			
			if(upgradeCheck(newPos.y)) {
				upgradeTile(newPos.x, newPos.y);
			}
			if(gameWon()) {
				System.out.println("Player " + playerNumber + " won!!!");
			}
			removeOpponentTile(playerNumber);
			checkForWinner();
		} else {
			throw new IllegalArgumentException("Invalid take");
		}
	}
	
	private boolean gameWon() {
		return player1TileCount == 0 || player2TileCount == 0;
	}

	private void removeOpponentTile(int playerNumber) {
		if(playerNumber == 1) player2TileCount--;
		else player1TileCount--;
	}

	private void checkForWinner() {
		if(player1TileCount == 0) {
			winner = player2;
			playing = false;
		} else if(player2TileCount == 0) {
			winner = player1;
			playing = false;
		}
	}
	
	private boolean validTake(int playerNumber, Coordinate2D currentPos, Coordinate2D newPos) {
		int takingCounter = boardState[(newPos.y+currentPos.y)/2][(newPos.x+currentPos.x)/2];
		if(playerNumber != takingCounter &&
				takingCounter != 0 &&
				boardState[newPos.y][newPos.x] == 0 &&
				Math.abs(boardState[currentPos.y][currentPos.x]) == playerNumber) {
			if(boardState[currentPos.y][currentPos.x] > 0) {
				return validMoveStandard(playerNumber, currentPos, newPos);
			} else {
				return true;
			}
		} else {
			return false;
		}		
	}

	private boolean validMove(int playerNumber, Coordinate2D currentPos, Coordinate2D newPos) {
		if(Math.abs(boardState[currentPos.y][currentPos.x]) == playerNumber &&
				boardState[newPos.y][newPos.x] == 0 &&
				Math.abs(currentPos.x-newPos.x) == 1 && 
				Math.abs(currentPos.y-newPos.y) == 1) {
			if(boardState[currentPos.y][currentPos.x] > 0) {
				return validMoveStandard(playerNumber, currentPos, newPos);
			} else 
				return boardState[currentPos.y][currentPos.x] < 0;
		}
		return false;
	}

	// Checks that standard counters are moving forward
	private boolean validMoveStandard(int playerNumber, Coordinate2D currentPos, Coordinate2D newPos) {
		if(playerNumber == 1) {
			return  currentPos.y < newPos.y;
		} else {
			return  currentPos.y > newPos.y;
		}		
	}

	private void upgradeTile(int x, int y) {
		if(!tileUpgraded(x, y))
			boardState[y][x] *= -1;
	}

	private boolean tileUpgraded(int x, int y) {
		return boardState[y][x] < 0; 
	}

	private boolean upgradeCheck(int y) {
		return y==0 || y==BOARD_SIZE-1;		
	}

	private void changePlayersTurn() {
		if(playersTurn == 1) 
			playersTurn = 2;
		else playersTurn = 1;
	}

	public int getPlayersTurn() {
		return playersTurn;
	}
	
	public void setUsername(String username) {
		this.player1 = username;
	}

	public String getPlayer1() {
		return player1;
	}

	public String getPlayer2() {
		return player2;
	}

	public int getGameID() {
		return gameID;
	}

	public int[][] getBoardState() {
		return boardState;
	}

	public int getPlayer1TileCount() {
		return player1TileCount;
	}

	public int getPlayer2TileCount() {
		return player2TileCount;
	}

	public Timestamp getTimeStarted() {
		return timeStarted;
	}

	public Timestamp getTimeEnded() {
		return timeEnded;
	}

	public String getWinner() {
		return winner;
	}

	public boolean playing() {
		return playing;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public String toString() {
		return gameID + "";
	}

	// Debug method to check board state
	public void printBoard() {
		for(int i=0; i<BOARD_SIZE; i++) {
			String out = "";
			for(int j=0; j<BOARD_SIZE; j++) {
				out += boardState[i][j] + " ";
			}
			System.out.println(out);
		}
	}

}
