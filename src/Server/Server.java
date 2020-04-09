package Server;

import java.io.IOException;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import Game.Board;

import java.net.ServerSocket;

public class Server {
	private final int port = 50000;
	private ServerSocket serverSocket;
	private ArrayList<ClientThread> clients;
	private ArrayList<Board> games;
	
	public Server() {
		clients = new ArrayList<>();
		games = new ArrayList<>();
		createServerSocket();		
	}
	
	public void createServerSocket() {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server started");
			while(true) {
				acceptClient();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void acceptClient() {
		try {
			Socket socket = serverSocket.accept();
			createClient(socket);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createClient(Socket socket) {
		ClientThread client = new ClientThread(this, socket);
		client.start();
		clients.add(client);
		System.out.println("Client added");
	}
	
	public void createGame(Board newGame) {
		games.add(newGame);
	}
	

	public ArrayList<Board> getGames() {
		return games;
	}
	
	
	void joinGame(Board game) {
		updateGame(game);
		updatePlayersGame(game.getGameID());
	} 
	
	void updatePlayersGame(int gameID) {
		Board latestGame = getBoard(gameID);
		ArrayList<ClientThread> gameClients = getGameClients(latestGame);
		updateClientGame(gameClients, latestGame);
	}
	
	ArrayList<ClientThread> getGameClients(Board game) {
		ArrayList<ClientThread> gameClients = new ArrayList<>();
		String player1 = game.getPlayer1();
		String player2 = game.getPlayer2();
		for(ClientThread client:clients) {
			if(client.getUsername().equals(player1) || client.getUsername().equals(player2)) {
				gameClients.add(client);
			}
		}
		return gameClients;
	}
	
	void updateClientGame(ArrayList<ClientThread> clients, Board game) {
		for(ClientThread client:clients) {
			client.updateGame(game);
		}
	}
	
	Board getBoard(int gameID) {
		for(Board game:games) {
			if(game.getGameID() == gameID) 
				return game;
		}
		throw new InvalidParameterException("Game not found");
	}
	
	void updateGame(Board game) {
		int gameIndex = -1;
		for(int i=0; i<games.size(); i++) {
			if(games.get(i).getGameID() == game.getGameID()) {
				gameIndex = i;
				break;
			}
		}
		games.remove(gameIndex);
		games.add(game);
	}
	
	public static void main(String[] args) {
		new Server();
	}
	
	

}
