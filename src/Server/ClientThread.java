package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import Game.Board;

public class ClientThread extends Thread implements Runnable {
	private final Server server;
	private final Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	public ClientThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		System.out.println("1");
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
	
	Board recieveBoard() {
		Board incomingBoard = null;
		try {
			incomingBoard =(Board) in.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return incomingBoard;
	}
	
	void sendBoard(Board board) {
		try {
			out.writeObject(board);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void sendGameList(ArrayList<Board> gameList) {
		try {
			out.writeObject(gameList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(socket.isConnected()) {
			Board inputBoard = recieveBoard();
			System.out.println(inputBoard);

			switch (inputBoard.getCommand()) {
				case CREATE_GAME:
					server.createGame(inputBoard);
					break;
				case GET_GAMES:
					getGames();
					break;
				default:
					break;
			}
			
		}
		disconnectClient();
	}
	
	void getGames() {
		sendGameList(server.getGames());
	}

}
