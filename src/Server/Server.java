package Server;

import Database.DatabaseInsert;
import Database.DatabaseQuery;
import Game.Board;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * Accepts connections to new clients and
 * distributes updates to all clients
 * @author Max Snelling
 * @version 5/5/20
 */
public class Server {
	private final int port = 50000;
	private ServerSocket serverSocket;
	private final ArrayList<ClientThread> clients;
	private final ArrayList<Board> games;
	
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
	
	public void createGame() {
		Board newGame = DatabaseInsert.createGame();
		games.add(newGame);
		updateClientGameList();
	}	
	
	/**
	 * Adds user to game by working out which is the
	 * new player. Updates players, games list and database
	 * @param game	game with new player joined
	 */
	public void joinGame(Board game) {
		Board joiningGameServer = getBoard(game.getGameID());
		addPlayer(joiningGameServer, game);
		updateGameServer(joiningGameServer);
		updatePlayersGame(game.getGameID());
		updateClientGameList();
	} 
	
	/**
	 * Based on if a player is already in the game, only
	 * the new player is added to the server copy of the 
	 * game
	 * @param serverGame	server copy of game
	 * @param clientGame	client game with added player
	 */
	public void addPlayer(Board serverGame, Board clientGame) {
		if(serverGame.getPlayer1() == null) {
			serverGame.addPlayer(clientGame.getPlayer1());
		} else if(serverGame.getPlayer2() == null) {
			serverGame.addPlayer(clientGame.getPlayer2());
		}		
		DatabaseQuery.addPlayer(serverGame);
	}
	
	public void updatePlayersGame(int gameID) {
		Board latestGame = getBoard(gameID);
		ArrayList<ClientThread> gameClients = getGameClients(latestGame);
		updateClientsGame(gameClients, latestGame);		
	}
	
	public ArrayList<ClientThread> getGameClients(Board game) {
		ArrayList<ClientThread> gameClients = new ArrayList<>();
		String player1 = game.getPlayer1();
		String player2 = game.getPlayer2();
		System.out.println(clients);
		for(ClientThread client:clients) {
			if(client.getUsername().equals(player1) || client.getUsername().equals(player2)) {
				gameClients.add(client);
			}
		}
		return gameClients;
	}
	
	public void updateClientsGame(ArrayList<ClientThread> clients, Board game) {
		for(ClientThread client:clients) {
			client.updateGame(game);
			if(!game.playing()) {
				client.updateGameEnded(game);
				DatabaseQuery.updateGameEnd(game);
			}
		}
	}
	
	public Board getBoard(int gameID) {
		for(Board game:games) {
			if(game.getGameID() == gameID) 
				return game;
		}
		throw new InvalidParameterException("Game not found");
	}
	
	public void updateGameServer(Board game) {
		int gameIndex = -1;
		for(int i=0; i<games.size(); i++) {
			if(games.get(i).getGameID() == game.getGameID()) {
				gameIndex = i;
				break;
			}
		}
		games.set(gameIndex, game);
	}
	
	public void updateClientGameList() {
		for(ClientThread client:clients) {
			client.sendObjectToClient(games);
		}
	}
	
	public void logOutClient(ClientThread client) {
		clients.remove(client);
		System.out.println("Disconnected Client");
	}
	
	public ArrayList<Board> getGames() {
		return games;
	}
	
	public ArrayList<ClientThread> getClients() {
		return clients;
	}
	
	public static void main(String[] args) {
		new Server();
	}

}
