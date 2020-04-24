package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Database.DatabaseQuery;
import Game.Board;
import Game.Profile;

public class ClientThread extends Thread implements Runnable {
	private final Server server;
	private final Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Profile profile;
	
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
			Object inputObject = null;
			while(inputObject == null) {
				inputObject = recieveObject();
			}
			
			if(inputObject instanceof Board) {
				Board inputBoard = (Board) inputObject;
				switch (inputBoard.getCommand()) {
					case NEW_GAME:
						newGame(inputBoard);
						break;
					case GET_GAMES:
						sendGameList();
						break;
					case JOIN_GAME:
						joinGame(inputBoard);
						break;
					case UPDATE:
						updateGameServer(inputBoard);
					default:
						break;
				}
			} else if(inputObject instanceof Profile) {
				Profile inputProfile = (Profile) inputObject;
				switch (inputProfile.getCommand()) {
					case LOGIN:
						login(inputProfile);
						break;
					default:
						break;
				
				}
			}
		}
	}
	
	void login(Profile profile) {
		Profile serverProfile = DatabaseQuery.logIn(profile);
		this.profile = serverProfile;
		sendObjectToClient(serverProfile);
	}
	
	void newGame(Board game) {
		server.createGame(game);
	}
	
	void sendGameList() {
		sendObjectToClient(server.getGames());
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
		sendObjectToClient(game);
	}
	
	private Object recieveObject() {
		try {
			return in.readObject();
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
	
	<E> void sendObjectToClient(E object) {
		try {
			out.writeObject(object);
			out.flush();
			out.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getUsername() {
		return profile.getUsername();
	}	

}
