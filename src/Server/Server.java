package Server;

import java.io.IOException;
import java.net.Socket;
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
		System.out.println(newGame);
		games.add(newGame);
	}
	
	public static void main(String[] args) {
		new Server();
	}

	public ArrayList<Board> getGames() {
		return games;
	}
	
	

}
