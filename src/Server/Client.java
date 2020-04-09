package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Game.Board;

public class Client implements Serializable {
	private static final long serialVersionUID = 1L;
	private final int port = 50000;
	private InetAddress hostIP;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String username; 
	private Board currentGame;
	private int playerNumber;
	
	public Client() {
		getHostIP();
		createSocket();
		createObjectDataStreams();
	}
	
	public void getHostIP() {
		try {
			hostIP = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void createSocket() {
		try {
			socket = new Socket(hostIP, port);
			System.out.println("Joined Server");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createObjectDataStreams() {
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnectServer() {
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void inGame() {
		while(socket.isConnected()) {
			Board inputBoard = null;
			while(inputBoard == null) {
				inputBoard = recieveBoard();
			}

			switch (inputBoard.getCommand()) {
			case UPDATE:
				updateBoard(inputBoard);
				break;
			default:
				break;
			}
		}
		
		disconnectServer();
	}
	
	public void logIn(String username) {
		this.username = username;
		sendServerUsername();
	}
	
	void sendServerUsername() {
		Board messageBoard = new Board(0);
		messageBoard.setCommand(Command.LOGIN);
		messageBoard.addPlayer(username);
		sendBoard(messageBoard);
	}
	
	void sendBoard(Board board) {
		try {
			out.writeObject(board);
			out.flush();
			out.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	Board recieveBoard() {
		try {
			return (Board) in.readObject();
		} catch (ClassNotFoundException | IOException e) {
			threadWait();
		}
		return null;
	}
	
	void threadWait() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	ArrayList<Board> recieveGameList() {
		try {
			return (ArrayList<Board>) in.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<Board> getActiveGames(){
		Board messageBoard = new Board(0);
		messageBoard.setCommand(Command.GET_GAMES);
		sendBoard(messageBoard);
		return recieveGameList();
	}
	
	public void createGame() {
		Board newGame = new Board((int) Math.round(Math.random()*100));
		newGame.setCommand(Command.NEW_GAME);
		sendBoard(newGame);
	}
	
	public void joinGame(Board game) {
		playerNumber = game.addPlayer(this.username);
		currentGame = game;
		game.setCommand(Command.JOIN_GAME);
		sendBoard(game);
//		inGame();
	}
	
	public void moveCounter(int currentX, int currentY, int newX, int newY) {
		currentGame.moveCounter(playerNumber, currentX, currentY, newX, newY);
		currentGame.setCommand(Command.UPDATE);
		sendBoard(currentGame);
	}
	
	public void updateBoard(Board latestGame) {
		this.currentGame = latestGame;
	}
	
	public static void main(String[] args) {
		Client test = new Client();
		test.logIn("Test");
//		test.createGame();
		System.out.println(test.getActiveGames());
		test.joinGame(test.getActiveGames().get(0));
		test.recieveBoard();
		System.out.println(test.getActiveGames());
		System.out.println(test.getActiveGames().get(0).getPlayer1());
		System.out.println(test.getActiveGames().get(0).getPlayer2());
		
	}

}
