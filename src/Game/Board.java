package Game;

import java.io.Serializable;
import java.sql.Timestamp;

import Server.Command;

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


	public Board(int gameID) {		
		this.gameID = gameID;
		boardState = new int[BOARD_SIZE][BOARD_SIZE];
		player1TileCount = COUNTER_NUMBER;
		player2TileCount = COUNTER_NUMBER;
		fillBoard();
		playing = true;
		playersTurn = 1;
	}
	
	public Board(int gameID, String player1, String player2, String winner, Timestamp timeStarted, Timestamp timeEnded) {
		this.gameID = gameID;
		this.player1 = player1;
		this.player2 = player2;
		this.winner = winner;	
		this.timeStarted = timeStarted;
		this.timeEnded = timeEnded;
	}
	
	public Board() {
	}

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

	public void setUsername(String username) {
		this.player1 = username;
	}

	boolean gameWon() {
		return player1TileCount == 0 || player2TileCount == 0;
	}

	void fillBoard() {
		fillPlayerTiles(1);
		fillPlayerTiles(2);		
	}

	void fillPlayerTiles(int playerNumber) {
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

	public void printBoard() {
		for(int i=0; i<BOARD_SIZE; i++) {
			String out = "";
			for(int j=0; j<BOARD_SIZE; j++) {
				out += boardState[i][j] + " ";
			}
			System.out.println(out);
		}
	}

	public void moveCounter(int playerNumber, int currentX, int currentY, int newX, int newY) {
		if(playerNumber == playersTurn) {
			if(Math.abs(currentX-newX)==2 && Math.abs(currentY-newY)==2) {
				takeCounter(playerNumber, currentX, currentY, newX, newY);
			} else if(validMove(playerNumber, currentX, currentY, newX, newY)) {
				boardState[newY][newX] = boardState[currentY][currentX];
				boardState[currentY][currentX] = 0;			
				if(upgradeCheck(newY)) {
					upgradeTile(newX, newY);
				}
			} else {
				throw new IllegalArgumentException("Invalid move");
			}	
			changePlayersTurn();
		} else {
			throw new IllegalArgumentException("Invalid Move, it is not this players turn");
		}
	}

	void takeCounter(int playerNumber, int currentX, int currentY, int newX, int newY) {
		if(validTake(playerNumber, currentX, currentY, newX, newY)) {
			boardState[newY][newX] = boardState[currentY][currentX];
			boardState[currentY][currentX] = 0;
			boardState[(currentY+newY)/2][(currentX+newX)/2] = 0;			
			if(upgradeCheck(newY)) {
				upgradeTile(newX, newY);
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

	void removeOpponentTile(int playerNumber) {
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

	boolean validTake(int playerNumber, int currentX, int currentY, int newX, int newY) {
		int takingCounter = boardState[(currentY+newY)/2][(currentX+newX)/2];
		if(playerNumber != takingCounter &&
				takingCounter != 0 &&
				boardState[newY][newX] == 0 &&
				Math.abs(boardState[currentY][currentX]) == playerNumber) {
			if(boardState[currentY][currentX] > 0) {
				return validMoveStandard(playerNumber, currentX, currentY, newX, newY);
			} else {
				return true;
			}
		} else {
			return false;
		}		
	}

	boolean validMove(int playerNumber, int currentX, int currentY, int newX, int newY) {
		if(Math.abs(boardState[currentY][currentX]) == playerNumber &&
				boardState[newY][newX] == 0 &&
				Math.abs(currentX-newX) == 1 && 
				Math.abs(currentY-newY) == 1) {
			if(boardState[currentY][currentX] > 0) {
				return validMoveStandard(playerNumber, currentX, currentY, newX, newY);
			} else 
				return boardState[currentY][currentX] < 0;
		}
		return false;
	}

	boolean validMoveStandard(int playerNumber, int currentX, int currentY, int newX, int newY) {
		if(playerNumber == 1) {
			return  currentY < newY;
		} else {
			return currentY > newY;
		}		
	}

	void upgradeTile(int x, int y) {
		if(!tileUpgraded(x, y))
			boardState[y][x] *= -1;
	}

	boolean tileUpgraded(int x, int y) {
		return boardState[y][x] < 0; 
	}

	boolean upgradeCheck(int y) {
		return y==0 || y==BOARD_SIZE-1;		
	}
	
	void changePlayersTurn() {
		if(playersTurn == 1) 
			playersTurn = 2;
		else playersTurn = 1;
	}
	
	public int getPlayersTurn() {
		return playersTurn;
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

}
