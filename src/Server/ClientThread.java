package Server;

import java.net.Socket;

public class ClientThread {
	public final Server server;
	public final Socket socket;
	
	public ClientThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

}
