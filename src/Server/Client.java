package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Game.Board;

public class Client {
	private final int port = 50000;
	private InetAddress hostIP;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String username; 
	
	public Client() {
		getHostIP();
		createSocket();
		createObjectDataStreams();
		//receiveObject();
		//System.out.println(getActiveGames());
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
	
	public void receiveObject() {
		while(socket.isConnected()) {
			
		}
		
		disconnectServer();
	}
	
	public void logIn(String username) {
		this.username = username;
	}
	
	void sendBoard(Board board) {
		try {
			out.writeObject(board);
			out.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	Board recieveBoard() {
		try {
			return (Board) in.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	ArrayList<Board> recieveGameList() {
		try {
			return (ArrayList<Board>) in.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<Board> getActiveGames(){
		System.out.println("here");
		Board messageBoard = new Board(0);
		messageBoard.setCommand(Command.GET_GAMES);
		sendBoard(messageBoard);
		return recieveGameList();
	}
	
	public void createGame() {
		Board newGame = new Board(0);
		newGame.setCommand(Command.NEW_GAME);
		sendBoard(newGame);
	}
	
	public static void main(String[] args) {
		new Client();
		
	}

}
