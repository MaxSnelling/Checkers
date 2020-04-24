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
import Game.Profile;

public class Client implements Serializable {
	private static final long serialVersionUID = 1L;
	private final int port = 50000;
	private InetAddress hostIP;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Profile profile; 
	private Board currentGame;
	private ArrayList<Board> boardList;
	private int playerNumber;
	private ObjectInThread objectInThread;
	
	public Client() {
		getHostIP();
		createSocket();
		createObjectDataStreams();
		boardList = new ArrayList<>();
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
	
	public void logIn(Profile profile) {
		this.profile = profile;
		profile.setCommand(Command.LOGIN);
		sendObjectToServer(profile);
	}
	
	public boolean passwordCheck(Profile profile) {
		profile.setCommand(Command.PASSWORD_CHECK);
		sendObjectToServer(profile);
		Profile profileIn = recieveProfile();
		return profileIn.getCommand().equals(Command.CORRECT);
	}
	
	public void createGame() {
		Board messageBoard = new Board();
		messageBoard.setCommand(Command.NEW_GAME);
		sendObjectToServer(messageBoard);
	}
	
	public void joinGame(Board game) {
		playerNumber = game.addPlayer(this.profile.getUsername());
		game.setCommand(Command.JOIN_GAME);
		sendObjectToServer(game);
	}
	
	public void moveCounter(int currentX, int currentY, int newX, int newY) {
		currentGame.moveCounter(playerNumber, currentX, currentY, newX, newY);
		currentGame.setCommand(Command.UPDATE);
		sendObjectToServer(currentGame);
	}
	
	public void updateBoard(Board latestGame) {
		System.out.println("game" + latestGame);
		this.currentGame = latestGame;
	}
	
	public void startObjectInThread() {
		objectInThread = new ObjectInThread(this);
		objectInThread.start();
		getActiveGames();
	}
	
	public void stopBoardListThread() {
		objectInThread.interrupt();
	}
	
	public void getActiveGames(){
		Board messageBoard = new Board(0);
		messageBoard.setCommand(Command.GET_GAMES);
		sendObjectToServer(messageBoard);
	}
	
	private<E> void sendObjectToServer(E object) {
		try {
			out.writeObject(object);
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
	
	Profile recieveProfile() {
		try {
			return (Profile) in.readObject();
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
	
	public Board getCurrentGame() {
		return currentGame;
	}
	
	public void updateBoardList(ArrayList<Board> boardList) {
		this.boardList = boardList;
	}
	
	public ArrayList<Board> getBoardList() {
		return boardList;
	}
	
	public ObjectInputStream getObjectInStream() {
		return in;
	}
	
	public int getPlayerNumber() {
		return playerNumber;
	}
	
	public void updateProfile(Profile profile) {
		this.profile = profile;
	}

}
