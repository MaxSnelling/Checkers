package Server;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {
	private final int port = 5000;
	private ServerSocket serverSocket;
	
	public Server() {
		createServerSocket();
	}
	
	public void createServerSocket() {
		try {
			serverSocket = new ServerSocket(port);
			while(true)
				acceptClient();
		} catch(IOException e) {
			System.out.println("Could not create server socket");
			e.printStackTrace();
		}
	}
	
	public void acceptClient() {
		try {
			Socket socket = serverSocket.accept();
			ClientThread client = new ClientThread(this, socket);
		} catch(IOException e) {
			System.out.println("Could not create client socket");
			e.printStackTrace();
		}
	}

}
