package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import Game.Board;

public class ClientThread extends Thread implements Runnable {
	private final Server server;
	private final Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String username;
	
	public ClientThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		createObjectDataStreams();
	}
	
	public void createObjectDataStreams() {
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}    	
	}
	
	public void disconnectClient() {
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(socket.isConnected()) {
			Board inputBoard = null;
			while(inputBoard == null) {
				inputBoard = recieveBoard();
			}

			switch (inputBoard.getCommand()) {
				case LOGIN:
					login(inputBoard.getPlayer1());
					break;
				case NEW_GAME:
					newGame(inputBoard);
					break;
				case GET_GAMES:
					getGames();
					break;
				case JOIN_GAME:
					joinGame(inputBoard);
					break;
				case UPDATE:
					updateGameServer(inputBoard);
				default:
					break;
			}
		}
	}
	
	void login(String username) {
		this.username = username;
	}
	
	void newGame(Board game) {
		server.createGame(game);
	}
	
	void getGames() {
		sendGameList(server.getGames());
	}
	
	void joinGame(Board game) {
		server.joinGame(game);
	}
	
	void updateGameServer(Board game) {
		server.updateGameServer(game);
		server.updatePlayersGame(game.getGameID());
	}
	
	public void updateGame(Board game) {
		game.setCommand(Command.UPDATE);
		sendBoard(game);
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
	
	void sendBoard(Board board) {
		System.out.println(board);
		try {
			out.writeObject(board);
			out.flush();
			out.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void sendGameList(ArrayList<Board> gameList) {
		System.out.println(gameList);
		try {
			out.writeObject(gameList);
			out.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getUsername() {
		return username;
	}	

}
