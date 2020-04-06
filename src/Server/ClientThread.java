package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

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
		
		try {
			out.writeObject("hello");
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {}
	}
	
	public void createObjectDataStreams() {
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			try {
				in.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
	}

	@Override
	public void run() {
		System.out.println("Client Thread Running");		
	}
	

}
